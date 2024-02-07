
package org.jetbrains.teamcity.sccache

import org.jetbrains.teamcity.sccache.SCCacheConstants.BuildFeatureSettings

data class SCCacheBuildFeatureSettings(
        val backendId: String,
        var port: Int,
        var backendConfig: BackendConfig? = null
) {
    constructor(map: Map<String, String>) : this(
            map[BuildFeatureSettings.BACKEND] ?: BuildFeatureSettings.BACKEND_DEFAULT,
            map[BuildFeatureSettings.PORT]?.toIntOrNull() ?: BuildFeatureSettings.PORT_DEFAULT,
    )
}