import org.spongepowered.asm.gradle.plugins.MixinExtension

val mod_id: String by extra
val jei_version: String by extra
val mc_version: String by extra
val top_version: String by extra

forge {
    enableMixins()

    dependOn(project(":common"))
}

configure<MixinExtension> {
    config("${mod_id}-forge.mixins.json")
}

dependencies {
    modCompileOnly("mezz.jei:jei-${mc_version}-common-api:${jei_version}")
    modCompileOnly("mezz.jei:jei-${mc_version}-forge-api:${jei_version}")

    modImplementation("maven.modrinth:the-one-probe:${top_version}") {
        isTransitive = false
    }

    if (!env.isCI) {
        modRuntimeOnly("mezz.jei:jei-${mc_version}-forge:${jei_version}")
    }
}

uploadToCurseforge()
uploadToModrinth {
    syncBodyFromReadme()
}
