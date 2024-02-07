
package org.jetbrains.teamcity.sccache

import jetbrains.buildServer.agent.BuildProgressLogger

fun <T> BuildProgressLogger.activity(activityName: String, activityType: String, body: () -> T): T {
    this.activityStarted(activityName, activityType)
    try {
        return body()
    } catch (t: Throwable) {
        this.internalError(activityType, "Exception occurred: ${t.message}", t)
        throw t
    } finally {
        this.activityFinished(activityName, activityType)
    }
}