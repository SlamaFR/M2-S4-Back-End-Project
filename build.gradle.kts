import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.8.10"
	id("org.springframework.boot") version "3.0.4"
	id("io.spring.dependency-management") version "1.1.0"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.allopen") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
}

group = "com.kamelia"
version = "0.1.0"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")

	implementation("org.hibernate.validator:hibernate-validator")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("at.favre.lib:bcrypt:0.10.2")

	runtimeOnly("com.h2database:h2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.Embeddable")
	annotation("jakarta.persistence.MappedSuperclass")
}

fun KotlinCompile.kotlinConfig() = kotlinOptions {
	freeCompilerArgs = listOf("-Xjsr305=strict")
	jvmTarget = "17"
}

tasks {
	compileKotlin {
		kotlinConfig()
	}

	compileTestKotlin {
		kotlinConfig()
	}

	compileJava {
		sourceCompatibility = JavaVersion.VERSION_17.toString()
		targetCompatibility = JavaVersion.VERSION_17.toString()
	}

	test {
		useJUnitPlatform()
	}
}
