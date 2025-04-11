// Arquivo build.gradle para a pasta raiz do projeto

buildscript {
    repositories {
        mavenCentral()
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
        google()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.0.4'
    }
}

allprojects {
    apply plugin: "eclipse"
    apply plugin: "idea"

    version = '1.0'
    ext {
        appName = "evolution-simulator"
        gdxVersion = '1.11.0'
        roboVMVersion = '2.3.16'
        box2DLightsVersion = '1.5'
        ashleyVersion = '1.7.4'
        aiVersion = '1.8.2'
        gdxControllersVersion = '2.2.1'
    }

    repositories {
        mavenCentral()
        google()
        maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
        maven { url "https://oss.sonatype.org/content/repositories/releases/" }
    }
}

// Arquivo build.gradle para o módulo android

project(":android") {
    apply plugin: "com.android.application"

    configurations { natives }

    dependencies {
        implementation project(":core")
        api "com.badlogicgames.gdx:gdx-backend-android:$gdxVersion"
        natives "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-armeabi-v7a"
        natives "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-arm64-v8a"
        natives "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86"
        natives "com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-x86_64"
        api "com.badlogicgames.gdx:gdx-box2d:$gdxVersion"
        natives "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-armeabi-v7a"
        natives "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-arm64-v8a"
        natives "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86"
        natives "com.badlogicgames.gdx:gdx-box2d-platform:$gdxVersion:natives-x86_64"
    }
}

// Arquivo build.gradle para o módulo core

project(":core") {
    apply plugin: "java-library"

    dependencies {
        api "com.badlogicgames.gdx:gdx:$gdxVersion"
        api "com.badlogicgames.gdx:gdx-box2d:$gdxVersion"
    }
}
