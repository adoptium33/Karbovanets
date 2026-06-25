public class Transaction {
    private int sum;
    private boolean outgo;

    public Transaction(int sum, boolean outgo) {
        this.sum = sum;
        this.outgo = outgo;
    }

    public int getSum() {
        return sum;
    }

    public boolean isOutgo() {
        return outgo;
    }
}
