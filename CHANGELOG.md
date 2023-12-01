# Changelog

All notable changes to this project will be documented in this file.

This project is versioned following the [major release].[last 2 digits in release year].[minor release] format.

## [1.23.1] - 2023-12-01 - Black Brook

### Added

- Add missing load/save convenience functions for table views
- Improve show() functions to allow for more flexibility + return URL
- Add some missing convenience properties on Column and Row
- Add function to compact a table + docs

### Fixed

- Fix row move/copy bug on empty cells
- Tidy up and fix some CSS issues

### Changed

- Documentation improvements
- Remove println in UI.kt and replace with logging
- Clean up and split into CellUtils.kt from TableUtils.kt

### Removed

- Nothing

## [1.23.0] - 2023-11-22 - First release

First release