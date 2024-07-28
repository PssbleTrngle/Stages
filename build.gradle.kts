val mod_id: String by extra
val mod_version: String by extra

plugins {
    idea
    id("com.possible-triangle.gradle") version ("0.1.5")
}

subprojects {
    repositories {
        apply(from = "https://raw.githubusercontent.com/PssbleTrngle/GradleHelper/main/repositories/create-fabric.build.kts")

        modrinthMaven()
        curseMaven()

        maven {
            url = uri("https://maven.blamejared.com")
            content {
                includeGroup("mezz.jei")
            }
        }

        maven {
            url = uri("https://maven.k-4u.nl")
            content {
                includeGroup("mcjty.theoneprobe")
            }
        }
    }

    enablePublishing {
        githubPackages()
    }

    tasks.withType<Jar> {
        exclude("datapacks")
    }
}

enableSonarQube()
