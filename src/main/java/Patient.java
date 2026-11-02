
public class Patient extends Thread {

    private final int id;
    private final WaitingRoom waitingRoom;
    private final EventLogger logger ;

    public Patient(int id, WaitingRoom waitingRoom, EventLogger logger) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.logger=logger;
    }

    public int getPatientId() {
        return id;
    }

    @Override
    public void run() {
        logger.log("Patient " + id + " arrived.");

        try {
            waitingRoom.addPatient(this);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
    @Override 
    public String toString() {
        return "Patient " + id;
    }
}
