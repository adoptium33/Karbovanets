public class Category {
    private String name;
    private boolean outgo;

    public Category(String name, boolean outgo) {
        this.name = name;
        this.outgo = outgo;
    }

    public String getName() {
        return name;
    }

    public boolean isOutgo() {
        return outgo;
    }
}
