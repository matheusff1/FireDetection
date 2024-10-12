class ForestCell {
    private char state;

    public ForestCell(char state) {
        this.state = state;
    }

    public synchronized char getState() {
        return this.state;
    }

    public synchronized void setState(char state) {
        this.state = state;
    }
}