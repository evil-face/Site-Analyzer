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
    implementation("io.javalin:javalin-rendering:5.5.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.thymeleaf:thymeleaf:3.1.1.RELEASE")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.0.0")
    implementation("org.thymeleaf.extras:thymeleaf-extras-java8time:3.0.4.RELEASE")
    implementation("org.webjars:bootstrap:5.2.0")

    implementation("io.ebean:ebean:13.15.0")
    implementation("io.ebean:ebean-ddl-generator:13.15.0")
    implementation("io.ebean:ebean-querybean:12.15.1")
    implementation("io.ebean:ebean-migration:13.7.0")
    implementation("io.ebean:ebean-annotation:8.3")
    annotationProcessor("io.ebean:querybean-generator:13.15.0")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.5")
    implementation("javax.activation:activation:1.1.1")

    implementation("com.h2database:h2:2.1.214")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("io.ebean:ebean-platform-postgres:13.15.0")

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