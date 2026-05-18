public class LuckyTickets {
    public static void main(String[] args) {
        int count = 0;
        for (int number = 1; number <= 999999; number++) {
            // Форматируем номер в 6 цифр с ведущими нулями
            String ticket = String.format("%06d", number);
            int sumFirst = 0, sumLast = 0;
            // Сумма первых трёх цифр
            for (int i = 0; i < 3; i++) {
                sumFirst += ticket.charAt(i) - '0';
            }
            // Сумма последних трёх цифр
            for (int i = 3; i < 6; i++) {
                sumLast += ticket.charAt(i) - '0';
            }
            if (sumFirst == sumLast) {
                count++;
            }
        }
        System.out.println("Number of lucky tickets ( 000001 - 999999): " + count);
    }
}