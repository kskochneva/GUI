public class Main {
  public static void main(String[] args) {
    int count = 0;
    // Перебираем все номера от 1 до 999999 включительно
    for (int number = 1; number <= 999999; number++) {
      // Форматируем номер в 6 цифр (добавляем ведущие нули)
      String ticket = String.format("%06d", number);//форматируем 5 - 000005
      int sumFirst = 0; // сумма первых трёх цифр
      int sumLast  = 0; // сумма последних трёх цифр

      // Суммируем первые три цифры (индексы 0,1,2)
      for (int i = 0; i < 3; i++) {
        sumFirst += ticket.charAt(i) - '0';//берем символ по индексу 0 превращает символ в число
      }
      // Суммируем последние три цифры (индексы 3,4,5)
      for (int i = 3; i < 6; i++) {
        sumLast += ticket.charAt(i) - '0';
      }

      if (sumFirst == sumLast) {
        count++;
      }
    }
    System.out.println("Количество счастливых билетов (000001–999999): " + count);
  }
}
