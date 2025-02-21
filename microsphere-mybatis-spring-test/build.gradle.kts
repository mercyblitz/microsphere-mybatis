
plugins {
    id("buildlogic.java-library-conventions")
}

dependencies {

    // BOM

    // Microsphere Spring Dependencies(BOM)
    implementation(platform(libs.microsphere.spring.dependencies))
    // Spring Framework BOM
    implementation(platform(libs.spring.framework.bom))

    // Libraries

    // Microsphere MyBatis Test
    api(project(":microsphere-mybatis-test"))

    // Microsphere Spring
    "optionalApi"("io.github.microsphere-projects:microsphere-spring-context")

    // Spring Framework
    "optionalApi"("org.springframework:spring-beans")
    "optionalApi"("org.springframework:spring-context")

    // MyBatis
    "optionalApi"(libs.mybatis)

    // MyBatis Spring
    "optionalApi"(libs.mybatis.spring)

}