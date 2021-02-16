/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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