plugins {
    id("java")
    id("org.springframework.boot") version "3.3.11" // 👈 升级到 3.3.x 稳定版，完美支持 Gradle 9
    id("io.spring.dependency-management") version "1.1.7" // 👈 引入 Spring 依赖管理插件
}

group = "edjon.example"
version = "1.0-SNAPSHOT"

// 显式指定项目使用 Java 21 编译
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

// repositories {
//     mavenCentral()
// }

dependencies {
    // 💡 移除 :3.1.2 尾缀，由上面的 dependency-management 插件自动管理最兼容的版本
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // H2 数据库也交给 Spring Boot 托管版本，防冲突
    runtimeOnly("com.h2database:h2")

    // JUnit 5 同样不需要写版本号，Spring Boot 3.3 内部自带了非常新且稳定的 JUnit 5 版本
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // 第三方库需要保留版本号（升级到了适配 Boot 3 / Jakarta 的新版 ModelMapper）
    implementation("org.modelmapper:modelmapper:3.2.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.bootRun {
    args("--spring.profiles.active=local")
}
