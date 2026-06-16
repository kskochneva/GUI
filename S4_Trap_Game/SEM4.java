import java.util.*;
//Алгоритм Дейкстры находит кратчайшие пути от  вершины  до всех остальных

public class SEM4 {
    //static - метод принадлежит классу, можно вызвать без создания объекта
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите количество комнат: ");
        int n = scanner.nextInt();

        // Создаём таблица расстояний
        int[][] graph = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(graph[i], -1);  // -1 означает "нет пути"
        }

        System.out.print("Введите количество коридоров: ");
        int m = scanner.nextInt();

        System.out.println("Введите коридоры (откуда куда длина):");
        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            int w = scanner.nextInt();
            graph[u][v] = w;
            graph[v][u] = w;  // двусторонний
        }

        System.out.print("Введите начальную комнату: ");
        int start = scanner.nextInt();

        System.out.print("Введите комнату с сокровищем: ");
        int end = scanner.nextInt();

        scanner.close();

        // Дейкстра
        int[] dist = new int[n];//создаем массив
        boolean[] visited = new boolean[n];
        Arrays.fill(dist, Integer.MAX_VALUE);//заполянем бесконечностью
        dist[start] = 0;

        for (int i = 0; i < n; i++) {
            // Находим непосещённую вершину с минимальным расстоянием
            int u = -1;
            int minDist = Integer.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && dist[j] < minDist) {
                    minDist = dist[j];
                    u = j;
                }
            }

            if (u == -1) break;  // нет достижимых вершин
            visited[u] = true;

            // Обновляем расстояния до соседей
            for (int v = 0; v < n; v++) {
                if (graph[u][v] != -1 && dist[u] + graph[u][v] < dist[v]) {
                    dist[v] = dist[u] + graph[u][v];
                }
            }
        }

        if (dist[end] == Integer.MAX_VALUE) {
            System.out.println("Сокровище недостижимо");
        } else {
            System.out.println("Длина кратчайшего пути: " + dist[end]);
        }
    }
}
