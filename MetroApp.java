import javax.swing.*;      // библиотека для создания окон, кнопок, панелей
import java.awt.*;         // для рисования графики
import java.util.*;        // списки, карты, очереди
import java.util.List;     // интерфейс List (список)

// extends JFrame - наследуемся от JFrame,  приложение будет окном
public class MetroApp extends JFrame {
    private DataBase db;
    private List<Station> stations;
    private Map<Integer, List<Integer>> graph;           // от станции → список соседей
    private MetroMapPanel mapPanel;                      // панель для рисования карты
    private JComboBox<Station> startCombo;               // выпадающий список "Откуда"
    private JComboBox<Station> endCombo;                 // выпадающий список "Куда"
    private JTextArea routesArea;                        // куда выводится маршрут

    // конструктоа
    public MetroApp() {
        setTitle("Moscow Metro - Route Finder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);   // при закрытии окна программа завершается
        setSize(1200, 650);
        setLayout(new BorderLayout());                    // расположение: Север, Юг, Запад, Восток, Центр

        // Загрузка данных из БД
        db = new DataBase();
        stations = db.getAllStations();
        graph = db.getConnections();


        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(240, 240, 240));

        // откуда"
        topPanel.add(new JLabel("From:"));

        //  список "Откуда"
        startCombo = new JComboBox<>(stations.toArray(new Station[0]));
        startCombo.setPreferredSize(new Dimension(220, 28));
        startCombo.setFont(new Font("Dialog", Font.PLAIN, 12));
        topPanel.add(startCombo);

        // Надпись "To:"
        topPanel.add(new JLabel("To:"));

        // Выпадающий список "Куда"
        endCombo = new JComboBox<>(stations.toArray(new Station[0]));
        endCombo.setPreferredSize(new Dimension(220, 28));
        endCombo.setFont(new Font("Dialog", Font.PLAIN, 12));
        topPanel.add(endCombo);

        // Кнопка "Find Route"
        JButton findBtn = new JButton("Find Route");
        findBtn.setBackground(new Color(0, 120, 215));
        findBtn.setForeground(Color.WHITE);
        findBtn.setFont(new Font("Dialog", Font.BOLD, 12));
        findBtn.setPreferredSize(new Dimension(120, 28));
        findBtn.addActionListener(e -> findRoute());
        // при нажатии ищем маршрут
        topPanel.add(findBtn);

        add(topPanel, BorderLayout.NORTH);              // размещаем верхнюю панель

        // тут маршруты
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(380, 0));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Route Details"));

        routesArea = new JTextArea();
        routesArea.setEditable(false);                   // нельзя редактировать
        routesArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        routesArea.setBackground(new Color(252, 252, 252));
        //Оборачиваем текстовое поле в полосу прокрутки (чтобы можно было скроллить длинный маршрут) и кладём в центр левой панели
        leftPanel.add(new JScrollPane(routesArea), BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);  // размещаем левую панель

