package com.example.metro.ui;

import com.example.metro.model.Station;
import com.example.metro.model.Line;
import com.example.metro.model.Connection;
import com.example.metro.service.MetroService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class MetroMapPanel extends JPanel {

    private MetroService metroService;
    private List<Station> allStations;
    private List<Line> allLines;
    private List<Connection> allConnections;
    private List<Integer> currentPath;
    private Station selectedFrom;
    private Station selectedTo;
    private boolean isSelectionMode = false;
    private int selectionStep = 0;

    public MetroMapPanel(MetroService metroService) {
        this.metroService = metroService;
        this.currentPath = new ArrayList<>();
        loadData();
        setupMouseListener();
        setPreferredSize(new Dimension(800, 750));
        setBackground(Color.WHITE);
    }

    private void loadData() {
        this.allStations = metroService.getAllStations();
        this.allLines = metroService.getAllLines();
        this.allConnections = metroService.getAllConnections();
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isSelectionMode) return;
                Station clicked = findStationAt(e.getX(), e.getY());
                if (clicked == null) return;

                if (selectionStep == 0) {
                    selectedFrom = clicked;
                    selectionStep = 1;
                    repaint();
                } else if (selectionStep == 1) {
                    selectedTo = clicked;
                    selectionStep = 0;
                    isSelectionMode = false;
                    buildRoute(selectedFrom, selectedTo);
                    repaint();
                }
            }
        });
    }

    private Station findStationAt(int x, int y) {
        for (Station s : allStations) {
            int dx = Math.abs((int) s.pos_x - x);
            int dy = Math.abs((int) s.pos_y - y);
            if (dx <= 15 && dy <= 15) return s;
        }
        return null;
    }

    private void buildRoute(Station from, Station to) {
        if (from == null || to == null) return;
        if (from.id == to.id) {
            currentPath = Arrays.asList(from.id);
            return;
        }

        Map<Integer, Integer> parent = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(from.id);
        visited.add(from.id);
        parent.put(from.id, null);

        while (!queue.isEmpty()) {
            int curr = queue.poll();
            if (curr == to.id) {
                currentPath = new ArrayList<>();
                Integer step = to.id;
                while (step != null) {
                    currentPath.add(0, step);
                    step = parent.get(step);
                }
                return;
            }

            for (Connection conn : allConnections) {
                int next = -1;
                if (conn.station_from == curr) next = conn.station_to;
                else if (conn.station_to == curr) next = conn.station_from;

                if (next != -1 && !visited.contains(next)) {
                    visited.add(next);
                    parent.put(next, curr);
                    queue.add(next);
                }
            }
        }
        currentPath = new ArrayList<>();
    }

    private Station getStationById(int id) {
        for (Station s : allStations) {
            if (s.id == id) return s;
        }
        return null;
    }

    private boolean isStationOnPath(Station s) {
        if (currentPath == null || currentPath.isEmpty()) return false;
        if (currentPath.contains(s.id)) return true;

        for (int pathId : currentPath) {
            Station pathStation = getStationById(pathId);
            if (pathStation != null && pathStation.pos_x == s.pos_x && pathStation.pos_y == s.pos_y) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Рисуем линии
        for (Line line : allLines) {
            Color color = Color.decode(line.color_hex);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(3));

            for (Connection conn : allConnections) {
                Station s1 = getStationById(conn.station_from);
                Station s2 = getStationById(conn.station_to);
                if (s1 == null || s2 == null) continue;
                if (s1.line_id == line.id && s2.line_id == line.id) {
                    g2d.drawLine((int) s1.pos_x, (int) s1.pos_y, (int) s2.pos_x, (int) s2.pos_y);
                }
            }
        }

        // Рисуем маршрут
        if (currentPath != null && currentPath.size() > 1) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(5));
            for (int i = 0; i < currentPath.size() - 1; i++) {
                Station s1 = getStationById(currentPath.get(i));
                Station s2 = getStationById(currentPath.get(i + 1));
                if (s1 != null && s2 != null) {
                    g2d.drawLine((int) s1.pos_x, (int) s1.pos_y, (int) s2.pos_x, (int) s2.pos_y);
                }
            }
        }

        // Определяем для каждой линии, какие станции будут с подписью сверху, какие снизу
        Map<Integer, Integer> labelOffset = new HashMap<>();

        for (Line line : allLines) {
            if (line.id == 1) continue;

            List<Station> lineStations = new ArrayList<>();
            for (Station s : allStations) {
                if (s.line_id == line.id) {
                    lineStations.add(s);
                }
            }

            boolean isHorizontal = true;
            if (lineStations.size() > 1) {
                double avgX = 0, avgY = 0;
                for (Station s : lineStations) {
                    avgX += s.pos_x;
                    avgY += s.pos_y;
                }
                avgX /= lineStations.size();
                avgY /= lineStations.size();

                double varX = 0, varY = 0;
                for (Station s : lineStations) {
                    varX += Math.pow(s.pos_x - avgX, 2);
                    varY += Math.pow(s.pos_y - avgY, 2);
                }
                isHorizontal = varX > varY;
            }

            if (isHorizontal) {
                lineStations.sort(Comparator.comparingDouble(s -> s.pos_x));
            } else {
                lineStations.sort(Comparator.comparingDouble(s -> s.pos_y));
            }

            for (int i = 0; i < lineStations.size(); i++) {
                Station s = lineStations.get(i);
                if (!labelOffset.containsKey(s.id)) {
                    if (isHorizontal) {
                        labelOffset.put(s.id, i % 2 == 0 ? 0 : 1);
                    } else {
                        labelOffset.put(s.id, i % 2 == 0 ? 2 : 3);
                    }
                }
            }
        }

        // Список станций, которые показываем ТОЛЬКО на кольце
        String[] onlyCircleNames = {
                "Kievskaya", "Krasnopresnenskaya", "Kurskaya",
                "Taganskaya", "Komsomolskaya", "Belorusskaya",
                "Novoslobodskaya", "Prospekt Mira"
        };

        // Рисуем станции
        for (Station s : allStations) {
            boolean inPath = isStationOnPath(s);
            int radius = inPath ? 10 : 7;

            // Рисуем кружок станции
            if (inPath) {
                g2d.setColor(Color.RED);
                g2d.fillOval((int) s.pos_x - radius, (int) s.pos_y - radius, radius * 2, radius * 2);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval((int) s.pos_x - radius, (int) s.pos_y - radius, radius * 2, radius * 2);
            } else {
                Line line = getLineById(s.line_id);
                g2d.setColor(line != null ? Color.decode(line.color_hex) : Color.GRAY);
                g2d.fillOval((int) s.pos_x - radius, (int) s.pos_y - radius, radius * 2, radius * 2);
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawOval((int) s.pos_x - radius, (int) s.pos_y - radius, radius * 2, radius * 2);
            }

            // Рисуем подпись
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));

            int offsetX = 0, offsetY = 0;
            Integer offset = labelOffset.get(s.id);

            if (s.line_id == 1) {
                double angle = Math.atan2(s.pos_y - 400, s.pos_x - 400);
                offsetX = (int)(Math.cos(angle) * 22);
                offsetY = (int)(Math.sin(angle) * 22);
                if (Math.abs(offsetY) > Math.abs(offsetX)) {
                    offsetX = 0;
                    offsetY = offsetY > 0 ? 20 : -10;
                } else {
                    offsetY = 0;
                    offsetX = offsetX > 0 ? 20 : -20;
                }
            } else if (offset != null) {
                switch (offset) {
                    case 0: offsetX = 0; offsetY = -12; break;
                    case 1: offsetX = 0; offsetY = 18; break;
                    case 2: offsetX = 14; offsetY = -4; break;
                    case 3: offsetX = -14; offsetY = -4; break;
                    default: offsetX = 12; offsetY = -4;
                }
            } else {
                offsetX = 12;
                offsetY = -4;
            }

            // Проверяем: если станция есть на кольце и ветке — подпись ТОЛЬКО на кольце
            boolean isDuplicate = false;
            for (String name : onlyCircleNames) {
                if (s.name.equals(name) && s.line_id != 1) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                g2d.drawString(s.name, (int) s.pos_x + offsetX, (int) s.pos_y + offsetY);
            }
        }

        // Подсказки
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        if (isSelectionMode && selectionStep == 0) {
            g2d.drawString("Выберите начальную станцию", 20, 30);
        } else if (isSelectionMode && selectionStep == 1) {
            g2d.drawString("Выберите конечную станцию", 20, 30);
        } else if (selectedFrom != null && selectedTo != null) {
            g2d.drawString("Маршрут: " + selectedFrom.name + " → " + selectedTo.name, 20, 30);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString("Кликните по карте для нового маршрута", 20, 50);
        }
    }

    private Line getLineById(int id) {
        for (Line line : allLines) {
            if (line.id == id) return line;
        }
        return null;
    }

    public void startRouteSelection() {
        isSelectionMode = true;
        selectionStep = 0;
        selectedFrom = null;
        selectedTo = null;
        currentPath = new ArrayList<>();
        repaint();
    }

    public void clearRoute() {
        currentPath = new ArrayList<>();
        selectedFrom = null;
        selectedTo = null;
        isSelectionMode = false;
        selectionStep = 0;
        repaint();
    }

    public void refreshData() {
        loadData();
        repaint();
    }
}
