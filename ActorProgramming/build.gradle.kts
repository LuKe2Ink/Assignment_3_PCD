plugins {
    id("java")
}


group = "it.unibo.ppc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("com.typesafe.akka:akka-actor-typed_3:2.8.3")
    implementation(project(mapOf("path" to ":")))
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_3:2.8.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.register<JavaExec>("run") {
    mainClass.set("it.unibo.ppc.StartGUI")
    // mainClass.set("it.unibo.ppc.AkkaQuickstart")
    classpath = sourceSets.main.get().runtimeClasspath
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}