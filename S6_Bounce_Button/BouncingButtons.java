import javax.swing.*;//для кнопок и окон
import java.awt.*;//для рисования (цвета, формы)
import java.awt.event.*;//нажатие кнопки мыши
import java.util.*;//списки, случайные числа
import java.util.List;//Объявляем переменную List<BounceBtn> buttons

//Наследуемся от JFrame → наше приложение будет окном
public class BouncingButtons extends JFrame {

    static final int MAX_BUTTONS   = 1000;
    static final int START_SIZE    = 50;//Первая кнопка будет 50×50 пикселей
    static final int MAX_SIZE      = 100;//Когда кнопка вырастет до 100×100 → взрывается
    static final int SIZE_STEP     = 6;//При каждом ударе о стену кнопка увеличивается на 6 пикселей
    static final int FPS           = 60;

    // Pastel colour pairs: {background, border/text}
    static final Color[][] PALETTES = {
            {new Color(238,237,254), new Color(127,119,221)},
            {new Color(225,245,238), new Color(29,158,117)},
            {new Color(250,236,231), new Color(216,90,48)},
            {new Color(251,234,240), new Color(212,83,126)},
            {new Color(230,241,251), new Color(55,138,221)},
            {new Color(250,238,218), new Color(186,117,23)},
    };

    private GamePanel gamePanel;

    public BouncingButtons() {
        setTitle("Bouncing Buttons");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //Когда пользователь нажимает на крестик закрытия окна, программа полностью завершается
        setResizable(false);

        gamePanel = new GamePanel();
        add(gamePanel);//Добавляем эту панель в окно
        pack();//подстроить свой размер под содержимое
        setLocationRelativeTo(null);// Центрируем окно на экране. null  "относительно ничего"
        setVisible(true);//без этого пусто й экран
    }



    static class BounceBtn {
        double x, y;//Координаты верхнего левого угла кнопки
        double vx, vy;//корость по X и Y. Может быть положительной (вправо/вниз)
        // или отрицательной (влево/вверх
        int size;
        int paletteIdx;//Номер цветовой схемы
        boolean alive = true;//При взрыве становится false

        static final Random rng = new Random();// Генератор случайных чисел

        //конструктор
        BounceBtn(double cx, double cy, int size, int paletteIdx) {
            //Переводим центр в верхний левый угол
            this.x = cx - size / 2.0;//координаты центра
            this.y = cy - size / 2.0;
            this.size = size;
            this.paletteIdx = paletteIdx % PALETTES.length;
            // остаток от деления, чтобы не выйти за пределы массива цветов

            double speed = 1.8 + rng.nextDouble() * 1.4;
            //случайны й угол
            double angle = rng.nextDouble() * Math.PI * 2;
            //разбиваем скорость
            vx = Math.cos(angle) * speed;
            vy = Math.sin(angle) * speed;
        }


        //ширина и высоты кнопки W H
        boolean move(int W, int H) {
            //овая позиция = старая + скорость
            x += vx;
            y += vy;
            //Флаг — было ли столкновение
            boolean hit = false;
            //Если кнопка вылетела за левый край
            //Прижимаем кнопку к левому краю
            //Кнопка летела влево (отрицательная скорость),
            // теперь полетит вправо (положительная).
            //Запоминаем, что было столкновение
            //и аналогично для отсльных
            if (x < 0)           { x = 0;          vx =  Math.abs(vx); hit = true; }
            if (x + size > W)    { x = W - size;   vx = -Math.abs(vx); hit = true; }
            if (y < 0)           { y = 0;           vy =  Math.abs(vy); hit = true; }
            if (y + size > H)    { y = H - size;    vy = -Math.abs(vy); hit = true; }
            if (hit) size += SIZE_STEP;
            //сли было столкновение → увеличиваем размер на 6 пикселей.
            return hit;
        }

        boolean shouldExplode() { return size >= MAX_SIZE; }

        //Возвращает центр кнопки  для взрыва и анимации
        Point center() { return new Point((int)(x + size/2), (int)(y + size/2)); }


        List<BounceBtn> explode() {
            //апоминаем центр, создаём пустой список для новых кнопок.
            alive = false;
            Point c = center();
            List<BounceBtn> kids = new ArrayList<>();
            //Размер новой кнопки
            int childSize = Math.max(START_SIZE - 8, 26);
            for (int i = 0; i < 4; i++)
                kids.add(new BounceBtn(c.x, c.y, childSize, paletteIdx + i + 1));
            return kids;
        }

