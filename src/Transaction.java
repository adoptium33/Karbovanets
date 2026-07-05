public class Transaction {
    private int sum;
    private Category category;

    public Transaction(int sum, Category category) {
        this.sum = sum;
        this.category = category;
    }

    public int getSum() {
        return sum;
    }

    public Category getCategory() {
        return category;
    }
}
