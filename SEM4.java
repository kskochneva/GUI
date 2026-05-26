import java.util.*;
//Алгоритм Дейкстры находит кратчайшие пути от  вершины  до всех остальных

public class SEM4 {
    //static - метод принадлежит классу, можно вызвать без создания объекта
    public static void main(String[] args) {
        int n = 4; // кол-во комнат

        // для каждой вершины храним список пар соседей и весов
        List<List<int[]>> graph = new ArrayList<>();
        //смоздаю граф набор точек и коридоров
        //[номер соседа ; вес коридора]
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        // Добавляем рёбра (u, v, w)
        // откуда куда вес
        addEdge(graph, 0, 1, 4); // му 0 и 1 с длиной 4
        addEdge(graph, 0, 2, 1);
        addEdge(graph, 1, 3, 1);
        addEdge(graph, 2, 1, 2);
        addEdge(graph, 2, 3, 5);

        int start = 0;//начало пути
        int end = 3;//тут сокровище

        int result = dijkstra(graph, n, start, end);
        //полуичли длину кратчайшего пути
        //макс вал - макс возможное значни int = бесконечность

        if (result == Integer.MAX_VALUE) {
            System.out.println("Сокровище недостижимо");
        } else {
            System.out.println("Длина кратчайшего пути: " + result);
        }
    }

    // доб ребра0
    //метод принадлежит классу, можно вызвать без создания объекта
    //метод ничего не возвращает, просто делает действие
    static void addEdge(List<List<int[]>> graph, int u, int v, int w) {
        graph.get(u).add(new int[]{v, w});
        graph.get(v).add(new int[]{u, w});//возьми ячейку под номером u
    }//коридор можно проходить в ОБЕ стороны
    //graph[0] = [ [1,4], [2,1] ]   → Из комнаты 0 можно пойти в 1 (длина 4) и в 2 (длина 1)
    static int dijkstra(List<List<int[]>> graph, int n, int start, int end) {
        // Массив минимальных расстояний
        //таблица, где для каждой комнаты записано, какое сейчас известно минимальное расстояние от старта до этой комнаты
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);// заполняем бесконечностью
        dist[start] = 0;// // расстояние до старта

        // Очередь с приоритетом (расстояние, вершина)
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));
        pq.offer(new int[]{0, start});// кладём стартовую вершину

        while (!pq.isEmpty()) {//пока в очереди есть вершины для обработки
            int[] current = pq.poll();//берёт первый элемент и УДАЛЯЕТ его из очереди
            int currentDist = current[0];
            int u = current[1];

            // Если нашли конечную вершину, можно завершить
            if (u == end) {
                return currentDist;
            }

            // Если в очереди устаревшая запись
            //Вершина может быть добавлена в очередь несколько раз с разными расстояниями
            if (currentDist > dist[u]) {
                continue;
            }

            // Проверяем всех соседей ТКЩЕЙ вершны
            //получаем список соседей вершины u
            for (int[] edge : graph.get(u)) {
                int v = edge[0];//Берёт номер соседней вершины
                int weight = edge[1];
                int newDist = currentDist + weight;//Вычисляет новый путь до соседа через текущую вершину

                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new int[]{newDist, v});//добавляем значение в очередь
                }
            }
        }

        return dist[end];
    }
}
