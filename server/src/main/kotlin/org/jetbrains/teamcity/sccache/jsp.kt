/*
 * Copyright 2000-2023 JetBrains s.r.o.
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
@file:Suppress("PropertyName")

package org.jetbrains.teamcity.sccache

class SCCacheBuildFeatureJspKeys {
    val BACKEND = SCCacheConstants.BuildFeatureSettings.BACKEND
    val PORT = SCCacheConstants.BuildFeatureSettings.PORT
    val REQUIREMENT = SCCacheConstants.BuildFeatureSettings.REQUIREMENT
    val REQUIREMENT_VALUE = SCCacheConstants.BuildFeatureSettings.REQUIREMENT_VALUE
}

class SCCacheProjectFeatureJspKeys {
    val TYPE = SCCacheConstants.ProjectFeatureSettings.BACKEND_TYPE

    val S3_TYPE = SCCacheConstants.ProjectFeatureSettings.S3Backend.TYPE
    val S3_BUCKET = SCCacheConstants.ProjectFeatureSettings.S3Backend.BUCKET
    val S3_PREFIX = SCCacheConstants.ProjectFeatureSettings.S3Backend.PREFIX
    val S3_IAM_CREDENTIALS_URL = SCCacheConstants.ProjectFeatureSettings.S3Backend.IAM_CREDENTIALS_URL
    val S3_ACCESS_KEY = SCCacheConstants.ProjectFeatureSettings.S3Backend.ACCESS_KEY
    val S3_SECRET_KEY = SCCacheConstants.ProjectFeatureSettings.S3Backend.SECRET_KEY
    val S3_REGION = SCCacheConstants.ProjectFeatureSettings.S3Backend.REGION
    val S3_ENDPOINT = SCCacheConstants.ProjectFeatureSettings.S3Backend.ENDPOINT
    val S3_SSL = SCCacheConstants.ProjectFeatureSettings.S3Backend.SSL
}