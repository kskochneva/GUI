import java.util.Scanner;


class TriangularPrism {

    double sideA;
    double sideB;
    double sideC;
    double height;

    // Конструктор
    TriangularPrism(double a, double b, double c, double h) {
        sideA = a;
        sideB = b;
        sideC = c;
        height = h;
    }


    double getArea() {
        double P = sideA + sideB + sideC;
        double S = P * height;
        return S;
    }


    double getVolume() {
        double areaBase = getAreaOfTriangle();
        return areaBase * height;
    }

    // сп формулу из геоиетрии
    double getAreaOfTriangle() {
        double p_2 = (sideA + sideB + sideC) / 2;
        return Math.sqrt(p_2 * (p_2 - sideA) * (p_2 - sideB) * (p_2 - sideC));
    }
}


public class Prism {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);


        try {
            System.out.print("Введите сторону A : ");
            double a = scanner.nextDouble();

            System.out.print("Введите сторону B : ");
            double b = scanner.nextDouble();

            System.out.print("Введите сторону C : ");
            double c = scanner.nextDouble();

            System.out.print("Введите высоту : ");
            double h = scanner.nextDouble();

            // существует ли такой треугольник
            if (a + b > c && a + c > b && b + c > a) {
                TriangularPrism prism = new TriangularPrism(a, b, c, h);


                System.out.println("Площадь боковой поверхности: " + prism.getArea());
                System.out.println("Объем призмы: " + prism.getVolume());
                System.out.println("Площадь основания : " + prism.getAreaOfTriangle());
            } else {
                System.out.println("не треугольник");
            }

        } catch (Exception e) {
            System.out.println(" введите числ");
        }

        scanner.close();
    }
}
