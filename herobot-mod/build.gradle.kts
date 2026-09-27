plugins {
    alias(libs.plugins.fabric.loom)
    id("herobot.java")
    id("herobot.shade")
    `maven-publish`
}

description = "HeroBot for Fabric."

version = extra["artifactVersion"] as String

base {
    archivesName = "herobot-fabric"
}

loom {
    splitEnvironmentSourceSets()
    accessWidenerPath.set(file("src/main/resources/herobot.classtweaker"))

    mods {
        register("herobot") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets["client"])
        }
    }
}

// MC 26.2 is unobfuscated (official namespace). Loom generates classtweaker with "named"
// namespace which doesn't match. Disable remapJar since code already uses official names.
tasks.matching { it.name == "remapJar" }.configureEach {
    enabled = false
}

// Since remapJar is disabled, make shadowJar produce the final artifact directly.
tasks.shadowJar {
    archiveClassifier = ""
    destinationDirectory = layout.buildDirectory.dir("libs")
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

repositories {
    maven("https://api.modrinth.com/maven") { name = "Modrinth" }
    maven("https://maven.fabricmc.net/") { name = "Fabric" }
}

// What gets bundled into the mod jar: `common` plus everything it exposes as `api`
// (pathetic). Transitive, unlike the rest of the runtime classpath, which is Minecraft
// and the mod dependencies and must stay out of the jar.
val shade = configurations.dependencyScope("shade")
val shadeClasspath = configurations.resolvable("shadeClasspath") {
    extendsFrom(shade.get())
}

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)

    implementation(libs.fabric.api)

    compileOnly(libs.carpet)

    compileOnly(libs.voicechat.api)
    "clientCompileOnly"(libs.voicechat.api)

    implementation(projects.herobotCommon)
    add(shade.name, projects.herobotCommon)
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version,
            "minecraft_version" to libs.versions.minecraft.get(),
            "loader_version" to libs.versions.fabric.loader.get(),
        )

        inputs.properties(props)
        filteringCharset = "UTF-8"

        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }

    // shadowJar assembles its own copy of both source sets plus the bundled dependencies, then
    // remapJar consumes it in place of Loom's `jar`. Both un-remapped jars sit in devlibs so
    // only the final remapped artifact lands in build/libs.
    shadowJar {
        configurations = listOf(shadeClasspath.get())

        // Loom adds the client source set to `jar`, not to shadowJar.
        from(sourceSets["client"].output)

        from(rootProject.file("LICENSE.txt")) {
            rename { "${it}_${base.archivesName.get()}" }
        }
    }

    jar {
        from(rootProject.file("LICENSE.txt")) {
            rename { "${it}_${base.archivesName.get()}" }
        }
    }
}

java {
    // Loom attaches sourcesJar to a RemapSourcesJar task and to `build` when present.
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
}
