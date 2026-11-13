plugins {
    id("com.android.application")
}

android {
    namespace = "com.hieuhlqb.hieunav"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hieuhlqb.hieunav"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "0.2.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // Android for Cars App Library: Android Auto projected experience
    implementation("androidx.car.app:app:1.7.0")
    implementation("androidx.car.app:app-projected:1.7.0")
}
