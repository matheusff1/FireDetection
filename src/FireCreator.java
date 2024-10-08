import java.util.Random;

class FireCreator extends Thread {
    private char[][] matrix;
    private Random random;
    private boolean flag;

    public FireCreator(char[][] matrix) {
        this.matrix = matrix;
        this.random = new Random();
        this.flag = true;
    }

    public void end() {
        this.flag = false;
    }

    @Override
    public void run() {
        while (flag) {
            int row = random.nextInt(matrix.length);
            int col = random.nextInt(matrix[0].length);

            synchronized (matrix) {
                if (matrix[row][col] == '-') {
                    matrix[row][col] = '@';
                    System.out.println("Fire Creator: NEW FIRE AT [" + row + "][" + col + "] para '@'");
                    Main.printMatrix(matrix);
                }
            }

            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("FireCreator thread interrupted.");
            }
        }
    }
}