
package org.jetbrains.teamcity.sccache

@Suppress("MayBeConstant")
object SCCacheConstants {
    val AGENT_SCCACHE_PATH_PARAMETER = "sccache.path"
    val AGENT_SCCACHE_VERSION_PARAMETER = "sccache.version"
    val SCCACHE_SERVER_LOGGING_LEVEL = "sccache.logging.level"

    object BuildFeatureSettings {
        val FEATURE_TYPE = "teamcity-sccache"
        val BACKEND = "backend"
        val PORT = "port"

        val BACKEND_DEFAULT = ""
        val PORT_DEFAULT = -1

        val REQUIREMENT = "agent_requirement"
        val REQUIREMENT_VALUE = "%$AGENT_SCCACHE_PATH_PARAMETER%"
    }

    object ProjectFeatureSettings {
        @JvmField val FEATURE_TYPE = "teamcity-sccache-remote-backend"

        val BACKEND_TYPE = "backend_type"

        object S3Backend {
            val TYPE = "s3"
            val BUCKET = "bucket"
            val PREFIX = "prefix"

            val IAM_CREDENTIALS_URL = "iam_credentials_url"
            val ACCESS_KEY = "access_key"
            val SECRET_KEY = "secure:secret_key"

            val REGION = "region"
            val ENDPOINT = "endpoint"
            val SSL = "ssl"
        }
    }


    object AgentEnvironment {
        val SCCACHE_SERVER_PORT = "SCCACHE_SERVER_PORT"
        val RUSTC_WRAPPER = "RUSTC_WRAPPER"
    }

}