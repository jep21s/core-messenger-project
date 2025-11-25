plugins {
    id("build-jvm")
    id("com.google.devtools.ksp") version "2.1.21-2.0.2"
    id("konvert")
    id("java-test-fixtures")
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.coroutines.core)

    implementation(projects.coreServiceCommon)

    testFixturesCompileOnly(kotlin("stdlib"))
    testFixturesCompileOnly(libs.coroutines.core)

    testFixturesCompileOnly(testFixtures("org.jep21s.messenger.core.libs:core-messenger-lib-test-common"))

    testFixturesCompileOnly(projects.coreServiceCommon)

    testFixturesCompileOnly(libs.bundles.junit)
    testFixturesCompileOnly(libs.mockk)
    testFixturesCompileOnly(libs.kotlinx.coroutines.test)
}

tasks.test {
    jvmArgs = listOf(
        "--add-opens=java.base/java.time=ALL-UNNAMED"
    )
    useJUnitPlatform()
}