plugins {
    alias(libs.plugins.datalift.android.library)
    alias(libs.plugins.datalift.android.library.compose)
    alias(libs.plugins.datalift.hilt)
}

android {
    namespace = "com.datalift.screenshot_testing"
}

dependencies {
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
    api(libs.roborazzi)
    implementation(libs.androidx.compose.ui.test)
    implementation(libs.androidx.activity.compose)
    implementation(libs.robolectric)
    implementation(libs.material)
    implementation(projects.core.designsystem)
}