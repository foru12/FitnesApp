plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation(libs.coroutines)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testImplementation(kotlin("test"))
}

