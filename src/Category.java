public class Category {
    private String name;
    private boolean outgo;

    public Category(String name, boolean outgo) {
        this.name = name;
        this.outgo = outgo;
    }

    public boolean isOutgo() {
        return outgo;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
