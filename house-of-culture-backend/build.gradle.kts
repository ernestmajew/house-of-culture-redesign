import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.1.0"
	id("io.spring.dependency-management") version "1.1.2"
	kotlin("jvm") version "1.8.22"
	kotlin("plugin.spring") version "1.8.22"
	kotlin("plugin.jpa") version "1.3.72"
	kotlin("kapt") version "1.9.10"
	id("org.openapi.generator") version "6.4.0"
}

group = "eagle.dev"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	//spring boot
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-configuration-processor:3.1.0")
	implementation("org.springframework.boot:spring-boot-starter-mail")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.projectlombok:lombok:1.18.26")
	implementation("org.apache.httpcomponents.client5:httpclient5:5.2.1")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
	implementation("org.mnode.ical4j:ical4j:4.0.0-beta2") {
		exclude(group = "org.codehaus.groovy", module = "groovy")
	}

	//images
	implementation("commons-io:commons-io:2.11.0")

	//docker
	implementation("org.springframework.boot:spring-boot-docker-compose")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	//swagger
	implementation("io.swagger.core.v3:swagger-annotations:2.2.8")
	implementation("io.swagger.core.v3:swagger-models:2.2.8")

	//openapi
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")

	//liquibase + databse
	implementation("org.liquibase:liquibase-core")
	implementation("com.mysql:mysql-connector-j:8.0.33")

	//security
	implementation("org.springframework.boot:spring-boot-starter-security")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")

	// MapStruct
	implementation("org.mapstruct:mapstruct:1.4.2.Final")
	kapt("org.mapstruct:mapstruct-processor:1.4.2.Final")

	// Facebook/Instagram integration
	implementation("com.restfb:restfb:2023.12.0")

	//test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("mysql:mysql-connector-java:8.0.33")
	testImplementation("io.rest-assured:rest-assured:5.3.2")
	testImplementation("io.mockk:mockk:1.13.8")
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		freeCompilerArgs += "-Xjvm-default=all"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val generatedSourcesPath = "$buildDir/generated"

openApiGenerate {
	generatorName.set("kotlin-spring")
	inputSpec.set("$rootDir/../api-definition/client.yaml")
	outputDir.set(generatedSourcesPath)
	packageName.set("eagle.dev.houseofculture.openapi")
	apiPackage.set("eagle.dev.houseofculture.openapi.api")
	modelPackage.set("eagle.dev.houseofculture.openapi.model")
	modelNameSuffix.set("Ts")


	configFile.set("$rootDir/../api-definition/api-config.json")
}

kotlin.sourceSets["main"].kotlin.srcDir("$generatedSourcesPath/src/main/kotlin")

tasks.withType<KotlinCompile>().configureEach {
	dependsOn("openApiGenerate")
}
