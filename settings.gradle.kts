dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        mavenCentral()
    }
}

rootProject.name = "api-styles-poc"

include("domain")
include("application")
include("infrastructure")
include("adapter-rest")
include("adapter-graphql")
include("adapter-grpc")
include("adapter-websocket")
include("adapter-soap")
include("test-support")
include("architecture-tests")
