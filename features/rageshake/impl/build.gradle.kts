/*
 * Copyright (c) 2022 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// TODO: Remove once https://youtrack.jetbrains.com/issue/KTIJ-19369 is fixed
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("io.element.android-compose-library")
    alias(libs.plugins.anvil)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
}

android {
    namespace = "io.element.android.features.rageshake.impl"
}

anvil {
    generateDaggerFactories.set(true)
}

dependencies {
    implementation(projects.anvilannotations)
    anvil(projects.anvilcodegen)
    implementation(projects.libraries.androidutils)
    implementation(projects.libraries.core)
    implementation(projects.libraries.architecture)
    implementation(projects.libraries.designsystem)
    implementation(projects.libraries.elementresources)
    implementation(projects.libraries.uiStrings)
    api(libs.squareup.seismic)
    api(projects.features.rageshake.api)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    ksp(libs.showkase.processor)

    testImplementation(libs.test.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.molecule.runtime)
    testImplementation(libs.test.truth)
    testImplementation(libs.test.turbine)
    testImplementation(libs.test.mockk)
    testImplementation(projects.libraries.matrix.test)
    testImplementation(projects.features.rageshake.test)

    androidTestImplementation(libs.test.junitext)
}