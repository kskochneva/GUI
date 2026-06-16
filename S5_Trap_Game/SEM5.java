import java.util.Random;
import java.util.Scanner;

public class SEM5 {


    static int N;//строки
    static int M;//столбцы
    static int trapPercent;
    static int K;//когда меняется ловушки
    static int trapCount;
    static int totalCells;

    static char[][] display;//таблица
    static boolean[][] isTrap;
    static boolean[][] isOpen;
    static int moves = 0;
    static int safeCellsOpened = 0;
    static Random rand = new Random();
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.print("Введите количество строк (N): ");
        N = scanner.nextInt();

        System.out.print("Введите количество столбцов (M): ");
        M = scanner.nextInt();

        System.out.print("Введите процент ловушек (10 или 20): ");
        trapPercent = scanner.nextInt();

        System.out.print("Через сколько ходов перемещать ловушки (K): ");
        K = scanner.nextInt();

        totalCells = N * M;
        trapCount = totalCells * trapPercent / 100;

        if (trapCount >= totalCells) {
            trapCount = totalCells - 1;
            System.out.println(" слишком много ловушек ");
        }

        display = new char[N][M];
        isTrap = new boolean[N][M];
        isOpen = new boolean[N][M];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                display[i][j] = '#';//закрытпя клетка
            }
        }

        placeTrapsRandomly();

        while (true) {

            printField();

            System.out.print("\nВвести координаты: ");
            int row = scanner.nextInt();
            int col = scanner.nextInt();

            if (row < 0 || row >= N || col < 0 || col >= M) {
                System.out.println("Неправильные координаты ");
                continue;
            }

            if (isOpen[row][col]) {
                System.out.println("Эта клетка уже открыта");
                continue;
            }

            if (isTrap[row][col]) {
                System.out.println("\nЛовушка");
                revealAllTraps();
                printField();
                break;
            }

            openCell(row, col);
            safeCellsOpened++;
            moves++;

            int safeCellsTotal = totalCells - trapCount;
            if (safeCellsOpened == safeCellsTotal) {
                System.out.println("\n ПОБЕДА! ");
                printField();
                break;
            }

            if (moves % K == 0) {
                System.out.println("\n ЛОВУШКИ ПЕРЕМЕСТИЛИСЬ! ");
                moveTraps();
                updateAllOpenCells();
            }
        }

        scanner.close();
    }

    static void placeTrapsRandomly() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                isTrap[i][j] = false;
            }
        }

        int placed = 0;
        while (placed < trapCount) {
            int row = rand.nextInt(N);
            int col = rand.nextInt(M);
            if (!isTrap[row][col]) {
                isTrap[row][col] = true;
                placed++;
            }
        }
    }

    static void openCell(int row, int col) {
        int count = countAdjacentTraps(row, col);
        display[row][col] = (char) (count + '0');
        isOpen[row][col] = true;
    }
    //показать где ловушки рядом
    static int countAdjacentTraps(int row, int col) {
        int count = 0;
        //создаем окно 3 на 3 вокрг клетки
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                //пропускаем саму клетку
                int newRow = row + i;
                int newCol = col + j;
                //убеждаемся что соседняя клетка существет не выходит за пределы поля
                if (newRow >= 0 && newRow < N && newCol >= 0 && newCol < M) {
                    if (isTrap[newRow][newCol]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    static void moveTraps() {
        // Случайно выбираем клетки для новых ловушек
        boolean[][] newTraps = new boolean[N][M];
        int newTrapsPlaced = 0;

        while (newTrapsPlaced < trapCount) {

            int row = rand.nextInt(N);
            int col = rand.nextInt(M);
            if (!isOpen[row][col] && !newTraps[row][col]) {
                newTraps[row][col] = true;
                newTrapsPlaced++;
                // Ставим ловушку во временный массив newTraps
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                isTrap[i][j] = newTraps[i][j];// Переносит данные из временного массива в основной
            }
        }
    }

    static void updateAllOpenCells() {
       //Обновляет цифры на ВСЕХ уже открытых клетках после того, как ловушки переместились
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (isOpen[i][j]) {
                    //У открытых клеток в display остались старые цифры
                    //Нужно пересчитать эти цифры с учётом НОВОГО расположения ловушек
                    int count = countAdjacentTraps(i, j);
                    display[i][j] = (char) (count + '0');
                }
            }
        }
    }

    static void printField() {
        System.out.println("\nТекущее поле:");

        System.out.print("    ");
        for (int j = 0; j < M; j++) {
            System.out.print(j + "   ");
        }
        System.out.println();

        System.out.print("   ");
        for (int j = 0; j < M; j++) {
            System.out.print("----");
        }
        System.out.println();

        for (int i = 0; i < N; i++) {
            System.out.print(i + " | ");
            for (int j = 0; j < M; j++) {
                System.out.print(display[i][j] + " | ");
            }
            System.out.println();

            System.out.print("   ");
            for (int j = 0; j < M; j++) {
                System.out.print("----");
            }
            System.out.println();
        }

        System.out.println("Ходов сделано: " + moves);
        System.out.println("Безопасных клеток осталось: " + ((totalCells - trapCount) - safeCellsOpened));
    }

    static void revealAllTraps() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (isTrap[i][j]) {
                    display[i][j] = '*';
                } else if (!isOpen[i][j]) {
                    display[i][j] = '#';
                }
            }
        }
    }
}
