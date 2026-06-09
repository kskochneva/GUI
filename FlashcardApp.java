package com.flashcard;

import javax.swing.*;// для окон, кнопок, надписей.
import java.awt.*;//для цветов, шрифтов, расположения элементов.
import java.util.ArrayList;//куда можно складывать карточки.
import java.util.List;

//приложение будет окном.
public class FlashcardApp extends JFrame {

    private List<Flashcard> flashcards;
    private int currentIndex = 0;
    private boolean showingAnswer = false;

    //Метка, на которой будет текст вопроса
    private JLabel cardLabel;
    //кнопка и счетски
    private JButton showAnswerBtn;
    private JButton nextBtn;
    private JLabel counterLabel;

    //вызывается один раз и открывает окно при ложения
    public FlashcardApp() {
        setTitle("Flashcards - Учи слова");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);//по центру
        setLayout(new BorderLayout());//располложение элементов

        // Создаём карточки
        createFlashcards();

        // Верхняя панель (голубая)
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(74, 144, 226));
        JLabel titleLabel = new JLabel(" FLASHCARDS - УЧИ СЛОВА ");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        topPanel.add(titleLabel);
        add(topPanel, BorderLayout.NORTH);

        // Центр - карточка
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(245, 245, 245));

        cardLabel = new JLabel("", SwingConstants.CENTER);
        cardLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        cardLabel.setPreferredSize(new Dimension(400, 150));
        cardLabel.setBackground(Color.WHITE);
        cardLabel.setOpaque(true);
        cardLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(74, 144, 226), 2),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        cardLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Когда пользователь кликает на карточку,
        // вызывается метод toggleAnswer() (перевернуть карточку)
        cardLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                toggleAnswer();
            }
        });

        centerPanel.add(cardLabel);
        add(centerPanel, BorderLayout.CENTER);

        // Нижняя панель с кнопками
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));

        showAnswerBtn = new JButton("Показать ответ");
        showAnswerBtn.setBackground(new Color(74, 144, 226));
        showAnswerBtn.setForeground(Color.WHITE);
        showAnswerBtn.setFont(new Font("Arial", Font.BOLD, 14));
        showAnswerBtn.setPreferredSize(new Dimension(150, 40));
        showAnswerBtn.addActionListener(e -> toggleAnswer());

        nextBtn = new JButton("Следующая ");
        nextBtn.setBackground(new Color(39, 174, 96));
        nextBtn.setForeground(Color.WHITE);
        nextBtn.setFont(new Font("Arial", Font.BOLD, 14));
        nextBtn.setPreferredSize(new Dimension(180, 40));
        nextBtn.addActionListener(e -> nextCard());

        //добавляем кнопки на панель
        buttonPanel.add(showAnswerBtn);
        buttonPanel.add(nextBtn);

        counterLabel = new JLabel("", SwingConstants.CENTER);
        counterLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        counterLabel.setForeground(Color.GRAY);
        counterLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(counterLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        updateCard();
    }

    private void createFlashcards() {
        flashcards = new ArrayList<>();
        flashcards.add(new Flashcard("Что такое Java?", "Язык программирования"));
        flashcards.add(new Flashcard("Что такое JDK?", "Java Development Kit"));
        flashcards.add(new Flashcard("Что такое JVM?", "Java Virtual Machine"));
        flashcards.add(new Flashcard("Что такое переменная?", "Контейнер для данных"));
        flashcards.add(new Flashcard("Что такое массив?", "Коллекция элементов одного типа"));
        flashcards.add(new Flashcard("Что такое цикл for?", "Повторяет код несколько раз"));
        flashcards.add(new Flashcard("Что такое метод?", "Блок кода, который можно вызвать"));
        flashcards.add(new Flashcard("Что такое класс?", "Шаблон для создания объектов"));
        flashcards.add(new Flashcard("Что такое объект?", "Экземпляр класса"));
        flashcards.add(new Flashcard("Что такое наследование?", "Класс получает свойства другого"));
    }

    private void updateCard() {
        if (flashcards.isEmpty()) return;

        Flashcard current = flashcards.get(currentIndex);
        // Берём текущую карточку по индексу.

        if (!showingAnswer) {
            cardLabel.setText("<html><center>" + current.getQuestion() + "</center></html>");
            showAnswerBtn.setText("Показать ответ");
        } else {
            cardLabel.setText("<html><center>" + current.getAnswer() + "</center></html>");
            showAnswerBtn.setText("Скрыть ответ");
        }

        counterLabel.setText((currentIndex + 1) + " из " + flashcards.size());
    }
    //Меняем флаг showingAnswer на противоположный и обновляем карточку.
    private void toggleAnswer() {
        if (flashcards.isEmpty()) return;
        showingAnswer = !showingAnswer;
        updateCard();
    }


    //Переходим к следующему индексу
    //% flashcards.size() — если дошли до конца, начинаем сначала
    //Сбрасываем флаг ответа
    //Обновляем карточку
    private void nextCard() {
        if (flashcards.isEmpty()) return;
        currentIndex = (currentIndex + 1) % flashcards.size();
        showingAnswer = false;
        updateCard();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FlashcardApp().setVisible(true));
    }
}