plugins {
    alias(libs.plugins.datalift.android.library)
    alias(libs.plugins.datalift.android.library.compose)
//    alias(libs.plugins.android.application)
//    alias(libs.plugins.kotlin.android)
//    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.datalift.ui"
}

dependencies {
//    lintPublish(projects.lint)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.material3.android)
    implementation(libs.androidx.compose.ui)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui.tooling.preview.android)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}