public class Message {
    private Sensor sender;
    private Sensor inFire;
    private String message;

    public Message(Sensor sender, Sensor inFire, String message) throws Exception {
        if(sender == null || inFire == null || message == null)
        {throw new Exception("Messages atributes cant be null.");}
        this.sender = sender;
        this.inFire = inFire;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Sensor getInFire() {
        return inFire;
    }

    public Sensor getSender() {
        return sender;
    }
}
