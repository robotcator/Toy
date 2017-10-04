public class Triple {
    String Pattern;
    int root;
    int bottom;

    public Triple(String pattern, int root, int bottom) {
        Pattern = pattern;
        this.root = root;
        this.bottom = bottom;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "Pattern='" + Pattern + '\'' +
                ", root=" + root +
                ", bottom=" + bottom +
                '}';
    }
}
