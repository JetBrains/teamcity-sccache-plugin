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
package org.jetbrains.teamcity.sccache

import org.testng.Assert.*
import org.testng.annotations.Test

class SCCacheBuildFeatureTest {
    @Test
    fun testVersionFromOutput() {
        assertEquals(SCCacheBuildFeature.getVersionFromOutput("sccache 0.3.1\n"), "0.3.1")
        assertEquals(SCCacheBuildFeature.getVersionFromOutput("sccache 0.5.4"), "0.5.4")
        assertEquals(SCCacheBuildFeature.getVersionFromOutput(""), null)
    }
}