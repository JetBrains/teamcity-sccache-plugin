
package org.jetbrains.teamcity.sccache

import org.testng.Assert.*
import org.testng.annotations.Test

class SCCacheBuildFeatureTest {
    @Test
    fun testVersionFromOutput() {
        assertEquals(SCCacheBuildFeature.getVersionFromOutput("sccache 0.3.1\n"), "0.3.1")
        assertEquals(SCCacheBuildFeature.getVersionFromOutput("sccache 0.5.4"), "0.5.4")
        assertEquals(SCCacheBuildFeature.getVersionFromOutput("sccache 0.4.0-pre.6"), "0.4.0-pre.6")
        assertEquals(SCCacheBuildFeature.getVersionFromOutput(""), null)
    }
}