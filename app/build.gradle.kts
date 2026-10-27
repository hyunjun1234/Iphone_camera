import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val signingPropertiesFile = providers.environmentVariable("MOMENT_SIGNING_PROPERTIES")
    .map { file(it) }
    .getOrElse(File(System.getProperty("user.home"), ".config/moment-camera/release.properties"))
val uploadSigning = Properties().apply {
    if (signingPropertiesFile.isFile) signingPropertiesFile.reader(Charsets.UTF_8).use { load(it) }
}
val signingFields = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
val hasUploadSigning = signingFields.all { !uploadSigning.getProperty(it).isNullOrBlank() }
require(!signingPropertiesFile.exists() || hasUploadSigning) {
    "Upload signing configuration is incomplete. Required fields: ${signingFields.joinToString()}."
}
require(providers.environmentVariable("MOMENT_REQUIRE_SIGNING").orNull != "true" || hasUploadSigning) {
    "Upload signing is required. Run python3 scripts/prepare_play_release.py --create-upload-key for a new app, or set MOMENT_SIGNING_PROPERTIES to an existing upload key configuration."
}

android {
    namespace = "studio.moment.camera"
    compileSdk = 36
    defaultConfig {
        applicationId = "studio.moment.camera"
        minSdk = 26
        targetSdk = 36
        versionCode = 9
        versionName = "0.2.7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures { compose = true; buildConfig = true }
    signingConfigs {
        if (hasUploadSigning) create("upload") {
            storeFile = signingPropertiesFile.parentFile.resolve(uploadSigning.getProperty("storeFile"))
            require(storeFile!!.isFile) { "Upload keystore does not exist. Check storeFile in the signing configuration." }
            storePassword = uploadSigning.getProperty("storePassword")
            keyAlias = uploadSigning.getProperty("keyAlias")
            keyPassword = uploadSigning.getProperty("keyPassword")
        }
    }
    buildTypes {
        release {
            signingConfig = signingConfigs.findByName("upload")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    testOptions { unitTests.isReturnDefaultValues = true }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2025.01.01"))
    implementation("androidx.activity:activity-compose:1.10.1")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.exifinterface:exifinterface:1.4.2")
    val cameraVersion = "1.5.3"
    implementation("androidx.camera:camera-core:$cameraVersion")
    implementation("androidx.camera:camera-camera2:$cameraVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraVersion")
    implementation("androidx.camera:camera-view:$cameraVersion")
    implementation("androidx.camera:camera-extensions:$cameraVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test:runner:1.6.2")
    androidTestImplementation(platform("androidx.compose:compose-bom:2025.01.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
