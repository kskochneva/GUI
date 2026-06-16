import java.util.Scanner;
import java.util.ArrayList;

public class Factorize_Prime_Numbers {



    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите натуральное число: ");


        int n = scanner.nextInt();

            if (n <= 0) {
                System.out.println("Ошибка: введите положительное натуральное число (больше 0).");
            } else if (n == 1) {
                System.out.println("1 не раскладывается на простые множители.");
            } else {
                // Раскладываем число
                ArrayList<Integer> factors = new ArrayList<>();
                int temp = n;


                while (temp % 2 == 0) {
                    factors.add(2);
                    temp = temp / 2;
                }

                //
                int divisor = 3;
                while (divisor * divisor <= temp) {
                    while (temp % divisor == 0) {
                        factors.add(divisor);
                        temp = temp / divisor;
                    }
                    divisor = divisor + 2;
                }

                // Если осталось число больше 1 — оно простое
                if (temp > 1) {
                    factors.add(temp);
                }

                // Выводим результат
                System.out.print(n + " = ");
                for (int i = 0; i < factors.size(); i++) {
                    System.out.print(factors.get(i));
                    if (i < factors.size() - 1) {
                        System.out.print(" * ");
                    }
                }
                System.out.println();
            }


    }
}
