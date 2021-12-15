plugins {
    application
    id("io.freefair.lombok") version "6.2.0"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "me.uwudev"
version = "1.0.0"

application {
    this.mainClass.set("me.uwu.motentrop.Main")
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    fun deps(g: String, n: String, v: String) = implementation(g, n, v)
    fun deps(g: String, ns: Array<String>, v: String) = ns.forEach { implementation(g, it, v) }

    deps("org.jetbrains", "annotations", "23.0.0")

    deps("net.sf.jopt-simple", "jopt-simple", "5.0.4")
    deps("com.google.code.gson", "gson", "2.8.9")

    // Update log4j2, fix CVE-2021-44228
    deps("org.apache.logging.log4j", arrayOf("log4j-api", "log4j-core", "log4j-slf4j-impl"), "2.15.0")

    deps("org.slick2d", "slick2d-core", "1.0.2")

}

tasks.withType<Jar>().configureEach  {
    manifest.attributes.let {
        it["Implementation-Title"] = "Mot-en-Trop"
        it["Implementation-Version"] = project.version
        it["Implementation-Author"] = "UwU"
    }
}

tasks.named("build").get().dependsOn(tasks.named("shadowJar"))