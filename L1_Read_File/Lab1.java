import java.io.*;
import java.util.Scanner;

public class Lab1 {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите путь к файлу с числами: ");
        String filePath = scanner.nextLine();

        int[] numbers = null;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String[] lines = reader.lines().toArray(String[]::new);
            reader.close();

            String[] strNumbers;

            if (lines.length == 1 && lines[0].contains(",")) {
                strNumbers = lines[0].split(",");
            } else {
                strNumbers = lines;
            }

            numbers = new int[strNumbers.length];

            for (int i = 0; i < strNumbers.length; i++) {

                numbers[i] = Integer.parseInt(strNumbers[i]);
            }

            reader.close();
            System.out.println(" Файл прочитан успешно!");

        } catch (FileNotFoundException e) {
            System.out.println(" Файл не найден! Проверьте путь: " + filePath);
            scanner.close();
            return;
        } catch (IOException e) {
            System.out.println(" Ошибка чтения файла!");
            scanner.close();
            return;
        } catch (NumberFormatException e) {
            System.out.println("В файле некорректные данные (не числа)!");
            scanner.close();
            return;
        }

        printArray("Массив", numbers);

        int sum = getSum(numbers);
        double average = getAverage(numbers);
        int max = getMax(numbers);
        int min = getMin(numbers);
        double variance = getVariance(numbers);

        System.out.println("Сумма элементов: " + sum);
        System.out.println("Среднее значение: " + average);
        System.out.println("Максимальный элемент: " + max);
        System.out.println("Минимальный элемент: " + min);
        System.out.println("Дисперсия: " + variance);

        bubbleSort(numbers);
        printArray("Отсортированный массив", numbers);

        scanner.close();
    }

    public static void printArray(String name, int[] arr) {
        System.out.print(name + ": [");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
            if (i < arr.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    public static int getSum(int[] arr) {
        int sum = 0;
        for (int i = 0; i < arr.length; i++) {
            sum = sum + arr[i];
        }
        return sum;
    }

    public static double getAverage(int[] arr) {
        int sum = getSum(arr);
        return (double) sum / arr.length;
    }

    public static int getMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    public static int getMin(int[] arr) {
        int min = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return min;
    }

    public static double getVariance(int[] arr) {
        double average = getAverage(arr);
        double sum = 0;
        for (int i = 0; i < arr.length; i++) {
            double diff = arr[i] - average;
            sum = sum + (diff * diff);
        }
        return sum / arr.length;
    }

    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }
}
