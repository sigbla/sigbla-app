# Functions

Sigbla functions provided various types of calculations with the result assigned to a given cell. When the input
to these functions update, the calculations are redone and the result updated.

The functions currently available out of the box is limited, but will expand over time. They are described below
together with a basic example.

## Count

Counts the number of cells from its inputs.

``` kotlin
val table = Table["table"]

table["Input", 0] = "String 1"
table["Input", 1] = 150
table["Input", 2] = "String 2"
table["Input", 3] = 250
table["Input", 4] = "String 3"

// Simple count with otherwise default values
table["Output", 5] = count(table["Input", 0]..table["Input", 4])

// Only include string cells in count
table["Output", 6] = count(table["Input", 0]..table["Input", 4]) { it is StringCell }

print(table)

// Output:
//          |Input    |Output   
// 0        |String 1 |         
// 1        |150      |         
// 2        |String 2 |         
// 3        |250      |         
// 4        |String 3 |         
// 5        |         |5        
// 6        |         |3        
```

## Sum

Calculates the sum of its inputs.

``` kotlin
val table = Table["table"]

table["Input", 0] = 100
table["Input", 1] = 150
table["Input", 2] = 200
table["Input", 3] = 250
table["Input", 4] = 300

// Simple sum with otherwise default values
table["Output", 5] = sum(table["Input", 0]..table["Input", 4])

// Only include cells less than or equal to 200 in sum
table["Output", 6] = sum(table["Input", 0]..table["Input", 4]) { it <= 200 }

print(table)

// Output:
//        |Input  |Output 
// 0      |100    |       
// 1      |150    |       
// 2      |200    |       
// 3      |250    |       
// 4      |300    |       
// 5      |       |1000   
// 6      |       |450    
```

## Max

Finds the maximum of its inputs.

``` kotlin
val table = Table["table"]

table["Input", 0] = 100
table["Input", 1] = 150
table["Input", 2] = 200
table["Input", 3] = 250
table["Input", 4] = 300

// Simple max with otherwise default values
table["Output", 5] = max(table["Input", 0]..table["Input", 4])

// Only include cells less than or equal to 200 in max
table["Output", 6] = max(table["Input", 0]..table["Input", 4]) { it <= 200 }

print(table)

// Output:
//        |Input  |Output 
// 0      |100    |       
// 1      |150    |       
// 2      |200    |       
// 3      |250    |       
// 4      |300    |       
// 5      |       |300    
// 6      |       |200    
```

## Min

Finds the minimum of its inputs.

``` kotlin
val table = Table["table"]

table["Input", 0] = 100
table["Input", 1] = 150
table["Input", 2] = 200
table["Input", 3] = 250
table["Input", 4] = 300

// Simple min with otherwise default values
table["Output", 5] = min(table["Input", 0]..table["Input", 4])

// Only include cells greater than 200 in min
table["Output", 6] = min(table["Input", 0]..table["Input", 4]) { it > 200 }

print(table)

// Output:
//        |Input  |Output 
// 0      |100    |       
// 1      |150    |       
// 2      |200    |       
// 3      |250    |       
// 4      |300    |       
// 5      |       |100    
// 6      |       |250    
```

## Removing a function

Once a function is assigned to a cell, it will remain until the cell is overwritten. Below is an example of how we
might do just that to remove the sum function.

``` kotlin
val table = Table[null]

table["A", 0] = 100
table["A", 1] = 200
table["A", 2] = 300

table["Sum", 0] = sum(table["A"])

print(table)

// Output:
//     |A   |Sum
// 0   |100 |600
// 1   |200 |
// 2   |300 |

// Examples of how to remove the sum function on ["Sum", 0]
// remove(table["Sum"])
// clear(table["Sum"])
table["Sum", 0] = Unit

table["A", 0] = 400

print(table)

// Output:
//     |A   |Sum
// 0   |400 |
// 1   |200 |
// 2   |300 |
```
