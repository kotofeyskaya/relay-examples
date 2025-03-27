import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot
import jetbrains.buildServer.configs.kotlin.buildFeatures.notifications
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.failureConditions.BuildFailureOnMetric
import jetbrains.buildServer.configs.kotlin.failureConditions.failOnMetricChange

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
})

object RelayExamplesBuild : BuildType({
    name = "Build Relay Examples"
    description = "Build all Relay example applications using yarn"

    vcs {
        root(RelayExamplesVcs)
    }

    steps {
        // Build todo example
        script {
            name = "Build Todo Example"
            workingDir = "todo"
            scriptContent = """
                yarn install
                yarn build
            """.trimIndent()
        }

        // Build issue-tracker example
        script {
            name = "Build Issue Tracker Example"
            workingDir = "issue-tracker"
            scriptContent = """
                yarn install
                yarn build
            """.trimIndent()
        }

        // Build issue-tracker-next-v13 example
        script {
            name = "Build Issue Tracker Next v13 Example"
            workingDir = "issue-tracker-next-v13"
            scriptContent = """
                yarn install
                yarn build
            """.trimIndent()
        }

        // Build data-driven-dependencies example
        script {
            name = "Build Data Driven Dependencies Example"
            workingDir = "data-driven-dependencies"
            scriptContent = """
                yarn install
                yarn build
            """.trimIndent()
        }

        // Build newsfeed example
        script {
            name = "Build Newsfeed Example"
            workingDir = "newsfeed"
            scriptContent = """
                yarn install
                yarn build
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

    // Publish artifacts
    artifactRules = """
        todo => todo.zip
        issue-tracker => issue-tracker.zip
        issue-tracker-next-v13 => issue-tracker-next-v13.zip
        data-driven-dependencies => data-driven-dependencies.zip
        newsfeed => newsfeed.zip
    """.trimIndent()
})
