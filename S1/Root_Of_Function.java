public class Root_Of_Function {
    //сначала границы
    //сужаем интервал
    public static void main(String[] args) {
    double left = 0;

    // Ищем отрицательное значение слева
    while (left*left*left + left + 1 >= 0) {
    left = left - 1;
}

double right = left;

// Ищем положительное значение справа
while (right*right*right + right + 1 <= 0) {
    right = right + 1;
}

// Делим пополам 100 раз
for (int i = 0; i < 100; i++) {
    double mid = (left + right) / 2;
    double val = mid*mid*mid + mid + 1;

    if (val < 0) {
        left = mid;
    } else {
        right = mid;
    }
}

System.out.println("Корень: " + (left + right) / 2);
}
}
