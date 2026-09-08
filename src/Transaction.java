import java.time.LocalDate;

public class Transaction {
    private double sum;
    private Category category;
    private LocalDate date;

    public Transaction(double sum, Category category, LocalDate date) {
        this.sum = sum;
        this.category = category;
        this.date = date;
    }

    public double getSum() {
        return sum;
    }

    public Category getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }
}
