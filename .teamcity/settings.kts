import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.buildFeatures.notifications
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.failureConditions.failOnMetricChange
import jetbrains.buildServer.configs.kotlin.buildFeatures.commitStatusPublisher

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2024.12"

project {
    description = "Relay Examples - A collection of example applications using Relay"

    // VCS Root definition
    vcsRoot(RelayExamplesVcs)

    // Build configuration
    buildType(RelayExamplesBuild)
}

object RelayExamplesVcs : GitVcsRoot({
    name = "Relay Examples"
    url = "https://github.com/relayjs/relay-examples.git"
    branch = "refs/heads/main"
    branchSpec = """
        +:refs/heads/*
    """.trimIndent()
})

object RelayExamplesBuild : BuildType({
    name = "Build Relay Examples"
    description = "Build all Relay example applications using yarn"

    vcs {
        root(RelayExamplesVcs)
    }

    steps {
        // Install yarn globally if not already installed
        script {
            name = "Install yarn"
            scriptContent = """
                npm install -g yarn
            """.trimIndent()
        }

        // Build each example project
        script {
            name = "Build examples"
            scriptContent = """
                # Install dependencies and build each example
                for dir in */; do
                    if [ -f "${'$'}dir/package.json" ]; then
                        echo "Building ${'$'}dir"
                        cd ${'$'}dir
                        yarn install
                        yarn build
                        cd ..
                    fi
                done
            """.trimIndent()
        }
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }

    features {
        perfmon {
        }
    }

    artifactRules = """
        # Publish build artifacts for each example
        */public/** => public-assets.zip
        */__generated__/** => generated-artifacts.zip
        */dist/** => dist-artifacts.zip
    """.trimIndent()
})
