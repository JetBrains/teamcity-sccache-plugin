
package org.jetbrains.teamcity.sccache

import jetbrains.buildServer.serverSide.BuildStartContext
import jetbrains.buildServer.serverSide.BuildStartContextProcessor
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor
import jetbrains.buildServer.serverSide.SRunningBuild
import jetbrains.buildServer.serverSide.oauth.OAuthConstants

class SCCacheBuildStartContextProcessor : BuildStartContextProcessor {
    companion object {
        private fun getBuildFeature(build: SRunningBuild): SCCacheBuildFeatureSettings? {
            val featureDescriptor = build.getBuildFeaturesOfType(SCCacheConstants.BuildFeatureSettings.FEATURE_TYPE).firstOrNull()
            return featureDescriptor?.let { SCCacheBuildFeatureSettings(it.parameters) }
        }

        private fun getBackendProjectFeature(build: SRunningBuild, buildFeature: SCCacheBuildFeatureSettings): SProjectFeatureDescriptor? {
            val buildType = build.buildType ?: return null

            val projectFeatures = buildType.project.getAvailableFeaturesOfType(OAuthConstants.FEATURE_TYPE).filter {
                SCCacheConstants.ProjectFeatureSettings.FEATURE_TYPE == it.parameters[OAuthConstants.OAUTH_TYPE_PARAM]
            }
            return projectFeatures.firstOrNull {
                it.id == buildFeature.backendId
            }
        }
    }

    override fun updateParameters(context: BuildStartContext) {
        // Since project features are not passed onto agent
        // we've to get them and add as shared parameters for a BuildStartContext
        val build = context.build
        val buildFeature = getBuildFeature(build) ?: return
        val projectFeature = getBackendProjectFeature(build, buildFeature) ?: return

        val additionalParameters = projectFeature.parameters
                .filterKeys { it != "providerType" }
                .mapKeys {
                    "${SCCacheConstants.ProjectFeatureSettings.FEATURE_TYPE}.${projectFeature.id}.${it.key}"
                }
        additionalParameters.forEach {
            context.addSharedParameter(it.key, it.value)
        }
    }
}