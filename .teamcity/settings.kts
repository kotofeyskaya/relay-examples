import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

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
    description = "Relay Examples Project"

    // Define VCS Root
    val vcsRoot = GitVcsRoot {
        id("RelayExamplesVcs")
        name = "relay-examples git repository"
        url = "https://github.com/relayjs/relay-examples.git"
        branch = "refs/heads/main"
    }
    vcsRoot(vcsRoot)

    // Define Build Configuration
    buildType {
        id("RelayExamplesBuild")
        name = "Build Relay Examples"

        vcs {
            root(vcsRoot)
        }

        // Build Steps
        steps {
            // Install dependencies using yarn
            script {
                name = "Install Dependencies"
                scriptContent = "yarn install"
                workingDir = "todo"
            }

            // Build the project using yarn
            script {
                name = "Build Project"
                scriptContent = "yarn build"
                workingDir = "todo"
            }

            // Run tests if needed
            script {
                name = "Run Linting"
                scriptContent = "yarn lint"
                workingDir = "todo"
            }
        }

        // Triggers
        triggers {
            vcs {
                branchFilter = "+:*"
            }
        }

        // Features
        features {
            perfmon {
            }
        }

        // Artifacts
        artifactRules = """
            todo/__generated__/relay => relay-artifacts.zip
            todo/__generated__/queries.json => queries.json
            todo/data => data.zip
            todo/js => js.zip
        """.trimIndent()
    }
}
