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
        // Keep versionCode increasing on every GitHub Actions build.
        versionCode = 1000 + (System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 1)
        versionName = "0.2.${System.getenv("GITHUB_RUN_NUMBER") ?: "1"}"
    }

    signingConfigs {
        val keyPath = System.getenv("HIEUNAV_KEYSTORE_PATH")
        if (!keyPath.isNullOrBlank()) {
            create("hieuNav") {
                storeFile = file(keyPath)
                storePassword = System.getenv("HIEUNAV_STORE_PASSWORD")
                keyAlias = System.getenv("HIEUNAV_KEY_ALIAS")
                keyPassword = System.getenv("HIEUNAV_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            // CI must provide the persistent keystore; local builds may use Android debug key.
            if (!System.getenv("HIEUNAV_KEYSTORE_PATH").isNullOrBlank()) {
                signingConfig = signingConfigs.getByName("hieuNav")
            }
        }
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
