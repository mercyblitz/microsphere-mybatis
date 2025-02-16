# Microsphere MyBatis

[![Maven Build](https://github.com/microsphere-projects/microsphere-mybatis/actions/workflows/gradle-build.yml/badge.svg)](https://github.com/microsphere-projects/microsphere-mybatis/actions/workflows/gradle-build.yml)
[![Codecov](https://codecov.io/gh/microsphere-projects/microsphere-mybatis/branch/dev/graph/badge.svg)](https://app.codecov.io/gh/microsphere-projects/microsphere-mybatis)
![Maven](https://img.shields.io/maven-central/v/io.github.microsphere-projects/microsphere-mybatis.svg)
![License](https://img.shields.io/github/license/microsphere-projects/microsphere-mybatis.svg)
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/microsphere-projects/microsphere-mybatis.svg)](http://isitmaintained.com/project/microsphere-projects/microsphere-mybatis "Average time to resolve an issue")
[![Percentage of issues still open](http://isitmaintained.com/badge/open/microsphere-projects/microsphere-mybatis.svg)](http://isitmaintained.com/project/microsphere-projects/microsphere-mybatis "Percentage of issues still open")

## 1. Introduction

Microsphere MyBatis is a Java ECO project of [Microsphere](https://github.com/orgs/microsphere-projects) ,
which integrates [MyBatis](https://github.com/mybatis/mybatis-3) and supports the extension features that would be
used for [Microsphere Sentinel](https://github.com/microsphere-projects/microsphere-sentinel),
[Microsphere Resilience4j](https://github.com/microsphere-projects/microsphere-resilience4j),
[Microsphere Observability](https://github.com/microsphere-projects/microsphere-observability) and so on.

## 2. Features

Microsphere MyBatis supports the following features:

- Interception on executing the SQL Statements

## 3. Usage

> ${microsphere-mybatis.version} : The latest version of Microsphere MyBatis

### 3.1. Dependency 

#### 3.1.1 Maven

```xml

<dependency>
    <groupId>io.github.microsphere-projects</groupId>
    <artifactId>microsphere-mybatis</artifactId>
    <version>${microsphere-mybatis.version}</version>
</dependency>
```

#### 3.1.2 Gradle Dependency

```kotlin
implementation("io.github.microsphere-projects:microsphere-mybatis:${microsphere-mybatis.version}")
```

## 4. Contributing

We welcome all kinds of contributions, such as:
- Submitting [issues](https://github.com/microsphere-projects/microsphere-mybatis/issues)
- Submitting [pull requests](https://github.com/microsphere-projects/microsphere-mybatis/pulls)
- Editing [Wiki](https://github.com/microsphere-projects/microsphere-mybatis/wiki) 


## 5. License

Microsphere MyBatis is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
