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
    implementation("com.typesafe.akka:akka-actor_3:2.8.1")
    implementation("com.typesafe.akka:akka-actor-typed_3:2.8.1")
    testImplementation("com.typesafe.akka:akka-actor-testkit-typed_3:2.8.1")
    testImplementation("com.typesafe.akka:akka-testkit_3:2.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.register<JavaExec>("run") {
    mainClass.set("it.unibo.ppc")
    classpath = sourceSets.main.get().runtimeClasspath
//    args.add("7450", "6", "3")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}