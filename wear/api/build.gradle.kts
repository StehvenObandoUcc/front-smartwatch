import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.openapi.generator)
}

kotlin {
    jvmToolchain(17)
}

// Cliente generado desde el contrato fijado en ../contract.lock (scripts/sync-contract.sh).
// Se versiona la salida; regenerar: ./gradlew :api:generateApi
tasks.register<GenerateTask>("generateApi") {
    generatorName.set("kotlin")
    inputSpec.set(rootProject.file("../.contract/openapi.yaml").toURI().toString())
    outputDir.set(layout.projectDirectory.dir("generated").asFile.absolutePath)
    packageName.set("com.smartwatch.recordatorios.api")
    apiPackage.set("com.smartwatch.recordatorios.api.apis")
    modelPackage.set("com.smartwatch.recordatorios.api.models")
    configOptions.set(
        mapOf(
            "library" to "jvm-retrofit2",
            "serializationLibrary" to "kotlinx_serialization",
            "useCoroutines" to "true",
            "dateLibrary" to "java8",
            "omitGradleWrapper" to "true",
            "useSettingsGradle" to "false",
        ),
    )
    // DeviceTokenRequest es un oneOf que el generador no sabe emitir con kotlinx: se envía como JsonObject.
    importMappings.set(mapOf("DeviceTokenRequest" to "kotlinx.serialization.json.JsonObject"))
    globalProperties.set(mapOf("apis" to "", "models" to "", "supportingFiles" to ""))
    val modelsDir = layout.projectDirectory.dir("generated/src/main/kotlin/com/smartwatch/recordatorios/api/models").asFile
    doLast {
        listOf("DeviceTokenRequest", "DeviceCodeGrant", "DeviceRefreshGrant", "kotlinx.serialization.json.JsonObject")
            .forEach { modelsDir.resolve("$it.kt").delete() }
    }
}

sourceSets.main {
    kotlin.srcDir("generated/src/main/kotlin")
}

dependencies {
    api(libs.retrofit)
    api(libs.okhttp)
    api(libs.kotlinx.serialization.json)
    api(libs.retrofit.kotlinx.serialization)
    api(libs.retrofit.scalars)
    api(libs.okhttp.logging)
}
