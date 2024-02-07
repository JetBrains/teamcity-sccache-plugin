
package org.jetbrains.teamcity.sccache

import org.jetbrains.teamcity.sccache.SCCacheConstants.ProjectFeatureSettings
import org.jetbrains.teamcity.sccache.SCCacheConstants.ProjectFeatureSettings.S3Backend
import java.util.*


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
        val region: String = "",
        val endpoint: String = "",
        val useSSL: Boolean = true,
) : BackendConfig {
    constructor(map: Map<String, String>) : this(
            map[S3Backend.BUCKET] ?: "",
            map[S3Backend.PREFIX] ?: "",
            map[S3Backend.IAM_CREDENTIALS_URL] ?: "",
            map[S3Backend.ACCESS_KEY] ?: "",
            map[S3Backend.SECRET_KEY] ?: "",
            map[S3Backend.REGION] ?: "",
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
        if (region.isNotEmpty()) {
            env["SCCACHE_REGION"] = region
        }
        if (endpoint.isNotEmpty()) {
            env["SCCACHE_ENDPOINT"] = endpoint
            if (region.isEmpty()) {
                getAWSRegionFromEndpoint(endpoint)?.let {
                    env["SCCACHE_REGION"] = it
                }
            }
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
            if (region.isNotEmpty()) {
                append(", Region: $region")
            } else if (endpoint.isNotEmpty()) {
                getAWSRegionFromEndpoint(endpoint)?.let {
                    append(", Detected region: $it")
                }
            }
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

    companion object {
        fun getAWSRegionFromEndpoint(endpoint: String): String? {
            val value = endpoint.trim('/')
            if (!value.endsWith("amazonaws.com")) return null
            val split = LinkedList(value.split('.').asReversed())
            if (split.pop() != "com") return null
            if (split.pop() != "amazonaws") return null
            val candidate = split.pop()

            if (candidate == "s3" && split.isEmpty()) return "us-east-1" // special legacy case s3.amazonaws.com

            if (!split.pop().contains("s3")) return null

            val dashes = candidate.count { it == '-' }
            // two dashes in regular regions, three in gov regions
            if (dashes != 2 && dashes != 3) return null
            return candidate

            // s3.REGION.amazonaws.com
            // s3-fips.REGION.amazonaws.com
            // s3.dualstack.REGION.amazonaws.com
            // s3-fips.dualstack.REGION.amazonaws.com

            // ACCOUNT.s3-control.REGION.amazonaws.com
            // ACCOUNT.s3-control-fips.REGION.amazonaws.com
            // ACCOUNT.s3-control.dualstack.REGION.amazonaws.com
            // ACCOUNT.s3-control-fips.dualstack.REGION.amazonaws.com
        }
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