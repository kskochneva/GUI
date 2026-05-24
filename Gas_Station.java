import java.util.*;
//импортирую все из пакета
//сканнер очередь рандом и тд

public class Gas_Station {

    // Класс "Автомобиль"
    static class Car {
        double tankVolume;      // объём бака
        double currentFuel;     // сколько уже есть
        double wantToFill;      // сколько  залить
        double waitTime;        // сколько  уже ждет

        Car(double tankVolume, double currentFuel, double wantToFill) {
            this.tankVolume = tankVolume;
            this.currentFuel = currentFuel;
            this.wantToFill = wantToFill;
            this.waitTime = 0;
        }

        //  свободного места в баке
        double freeSpace() {
            return tankVolume - currentFuel;
        }

        // Сколько  зальёт
        double actualFill() {
            return Math.min(wantToFill, freeSpace());
        }
    }

    // Класс "Колонка"
    static class Pump {
        int id;
        double fillSpeed;
        double remainingTime;
        Car currentCar;
        int carsServed;

        Pump(int id, double fillSpeed) {
            this.id = id;
            this.fillSpeed = fillSpeed;
            this.remainingTime = 0;
            this.currentCar = null;
            this.carsServed = 0;
        }

        boolean isFree() {
            return remainingTime <= 0.001 && currentCar == null;
        }

        // Начать заправку машины
        void startFill(Car car) {
            if (!isFree()) {
                System.err.println("Колонка занята");
                return;
            }
            this.currentCar = car;
            double liters = car.actualFill();
            this.remainingTime = liters / fillSpeed;
        }


