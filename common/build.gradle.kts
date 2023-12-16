val jei_version: String by extra
val mc_version: String by extra

common()

dependencies {
    compileOnly("mezz.jei:jei-${mc_version}-common-api:${jei_version}")
}