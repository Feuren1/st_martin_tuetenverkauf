# Learnings 08.09.2026



# How Java runs

* The JRE (Java Runtime Environment) contains the components needed to run Java applications

  * This includes the JVM and Java runtime libraries
* Java code is compiled using `javac`

  * `javac` is part of the JDK
  * `.class` files are created containing JVM bytecode instructions
* The JVM executes the bytecode

  * The interpreter executes bytecode instructions
  * JIT compilation compiles frequently executed ("hot") code into native machine code that can be reused
* The JVM implementation is different for different platforms / CPU architectures

  * This allows the same Java bytecode to run on different platforms as long as a compatible JVM exists

```text
Java source
    ↓ javac
JVM bytecode (.class)
    ↓
    ├── Interpreter → executes bytecode
    │
    └── JIT → native machine code → CPU
```

# How Java is packaged

* JAR (Java ARchive) files are archives used to package Java applications and libraries
* They can contain `.class` files, resources, metadata, and other files
* A JAR is essentially a ZIP archive with a standardized structure

```text
Java source
    ↓ javac
.class files
    ↓
JAR
```

# How Java is developed

* The JDK (Java Development Kit) contains the tools needed to develop Java applications

  * `javac` → compiles Java source code
  * `java` → launches Java applications
  * `jar` → creates/manages JAR files
  * debugging and other development tools
  * JVM/runtime components

# Maven
* Maven is a build tool capable of downloading external dependencies and managing them (in this project it builds one executable Jar)
  * It downloads jars for each dependency
  * Maven resolves and provides the dependency JARs → Maven invokes javac with those JARs on the classpath → javac compiles the code.
* Maven Wrapper is a set of project files that specifies the Maven version required by the project and allows that version to be downloaded and used automatically if necessary across supported platforms.

## Docker
Key Things:

- Docker file 
  - Instructions for building a Docker image
  - Defines the base image, dependencies, files and commands needed by the application

- Images
  - A file that delcares a ready to use compacted virtual environment with all necessary dependencies

- Containers
  - A running instance of an image

- Compose
  - A file to define different services and how they are connected, can refer to images 
  
# Project Specific things

The dockerfile:             
```
BUILD STAGE
│
eclipse-temurin:21-jdk
│
▼
Maven Wrapper
│
▼
Maven
│
┌───────┴───────┐
│               │
pom.xml          src/
│               │
└───────┬───────┘
▼
javac
│
▼
.class files
│
▼
JAR
│
│ COPY --from=builder
▼
RUNTIME STAGE
│
eclipse-temurin:21-jre
│
▼
app.jar
│
▼
java -jar app.jar
│
▼
JVM
│
Interpreter / JIT
│
▼
CPU
```

## Caddy
