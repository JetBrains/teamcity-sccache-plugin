
package org.jetbrains.teamcity.sccache

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.SystemInfo
import jetbrains.buildServer.NetworkUtil
import jetbrains.buildServer.SimpleCommandLineProcessRunner
import jetbrains.buildServer.agent.*
import jetbrains.buildServer.log.Loggers
import jetbrains.buildServer.util.EventDispatcher
import jetbrains.buildServer.util.FileUtil
import org.jetbrains.teamcity.sccache.SCCacheConstants.AgentEnvironment
import org.jetbrains.teamcity.sccache.SCCacheConstants.BuildFeatureSettings
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class SCCacheBuildFeature(dispatcher: EventDispatcher<AgentLifeCycleListener>) : AgentLifeCycleAdapter() {
    companion object {
        val LOG = Logger.getInstance(Loggers.AGENT_CATEGORY + "." + SCCacheBuildFeature::class.java.name)!!

        private fun findExecutableInPath(): String? {
            val name = "sccache"
            val path = when {
                SystemInfo.isWindows -> System.getenv("Path")
                SystemInfo.isUnix -> System.getenv("PATH")
                else -> System.getenv("PATH") ?: System.getenv("Path")
            } ?: return null
            val suffix = if (SystemInfo.isWindows) ".exe" else ""

            val st = StringTokenizer(path, File.pathSeparator)
            while (st.hasMoreTokens()) {
                val token = st.nextToken()

                val file = File(token, name + suffix)
                if (file.exists() && file.isFile) {
                    return FileUtil.getCanonicalFile(file).path
                }
            }
            return null
        }

        internal fun getVersion(path: String): String? {
            val cl = GeneralCommandLine()
            cl.exePath = FileUtil.toSystemDependentName(path)
            cl.addParameter("--version")
            cl.envParams = HashMap()
            val result = SimpleCommandLineProcessRunner.runCommand(cl, null)
            if (result.exception != null) return null
            if (result.exitCode != 0) return null
            return getVersionFromOutput(result.stdout)
        }

        internal fun getVersionFromOutput(stdout: String): String? {
            val line = stdout.lines().firstOrNull { it.isNotBlank() } ?: return null
            val version = line.lowercase().removePrefix("sccache").trim()
            return if (version.matches("[0-9]+\\..*".toRegex())) version
            else null
        }
    }

    init {
        dispatcher.addListener(this)
    }

    private val servers = ConcurrentHashMap<Long, SCCacheServer>()


    override fun afterAgentConfigurationLoaded(agent: BuildAgent) {
        if (agent.configuration.configurationParameters.containsKey(SCCacheConstants.AGENT_SCCACHE_PATH_PARAMETER)) {
            return
        }

        val path = findExecutableInPath()
        if (!path.isNullOrBlank()) {
            agent.configuration.addConfigurationParameter(SCCacheConstants.AGENT_SCCACHE_PATH_PARAMETER, path)
            val version = getVersion(path)
            if (!version.isNullOrBlank()) {
                agent.configuration.addConfigurationParameter(SCCacheConstants.AGENT_SCCACHE_VERSION_PARAMETER, version)
            }
        }
    }


    override fun buildStarted(runningBuild: AgentRunningBuild) {
        val feature = runningBuild.getBuildFeaturesOfType(BuildFeatureSettings.FEATURE_TYPE).firstOrNull()
                ?: return

        val settings = SCCacheBuildFeatureSettings(feature.parameters)

        val logger = runningBuild.buildLogger
        logger.activity("Configuring and starting sccache server", BuildFeatureSettings.FEATURE_TYPE) {
            if (settings.port == -1) settings.port = NetworkUtil.getFreePort(4226)
            val backendConfig = getBackendConfigFromSharedParameters(runningBuild.sharedConfigParameters, settings.backendId)
            settings.backendConfig = backendConfig

            // user may override parameter in build configuration, otherwise use agent one
            val executable = runningBuild.sharedConfigParameters[SCCacheConstants.AGENT_SCCACHE_PATH_PARAMETER]
                    ?: runningBuild.agentConfiguration.configurationParameters[SCCacheConstants.AGENT_SCCACHE_PATH_PARAMETER]
                    ?: findExecutableInPath()
                    ?: "sccache"

            val server = SCCacheServer(settings, executable)

            // stop any running server
            try {
                server.stop(logger, true)
            } catch (e: Exception) {
                LOG.warn("Failed to stop running sccache server before starting our", e)
            }

            if (backendConfig != null) {
                if (!backendConfig.validate(HashMap())) {
                    logger.internalError(feature.type, "Remote backend configuration is incorrect, check build and project feature settings: $backendConfig", null)
                    return@activity
                }
                backendConfig.getSecureValues().forEach {
                    runningBuild.passwordReplacer.addPassword(it)
                }
            }
            try {
                servers[runningBuild.buildId] = server
                server.start(logger, runningBuild)
                logger.message("Server successfully started on port ${settings.port}")
            } catch (e: Exception) {
                logger.error("Failed to start server on port ${settings.port}: " + e.message)
                logger.exception(e)
                return@activity
            }

            runningBuild.addSharedEnvironmentVariable(AgentEnvironment.SCCACHE_SERVER_PORT, settings.port.toString())
        }
    }


    override fun beforeBuildFinish(build: AgentRunningBuild, buildStatus: BuildFinishedStatus) {
        val server = servers[build.buildId] ?: return
        val logger = build.buildLogger
        logger.activity("Stopping sccache server", BuildFeatureSettings.FEATURE_TYPE) {
            server.reportStatistics(logger)
            server.stop(logger)
            server.publishLog(logger)
        }
    }

    override fun buildFinished(build: AgentRunningBuild, buildStatus: BuildFinishedStatus) {
        servers.remove(build.buildId)
    }

}