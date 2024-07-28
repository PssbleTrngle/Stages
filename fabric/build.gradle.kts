val mc_version: String by extra
val jei_version: String by extra
val porting_lib_version: String by extra

fabric {
    enableMixins()

    dataGen()

//    includesMod("io.github.fabricators_of_create.Porting-Lib:Porting-Lib:${porting_lib_version}+${mc_version}")
    dependOn(project(":common"))
}

dependencies {
    modCompileOnly("mezz.jei:jei-${mc_version}-common-api:${jei_version}")
    modCompileOnly("mezz.jei:jei-${mc_version}-fabric-api:${jei_version}")

    if (!env.isCI) {
        modRuntimeOnly("mezz.jei:jei-${mc_version}-fabric:${jei_version}")
    }
}

uploadToCurseforge()
uploadToModrinth()
