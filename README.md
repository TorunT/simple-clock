# Simple Clock Application

> A multithreaded Java console application that displays the current time and date continuously using two concurrent threads with different priorities.

---

## Overview

**SimpleClockApplication** demonstrates the Java Thread model through a real-world clock scenario. Two threads run concurrently — one updates the time in the background at high frequency, while the other prints it to the console every second. Thread priorities are used to ensure timekeeping precision.

---

## Features

| Feature | Description |
|---|---|
| Real-time clock | Displays current time and date updated every second |
| Multithreading | Two concurrent threads running simultaneously |
| Thread priorities | Display thread (MAX) has higher priority than updater (MIN) |
| Synchronization | `synchronized` methods prevent race conditions |
| Graceful shutdown | Threads stop cleanly using flags and `join()` |
| Formatted output | Time displayed as `HH:mm:ss  dd-MM-yyyy` |

---

## Thread Architecture

```
┌─────────────────────────────────────────────────┐
│                 ClockApplication                 │
│                   (main thread)                  │
└───────────────┬─────────────────┬───────────────┘
                │                 │
                ▼                 ▼
  ┌─────────────────────┐   ┌─────────────────────┐
  │  BackgroundUpdater  │   │    ClockDisplay      │
  │  Priority: MIN (1)  │   │  Priority: MAX (10)  │
  │  every 100ms        │   │  every 1000ms        │
  └──────────┬──────────┘   └──────────┬──────────┘
             │                         │
             └──────────┬──────────────┘
                        │
                        ▼
               ┌─────────────┐
               │    Clock    │
               │  (shared)   │
               │synchronized │
               └─────────────┘
```

---

## Sample Output

```
=== Multithreaded Clock Application ===

[BackgroundUpdater-Thread] started  (priority=1)
[ClockDisplay-Thread] started  (priority=10)

╔══════════════════════════════════════════════╗
║        SIMPLE CLOCK APPLICATION              ║
║     Time              Date                   ║
╠══════════════════════════════════════════════╣
  14:32:45  28-04-2026
  14:32:46  28-04-2026
  14:32:47  28-04-2026
  14:32:48  28-04-2026
  14:32:49  28-04-2026
╚══════════════════════════════════════════════╝

[ClockDisplay-Thread] stopped.
[BackgroundUpdater-Thread] stopped.

=== Clock Application terminated. ===
```

---

## Project Structure

```
simple-clock/
│
├── ClockApplication.java      # All classes in a single file
│
├── screenshots/
│   └── output.png             # Sample program output
│
└── README.md
```

---

## Class Overview

### `Clock`
Shared data object accessed by both threads. Stores the current time as a formatted string. Uses `synchronized` methods and `volatile` field to ensure thread-safe access.

### `BackgroundUpdater` (extends Thread)
Runs at `Thread.MIN_PRIORITY` (1). Updates the shared `Clock` object every 100ms in the background. Handles `InterruptedException` for clean shutdown.

### `ClockDisplay` (extends Thread)
Runs at `Thread.MAX_PRIORITY` (10). Reads the current time from the `Clock` object and prints it to the console every second. Higher priority ensures display accuracy.

### `ClockApplication`
Entry point. Creates the shared `Clock`, initializes both threads, starts them in the correct order, and uses `join()` to wait for clean termination.

---

## How to Run

### Prerequisites
- Java JDK 8 or higher

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/TorunT/simple-clock.git
cd simple-clock

# 2. Compile
javac ClockApplication.java

# 3. Run
java ClockApplication
```

The clock runs for 10 seconds then exits cleanly.

---

## Concepts Demonstrated

- `extends Thread` for custom thread creation
- `Thread.MIN_PRIORITY` and `Thread.MAX_PRIORITY` for task prioritization
- `synchronized` methods for mutual exclusion
- `volatile` keyword for cross-thread visibility
- `Thread.sleep()` for controlled timing
- `InterruptedException` handling for graceful shutdown
- `join()` to wait for thread completion
- Shared object pattern for inter-thread communication

---




---

## References

- Eck, D. J. (2022). *Introduction to programming using Java version 9, JavaFX edition*. https://math.hws.edu/javanotes/
- Samoylov, N. (2018). *Introduction to programming: Learn to program in Java with data structures, algorithms, and logic*. Packt Publishing.