        //  true, если машина  закончила заправку
        boolean update() {
            if (remainingTime > 0) {
                remainingTime--;
                if (remainingTime <= 0.001 && currentCar != null) {
                    carsServed++;
                    currentCar = null;
                    return true;
                }
            }
            return false;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //константа по заданию
        final double PUMP_SPEED = 20.0; // 20 литров в минуту


        System.out.println("Скорость заправки  " + PUMP_SPEED + " л/мин");
        System.out.print("Количество колонок: ");
        int pumpCount = scanner.nextInt();

        System.out.print(" время ожидания в очереди в мнн: ");
        double maxWaitTime = scanner.nextDouble();

        System.out.print(" запас бензина на АЗС в л: ");
        double fuelStock = scanner.nextDouble();

        System.out.print(" поставки бензина каждые : ");
        int deliveryInterval = scanner.nextInt();

        System.out.print(" литров  за  поставку: ");
        double deliveryAmount = scanner.nextDouble();

        System.out.print("время симуляции: ");
        int simulationMinutes = scanner.nextInt();



        Pump[] pumps = new Pump[pumpCount];
        for (int i = 0; i < pumpCount; i++) {
            pumps[i] = new Pump(i + 1, PUMP_SPEED);
            System.out.printf("Колонка %d: скорость %.1f л/мин\n", i+1, PUMP_SPEED);
        }

        Queue<Car> queue = new LinkedList<>();
        Random rand = new Random();

        int totalCarsArrived = 0;
        int totalCarsServed = 0;
        int totalCarsLeftQueue = 0;
        int totalCarsLeftNoFuel = 0;


        boolean extraPumpScheduled = false;
        int minutesUntilExtraPump = 0;
        int totalPumpsOpened = 0;


        int maxQueueSize = 0;


        for (int minute = 0; minute < simulationMinutes; minute++) {
            int hourOfDay = (minute / 60) % 24;  // реальный час (0-23)
            int minuteOfHour = minute % 60;


            double baseChance;
            String timeOfDay = "";

            if ((hourOfDay >= 8 && hourOfDay <= 10) || (hourOfDay >= 17 && hourOfDay <= 19)) {
                baseChance = 0.5;
                timeOfDay = "сильная нагрузка утро и вечер";
            } else if (hourOfDay >= 0 && hourOfDay <= 5) {
                baseChance = 0.05;
                timeOfDay = "ночные время";
            } else if ((hourOfDay >= 12 && hourOfDay <= 14)) {
                baseChance = 0.35;   // обеденное время
                timeOfDay = "середина дня";
            } else {
                baseChance = 0.2;   // обычное время
                timeOfDay = "другие часы";
            }

            //теперь имитируем реальную заправку
            //очередь пропускаем
            //пусто заезжаем
            //queueFactor коэффициент загруженности очереди
            double queueFactor = Math.min(1.0, queue.size() / 10.0);
            double actualChance = baseChance * (1 + queueFactor);
            //actualChance - итоговая ввероятность
            if (rand.nextDouble() < actualChance) {
                totalCarsArrived++;
                //генерируем вероятность и сравниваем с итоговой
                //и если меньше то машина заедет на запрвку
                // Случайные параметры машины
                double tankVolume = 40 + rand.nextDouble() * 30;
                double currentFuel = tankVolume * (0.1 + rand.nextDouble() * 0.5);  // 10%-60% заполненности
                double wantToFill = 10 + rand.nextDouble() * 40;   //  10-50 литров

                Car car = new Car(tankVolume, currentFuel, wantToFill);
                double actual = car.actualFill();

                // Проверка: хватает ли бензина на АЗС
                if (actual <= fuelStock + 0.001) { //погрешность чтобы наверняка
                    queue.add(car);
                    System.out.printf("[%02d:%02d] (%s) [+] Машина приехала: бак=%.1fл, есть=%.1fл(%.0f%%), хочет=%.1fл, зальёт=%.1fл. Очередь: %d\n",
                            hourOfDay, minuteOfHour, timeOfDay, tankVolume, currentFuel,
                            (currentFuel/tankVolume)*100, wantToFill, actual, queue.size());

                    if (queue.size() > maxQueueSize) {
                        maxQueueSize = queue.size();
                    }
                } else {
                    totalCarsLeftNoFuel++;
                    System.out.printf("[%02d:%02d] (%s) [!] Машина уехала: НЕТ БЕНЗИНА (нужно %.1fл, осталось %.1fл)\n",
                            hourOfDay, minuteOfHour, timeOfDay, actual, fuelStock);
                }
            }

            //создаем объект для безопасного проходп по очереди
            Iterator<Car> iterator = queue.iterator();
            while (iterator.hasNext()) {
                Car car = iterator.next();
                car.waitTime++;


                if (car.waitTime > maxWaitTime) {
                    iterator.remove();
                    totalCarsLeftQueue++;
                    System.out.printf("[%02d:%02d] [X] Машина уехала из очереди: ожидала %.0f мин > лимита %.0f мин\n",
                            hourOfDay, minuteOfHour, car.waitTime, maxWaitTime);

                    // Запланировать открытие дополнительной колонки через 2 дня
                    if (!extraPumpScheduled) {
                        extraPumpScheduled = true;
                        minutesUntilExtraPump = 2 * 24 * 60; // 2 дня в минутах
                        System.out.printf("[%02d:%02d] [ПЛАН] Запланировано открытие доп. колонки через 2 дня (%d минут)\n",
                                hourOfDay, minuteOfHour, minutesUntilExtraPump);
                    }
                }
            }


            if (extraPumpScheduled && minutesUntilExtraPump > 0) {
                minutesUntilExtraPump--;
                if (minutesUntilExtraPump == 0) {


                    //создаю массив на 1 больше для колонки
                    Pump[] newPumps = new Pump[pumps.length + 1];
                    System.arraycopy(pumps, 0, newPumps, 0, pumps.length);
                    //добавляем в массив новую

                    newPumps[pumps.length] = new Pump(pumps.length + 1, PUMP_SPEED);
                    pumps = newPumps;
                    totalPumpsOpened++;
                    System.out.printf("[%02d:%02d] [НОВАЯ КОЛОНКА] Открыта дополнительная колонка #%d! Скорость: %.1f л/мин\n",
                            hourOfDay, minuteOfHour, pumps.length, PUMP_SPEED);
                    extraPumpScheduled = false;
                }
            }


            for (Pump pump : pumps) {
                boolean finished = pump.update();
                if (finished) {
                    totalCarsServed++;
                    System.out.printf("[%02d:%02d] [ГОТОВО] Колонка %d закончила заправку. Обслужено машин этой колонкой: %d\n",
                            hourOfDay, minuteOfHour, pump.id, pump.carsServed);
                }
            }


            for (Pump pump : pumps) {
                if (pump.isFree() && !queue.isEmpty()) {
                    Car car = queue.poll();// Достаем и удаляем первый элемент из очереди
                    double actualFill = car.actualFill();


                    if (actualFill <= fuelStock + 0.001) {
                        pump.startFill(car);
                        fuelStock -= actualFill;
                        double fillTime = actualFill / pump.fillSpeed;
                        System.out.printf("[%02d:%02d] [ЗАПРАВКА] Колонка %d начала заправку: %.1fл, время=%.2f мин. Осталось бензина: %.1fл\n",
                                hourOfDay, minuteOfHour, pump.id, actualFill, fillTime, fuelStock);
                    } else {

                        queue.add(car);
                        totalCarsLeftNoFuel++;
                        System.out.printf("[%02d:%02d] [!] НЕТ БЕНЗИНА для заправки (нужно %.1fл, осталось %.1fл). Машина вернулась в очередь\n",
                                hourOfDay, minuteOfHour, actualFill, fuelStock);
                        break;
                    }
                }
            }


            if (minute > 0 && minute % deliveryInterval == 0) {
                fuelStock += deliveryAmount;
                System.out.printf("[%02d:%02d] [ДОСТАВКА] Привезли бензин: +%.1fл, теперь %.1fл\n",
                        hourOfDay, minuteOfHour, deliveryAmount, fuelStock);
            }

            //понять какое количество колонок будет необходимо
            //ля этого возьмем статистику каждые 60 минут
            if (minute > 0 && minute % 60 == 0) {
                System.out.println("\n" + "=".repeat(60));
                System.out.printf("СТАТИСТИКА на %02d:00 (минута %d)\n", hourOfDay, minute);
                System.out.println("=".repeat(60));
                System.out.printf("  Очередь: %d машин (максимум было: %d)\n", queue.size(), maxQueueSize);
                System.out.printf("  Бензина на АЗС: %.1f л\n", fuelStock);
                System.out.printf("  Обслужено машин всего: %d\n", totalCarsServed);
                System.out.printf("  Уехало (нет бензина): %d\n", totalCarsLeftNoFuel);
                System.out.printf("  Уехало (долгое ожидание): %d\n", totalCarsLeftQueue);
                System.out.printf("  Всего приехало машин: %d\n", totalCarsArrived);
                System.out.printf("  Открыто доп. колонок: %d\n", totalPumpsOpened);
                System.out.printf("  Текущее количество колонок: %d\n", pumps.length);
                System.out.println("=".repeat(60) + "\n");
            }

            // Экстренная остановка
            if (fuelStock < 0.1 && queue.isEmpty()) {
                boolean anyBusy = false;
                for (Pump pump : pumps) {
                    if (!pump.isFree()) {
                        anyBusy = true;
                        break;
                    }
                }
                if (!anyBusy) {
                    System.out.printf("[%02d:%02d] [СТОП] Бензин закончился, очередь пуста. Симуляция остановлена.\n",
                            hourOfDay, minuteOfHour);
                    break;
                }
            }
        }


        System.out.println("\n" + "=".repeat(60));
        System.out.println("Выводим результатт");
        System.out.println("=".repeat(60));
        System.out.printf("Всего приехало машин: %d\n", totalCarsArrived);
        System.out.printf("Обслужено машин: %d\n", totalCarsServed);
        System.out.printf("Уехало из-за нехватки бензина: %d\n", totalCarsLeftNoFuel);
        System.out.printf("Уехало из-за долгого ожидания: %d\n", totalCarsLeftQueue);
        System.out.printf("Осталось в очереди: %d машин\n", queue.size());
        System.out.printf("Максимальная очередь: %d машин\n", maxQueueSize);
        System.out.printf("Остаток бензина на АЗС: %.1f л\n", fuelStock);
        System.out.printf("Всего открыто дополнительных колонок: %d\n", totalPumpsOpened);
        System.out.printf("Финальное количество колонок: %d\n", pumps.length);
        System.out.println("\nСтатистика по колонкам:");
        for (Pump pump : pumps) {

            System.out.printf("  Колонка #%d: обслужила %d машин , скорость: %.1f л/мин\n",
                    pump.id, pump.carsServed, pump.fillSpeed);
        }
        System.out.println("=".repeat(60));


        scanner.close();
    }
}
