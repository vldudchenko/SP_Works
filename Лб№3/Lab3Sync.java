public class Lab3Sync {
    private static int counter = 0;
    private static final int ITERATIONS = 100000;

    static class IncrementThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < ITERATIONS; i++) {
                synchronized (Lab3Sync.class) {
                    int local = counter;
                    local++;
                    counter = local;
                }
            }
        }
    }

    static class DecrementThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < ITERATIONS; i++) {
                synchronized (Lab3Sync.class) {
                    int local = counter;
                    local--;
                    counter = local;
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Lab3Sync <n> <m>");
            System.out.println("where n is the number of incrementing threads");
            System.out.println("and m is the number of decrementing threads");
            System.exit(1);
        }

        int n = Integer.parseInt(args[0]);
        int m = Integer.parseInt(args[1]);

        Thread[] incrementThreads = new Thread[n];
        Thread[] decrementThreads = new Thread[m];

        long startTime = System.nanoTime();

        // Создаем и запускаем потоки инкремента
        for (int i = 0; i < n; i++) {
            incrementThreads[i] = new IncrementThread();
            incrementThreads[i].start();
        }

        // Создаем и запускаем потоки декремента
        for (int i = 0; i < m; i++) {
            decrementThreads[i] = new DecrementThread();
            decrementThreads[i].start();
        }

        // Ждем завершения всех потоков инкремента
        try {
            for (int i = 0; i < n; i++) {
                incrementThreads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ждем завершения всех потоков декремента
        try {
            for (int i = 0; i < m; i++) {
                decrementThreads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long endTime = System.nanoTime();
        long executionTime = endTime - startTime;

        System.out.println("Final counter value: " + counter);
        System.out.println("Execution time: " + executionTime + " nanoseconds");
        System.out.println("Execution time: " + (executionTime / 1_000_000.0) + " milliseconds");
    }
}

