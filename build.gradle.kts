plugins {
    id("java")
}

group = "it.unibo.ppc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.typesafe.akka:akka-actor-typed_3:2.8.3")
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_3:2.8.3")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}