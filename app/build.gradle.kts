plugins {
    application
    id("checkstyle")
    id("io.ebean") version "13.17.4"
}

application {
    mainClass.set("hexlet.code.App")
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.compileJava {
    options.release.set(17)
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("io.javalin:javalin:5.5.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")

    implementation("io.ebean:ebean:13.6.5")
    implementation("io.ebean:ebean-querybean:13.6.5")
    annotationProcessor("io.ebean:querybean-generator:13.6.5")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.5")
    implementation("javax.activation:activation:1.1.1")
    implementation("io.ebean:ebean-ddl-generator:13.6.2")
    implementation("io.ebean:ebean-migration:13.6.2")

    implementation("com.h2database:h2:2.1.214")
    implementation("org.postgresql:postgresql:42.6.0")
    testImplementation("io.ebean:ebean-platform-all:13.17.4")
}

tasks.test {
    useJUnitPlatform()
}

task("stage") {
    dependsOn("clean", "installDist", "jar")
}

tasks.register<JavaExec>("generateMigrations") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("hexlet.code.MigrationGenerator")
}

tasks.jar {
    manifest.attributes["Main-Class"] = "hexlet.code.App"
    val dependencies = configurations
            .runtimeClasspath
            .get()
            .map(::zipTree)
    from(dependencies)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}