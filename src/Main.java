public class Main {
    static final int NUM = 5;

    public static void main(String[] args) {
        try {
            ForestCell[][] forestMatrix = new ForestCell[NUM][NUM];
            Sensor[][] sensorMatrix = new Sensor[NUM][NUM];
            FireCreator fireCreator = new FireCreator(forestMatrix);
            CentralControl central = new CentralControl(forestMatrix, sensorMatrix);

            for (int i = 0; i < NUM; i++) {
                for (int j = 0; j < NUM; j++) {
                    forestMatrix[i][j] = new ForestCell('-');
                    sensorMatrix[i][j] = new Sensor(forestMatrix, sensorMatrix, central);
                }
            }

            central.start();

            for (int i = 0; i < NUM; i++) {
                for (int j = 0; j < NUM; j++) {
                    sensorMatrix[i][j].start();
                }
            }

            fireCreator.start();

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    static void printMatrix(ForestCell[][] matrix) {
        for (ForestCell[] row : matrix) {
            for (ForestCell cell : row) {
                System.out.print(cell.getState() + " ");
            }
            System.out.println();
        }
    }
}