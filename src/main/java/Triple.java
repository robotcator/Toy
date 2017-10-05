public class Triple {
//    String Pattern;
    Pattern pat;
    int root;
    int bottom;

    public Triple(Pattern pattern, int root, int bottom) {
        this.pat = pattern;
        this.root = root;
        this.bottom = bottom;
    }

    @Override
    public String toString() {
        return "Triple{" +
                "Pattern='" + pat + '\'' +
                ", root=" + root +
                ", bottom=" + bottom +
                '}';
    }
}
