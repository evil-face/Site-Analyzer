plugins {
    application
    id("checkstyle")
    id("io.ebean") version "13.17.4"
    id("jacoco")
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
    options.encoding = "UTF-8"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("io.ebean:ebean-test:13.15.0")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")

    implementation("io.javalin:javalin:5.5.0")
    implementation("io.javalin:javalin-rendering:5.5.0")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.thymeleaf:thymeleaf:3.1.1.RELEASE")
    implementation("com.github.zhanhb:thymeleaf-layout-dialect:3.2.0")
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
    implementation("com.konghq:unirest-java:3.11.09")
    implementation("org.jsoup:jsoup:1.15.3")


    implementation("com.h2database:h2:2.1.214")
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("io.ebean:ebean-platform-postgres:13.15.0")

}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("generateMigrations") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("hexlet.code.MigrationGenerator")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}