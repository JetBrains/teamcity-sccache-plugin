/*
 * Copyright 2000-2020 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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