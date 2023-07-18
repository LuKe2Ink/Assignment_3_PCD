plugins {
    id("java")
}

group = "it.unibo.ppc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.typesafe.akka:akka-actor_3:2.8.1")
    testImplementation("com.typesafe.akka:akka-testkit_3:2.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}