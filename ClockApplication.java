import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 *  ClockApplication — Simple Multithreaded Clock
 * 
 * ============================================================
 *  Demonstrates Java Thread model with two concurrent threads:
 *  - BackgroundUpdater : updates the shared time (lower priority)
 *  - ClockDisplay      : prints the time to console (higher priority)
 * ============================================================
 */

// ── Shared time data (accessed by both threads) ───────────────
class Clock {

    /** Formatter for readable output: HH:mm:ss dd-MM-yyyy */
    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("HH:mm:ss  dd-MM-yyyy");

    /** The latest formatted time string, updated by BackgroundUpdater */
    private volatile String currentTime = "";

    /**
     * Updates currentTime to the present moment.
     * Called repeatedly by the background thread.
     */
    public synchronized void updateTime() {
        currentTime = LocalDateTime.now().format(FORMATTER);
    }

    /**
     * Returns the most recently updated time string.
     * Called repeatedly by the display thread.
     */
    public synchronized String getCurrentTime() {
        return currentTime;
    }
}

// ── Background thread: keeps the clock's time fresh ──────────
class BackgroundUpdater extends Thread {

    private final Clock clock;
    private volatile boolean running = true;

    public BackgroundUpdater(Clock clock) {
        this.clock = clock;
        setName("BackgroundUpdater-Thread");
        // Lower priority — just keeps data up to date
        setPriority(Thread.MIN_PRIORITY);          // priority = 1
    }

    @Override
    public void run() {
        System.out.println("[" + getName() + "] started  (priority=" + getPriority() + ")");
        while (running) {
            clock.updateTime();
            try {
                Thread.sleep(100); // update every 100 ms for smooth precision
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
        System.out.println("[" + getName() + "] stopped.");
    }

    /** Gracefully stop the updater loop */
    public void stopRunning() {
        running = false;
        interrupt();
    }
}

// ── Display thread: prints the time to the console ───────────
class ClockDisplay extends Thread {

    private static final String RESET  = "\u001B[0m";
    private static final String CYAN   = "\u001B[36m";
    private static final String BOLD   = "\u001B[1m";
    private static final String YELLOW = "\u001B[33m";

    private final Clock   clock;
    private final int     totalTicks;   // how many seconds to run
    private volatile boolean running = true;

    public ClockDisplay(Clock clock, int totalTicks) {
        this.clock      = clock;
        this.totalTicks = totalTicks;
        setName("ClockDisplay-Thread");
        // Higher priority — ensures time is printed accurately
        setPriority(Thread.MAX_PRIORITY);          // priority = 10
    }

    @Override
    public void run() {
        System.out.println("[" + getName() + "] started  (priority=" + getPriority() + ")\n");
        printHeader();

        int ticks = 0;
        while (running && ticks < totalTicks) {
            String time = clock.getCurrentTime();
            if (!time.isEmpty()) {
                System.out.println(CYAN + BOLD + "  " + time + RESET);
            }
            ticks++;
            try {
                Thread.sleep(1000); // print once per second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
        printFooter();
    }

    private void printHeader() {
        System.out.println(YELLOW + BOLD +
            "╔══════════════════════════════════════════════╗\n" +
            "║        SIMPLE CLOCK APPLICATION              ║\n" +
            "║     Time              Date                   ║\n" +
            "╠══════════════════════════════════════════════╣" + RESET);
    }

    private void printFooter() {
        System.out.println(YELLOW + BOLD +
            "╚══════════════════════════════════════════════╝\n" + RESET);
        System.out.println("[" + getName() + "] stopped.");
    }

    public void stopRunning() {
        running = false;
        interrupt();
    }
}

// ── Main program ──────────────────────────────────────────────
public class ClockApplication {

    public static void main(String[] args) {

        System.out.println("=== Multithreaded Clock Application ===\n");

        // Shared Clock object used by both threads
        Clock clock = new Clock();

        // Create threads with different priorities
        BackgroundUpdater updater = new BackgroundUpdater(clock);
        ClockDisplay      display = new ClockDisplay(clock, 10); // run 10 seconds

        // Start background updater first so time is ready before display
        updater.start();

        // Small delay to ensure updater has a value ready
        try { Thread.sleep(50); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        display.start();

        // Wait for display thread to finish (10 seconds)
        try {
            display.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Gracefully stop the background updater
        updater.stopRunning();
        try {
            updater.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n=== Clock Application terminated. ===");
    }
}
