plugins {
    id("maven-publish")
    id("signing")
}

interface CustomPublishingExtension {
    val description: Property<String>
    val repositoryUrl: Property<String>
    val license: Property<String>
}

val extension = project.extensions.create<CustomPublishingExtension>("publish")

group = findProperty("group")!!
version = findProperty("version")!!

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

afterEvaluate {
    val javadocJar by tasks.registering(Jar::class) {
        archiveClassifier = "javadoc"
    }

    publishing {
        repositories {
            val githubUsername = findProperty("githubUsername")?.toString()
            val githubToken = findProperty("githubToken")?.toString()
            if (githubUsername != null && githubToken != null) {
                maven("https://maven.pkg.github.com/boswelja/jmdict-kmp") {
                    name = "github"
                    credentials {
                        username = githubUsername
                        password = githubToken
                    }
                }
            }
            val ossrhUsername = findProperty("centralUsername")?.toString()
            val ossrhPassword = findProperty("centralPassword")?.toString()
            if (ossrhUsername != null && ossrhPassword != null) {
                maven("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/") {
                    name = "central"
                    credentials {
                        username = ossrhUsername
                        password = ossrhPassword
                    }
                }
            }
        }

        publications.withType<MavenPublication> {
            artifact(javadocJar)
            pom {
                name = project.name
                description = extension.description
                url = extension.repositoryUrl.get()
                licenses {
                    license {
                        name = extension.license.get()
                        url = "${extension.repositoryUrl}/blob/main/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "boswelja"
                        name = "Jack Boswell (boswelja)"
                        email = "boswelja@outlook.com"
                        url = "https://github.com/boswelja"
                    }
                }
                scm {
                    url.set(extension.repositoryUrl.get())
                }
            }
        }
    }

    // TODO: Remove after https://youtrack.jetbrains.com/issue/KT-46466 is fixed
    //  Thanks to KSoup repository for this code snippet
    tasks.withType(AbstractPublishToMaven::class).configureEach {
        dependsOn(tasks.withType(Sign::class))
    }
}
