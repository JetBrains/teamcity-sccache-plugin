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

@Suppress("MayBeConstant")
object SCCacheConstants {
    val AGENT_SCCACHE_PATH_PARAMETER = "sccache.path"

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

            val ENDPOINT = "endpoint"
            val SSL = "ssl"
        }
    }


    object AgentEnvironment {
        val SCCACHE_SERVER_PORT = "SCCACHE_SERVER_PORT"
        val RUSTC_WRAPPER = "RUSTC_WRAPPER"
    }

}