        void draw(Graphics2D g2) {
            Color bg   = PALETTES[paletteIdx][0];
            Color fg   = PALETTES[paletteIdx][1];
            int ix = (int)x, iy = (int)y;

            g2.setColor(bg);
            g2.fillRoundRect(ix, iy, size, size, 10, 10);

            g2.setColor(fg);
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawRoundRect(ix, iy, size, size, 10, 10);

            //нащли шриф для кнопки
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, Math.max(10, size / 3)));
            FontMetrics fm = g2.getFontMetrics();
            String label = "💥";
            //ставим по центру
            int tx = ix + (size - fm.stringWidth(label)) / 2;
            int ty = iy + (size + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(label, tx, ty);
        }
    }


    //анимаци взрыва
    static class ExplosionRing {
        int cx, cy;
        Color color;
        long startMs;//время создания
        static final int DURATION = 420;//420мс


        ExplosionRing(int cx, int cy, Color color) {
            this.cx = cx; this.cy = cy;
            this.color = color;
            this.startMs = System.currentTimeMillis();
        }

        boolean isDone() { return System.currentTimeMillis() - startMs > DURATION; }


        //t — сколько прошло от 0 до 1.
        //r — радиус кольца (растёт со временем).
        //alpha — прозрачность (уменьшается).
        void draw(Graphics2D g2) {
            float t = (System.currentTimeMillis() - startMs) / (float) DURATION;
            int r = (int)(20 + 60 * t);
            float alpha = 1f - t;
            g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                    (int)(alpha * 200)));
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
        }
    }


    //implements ActionListener — чтобы таймер мог вызывать  код
    class GamePanel extends JPanel implements ActionListener {

        List<BounceBtn>     buttons    = new ArrayList<>();
        //rings — список анимаций взрывов.
        List<ExplosionRing> rings      = new ArrayList<>();
        javax.swing.Timer timer = new javax.swing.Timer(1000 / FPS, this);  // ← ИСПРАВЛЕНО!
        boolean             started    = false;
        int                 totalBoom  = 0;

        JButton startBtn;

        GamePanel() {
            setPreferredSize(new Dimension(900, 550));
            setBackground(new Color(245, 244, 240));
            setLayout(null);

            //при нажатии запускаем игру
            startBtn = new JButton("Нажми, чтобы начать");
            styleOverlayBtn(startBtn, "▶  Нажми, чтобы начать");
            startBtn.addActionListener(e -> startGame());
            add(startBtn);
        }

        void styleOverlayBtn(JButton btn, String text) {
            btn.setText(text);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            btn.setBackground(new Color(238, 237, 254));
            btn.setForeground(new Color(83, 74, 183));
            //Создаём двойную рамку вокруг кнопки:
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(127, 119, 221), 2, true),
                    BorderFactory.createEmptyBorder(10, 28, 10, 28)));
            //Убираем рамку фокуса (которая появляется при нажатии)
            btn.setFocusPainted(false);
            // Когда мышь наводится на кнопку, курсор меняется на руку
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        public void doLayout() {
            super.doLayout();//сначала делаем стандартную раскладку
            if (startBtn != null) {
                Dimension ps = startBtn.getPreferredSize();
                //Кнопка всегда будет по центру экрана
                startBtn.setBounds((getWidth() - ps.width) / 2,
                        (getHeight() - ps.height) / 2,
                        ps.width, ps.height);
            }
        }

        void startGame() {
            //очищаем все перед игрой
            buttons.clear();
            rings.clear();
            totalBoom = 0;
            started = false;

            startBtn.setText("▶  Нажми, чтобы начать");
            styleOverlayBtn(startBtn, "▶  Нажми, чтобы начать");

            //Показываем кнопку и делаем её активной.
            startBtn.setVisible(true);
            startBtn.setEnabled(true);

            //Удаляем старый обработчик нажатий с кнопки (чтобы не было конфликтов)
            startBtn.removeActionListener(startBtn.getActionListeners()[0]);
            //обавляем новый обработчик: при нажатии на кнопку — прячем её
            // и запускаем первую летающую кнопку
            startBtn.addActionListener(e -> {
                startBtn.setVisible(false);
                launchFirstButton();
            });

            timer.stop();
            repaint();
        }

        void launchFirstButton() {
            started = true;
            buttons.add(new BounceBtn(getWidth() / 2.0, getHeight() / 2.0,
                    START_SIZE, 0));
            timer.start();
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!started) return;
            int W = getWidth(), H = getHeight();

            List<BounceBtn> toAdd = new ArrayList<>();
            List<BounceBtn> toRemove = new ArrayList<>();

            // Для каждой кнопки: двигаем её и проверяем столкновения со стенами.
            for (BounceBtn b : buttons) {
                b.move(W, H);
                //осуществляем взрыв
                if (b.shouldExplode()) {
                    Point c = b.center();
                    rings.add(new ExplosionRing(c.x, c.y,
                            PALETTES[b.paletteIdx][1]));
                    toAdd.addAll(b.explode());
                    toRemove.add(b);
                    totalBoom++;
                }
            }

            // Удаляем взорвавшиеся кнопки, добавляем новые.
            buttons.removeAll(toRemove);
            buttons.addAll(toAdd);

            //Удаляем анимации, которые закончились (через 420 мс)
            rings.removeIf(ExplosionRing::isDone);

            if (buttons.size() >= MAX_BUTTONS) {
                timer.stop();
                buttons.clear();
                rings.clear();
                started = false;

                //Показываем сообщение о достижении лимита и кнопку для перезапуска.
                SwingUtilities.invokeLater(() -> {
                    styleOverlayBtn(startBtn,
                            "💥 " + MAX_BUTTONS + " кнопок! Нажми, чтобы начать заново");
                    startBtn.setVisible(true);
                    startBtn.setEnabled(true);

                    //обновляем обработчик
                    for (ActionListener al : startBtn.getActionListeners())
                        startBtn.removeActionListener(al);
                    startBtn.addActionListener(ev -> {
                        startBtn.setVisible(false);
                        launchFirstButton();
                    });
                    repaint();
                });
                return;
            }

            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);//сначала стираем старую картинку
            Graphics2D g2 = (Graphics2D) g;//круче рисует
            //включаем сглаживание
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            for (ExplosionRing r : rings) r.draw(g2);
            for (BounceBtn b : buttons)   b.draw(g2);

            //показываем информационную панель
            if (started) {
                String hud = "кнопок: " + buttons.size()
                        + "   взрывов: " + totalBoom
                        + "   (лимит: " + MAX_BUTTONS + ")";
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                g2.setColor(new Color(120, 118, 130));
                g2.drawString(hud, 14, 22);
            }
        }
    }

    //запускаем программу
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BouncingButtons());
    }
}
