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

import org.jetbrains.teamcity.sccache.SCCacheConstants.ProjectFeatureSettings
import org.jetbrains.teamcity.sccache.SCCacheConstants.ProjectFeatureSettings.S3Backend


interface BackendConfig {
    fun getSecureValues(): List<String>
    fun getEnv(): Map<String, String>
    fun validate(result: MutableMap<String, String>): Boolean
    fun describe(): String
}

class S3BackendConfig(
        val bucket: String = "",
        val prefix: String = "",
        val iamCredentialsUrl: String = "",
        val accessKey: String = "",
        val secretKey: String = "",
        val endpoint: String = "",
        val useSSL: Boolean = true,
) : BackendConfig {
    constructor(map: Map<String, String>) : this(
            map[S3Backend.BUCKET] ?: "",
            map[S3Backend.PREFIX] ?: "",
            map[S3Backend.IAM_CREDENTIALS_URL] ?: "",
            map[S3Backend.ACCESS_KEY] ?: "",
            map[S3Backend.SECRET_KEY] ?: "",
            map[S3Backend.ENDPOINT] ?: "",
            map[S3Backend.SSL].toBoolean(),
    )

    override fun getSecureValues(): List<String> {
        return if (secretKey.isNotEmpty()) listOf(secretKey) else emptyList()
    }

    override fun getEnv(): Map<String, String> {
        val env = HashMap<String, String>()
        env["SCCACHE_BUCKET"] = bucket
        if (prefix.isNotEmpty()) {
            env["SCCACHE_S3_KEY_PREFIX"] = prefix
        }
        if (endpoint.isNotEmpty()) {
            env["SCCACHE_ENDPOINT"] = endpoint
        }
        if (iamCredentialsUrl.isNotEmpty()) {
            env["AWS_IAM_CREDENTIALS_URL"] = iamCredentialsUrl
        } else if (accessKey.isNotEmpty()) {
            env["AWS_ACCESS_KEY_ID"] = accessKey
            if (secretKey.isNotEmpty()) {
                env["AWS_SECRET_ACCESS_KEY"] = secretKey
            }
        }
        env["SCCACHE_S3_USE_SSL"] = useSSL.toString()
        return env
    }

    override fun validate(result: MutableMap<String, String>): Boolean {
        if (bucket.isEmpty()) {
            result[S3Backend.BUCKET] = "Should not be empty"
            return false
        }
        if (iamCredentialsUrl.isNotEmpty() && (accessKey.isNotEmpty() || secretKey.isNotEmpty())) {
            result[S3Backend.IAM_CREDENTIALS_URL] = "Conflicts with Access Key and Secret Key"
            result[S3Backend.ACCESS_KEY] = "Conflicts with IAM Credentials Url"
            result[S3Backend.SECRET_KEY] = "Conflicts with IAM Credentials Url"
        }
        return true
    }

    override fun describe(): String {
        return buildString {
            append("S3 (")
            if (endpoint.isNotEmpty()) {
                append("Endpoint: $endpoint, ")
            }
            append("Bucket: $bucket")
            if (prefix.isNotEmpty()) {
                append(", Keys Prefix: $prefix")
            }
            if (iamCredentialsUrl.isNotEmpty()) {
                append(", IAM Credentials URL: $iamCredentialsUrl")
            } else if (accessKey.isNotEmpty()) {
                append(", Access Key: $accessKey")
                if (secretKey.isNotEmpty()) {
                    append(", Secret Key ******")
                }
            }
            append(", Use SSL: $useSSL)")
        }

    }

    override fun toString(): String {
        return describe()
    }
}

fun getBackendConfigFromFeatureParameters(parameters: Map<String, String>): BackendConfig? {
    val type = parameters[ProjectFeatureSettings.BACKEND_TYPE] ?: return null

    return when (type) {
        S3Backend.TYPE -> S3BackendConfig(parameters)
        else -> null
    }
}

fun getBackendConfigFromSharedParameters(parameters: Map<String, String>, featureId: String): BackendConfig? {
    if (featureId.isEmpty()) return null
    val prefix = "${ProjectFeatureSettings.FEATURE_TYPE}.${featureId}."
    val params = parameters
            .filterKeys { it.startsWith(prefix) }
            .mapKeys { it.key.substring(prefix.length) }

    return getBackendConfigFromFeatureParameters(params)
}
