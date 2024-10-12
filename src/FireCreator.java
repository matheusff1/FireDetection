import java.util.Random;

class FireCreator extends Thread {
    private ForestCell[][] matrix;
    private Random random;
    private boolean flag;

    public FireCreator(ForestCell[][] matrix) {
        this.matrix = matrix;
        this.random = new Random();
        this.flag = true;
    }

    public void end() {
        this.flag = false;
    }

    public void run() {
        while (flag) {
            int row = random.nextInt(matrix.length);
            int col = random.nextInt(matrix[0].length);

            synchronized (matrix[row][col]) {
                if (matrix[row][col].getState() == '-') {
                    matrix[row][col].setState('@');
                    System.out.println("Fire Creator: NEW FIRE AT [" + row + "][" + col + "] ->'@'");
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