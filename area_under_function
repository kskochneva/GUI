import java.util.Scanner;//ввод - вывод

public class area_under_function {
    public static void main(String[] args) {
        //создаем объект типа sc
        Scanner sc = new Scanner(System.in);//считывв2аем числа границ

        System.out.print("Введите a: ");
        double a = sc.nextDouble();

        System.out.print("Введите b: ");
        double b = sc.nextDouble();

        int n = 1000000; // сколько кусочков
        double width = (b - a) / n; // ширина одного кусочка
        double area = 0.0;

        for (int i = 0; i < n; i++) {
            double x = a + i * width; // координата текущей точки
            double y = x*x*x + x + 1; // значение функции
            //x^3+x+1
            area += y * width;        // прибавляем площадь прямоугольничка
        }

        System.out.println("Площадь = " + area);
        sc.close();
    }
}
