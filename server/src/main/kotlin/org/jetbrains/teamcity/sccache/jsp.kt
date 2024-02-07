
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