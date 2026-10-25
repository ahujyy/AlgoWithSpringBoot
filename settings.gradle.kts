pluginManagement {
    repositories {
        maven { url = uri("https://aliyun.com") }
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://aliyun.com") }
        mavenCentral()
    }
}


rootProject.name = "AlgoWithSpringBoot"

