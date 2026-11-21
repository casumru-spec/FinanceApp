import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.detekt)
}

android {
    namespace = "com.example.financeapp"
    compileSdk = 37

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

val detektHtmlReport = layout.buildDirectory.file("reports/detekt/detekt.html")
val detektXmlReport = layout.buildDirectory.file("reports/detekt/detekt.xml")

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
}

dependencies {
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.material)

    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    testImplementation(libs.junit)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