        //  Карта метро (центр)
        mapPanel = new MetroMapPanel(stations, graph);
        JScrollPane scrollPane = new JScrollPane(mapPanel);
        //Оборачиваем карту в полосы прокрутки (карта может быть больше окна).
        scrollPane.setBorder(BorderFactory.createTitledBorder("Metro Scheme"));
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);                     // окно по центру экрана
    }

    //сокращаем для красоты на карте так как насловнеи
    private String shortenName(String name) {
        Map<String, String> shortNames = new HashMap<>();
        shortNames.put("Krasnopresnenskaya", "Krasnopresn.");
        shortNames.put("Komsomolskaya", "Komsomolsk.");
        shortNames.put("Novoslobodskaya", "Novoslobod.");
        shortNames.put("Belorusskaya", "Belorussk.");
        shortNames.put("Mendeleevskaya", "Mendeleev.");
        shortNames.put("Alekseevskaya", "Alekseev.");
        shortNames.put("Izmailovskaya", "Izmailov.");
        shortNames.put("Schyolkovskaya", "Schyolkov.");
        shortNames.put("Maryina Roscha", "Maryina R.");
        shortNames.put("Prospekt Mira", "Prospekt M.");
        shortNames.put("Ploschad Revolutsii", "Ploschad R.");
        shortNames.put("Ulitsa 1905 Goda", "Ulitsa 1905");
        shortNames.put("Park Kultury", "Park Kult.");
        shortNames.put("Chistye Prudy", "Chistye P.");
        shortNames.put("Smolenskaya", "Smolensk.");
        shortNames.put("Taganskaya", "Tagansk.");
        shortNames.put("Kurskaya", "Kursk.");
        shortNames.put("Kievskaya", "Kievsk.");
        shortNames.put("Pushkinskaya", "Pushkinsk.");
        shortNames.put("Tverskaya", "Tversk.");
        shortNames.put("Arbatskaya", "Arbatsk.");
        shortNames.put("Dinamo", "Dinamo");
        shortNames.put("Lubyanka", "Lubyanka");
        shortNames.put("Vykhino", "Vykhino");
        shortNames.put("Dostoevskaya", "Dostoevsk.");
        return shortNames.getOrDefault(name, name);
    }

    // поиса маршрута при нажании кнопки
    private void findRoute() {
        //Берём выбранные станции из выпадающих списков
        Station start = (Station) startCombo.getSelectedItem();
        Station end = (Station) endCombo.getSelectedItem();

        // Проверка: выбраны ли станции
        if (start == null || end == null) {
            routesArea.setText("Error: select both stations!");
            return;
        }

        // Проверка: разные ли станции
        if (start.id == end.id) {
            routesArea.setText("Start and end stations are the same!");
            return;
        }

        // Ищем до 3 разных путей
        List<List<Integer>> allPaths = db.findMultiplePaths(start.id, end.id, graph);

        if (allPaths.isEmpty()) {
            routesArea.setText("Route not found!");
            return;
        }


        // как блокнот, куда  маршрут по кусочкам, а потом  весь текст сразу
        //для экономии памяти вместо простого +
        //Создаём конструктор строк. Это эффективный способ собирать длинный текст.
        StringBuilder sb = new StringBuilder();

        // Выводим все найденные пути
        //Цикл по всем найденным путя
        for (int p = 0; p < allPaths.size(); p++) {
            List<Integer> path = allPaths.get(p);

            //Чтобы визуально разделять разные маршруты в текстовом поле
            sb.append("\n").append("=".repeat(45)).append("\n");
            sb.append("  ROUTE ").append(p + 1);
            if (p == 0) sb.append("  [SHORTEST - RECOMMENDED]");
            sb.append("\n").append("=".repeat(45)).append("\n\n");

            //Счётчик пересадок. Начинаем с нуля
            int transfers = 0;
            for (int i = 0; i < path.size(); i++) {
                Station s = db.getStationById(path.get(i), stations);
                sb.append("  - ").append(s.name);

                // Если следующая станция на другой линии → пересадка
                if (i < path.size() - 1) {
                    Station next = db.getStationById(path.get(i + 1), stations);
                    if (s.lineId != next.lineId) {
                        sb.append("\n      TRANSFER to ").append(next.lineName);
                        transfers++;
                    }
                }
                sb.append("\n");
            }

            // Статистика маршрута
            sb.append("\n  Stations: ").append(path.size());
            sb.append("  |   Transfers: ").append(transfers);
            sb.append("  |  ️ Time: ~").append(path.size() * 2).append(" min\n");
        }

        //Берём  текст и показываем его в левой панели
        routesArea.setText(sb.toString());

        // Подсвечиваем самый короткий путь (первый)
        if (!allPaths.isEmpty()) {
            //передаём карте первый путь (самый короткий)
            mapPanel.setPath(allPaths.get(0));
        }
    }

    // рисуем карту
    class MetroMapPanel extends JPanel {
        private List<Station> stations;//копия списка станций
        private Map<Integer, List<Integer>> connections;//копия графа связей
        private List<Integer> currentPath = null;//текущий маршрут (который надо подсветить)

        //Конструктор панели
        public MetroMapPanel(List<Station> stations, Map<Integer, List<Integer>> connections) {
            this.stations = stations;
            this.connections = connections;
            setPreferredSize(new Dimension(900, 600));
            setBackground(new Color(248, 248, 248));
        }

        // Устанавливаем путь для подсветки
        //Когда извне передают новый маршрут,
        // мы его сохраняем и вызываем repaint()
        public void setPath(List<Integer> path) {
            this.currentPath = path;
            repaint();   // перерисовываем карту
        }

        // Найти станцию по ID
        private Station getStationById(int id) {
            for (Station s : stations) {
                if (s.id == id) return s;
            }
            return null;
        }

        // Рисование карты (самый важный метод)
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;//умеет менять толщину линий, цвета
            //сглаживание
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // линии
            Set<String> drawnLines = new HashSet<>();
            // чтобы не рисовать одну линию дважды
            //Создаём множество уже нарисованных линий
            for (var entry : connections.entrySet()) {
                //connections — это  словарь, где
                //Ключ — ID станции
                //Значение — список ID соседей (с кем эта станция соединена)
                Station st1 = getStationById(entry.getKey());
                if (st1 == null) continue;
                //Метод entrySet() возвращает все пары (ключ + значение) из карты.

                //Цикл по всем соседям текущей станции.
                // Для каждого соседа ищем объект Station.
                for (int s2Id : entry.getValue()) {
                    Station st2 = getStationById(s2Id);
                    if (st2 == null) continue;

                    // Уникальный ключ для линии
                    //Создаём уникальный ключ для пары станций ( "3-5").
                    // Если такую линию уже рисовали — пропускаем.
                    String key = Math.min(entry.getKey(), s2Id) + "-" + Math.max(entry.getKey(), s2Id);
                    if (drawnLines.contains(key)) continue;
                    drawnLines.add(key);

                    // Проверяем, входит ли линия в маршрут
                    //Для этого обе станции должны быть в текущем пути.
                    boolean inPath = currentPath != null &&
                            currentPath.contains(entry.getKey()) &&
                            currentPath.contains(s2Id);
                    //Входит ли первая станция (из которой идёт линия) в маршру
                    //Входит ли вторая станция (в которую идёт линия) в маршрут
                    //Если ДА  значит, эта линия соединяет две станции,
                    // которые обе есть в маршруте  линия на маршруте
                    //Если все три условия истинны линия будет  КРАСНОЙ и ТОЛСТОЙ.
                    //currentPath — это список ID станций, которые входят в найденный маршрут.
                    //entry.getKey() — ID текущей станции (откуда идём
                    //s2Id — ID соседней станции (куда идём)

                    if (inPath) {
                        g2.setColor(new Color(255, 50, 50));   // красный для маршрута
                        g2.setStroke(new BasicStroke(5));       // толстая линия
                    } else {
                        try {
                            g2.setColor(Color.decode("#" + st1.lineColor));  // цвет линии из БД
                            g2.setStroke(new BasicStroke(3));
                        } catch (Exception e) {
                            g2.setColor(Color.GRAY);
                        }
                        //Если линия на маршруте: красный цвет, толщина 5 пикселей
                    }
                    g2.drawLine(st1.x, st1.y, st2.x, st2.y);
                    // Рисуем линию от координат первой станции до координат второй
                }
            }

            // рисуем станции если входит в маршрут
            for (Station s : stations) {
                boolean inPath = currentPath != null && currentPath.contains(s.id);

                // Подсветка станций на маршруте
                if (inPath) {
                    g2.setColor(new Color(255, 80, 80));
                    g2.setStroke(new BasicStroke(4));
                    g2.drawOval(s.x - 14, s.y - 14, 28, 28);
                }

                // Закрашенный кружок цветом линии
                try {
                    g2.setColor(Color.decode("#" + s.lineColor));
                } catch (Exception e) {
                    g2.setColor(Color.GRAY);
                }
                g2.fillOval(s.x - 9, s.y - 9, 18, 18);

                // Чёрная обводка
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawOval(s.x - 9, s.y - 9, 18, 18);

                // Золотой ободок для станций кольцевой линии
                if (s.isCircle) {
                    g2.setColor(new Color(255, 200, 0));
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(s.x - 13, s.y - 13, 26, 26);
                }

                //названия станций
                String displayName = shortenName(s.name);        // сокращаем длинные названия
                boolean labelAbove = (s.id % 2 == 0);           // чётный ID -> над, нечётный -> под

                g2.setFont(new Font("Dialog", Font.PLAIN, 9));
                int textWidth = g2.getFontMetrics().stringWidth(displayName);

                if (labelAbove) {
                    // Название НАД кружком
                    g2.setColor(new Color(255, 255, 255, 230));
                    g2.fillRect(s.x - textWidth / 2 - 3, s.y - 22, textWidth + 6, 13);
                    g2.setColor(new Color(180, 180, 180));
                    g2.drawRect(s.x - textWidth / 2 - 3, s.y - 22, textWidth + 6, 13);
                    g2.setColor(Color.BLACK);
                    g2.drawString(displayName, s.x - textWidth / 2, s.y - 12);
                } else {
                    // Название ПОД кружком
                    g2.setColor(new Color(255, 255, 255, 230));
                    g2.fillRect(s.x - textWidth / 2 - 3, s.y + 10, textWidth + 6, 13);
                    g2.setColor(new Color(180, 180, 180));
                    g2.drawRect(s.x - textWidth / 2 - 3, s.y + 10, textWidth + 6, 13);
                    g2.setColor(Color.BLACK);
                    g2.drawString(displayName, s.x - textWidth / 2, s.y + 20);
                }
            }
        }
    }

    // началао работы программы
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MetroApp().setVisible(true));
    }
    //SwingUtilities.invokeLater — запускаем код в спец потоке, чтобы не было конфликтов
}