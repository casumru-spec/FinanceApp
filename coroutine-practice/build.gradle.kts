plugins {
    id("org.jetbrains.kotlin.jvm")
    application
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

application {
    mainClass.set("com.example.financeapp.practice.CoroutinePracticeKt")
}