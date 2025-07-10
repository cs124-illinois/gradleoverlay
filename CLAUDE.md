# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Gradle plugin called `gradleoverlay` used for educational purposes at University of Illinois CS courses. It overlays student code submissions onto a base project structure for grading by selectively copying, merging, or deleting files based on YAML configuration.

## Key Commands

### Development
```bash
# Build the plugin
./gradlew build

# Format code (required before committing)
./gradlew kotlinterFormat

# Check code formatting
./gradlew kotlinterCheck

# Check for dependency updates
./gradlew dependencyUpdates

# Test locally by publishing to local Maven repository
./gradlew publishToMavenLocal

# Publish to Maven Central (requires credentials)
./gradlew publishToSonatype closeAndReleaseSonatypeStagingRepository
```

### Testing the Plugin
To test the plugin in a project:
1. Run `./gradlew publishToMavenLocal` to install locally
2. In the test project, add to `build.gradle.kts`:
```kotlin
plugins {
    id("org.cs124.gradleoverlay") version "2025.2.0"
}
```
3. Create `config/overlay.yaml` with overlay configuration
4. Run: `./gradlew overlay -Poverlayfrom=/path/to/source`

## Architecture

### Plugin Structure
- **Main Class**: `src/main/kotlin/edu/illinois/cs/cs125/gradleoverlay/Plugin.kt` - Contains all plugin logic in a single file
- **Plugin ID**: `org.cs124.gradleoverlay` (modern) or `edu.illinois.cs.cs125.overlay` (legacy)
- **Task Created**: `overlay` - Performs file overlay operations

### Configuration Model
The plugin uses Jackson to deserialize YAML configuration into data classes:
- `OverlayConfiguration`: Main config with overwrite/merge/delete file patterns
- `CheckpointConfiguration`: Map of checkpoint-specific overlays
- `GradeConfiguration`: Student's checkpoint specification

### Operation Flow
1. Plugin reads `config/overlay.yaml` from the target project
2. Determines checkpoint from `-Pcheckpoint` property or student's `grade.yaml`
3. Deletes files matching patterns in `delete` and `overwrite` lists
4. Copies files from source directory based on `overwrite` and `merge` patterns
5. Applies checkpoint-specific rules if checkpoint is specified

### Key Dependencies
- Jackson (YAML/JSON processing): Used for parsing configuration files
- Gradle API: For plugin development and file operations
- Apache Ant DirectoryScanner: For glob pattern matching

## Important Notes

- **Kotlin Version**: Uses Kotlin 2.1.20, targets JVM 17
- **Publishing**: Configured for Maven Central under group `org.cs124`
- **No Tests**: Currently lacks unit or integration tests
- **Single File Plugin**: All logic is in one file for simplicity
- **Educational Context**: Designed specifically for CS course grading workflows