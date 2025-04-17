import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import com.datalift.convention.configureFlavors
import com.datalift.convention.configureGradleManagedDevices
import com.datalift.convention.configureKotlinAndroid
import com.datalift.convention.configurePrintApksTask
import com.datalift.convention.disableUnnecessaryAndroidTests
import com.datalift.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target){
            apply(plugin = "com.android.library")
            apply(plugin = "org.jetbrains.kotlin.android")
//            apply(plugin = "")
            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 35
                defaultConfig.testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testOptions.animationsDisabled = true
                configureFlavors(this)
                configureGradleManagedDevices(this)
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"
                extensions.configure<LibraryAndroidComponentsExtension>{
                    configurePrintApksTask(this)
                    disableUnnecessaryAndroidTests(target)
                }
                dependencies {
                    "androidTestImplementation"(libs.findLibrary("kotlin.test").get())
                    "testImplementation"(libs.findLibrary("kotlin.test").get())

                    "implementation"(libs.findLibrary("androidx.tracing.ktx").get())
                }
            }
        }
    }
}