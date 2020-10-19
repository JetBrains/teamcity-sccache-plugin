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

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.intellij.execution.configurations.GeneralCommandLine
import jetbrains.buildServer.ExecResult
import jetbrains.buildServer.SimpleCommandLineProcessRunner
import jetbrains.buildServer.agent.BuildProgressLogger
import jetbrains.buildServer.messages.serviceMessages.BuildStatisticValue
import jetbrains.buildServer.util.FileUtil
import org.jetbrains.teamcity.sccache.SCCacheConstants.BuildFeatureSettings.FEATURE_TYPE

class SCCacheServer(private val settings: SCCacheBuildFeatureSettings, private val executable: String) {
    fun reportStatistics(logger: BuildProgressLogger) {
        // invoke 'sccache -s --stats-format json'
        // parse json
        // report statistics messages

        val result = run(null, "--show-stats", "--stats-format", "json")
        if (reportFailures(result, logger)) return

        val statistics = ArrayList<BuildStatisticValue>()

        try {
            val json = Gson().fromJson(result.stdout, JsonObject::class.java)
            val jsonStats = json.getAsJsonObject("stats")
            for ((name, el) in jsonStats.entrySet()) {
                if (el is JsonPrimitive) {
                    if (el.isNumber) {
                        statistics.add(BuildStatisticValue("sccache.${name}", el.asInt))
                    }
                } else if (el is JsonObject) {
                    if (name.endsWith("_duration")) {
                        val secs = el.getAsJsonPrimitive("secs").asInt
                        val nanos = el.getAsJsonPrimitive("nanos").asInt
                        statistics.add(BuildStatisticValue("sccache.${name}_ms", secs * 1_000 + nanos / 1_000_000))
                    } else if (el.has("counts")) {
                        val counts = el.getAsJsonObject("counts")
                        // TODO: consider report separate languages
                        val sum = counts.entrySet().mapNotNull { it.value as? JsonPrimitive }.map { it.asInt }.sum()
                        statistics.add(BuildStatisticValue("sccache.${name}", sum))
                    }
                }
            }
        } catch (e: Exception) {
            logger.error("Failed to parse sccache json output")
            logger.message("sccache output:\n" + result.stdout)
        }

        for (value in statistics) {
            logger.message(value.asString())
        }
    }

    fun start(logger: BuildProgressLogger) {
        // invoke 'sccache --start-server' with all necessary env variables

        val env = HashMap<String, String>()
        // server should never stop itself
        env["SCCACHE_IDLE_TIMEOUT"] = "0"
        env["SCCACHE_SERVER_PORT"] = settings.port.toString()
        settings.backendConfig?.let { env.putAll(it.getEnv()) }

        logger.message("Using env variables: ${env.toSortedMap()}")

        val result = run(env, "--start-server")
        if (reportFailures(result, logger)) return

        val stdout = result.stdout.trim()
        if (stdout.isNotEmpty() && stdout != "Starting sccache server...") {
            logger.message("sccache stdout:\n$stdout")
        }
        if (result.stderr.isNotEmpty()) logger.warning("sccache stderr:\n" + result.stderr)
    }

    fun stop(logger: BuildProgressLogger, ignoreErrors: Boolean = false) {
        // invoke 'sccache --stop-server'

        val result = run(null, "--stop-server")
        if (ignoreErrors) return
        if (reportFailures(result, logger)) return
    }

    private fun run(environment: Map<String, String>? = null, vararg args: String): ExecResult {
        val cl = GeneralCommandLine()
        cl.exePath = FileUtil.toSystemDependentName(executable)
        for (element in args) {
            cl.addParameter(element)
        }
        val env: HashMap<String, String> = if (environment != null) HashMap(environment) else HashMap()
        env["SCCACHE_SERVER_PORT"] = settings.port.toString()
        cl.envParams = env
        return SimpleCommandLineProcessRunner.runCommand(cl, null)
    }

    private fun reportFailures(result: ExecResult, logger: BuildProgressLogger): Boolean {
        if (result.exception != null) {
            logger.internalError(FEATURE_TYPE, "Failed to run 'sccache', exit code ${result.exitCode} ", result.exception)
            throw result.exception
        }
        if (result.exitCode != 0) {
            logger.internalError(FEATURE_TYPE, "Failed to run 'sccache', exit code ${result.exitCode}", result.exception)
            if (result.stdout.isNotEmpty()) logger.message("sccache stdout:\n" + result.stdout)
            if (result.stderr.isNotEmpty()) logger.warning("sccache stderr:\n" + result.stderr)
            return true
        }
        return false
    }

}
