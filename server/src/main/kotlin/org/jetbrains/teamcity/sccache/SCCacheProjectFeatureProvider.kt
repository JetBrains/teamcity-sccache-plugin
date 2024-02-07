
package org.jetbrains.teamcity.sccache

import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.serverSide.oauth.OAuthConnectionDescriptor
import jetbrains.buildServer.serverSide.oauth.OAuthProvider
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.jetbrains.teamcity.sccache.SCCacheConstants.ProjectFeatureSettings

class SCCacheProjectFeatureProvider(descriptor: PluginDescriptor) : OAuthProvider() {
    private val editUrl = descriptor.getPluginResourcesPath("sccache-project.jsp")

    override fun getType(): String = ProjectFeatureSettings.FEATURE_TYPE

    override fun getDisplayName(): String = "sccache remote backend connection"

    override fun getEditParametersUrl(): String = editUrl

    override fun describeConnection(connection: OAuthConnectionDescriptor): String {
        return SCCachePresentationProvider.getDescription(connection.parameters)
    }

    override fun getPropertiesProcessor(): PropertiesProcessor {
        return object : PropertiesProcessor {
            override fun process(properties: MutableMap<String, String>): Collection<InvalidProperty> {
                val config = getBackendConfigFromFeatureParameters(properties)
                        ?: return listOf(InvalidProperty(ProjectFeatureSettings.BACKEND_TYPE, "Should be specified"))
                val errors = HashMap<String, String>()
                config.validate(errors)
                return errors.map { (name, msg) -> InvalidProperty(name, msg) }
            }
        }
    }

    override fun getDefaultProperties(): MutableMap<String, String>? {
        return null
    }

}