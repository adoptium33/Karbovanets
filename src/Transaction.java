import java.time.LocalDate;

public class Transaction {
    private int sum;
    private Category category;
    private LocalDate date;

    public Transaction(int sum, Category category, LocalDate date) {
        this.sum = sum;
        this.category = category;
        this.date = date;
    }

    public int getSum() {
        return sum;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }
}
