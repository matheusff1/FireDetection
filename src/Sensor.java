import java.util.ArrayList;
import java.util.concurrent.Semaphore;

class Sensor extends Thread {
    private char[][] forest;
    private Sensor[][] sensors;
    public ArrayList<Message> messagesBuffer;
    private int row, column;
    private boolean flag;
    private ArrayList<Sensor> neighbours;
    private boolean isBorderNode;
    private CentralControl central;

    public Sensor(char[][] forest, Sensor[][] sensors, CentralControl central) throws Exception {
        if (forest == null || sensors == null || central == null) {
            throw new Exception("Null Parameter");
        }

        this.forest = forest;
        this.sensors = sensors;
        this.central = central;
        this.flag = true; // Inicializa a flag
        this.messagesBuffer = new ArrayList<>();
        this.neighbours = new ArrayList<>();
        defPos(sensors);
        defNeighbours(sensors);
        defIsBorderNode(row, column, sensors);
    }

    private void defPos(Sensor[][] sensors) {
        for (int i = 0; i < sensors.length; i++) {
            for (int j = 0; j < sensors[i].length; j++) {
                if (sensors[i][j] == this) {
                    this.row = i;
                    this.column = j;
                }
            }
        }
    }

    private void defNeighbours(Sensor[][] sensors) {
        if (row > 0 && sensors[row - 1][column] != null) {
            this.neighbours.add(sensors[row - 1][column]);
        }
        if (row < sensors.length - 1 && sensors[row + 1][column] != null) {
            this.neighbours.add(sensors[row + 1][column]);
        }
        if (column > 0 && sensors[row][column - 1] != null) {
            this.neighbours.add(sensors[row][column - 1]);
        }
        if (column < sensors[row].length - 1 && sensors[row][column + 1] != null) {
            this.neighbours.add(sensors[row][column + 1]);
        }
    }

    private void defIsBorderNode(int row, int column, Sensor[][] sensors) {
        this.isBorderNode = (row == 0 || row == sensors.length - 1 || column == 0 || column == sensors[0].length - 1);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    private void handleMessages() {
        for (int i = messagesBuffer.size() - 1; i >= 0; i--) {
            for (Sensor neighbour : neighbours) {
                if (isBorderNode) {
                    synchronized (central.messagesBuffer) {
                        central.messagesBuffer.add(messagesBuffer.get(i));
                    }
                } else {
                    if (messagesBuffer.get(i).getSender() != neighbour) {
                        synchronized (neighbour.messagesBuffer) {
                            neighbour.messagesBuffer.add(messagesBuffer.get(i));
                        }
                    }
                }
            }
            messagesBuffer.remove(i);
        }
    }

    private void sendMessages() {
        String messageText = "(" + this.row + "," + this.column + ") is on fire";
        Message message = null;
        try {
            message = new Message(this, this, messageText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (isBorderNode) {
            synchronized (central.messagesBuffer) {
                central.messagesBuffer.add(message);
            }
        } else {
            for (Sensor neighbour : neighbours) {
                synchronized (neighbour.messagesBuffer) {
                    neighbour.messagesBuffer.add(message);
                }
            }
        }
    }

    public void end() {
        this.flag = false;
    }

    public void run() {
        while (this.flag) {
            if (this.forest[this.row][this.column] == '-') {
                return; // Saída se não houver fogo
            }
            if (this.forest[this.row][this.column] == '@') {
                sendMessages();
            }
            if (!this.messagesBuffer.isEmpty()) {
                handleMessages();
            }
        }
    }
}