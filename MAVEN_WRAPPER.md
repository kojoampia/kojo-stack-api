# Maven Wrapper

Maven Wrapper (`mvnw`) allows developers to run Maven builds without having Maven installed globally. It automatically downloads and caches the specified Maven version.

## What is Maven Wrapper?

The Maven Wrapper is a tool that automatically downloads and manages Maven distributions. It ensures all developers use the same Maven version, eliminating "works on my machine" issues.

## Features

- **No Global Maven Installation Required** - Maven is automatically downloaded
- **Version Management** - Consistent Maven version across teams
- **Offline Support** - Cached Maven distribution works offline
- **Cross-Platform** - Bash script for Linux/macOS, batch script for Windows

## Files

```
mvnw                              # Unix/Linux/macOS wrapper script
mvnw.cmd                          # Windows wrapper batch file
.mvn/wrapper/
  └── maven-wrapper.properties    # Configuration (Maven version, etc.)
```

## Usage

### Linux / macOS / Git Bash

```bash
# Clean and package
./mvnw clean package

# Run tests
./mvnw test

# Run application
./mvnw spring-boot:run

# Run with specific goals
./mvnw clean install -DskipTests

# Enable debug output
./mvnw clean compile -X
```

### Windows

```cmd
# Clean and package
mvnw clean package

# Run tests
mvnw test

# Run application
mvnw spring-boot:run

# Run with specific goals
mvnw clean install -DskipTests
```

## Configuration

Edit `.mvn/wrapper/maven-wrapper.properties` to customize:

```properties
# Maven version to use
maven.version=3.9.6

# Maven Wrapper JAR URL
maven.wrapper.url=https://repo.maven.apache.org/maven2/...

# Maven Repository URL
maven.repo.local=

# Download timeout (milliseconds)
maven.wrapper.timeout=300000
```

## Common Maven Goals

```bash
./mvnw clean              # Delete build directory
./mvnw compile            # Compile source code
./mvnw test               # Run unit tests
./mvnw package            # Create JAR/WAR
./mvnw install            # Install to local repository
./mvnw clean package      # Clean and package
./mvnw spring-boot:run    # Run Spring Boot app
./mvnw site               # Generate project site
./mvnw dependency:tree    # Show dependency tree
```

## Troubleshooting

### Permission Denied (Linux/macOS)

```bash
chmod +x mvnw
```

### Maven Wrapper JAR Download Failed

The wrapper will automatically download `maven-wrapper.jar` on first run. If download fails:

1. Check internet connection
2. Verify Maven Repository URL in `.mvn/wrapper/maven-wrapper.properties`
3. Check firewall/proxy settings
4. Manually download from: https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/

### Out of Memory Error

```bash
# Increase heap size
export MAVEN_OPTS="-Xmx1024m -Xms512m"
./mvnw clean package
```

### Clear Cache

```bash
# Remove cached Maven distribution (will be re-downloaded next run)
rm -rf ~/.m2/wrapper

# Clear local Maven repository (if needed)
rm -rf ~/.m2/repository
```

## First Run

The first run will:
1. Detect missing Maven Wrapper JAR
2. Download `maven-wrapper.jar` from Maven Central
3. Download specified Maven version
4. Cache both locally
5. Execute your Maven command

This may take a few minutes on first run depending on your internet speed.

## Environment Variables

```bash
# Use custom Java home
export JAVA_HOME=/path/to/java/home
./mvnw clean package

# Set Maven options
export MAVEN_OPTS="-Xmx1024m"
./mvnw clean package

# Skip tests
./mvnw clean package -DskipTests

# Offline mode (uses cached dependencies)
./mvnw clean package -o
```

## Benefits

✅ **Consistency** - Same Maven version for all developers  
✅ **Simplicity** - No installation steps needed  
✅ **Portability** - Project includes all build tools  
✅ **Reliability** - Reproducible builds  
✅ **Transparency** - Version control tracks Maven version  

## More Information

- [Maven Wrapper Documentation](https://maven.apache.org/wrapper/)
- [Maven Official Site](https://maven.apache.org/)
- [Apache Maven GitHub](https://github.com/apache/maven-wrapper)

## Version

- Maven Wrapper Version: 3.2.0
- Maven Version: 3.9.6 (LTS)
