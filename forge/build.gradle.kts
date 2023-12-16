val kotlin_forge_version: String by extra
val jei_version: String by extra
val curios_forge_version: String by extra
val botania_version: String by extra
val twilight_version: String by extra
val quark_version: String by extra
val arl_version: String by extra
val mc_version: String by extra
val terrablender_version: String by extra
val bop_version: String by extra
val patchouli_version: String by extra
val top_version: String by extra

forge {
    enableMixins()

    dependOn(project(":common"))
}

dependencies {
    modCompileOnly("mezz.jei:jei-${mc_version}-common-api:${jei_version}")
    modCompileOnly("mezz.jei:jei-${mc_version}-forge-api:${jei_version}")

    modImplementation("mcjty.theoneprobe:theoneprobe:${top_version}") {
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
