# Release Notes

## v0.1.3

# Release Notes - Version 0.1.3

## New Features
- **CI/CD Enhancements**:  
  - Added auto-generation of release notes in the publish workflow. ([00e1b37](https://github.com/microsphere-projects/microsphere-core/commit/00e1b37))  
  - Updated workflow permissions to `contents=read`. ([c4d398b](https://github.com/microsphere-projects/microsphere-core/commit/c4d398b))  

## Bug Fixes
- Fixed trailing whitespace in `parent POM`. ([8acea17](https://github.com/microsphere-projects/microsphere-core/commit/8acea17))  
- Removed trailing newline from `pom.xml`. ([7d84eb5](https://github.com/microsphere-projects/microsphere-core/commit/7d84eb5))  
- Removed `logback.xml` from MyBatis test resources. ([ffc866a](https://github.com/microsphere-projects/microsphere-core/commit/ffc866a))  

## Other Changes
- Updated Maven wrapper to 3.3.4 and Maven to 3.9.15. ([7091c38](https://github.com/microsphere-projects/microsphere-core/commit/7091c38), [740e53e](https://github.com/microsphere-projects/microsphere-core/commit/740e53e))  
- Bumped `microsphere-spring-cloud` dependency to 0.1.10. ([dd23a90](https://github.com/microsphere-projects/microsphere-core/commit/dd23a90))  
- Bumped parent POM version to 0.1.10. ([c895f40](https://github.com/microsphere-projects/microsphere-core/commit/c895f40))  
- Updated branch versions in `README`. ([0b96806](https://github.com/microsphere-projects/microsphere-core/commit/0b96806))  

---

For the complete list of changes, please visit the [GitHub repository](https://github.com/microsphere-projects/microsphere-core/compare/0.1.2...0.1.3).

## v0.1.4

# Release Notes for v0.1.4

## Build and Workflow Enhancements
- Improved `maven-publish` workflow with enhanced release notes. ([c77d5f8](https://github.com/mercyblitz/microsphere-mybatis/commit/c77d5f8))

## Documentation
- Updated README to reflect latest branch versions. ([e476ee8](https://github.com/mercyblitz/microsphere-mybatis/commit/e476ee8))

## Dependency Updates
- Bumped `microsphere-spring-cloud` parent version to `0.1.11`. ([fcaad29](https://github.com/mercyblitz/microsphere-mybatis/commit/fcaad29))

## Bug Fixes
- Fixed indentation for Dependabot updates list. ([0825235](https://github.com/mercyblitz/microsphere-mybatis/commit/0825235))

## Other Changes
- Utilized `AnnotationUtils.getAnnotationAttributes` for enhanced annotation handling. ([b369c7a](https://github.com/mercyblitz/microsphere-mybatis/commit/b369c7a))
- Updated version to next patch (`0.1.4`) after publishing `0.1.3`. ([74d0c62](https://github.com/mercyblitz/microsphere-mybatis/commit/74d0c62)) 

_**Note:** Skipped unrelated merge commits from the list._ 

**Full Changelog**: https://github.com/microsphere-projects/microsphere-mybatis/compare/0.1.3...0.1.4## v0.1.5

# Release Notes for Version 0.1.5

## New Features
- **MyBatis Testing**: Introduced JUnit Jupiter test utilities for MyBatis. ([#52](https://github.com/mercyblitz/dev-1.x))

## Build and Workflow Enhancements
- **Maven Workflows**: Updated commands and improved caching in CI workflows.

## Other Changes
- Internal housekeeping: Merged `release-1.x` into `dev-1.x`, and bumped version to 0.1.5 for the next patch. ([skip ci])

---

**Full Changelog**: https://github.com/microsphere-projects/microsphere-mybatis/compare/0.1.4...0.1.5## v0.1.6

# Release Notes for Version 0.1.6

## Dependency Updates
- Bumped `microsphere-spring-cloud` to version **0.1.12**. ([4152bb2](https://example.com))

## Documentation
- Updated `README.md` for improved clarity and accuracy. ([c6f4c09](https://example.com))

## Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x` to synchronize branches. ([f4bbc3f](https://example.com))
- Incremented version to `0.1.6` post-release of `0.1.5`. ([49e8e62](https://example.com))

---

**Note:** No significant new features or bug fixes in this release.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-mybatis/compare/0.1.5...0.1.6## v0.1.7

# Release Notes for v0.1.7

## New Features
- Enhanced executor interception registration for improved flexibility. [#59](https://github.com/mercyblitz/dev-1.x)

## Documentation
- Removed outdated `zread` and `isitmaintained` badges from the `README`. [32efa50](https://github.com/mercyblitz/dev-1.x/commit/32efa50)
- Updated branch version details in the `README`. [6491280](https://github.com/mercyblitz/dev-1.x/commit/6491280)

## Dependency Updates
- Upgraded `microsphere-spring-cloud` to v0.1.14. [ecdc2e3](https://github.com/mercyblitz/dev-1.x/commit/ecdc2e3)

## Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x` branch to sync changes. [f08e4dc](https://github.com/mercyblitz/dev-1.x/commit/f08e4dc)
- Bumped the version to prepare for the next patch release. [e649d65](https://github.com/mercyblitz/dev-1.x/commit/e649d65)

## Other Changes
- Cleaned up whitespace: consolidated blank lines and removed trailing spaces across 50 Java files for better code readability. [df9e8b6](https://github.com/mercyblitz/dev-1.x/commit/df9e8b6) 

---

**Full Changelog**: [v0.1.6...v0.1.7](https://github.com/mercyblitz/dev-1.x/compare/v0.1.6...v0.1.7)

**Full Changelog**: https://github.com/microsphere-projects/microsphere-mybatis/compare/0.1.6...0.1.7## v0.1.8

# Release Notes - Version 0.1.8

## Dependency Updates
- Bumped `microsphere-spring-cloud` to version `0.1.15`. ([6d02ba3](#))

## Other Changes
- Consolidated bean registration logic via `registerBeans`. ([b04285f](#))
- Merged `release-1.x` into `dev-1.x`. ([716d63f](#))
- Bumped version to next patch after publishing 0.1.7. ([f2a95f2](#))

**Full Changelog**: https://github.com/microsphere-projects/microsphere-mybatis/compare/0.1.7...0.1.8## v0.1.9

# Release Notes - Version 0.1.9

## Build and Workflow Enhancements
- Bumped Microsphere parent to `0.1.16`. ([227a1ee](https://example.com))
- Merged `release-1.x` updates into `dev-1.x`. ([4c16603](https://example.com))

## New Features
- Simplified executor bean registration for improved performance. ([5a8e8e1](https://example.com))

## Documentation
- Updated project documentation. ([227a1ee](https://example.com))

## Other Changes
- Prepared for the next patch version after publishing `0.1.8`. ([efd464e](https://example.com))

**Full Changelog**: https://github.com/microsphere-projects/microsphere-mybatis/compare/0.1.8...0.1.9## v0.1.10

# Release Notes for Version 0.1.10

## New Features
- **MyBatis Enhancements**:  
  - Added `ConditionalOnMyBatisAvailable` annotation to improve conditional configuration support.  
  - Introduced MyBatis extension registrar for better modularity and flexibility.

## Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x` to keep branches aligned.  
- Bumped version to `0.1.10` post-release of `0.1.9`.

## Other Changes
- Dependency versions updated to maintain compatibility and improve stability.

**Full Changelog**: https://github.com/microsphere-projects/microsphere-mybatis/compare/0.1.9...0.1.10## v0.1.11

# Release Notes - Version 0.1.11

## New Features
- Moved MyBatis Cloud features to YAML configuration for streamlined setup. (#213353a)

## Refactor
- Refactored MyBatis import registrars API for improved flexibility. (#17ca7c2)

## Documentation
- Updated README with branch version table for better clarity. (#243af25)

## Dependency Updates
- Bumped Microsphere Spring Cloud Parent to version 0.1.21. (#912c7cc)

## Build and Workflow Enhancements
- Merged `release-1.x` into `dev-1.x` branch for alignment. (#a985bbf)  
- Bumped version to next patch after publishing 0.1.10. (#71e2050)

---

**Full Changelog**: https://github.com/microsphere-projects/microsphere-mybatis/compare/0.1.10...0.1.11