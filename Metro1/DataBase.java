import java.sql.*;//для работы с бд
import java.util.*;//списки очереди и тд

public class DataBase {
    //адрес бд
    private static final String URL = "jdbc:postgresql://localhost:5432/metro_db?sslmode=disable";
    //локалхост - на этом компьютере
    //константы для подключения
    //только внутри класса
    //одна на весь класс
    //нельзя изменить
    //?sslmode=disable отключаем шифрование
    private static final String USER = "postgres";
    private static final String PASSWORD = "KKsusha2805";

    private Connection conn;//переменная для подклчения

    //конструктор
    public DataBase() {
        try {
            Class.forName("org.postgresql.Driver");//загружаем драйвер для постгре
            conn = DriverManager.getConnection(URL, USER, PASSWORD);//Устанавливаем соединение с базой
            System.out.println("Подключено к БД!");
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // Получить все станции
    //етод возвращает список объектов Station
    public List<Station> getAllStations() {
        List<Station> stations = new ArrayList<>();//создаем пучтой список
        // из таблицы stations: id, название, координаты, is_circle
        //из таблицы lines: название линии и цвет
        //Соединим их по полю line_id
        String sql = "SELECT s.id, s.name, s.x, s.y, s.is_circle, " +
                "l.id as line_id, l.name as line_name, l.color " +
                "FROM stations s JOIN lines l ON s.line_id = l.id";
        try {
            Statement stmt = conn.createStatement();
            //несет наш запрос в базу
            ResultSet rs = stmt.executeQuery(sql);
            //возращенный ответ
            //пока в таблице есть следующая строка
            while (rs.next()) {
                Station st = new Station(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("line_id"),
                        rs.getString("line_name"),
                        rs.getString("color"),
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getBoolean("is_circle")
                );
                stations.add(st);
            }
            //освобождаем ресурсы
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stations;//получили список
    }

    // Получить все связи (граф)
    public Map<Integer, List<Integer>> getConnections() {
        //Возвращает карту, где:
        //Ключ → ID станции
        //Значение → список ID соседних станций
        Map<Integer, List<Integer>> graph = new HashMap<>();//Создаём пустую карту (граф)
        String sql = "SELECT station1_id, station2_id FROM connections";
        //взять все связи из таблицы connections
        try {
            //Для каждой строки берём ID двух связанных станций
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                int s1 = rs.getInt("station1_id");
                int s2 = rs.getInt("station2_id");
                //Добавляем связь в граф
                //Если у станции s1 ещё нет списка соседей — создаём пустой список
                //Добавляем s2 в список соседей s1
                // то же самое в обратную сторону (связь двусторонняя)
                graph.computeIfAbsent(s1, k -> new ArrayList<>()).add(s2);
                graph.computeIfAbsent(s2, k -> new ArrayList<>()).add(s1);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return graph;
    }


    // Ищем путь от startId до endId
    public List<Integer> findPath(int startId, int endId, Map<Integer, List<Integer>> graph) {
        Queue<List<Integer>> queue = new LinkedList<>();
        //Создаём очередь fifo
        Set<Integer> visited = new HashSet<>();
        //Множество посещённых станций

        //Начинаем с начальной станции
        // Добавляем её в очередь и отмечаем как посещённую
        List<Integer> startPath = new ArrayList<>();
        startPath.add(startId);
        queue.add(startPath);
        visited.add(startId);

        while (!queue.isEmpty()) {
            List<Integer> path = queue.poll();
            int lastNode = path.get(path.size() - 1);
            // Берём первый путь из очереди и смотрим на его последнюю станцию
            if (lastNode == endId) return path;
            // Если последняя станция — это искомая  мы нашли путь

            //Перебираем всех соседей последней станции
            for (int neighbor : graph.getOrDefault(lastNode, new ArrayList<>())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    List<Integer> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                    //Создаём новый путь = старый путь + этот сосед
                    //Добавляем новый путь в очередь
                }
            }
        }
        return null;
    }

    // варианты маршрута
    public List<List<Integer>> findMultiplePaths(int startId, int endId,
                                                 Map<Integer, List<Integer>> graph) {
        List<List<Integer>> allPaths = new ArrayList<>();
        Queue<List<Integer>> queue = new LinkedList<>();
        //allPaths — список найденных путей queue — очередь для поиска.
        List<Integer> startPath = new ArrayList<>();
        //Начинаем с начальной станции
        startPath.add(startId);
        queue.add(startPath);
        //цикл поиска
        while (!queue.isEmpty() && allPaths.size() < 3) {
            List<Integer> path = queue.poll();//Достаём первый элемент из очереди и удаляем его
            int lastNode = path.get(path.size() - 1);
            //path.size() - 1 Индекс последней станции
            //path путь от начальной станции до какой-то промежуточной
            //path.sixe Сколько станций в пути
            //lastNode ID последней станции
            //Если путь [1, 3, 5], то lastNode = 5
            if (lastNode == endId && !allPaths.contains(path)) {
                allPaths.add(new ArrayList<>(path));
                continue;
            }
            //Если мы доехали до конечной станции
            // И такого пути у нас ещё нет в списке  заходим внутрь

            for (int neighbor : graph.getOrDefault(lastNode, new ArrayList<>())) {
                if (!path.contains(neighbor)) {
                    List<Integer> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.add(newPath);
                }
            }
            //Продолжаем искать, не заходя на уже посещённые в этом пути станции.
            // Возвращаем все найденные пути.
        }
        return allPaths;
    }

    //Проходим по списку станций. Если нашли станцию с нужным ID
    // — возвращаем её. Если нет — возвращаем null.
    public Station getStationById(int id, List<Station> stations) {
        for (Station s : stations) {
            if (s.id == id) return s;
        }
        return null;
    }
    //освобождаем ресурсы
    public void close() {
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}
