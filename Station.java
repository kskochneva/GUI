public class Station {
    public int id;
    public String name;
    public int lineId;
    public String lineName;
    public String lineColor;
    public int x;
    public int y;
    public boolean isCircle;
    //на кольце

    public Station(int id, String name, int lineId, String lineName,
                   String lineColor, int x, int y, boolean isCircle) {
        this.id = id;
        this.name = name;
        this.lineId = lineId;
        this.lineName = lineName;
        this.lineColor = lineColor;
        this.x = x;
        this.y = y;
        this.isCircle = isCircle;
    }

    @Override
    public String toString() {
        return name;
    }
}
//метод для строкового представления
//переопределяем через оверрайд
//без этого выдает алрес в памяти