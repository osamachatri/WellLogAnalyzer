plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("org.jetbrains.compose") version "1.7.3"
    kotlin("plugin.compose") version "2.1.0"
    id("org.openjfx.javafxplugin") version "0.1.0"
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.swing")
}

repositories {
    mavenCentral()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.jzy3d.org/releases")
    maven("https://jitpack.io")
}

dependencies {
    // Compose Desktop
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    // Koin
    implementation("io.insert-koin:koin-core:3.5.6")
    implementation("io.insert-koin:koin-compose:1.1.5")
    implementation("io.insert-koin:koin-compose-viewmodel:1.2.0-Beta4")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.8.1")

    // Serialization + ProtoBuf
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.7.3")

    // 3D
    implementation("org.jzy3d:jzy3d-core:2.2.1")
    implementation("org.fxyz3d:fxyz3d:0.6.0")

    // 2D Charts
    implementation("org.jetbrains.lets-plot:lets-plot-common:4.5.2")
    implementation("org.jetbrains.lets-plot:platf-awt-jvm:4.5.2")
    implementation("org.jetbrains.lets-plot:lets-plot-batik:4.5.2")

    // Reports — exclude Log4j to avoid conflict with Logback
    implementation("org.apache.poi:poi-ooxml:5.2.3") {
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
        exclude(group = "org.apache.logging.log4j", module = "log4j-core")
    }

    implementation("org.docx4j:docx4j-JAXB-ReferenceImpl:11.5.3") {
        exclude(group = "org.apache.logging.log4j", module = "log4j-api")
        exclude(group = "org.apache.logging.log4j", module = "log4j-core")
        exclude(group = "org.slf4j", module = "slf4j-log4j12")
    }

    // JAXB API + RI that docx4j-JAXB-ReferenceImpl expects
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    implementation("com.sun.xml.bind:jaxb-impl:4.0.5")

    // Logging — Logback as the single backend + bridge for POI's Log4j calls
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.apache.logging.log4j:log4j-to-slf4j:2.20.0")
}

compose.desktop {
    application {
        mainClass = "com.oussama_chatri.MainKt"
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "WellLogAnalyzer"
            packageVersion = "1.0.0"
        }
    }
}