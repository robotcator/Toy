public class Triple {
    //    String Pattern;
    Pattern pat;
    int root;
    int bottom;

    public void setPat(Pattern pat) {
        this.pat = pat;
    }

    public void setRoot(int root) {
        this.root = root;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

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
