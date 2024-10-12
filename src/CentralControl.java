import java.util.ArrayList;

class CentralControl extends Thread {
    private char[][] forest;
    private Sensor[][] sensors;
    public ArrayList<Message> messagesBuffer = new ArrayList<>();
    private boolean flag;

    public CentralControl(char[][] forest, Sensor[][] sensors) throws Exception {
        if (forest == null || sensors == null) {
            throw new Exception("Null Parameters");
        }
        this.forest = forest;
        this.sensors = sensors;
        flag = true;
    }
    
    public boolean isMessageInBuffer(Message message) {
        for (Message m : messagesBuffer) {
            if (m.getMessage().equals(message.getMessage())) {
                return true; 
            }
        }
        return false; 
    }

    public void end() {
        this.flag = false;
    }

    public void run() {
        while (flag) {
            synchronized (messagesBuffer) {
                if (!messagesBuffer.isEmpty()) {
                    for (Message m : messagesBuffer) {
                        System.out.println(m.getMessage());
                        synchronized (forest) {
                            forest[m.getInFire().getRow()][m.getInFire().getColumn()] = '/';
                        }
                    }
                    messagesBuffer.clear();
                }
            }
        }
    }
}