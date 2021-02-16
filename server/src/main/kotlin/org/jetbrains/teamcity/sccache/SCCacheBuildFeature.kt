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

import com.intellij.util.SmartList
import jetbrains.buildServer.serverSide.BuildFeature
import jetbrains.buildServer.serverSide.InvalidProperty
import jetbrains.buildServer.serverSide.PropertiesProcessor
import jetbrains.buildServer.web.openapi.PluginDescriptor
import org.jetbrains.teamcity.sccache.SCCacheConstants.BuildFeatureSettings

class SCCacheBuildFeature(descriptor: PluginDescriptor) : BuildFeature() {
    private val editUrl = descriptor.getPluginResourcesPath("sccache-build.jsp")

    override fun getType(): String = BuildFeatureSettings.FEATURE_TYPE

    override fun getDisplayName(): String = "sccache" // TODO: Maybe better naming?

    override fun getEditParametersUrl(): String = editUrl

    override fun describeParameters(params: Map<String, String>): String {
        return "sccache with backend connection '${params[BuildFeatureSettings.BACKEND]}'"
    }

    override fun getParametersProcessor(): PropertiesProcessor {
        return PropertiesProcessor { properties ->
            val result = SmartList<InvalidProperty>()

            val backend = properties[BuildFeatureSettings.BACKEND]
            if (backend.isNullOrEmpty() || backend == "unspecified") {
                result.add(InvalidProperty(BuildFeatureSettings.BACKEND, "Connection should be selected"))
            }

            result
        }
    }

    override fun getDefaultParameters(): Map<String, String> {
        return mapOf(
                BuildFeatureSettings.BACKEND to BuildFeatureSettings.BACKEND_DEFAULT,
                BuildFeatureSettings.PORT to BuildFeatureSettings.PORT_DEFAULT.toString(),
        )
    }

    override fun isMultipleFeaturesPerBuildTypeAllowed(): Boolean = false

    override fun getPlaceToShow(): PlaceToShow = PlaceToShow.GENERAL

    override fun isRequiresAgent(): Boolean = true
}