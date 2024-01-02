# Changelog

All notable changes to this project will be documented in this file.

This project is versioned following the [major release].[last 2 digits in release year].[minor release] format.

## v1.24.0 - 2024-01-02 - Still Rice

### Added

- `swap(..)` function to swap rows or columns
- `sort(..)` function to sort a table by rows or columns
- Enhance `TableListenerEvent` and `TableViewListenerEvent` for easy access to event source details
- Allow setting the host/IP with `TableView[Host]` or `SIGBLA_HOST`

### Fixed

- Fix issue with on ref/name mismatch in UI for table views
- Ensure only `Row[at X]` can be used in row subscriptions
- Ensure valid `CellRange`, `ColumnRange`, and `RowRange` within constraints
- Support for view/table replacement when reusing existing view reference

### Changed

- Changed Dexx collections dependency with Sigbla PDS (no functional change)
- Tidy up implementation of `RowRange` and `ColumnRange`
- Documentation improvements and additions
- Various improvements to `toString` functions
- Various minor code cleaning

### Removed

- Nothing

## v1.23.1 - 2023-12-01 - Black Brook

### Added

- Add missing load/save convenience functions for table views
- Improve `show(..)` functions to allow for more flexibility + return URL
- Add some missing convenience properties on Column and Row
- Add function to compact a table + docs

### Fixed

- Fix row move/copy bug on empty cells
- Tidy up and fix some CSS issues

### Changed

- Documentation improvements
- Remove println in `UI.kt` and replace with logging
- Clean up and split into `CellUtils.kt` from `TableUtils.kt`

### Removed

- Nothing

## v1.23.0 - 2023-11-22 - First release

First release