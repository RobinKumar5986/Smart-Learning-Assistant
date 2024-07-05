plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.nationalhackaton.smartlearningassist"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nationalhackaton.smartlearningassist"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "API_KEY","")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //dependencies
    implementation("com.google.android.gms:play-services-auth:20.4.1")
    //-----dependency for fragments------//
    //1.circular image in chats
    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.google.firebase:firebase-storage:20.1.0")
    //----------------------------------//
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //gemini dependencies
    implementation(libs.generativeai)
    implementation(libs.guava)
    implementation(libs.reactive.streams)

    //card view
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("com.google.android.material:material:1.5.0")

    //circular progress
    implementation ("com.mikhaellopez:circularprogressbar:3.1.0")

}
