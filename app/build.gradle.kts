import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
    alias(libs.plugins.detekt)




    alias(libs.plugins.ksp)
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(rootProject.files("config/detekt/detekt.yml"))
    parallel = true
    ignoreFailures = false
    failOnSeverity = dev.detekt.gradle.extensions.FailOnSeverity.Warning
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    System.getenv("JAVA_HOME")?.let { jdkHome.set(file(it)) }

    reports {
        html.required.set(true)
        checkstyle.required.set(true)
    }
}

tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
    reports {
        html.required.set(true)
        checkstyle.required.set(true)
    }
}

val detektHtmlReport = layout.buildDirectory.file("reports/detekt/detekt.html")
val detektXmlReport = layout.buildDirectory.file("reports/detekt/detekt.xml")
val ciVersionCode = providers.environmentVariable("VERSION_CODE").orNull?.toIntOrNull()
val ciVersionName = providers.environmentVariable("VERSION_NAME").orNull

val openDetektReport by tasks.registering(Exec::class) {
    group = "verification"
    description = "Opens the Detekt HTML report when issues are found."

    val htmlFile = detektHtmlReport.get().asFile
    val xmlFile = detektXmlReport.get().asFile
    inputs.files(htmlFile, xmlFile)
    isIgnoreExitValue = true

    onlyIf("Detekt reported issues") {
        htmlFile.isFile &&
            xmlFile.isFile &&
            xmlFile.readText().contains("<error ")
    }

    when {
        System.getProperty("os.name").startsWith("Windows", ignoreCase = true) ->
            commandLine("cmd", "/c", "start", "", htmlFile.absolutePath)
        System.getProperty("os.name").startsWith("Mac", ignoreCase = true) ->
            commandLine("open", htmlFile.absolutePath)
        else -> commandLine("xdg-open", htmlFile.absolutePath)
    }
}

tasks.named("detekt") {
    finalizedBy(openDetektReport)

    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.android)

}

android {
    namespace = "com.example.financeapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.financeapp"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {

    val composeBom = platform(libs.androidx.compose.bom)
    implementation(libs.androidx.navigation.compose)
    implementation(composeBom)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.6")

    // Исправленные названия библиотек:
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.core.ktx)

    // Библиотека для ViewModel (нужна для практики 07)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    debugImplementation(libs.androidx.ui.tooling)

    testImplementation(libs.junit)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "17"
}
