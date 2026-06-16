import java.util.Scanner;

public class Fibs {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите число: ");
        long n = scanner.nextLong();

        // Первые числа Фибоначчи
        long a = 0; // для инт макс 2 млрд
        long b = 1;

        boolean found = false;

        // Если ввели 0 или 1 - сразу да
        if (n == 0 || n == 1) {
            found = true;
        }


        while (b < n) {
            long next = a + b;
            a = b;
            b = next;

            if (b == n) {
                found = true;
                break;
            }
        }

        // Выводим результат
        if (found) {
            System.out.println(n + " принадлежит посл Фибоначчи.");
        } else {
            System.out.println(n + " НЕ принадлежит посл Фибоначчи.");
        }

        scanner.close();
    }
}
