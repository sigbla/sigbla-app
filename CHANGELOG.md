# Changelog

All notable changes to this project will be documented in this file.

This project is versioned following the [major release].[last 2 digits in release year].[minor release] format.

## v1.24.5 - 2024-07-21 - Lucky Hat

Primary focus for this release is final core API stabilization in preparation for the first beta release. As such,
v1.24.5 marks the first beta release of Sigbla. A few more beta releases are expected before the first production
release, but as of this release, the core APIs are considered complete, with focus now shifting towards auxiliary
tools and utilities.

A big change in this release is an improvement to the APIs relating to resources, making them easier and more intuitive
to use. It also introduces global resources which enables applications to provide their own resources beyond what's
connected to a particular table.

Another significant addition is the support for temporal cell values, specifically LocalDate, LocalTime, LocalDateTime,
and ZonedDateTime. This has been on the TODO list for a while, and completes the data types supported by tables.

Work has also been done on optimizing the JSON parsing used as part of the UI, significantly improving the performance
of JSON sent to the frontend. Other minor changes, test improvements and documentation updates also included.

### Added

- Support for temporal cell types
- Global resources, included in overall Resource refactoring
- Position and Visibility now available on DerivedColumnView and DerivedRowView
- Augmented assignment added to Cell, allowing for things like `cell += value` rather than `table[cell] = cell + value`

### Fixed

- Fixed behaviour of `tableView[Table].source`, ensuring this returns the correct source from assignment
- Fixed `TableView.invoke(TableView)`, ensuring this now does all updates atomically

### Changed

- API related to Resource, allowing for `Resource[path] = handler` and `tableView[Resource[path]] = handler`, rather than the map based approach

### Removed

- Nothing

## v1.24.4 - 2024-06-24 - Flat Sun

Focus in this release has been on improving and refactoring frontend code, adding UI features such as column and row
hiding and locking through `Visibility` and `Position` meta classes. Other UI related features, such as supporting the
height and width of individual cells have also been implemented, allowing for column and row span functionality.

Additional frontend changes include adding a marker, which allows cells to be selected and improves the way input is
passed on to underlying UI cell content. This allows for better widget and chart functionality among other improvements
for code that wishes to extend the frontend functionality.

Finally, a more flexible approach is now supported around providing custom HTML/CSS/JS allowing for alternative styling
and other such changes to the frontend rendering. Two view configs are provided out of the box to illustrate this, the
compact and the spacious, with compact being the default choice when using `show(..)`.

It is expected that this will be the final alpha release of v1.

### Added

- Add a cell marker, allowing cells to be selected with input passed to underlying cell content
- Add functionality to position columns to the left or right and rows to the top or bottom
- Add functionality to hide columns and rows
- Add support for custom UI config with custom HTML/CSS/JS
- Implement cell height and width rendering when these are defined on cell

### Fixed

- Nothing

### Changed

- Rename init parameter to receiver for on(..) functions and process to processor on related for improved API intuition
- Change HTML structure of cells, also harmonize this with column and row headers
- Update widgets and charts to work with frontend changes
- Various documentation updates relating to changes

### Removed

- Nothing

## v1.24.3 - 2024-04-06 - Aged Moon

The theme of this release is about stabilizing core APIs, cleaning them up, improving type safety and other related
refactorings where needed. These changes make it clearer what types are accepted and removes some approaches that
previously could be confusing to the API user, such as with contains(..) and invoke(..).

As part of this change, Unit, rather than null, is encouraged when clearing values (be that in a table/view or in meta
classes). Nulls are still supported most places to clear values, but must now have a specified type.

The core APIs are unlikely to change drastically after this release, but new data types and new features are expected.

### Added

- Tighten equals checks with added support for contains across Table, Column, Row, Cell, and meta classes
- Add source to tableView and make source on Table public
- Cache transformed tables obtained via tableView[Table]
- Various test case improvements, better coverage
- Various related documentation improvements
- Add asBoolean and asString on Cell

### Fixed

- Fix issue with BasicFunctions on cell clear + added tests
- Tidy up some edge cases on Table related iterators
- Avoid accidental column create when not wanted

### Changed

- Simplified registry interactions and how tables/views are initially created and then obtained again through Table[..]
- Refactor transformer APIs and add support for TableTransformer, ColumnTransformer, and RowTransformer
- Update column filter on load functions to match column transformer function
- Change setters to accept nullable types, encourage use of Unit to reset
- Simplify class structure by removing BaseTable, BaseColumn, and BaseRow
- Change Cells constructor API to better match overall API with Cells[..]
- Refactor invoke(..) for better type safety and a more intuitive API
- Tighten acceptable types in contains(..) functions
- Allow columns to recreate themselves if needed

### Removed

- Nothing

## v1.24.2 - 2024-02-20 - Odd Dust

The general theme of this release is various minor API tidy-ups and improvements/fixes with increased test coverage,
plus added functionality within view related classes. The changes have focused on TableView.kt and related functionality.

### Added

- Various TableView, ColumnView, RowView, and CellView API improvements
- Various improvements to the APIs on view meta classes: CellClasses, CellTopics, Resources, etc
- Various test case improvements, better coverage
- Various related documentation improvements
- clear(TableView) added to TableViewOps.kt

### Fixed

- Fixed bug on CellView iterator to ensure no elements on empty cell
- Fixed bug on CellView.set for UnitCellTransformer case
- Other minor bugs

### Changed

- Change invoke parameter signature on CellView and other view related classes to match general pattern, improve ease of use

### Removed

- Some convenience functions on TableView.kt (and related) to ensure a better overall API structure, matching Table

## v1.24.1 - 2024-01-18 - Plain Hill

The general theme of this release is various minor API tidy-ups and improvements/fixes with increased test coverage,
plus added functionality within storage and cell types. The changes have focused on Table.kt and related functionality.

### Added

- Add storage support for WebCell
- Add support for boolean cell values
- Add convenience function to check if column is in table
- Improve BasicMath.kt number support and tidy up Cell.kt math
- Various Row, Cell and cell range improvements
- Various test case improvements, better coverage
- Various documentation improvements

### Fixed

- Fix bug preventing column/row resize from UI
- Fix Row.compareTo function

### Changed

- Rename ColumnHeader to Header, change how class is initialized via operator function

### Removed

- Some convenience functions on Table.kt (and related) to ensure a better overall API structure

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