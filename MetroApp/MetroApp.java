package com.example.metro;

import com.example.metro.service.MetroService;
import com.example.metro.ui.MetroMapPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class MetroApp implements CommandLineRunner {

    @Autowired
    private MetroService metroService;

    public static void main(String[] args) {
        new SpringApplicationBuilder(MetroApp.class)
                .headless(false)
                .run(args);
    }

    @Override
    public void run(String... args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Московское метро");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 800);
            frame.setLocationRelativeTo(null);

            MetroMapPanel mapPanel = new MetroMapPanel(metroService);

            JPanel buttonPanel = new JPanel();
            JButton findBtn = new JButton("Найти маршрут");
            findBtn.addActionListener(e -> mapPanel.startRouteSelection());

            JButton clearBtn = new JButton("Очистить");
            clearBtn.addActionListener(e -> mapPanel.clearRoute());

            JButton refreshBtn = new JButton("Обновить");
            refreshBtn.addActionListener(e -> mapPanel.refreshData());

            buttonPanel.add(findBtn);
            buttonPanel.add(clearBtn);
            buttonPanel.add(refreshBtn);

            frame.add(mapPanel, BorderLayout.CENTER);
            frame.add(buttonPanel, BorderLayout.SOUTH);
            frame.setVisible(true);
        });
    }
}