
package org.jetbrains.teamcity.sccache

import jetbrains.buildServer.Used
import jetbrains.buildServer.serverSide.SProjectFeatureDescriptor

class SCCachePresentationProvider {
    @Used("jsp")
    fun getDescription(descriptor: SProjectFeatureDescriptor): String {
        return getDescription(descriptor.parameters)
    }

    companion object {
        fun getDescription(parameters: MutableMap<String, String>): String {
            val config = getBackendConfigFromFeatureParameters(parameters)
                    ?: return "Unknown remote backend configuration"
            return config.describe()
        }
    }
}