public class Lab3Savages2 {
    private static int pot = 0; // Количество порций в горшке
    private static final int N = 5; // Максимальное количество порций в горшке
    private static final Object potLock = new Object();
    private static boolean cookNeeded = false;
    
    // Механизм справедливости: отслеживаем, кто уже поел из текущего горшка
    private static int[] hasEatenFromCurrentPot; // 1 - поел, 0 - не поел
    private static int potGeneration = 0; // Поколение горшка (увеличивается при наполнении)
    private static int numSavages;

    static class Savage extends Thread {
        private int id;

        public Savage(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            while (true) {
                synchronized (potLock) {
                    // Проверяем, можем ли мы есть из текущего горшка
                    // Если мы уже поели из текущего поколения горшка, ждем
                    while (hasEatenFromCurrentPot[this.id - 1] == 1) {
                        // Проверяем, все ли поели из текущего горшка
                        boolean allHaveEaten = true;
                        for (int i = 0; i < numSavages; i++) {
                            if (hasEatenFromCurrentPot[i] == 0) {
                                allHaveEaten = false;
                                break;
                            }
                        }
                        
                        // Если все поели, сбрасываем флаги для нового раунда
                        if (allHaveEaten) {
                            for (int i = 0; i < numSavages; i++) {
                                hasEatenFromCurrentPot[i] = 0;
                            }
                            break; // Выходим из цикла ожидания
                        }
                        
                        // Еще не все поели, ждем
                        try {
                            potLock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

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
                    hasEatenFromCurrentPot[this.id - 1] = 1;
                    System.out.println("Savage " + id + " took a serving. Pot now has " + pot + " servings.");
                    
                    // Будим всех остальных дикарей
                    potLock.notifyAll();
                }

                // Симуляция времени на поедание
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    potGeneration++; // Увеличиваем поколение горшка
                    System.out.println("Cook refilled the pot. Pot now has " + pot + " servings. Generation: " + potGeneration);
                    
                    // Сбрасываем флаги для нового поколения горшка
                    for (int i = 0; i < numSavages; i++) {
                        hasEatenFromCurrentPot[i] = 0;
                    }
                    
                    potLock.notifyAll(); // Будим всех дикарей
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Lab3Savages2 <number_of_savages>");
            System.out.println("Note: number_of_savages must be greater than " + N);
            System.exit(1);
        }

        numSavages = Integer.parseInt(args[0]);
        if (numSavages <= N) {
            System.out.println("Error: number of savages must be greater than " + N);
            System.exit(1);
        }

        // Инициализируем горшок
        pot = N;
        
        // Инициализируем массив для отслеживания справедливости
        hasEatenFromCurrentPot = new int[numSavages];
        for (int i = 0; i < numSavages; i++) {
            hasEatenFromCurrentPot[i] = 0;
        }

        // Создаем и запускаем повара
        Cook cook = new Cook();
        cook.setDaemon(true); // Повар работает в фоновом режиме
        cook.start();

        // Создаем и запускаем дикарей
        Thread[] savages = new Thread[numSavages];
        for (int i = 0; i < numSavages; i++) {
            savages[i] = new Savage(i + 1);
            savages[i].setDaemon(true); // Дикари работают в бесконечном цикле
            savages[i].start();
        }

        // Программа будет работать, пока не будет прервана
        // Для демонстрации можно добавить задержку
        try {
            Thread.sleep(10000); // Работаем 10 секунд
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Program terminated after demonstration period.");
    }
}

