import org.spongepowered.asm.gradle.plugins.MixinExtension

val mod_id: String by extra
val jei_version: String by extra
val mc_version: String by extra
val top_version: String by extra

val create_forge_version: String by extra
val patchouli_forge_version: String by extra
val curios_forge_version: String by extra
val botania_forge_version: String by extra

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

    modCompileOnly("maven.modrinth:the-one-probe:${top_version}") {
        isTransitive = false
    }

    if (!env.isCI) {
        modRuntimeOnly("mezz.jei:jei-${mc_version}-forge:${jei_version}")
        // modRuntimeOnly("maven.modrinth:create:${create_forge_version}")
        // modRuntimeOnly("maven.modrinth:patchouli:${patchouli_forge_version}")
        // modRuntimeOnly("maven.modrinth:curios:${curios_forge_version}")
        // modRuntimeOnly("maven.modrinth:botania:${botania_forge_version}")
    }
}

uploadToCurseforge()
uploadToModrinth {
    syncBodyFromReadme()
}
