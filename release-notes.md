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

**Full Changelog**: https://github.com/microsphere-projects/microsphere-mybatis/compare/0.1.3...0.1.4