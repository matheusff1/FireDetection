import java.util.ArrayList;

class Sensor extends Thread {
    private ForestCell[][] forest;
    private Sensor[][] sensors;
    public ArrayList<Message> messagesBuffer;
    private int row, column;
    private boolean flag;
    private ArrayList<Sensor> neighbours;
    private boolean isBorderNode;
    private boolean reportedFire;
    private CentralControl central;

    public Sensor(ForestCell[][] forest, Sensor[][] sensors, CentralControl central) throws Exception {
        if (forest == null || sensors == null || central == null) {
            throw new Exception("Null Parameter");
        }

        this.forest = forest;
        this.sensors = sensors;
        this.central = central;
        this.flag = true; 
        this.messagesBuffer = new ArrayList<>();
        this.neighbours = new ArrayList<>();
        this.reportedFire = false;
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

    public boolean isMessageInBuffer(Message message) {
        for (Message m : this.messagesBuffer) {
            if (m.getMessage().equals(message.getMessage())) {
                return true; 
            }
        }
        return false; 
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
            Message incomingMessage = messagesBuffer.get(i);

            boolean messageExistsInBuffer;
            
            synchronized (central.messagesBuffer) {
                messageExistsInBuffer = central.isMessageInBuffer(incomingMessage);
            }

            if (!messageExistsInBuffer) {
                if (isBorderNode) {
                    synchronized (central.messagesBuffer) {
                        central.messagesBuffer.add(incomingMessage);
                    }
                } else {
                    for (Sensor neighbour : neighbours) {
                        synchronized (neighbour.messagesBuffer) {
                            if (!neighbour.isMessageInBuffer(incomingMessage)) {
                                neighbour.messagesBuffer.add(incomingMessage);
                            }
                        }
                    }
                }
            }

            messagesBuffer.remove(i);
        }
    }

    private void sendMessages() {
        String messageText = "(" + this.row + "," + this.column + ") is on fire";
        Message message;

        try {
            message = new Message(this, this, messageText);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create message", e);
        }

        //System.out.println("NEW MESSAGE created: " + message.getMessage());

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
        defPos(sensors);
        defNeighbours(sensors);
        defIsBorderNode(row, column, sensors);
        System.out.println("Thread running at position (" + row + "," + column + ")");
        
        while (this.flag && !reportedFire) {
            synchronized (forest[this.row][this.column]) {
                if (forest[this.row][this.column].getState() == '@') {
                    System.out.println("Sensor at (" + row + "," + column + ") detected fire!");
                    sendMessages();
                    this.reportedFire = true;
                }
            }
            if (!this.messagesBuffer.isEmpty()) {
                handleMessages();
            }
            try {
                Thread.sleep(10000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Sensor thread interrupted.");
            }
        }
    }
}