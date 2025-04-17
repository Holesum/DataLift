pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "Datalift"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
//include(":core")
include(":core:designsystem")
//include(":build-logic")
//include(":build-logic:convention")
//include(":build-logic:convention")
//include(":build-logic:convention")
include(":core:screenshot-testing")
