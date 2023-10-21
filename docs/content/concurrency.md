# Concurrency

Whenever multiple threads interact with a data structure, you need to be aware of the limits and behavior expected.
All APIs and data structures in Sigbla can be assumed to be thread safe, and we'll dive into the behavior next.

## Updating a table

Making it easy to reason about is the main driver for the design choices made, and when updating a table
by assigning a new cell value or adding a new listener, these operations will synchronize and block other threads
trying to do similar mutating operations on the same table.

Put differently, if two threads try to write to a table, one thread will go before the other. The second thread can not
start writing before the first thread is done. It doesn't matter if they try to write to different cells, columns or
rows within the same table.

When adding listeners to a table, these form part of the write path. Hence, any event processing must complete before
the current writing thread can return. Event processing will never take place on a background thread. The initialization
phase of adding a listener also forms part of this write path, hence a listener being added will block other threads
from writing till after the listener is added.

Let's look at an example:

``` kotlin
val table = Table[null]

on(table) events {
    // Sleep to make this take a while
    println("Before sleep")
    Thread.sleep(TimeUnit.SECONDS.toMillis(5))
    println("After sleep")
}

// Start two threads with both updating the table concurrently
val t1 = thread {
    println("Starting thread 1")
    table["A", 1] = "A1 - from T1"
    println("Ending thread 1")
}

val t2 = thread {
    println("Starting thread 2")
    table["A", 1] = "A1 - from T2"
    println("Ending thread 2")
}

t1.join()
t2.join()

// Output:
// Starting thread 1
// Starting thread 2
// Before sleep
// After sleep
// Ending thread 1
// Before sleep
// After sleep
// Ending thread 2

print(table)

// Output:
//              |A            
// 1            |A1 - from T2 
```

The above code is valid and safe, with one thread waiting for the other. From the output we can see thread 1 starting
just before thread 2, event processing taking place before thread 1 ends, with event processing continuing thereafter
for thread 2.

## Reading table data

We've already covered that cells are immutable. At no point will the content of a particular cell instance change,
making them thread safe by design.

The same applies for iterators. Once created, all iterators deliver a stable snapshot of the cells as they existed when
the iterator was created. For example, if a thread needs to perform some operation across several cells, it doesn't
need to worry about any of these cells being updated while iterating over them.

Threads that read from a table are not synchronized. You can have multiple threads reading concurrently. Only threads
that write to a table are synchronized against other threads that also write to the table. A thread reading from a table
can continue to do so uninterrupted no matter what other threads might do on the table. Combined with the snapshots
provided by iterators, this makes it both efficient and safe.

Let's illustrate this:

``` kotlin
val table = Table[null]

// Fill the table with some data
for (i in 0..10) {
    table["A"][i] = i
}

// Start first thread that will slowly read the cells
val t1 = thread {
    println("Starting thread 1")
    table["A"].forEach {
        Thread.sleep(TimeUnit.SECONDS.toMillis(1))
        println("T1 read: $it")
    }
    println("Ending thread 1")
}

Thread.sleep(TimeUnit.SECONDS.toMillis(5))

// Start second thread that will change all the cells very quickly
val t2 = thread {
    println("Starting thread 2")
    for (i in 0..10) {
        table["A"][i] = "T2 was here"
    }
    println("Ending thread 2")
}

t1.join()
t2.join()

// Output:
// Starting thread 1
// T1 read: 0
// T1 read: 1
// T1 read: 2
// T1 read: 3
// Starting thread 2
// Ending thread 2
// T1 read: 4
// T1 read: 5
// T1 read: 6
// T1 read: 7
// T1 read: 8
// T1 read: 9
// T1 read: 10
// Ending thread 1

print(table)

// Output:
//             |A           
// 0           |T2 was here 
// 1           |T2 was here 
// 2           |T2 was here 
// 3           |T2 was here 
// 4           |T2 was here 
// 5           |T2 was here 
// 6           |T2 was here 
// 7           |T2 was here 
// 8           |T2 was here 
// 9           |T2 was here 
// 10          |T2 was here 
```

What can also be guaranteed is that as soon as one thread has finished making an update, all other threads can see the
update thereafter as soon as they start reading from the table. Put differently, there's no risk that cell updates
haven't been pushed back to main memory.

## Synchronizing between tables

There is no synchronization between tables. Writing to one table does not block other threads from writing to other
tables. If this is something you need in your code you will need to manage this yourself.