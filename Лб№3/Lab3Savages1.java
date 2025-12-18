public class Lab3Savages1 {
    private static int pot = 0; // Количество порций в горшке
    private static final int N = 5; // Максимальное количество порций в горшке
    private static final Object potLock = new Object();
    private static boolean cookNeeded = false;

    static class Savage extends Thread {
        private int id;

        public Savage(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            synchronized (potLock) {
                // Ждем, пока в горшке есть еда
                while (pot == 0) {
                    if (!cookNeeded) {
                        cookNeeded = true;
                        potLock.notify(); // Будим повара
                    }
                    try {
                        potLock.wait(); // Ждем, пока повар наполнит горшок
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // Берем порцию
                pot--;
                System.out.println("Savage " + id + " took a serving. Pot now has " + pot + " servings.");
            }
        }
    }

    static class Cook extends Thread {
        @Override
        public void run() {
            while (true) {
                synchronized (potLock) {
                    // Ждем, пока горшок не опустеет и дикари не попросят наполнить
                    while (pot > 0 || !cookNeeded) {
                        try {
                            potLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    // Наполняем горшок
                    pot = N;
                    cookNeeded = false;
                    System.out.println("Cook refilled the pot. Pot now has " + pot + " servings.");
                    potLock.notifyAll(); // Будим всех дикарей
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Lab3Savages1 <number_of_savages>");
            System.out.println("Note: number_of_savages must be greater than " + N);
            System.exit(1);
        }

        int numSavages = Integer.parseInt(args[0]);
        if (numSavages <= N) {
            System.out.println("Error: number of savages must be greater than " + N);
            System.exit(1);
        }

        // Инициализируем горшок
        pot = N;

        // Создаем и запускаем повара
        Cook cook = new Cook();
        cook.setDaemon(true); // Повар работает в фоновом режиме
        cook.start();

        // Создаем и запускаем дикарей
        Thread[] savages = new Thread[numSavages];
        for (int i = 0; i < numSavages; i++) {
            savages[i] = new Savage(i + 1);
            savages[i].start();
        }

        // Ждем завершения всех дикарей
        try {
            for (int i = 0; i < numSavages; i++) {
                savages[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("All savages have eaten. Final pot state: " + pot + " servings.");
    }
}

