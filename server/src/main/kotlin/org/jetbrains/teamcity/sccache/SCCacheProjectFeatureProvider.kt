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