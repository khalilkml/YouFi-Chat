import java.io.InputStream
import java.util.Properties

plugins {
        id("com.android.application")
        id("com.google.gms.google-services")
}


android {
    namespace = "com.example.chatapp"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.example.chatapp"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val properties = Properties()
        val inputStream: InputStream = project.rootProject.file("local.properties").inputStream()
        properties.load(inputStream)

        val apiKey = properties.getProperty("apiKey")
        buildConfigField("String", "apiKey", "\"$apiKey\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Scalable Size Unit (support for different screen sizes)
    implementation("com.intuit.sdp:sdp-android:1.0.6")
    implementation("com.intuit.ssp:ssp-android:1.0.6")

    // Rounded ImageVie
    implementation("com.makeramen:roundedimageview:2.3.0")

    //firebase
    implementation(platform("com.google.firebase:firebase-bom:32.6.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-messaging:23.4.0")
    implementation("com.google.firebase:firebase-firestore:24.10.0")

    //MultiDex
    implementation("androidx.multidex:multidex:2.0.1")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
}