plugins {
    alias(libs.plugins.datalift.android.library)
    alias(libs.plugins.datalift.android.library.compose)
    alias(libs.plugins.roborazzi)
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.datalift.designsystem"
}

dependencies {
//    lintPublish(projects.lint)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.compose.ui)
    api(libs.androidx.compose.runtime.android)
    implementation(libs.androidx.ui.tooling.preview.android)

//    testImplementation(libs.junit)
    testImplementation(libs.androidx.compose.ui.test)
    testImplementation(libs.androidx.compose.ui.testManifest)

    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.robolectric)
    testImplementation(projects.core.screenshotTesting)
}