import java.util.concurrent.ThreadLocalRandom;
public class Doctor extends Thread {

    private final int id;
    private final WaitingRoom waitingRoom;
    private final TreatmentRoom treatmentRoom;
    private final EventLogger logger;

    public Doctor(int id, WaitingRoom waitingRoom,
                  TreatmentRoom treatmentRoom, EventLogger logger) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRoom = treatmentRoom;
        this.logger = logger;
    }

    @Override
    public void run() {

        while (true){
            try {
                Patient patient = waitingRoom.getNextPatient();
                logger.log("Doctor " + id + " took " + patient);


                if(!treatmentRoom.tryAcquire()){
                    logger.log("Doctor " + id + " couldn't get a treatment room for " + patient);
                    logger.log(patient + " is returning to the waiting room.");
                    
                    waitingRoom.addPatient(patient);
                    continue;
                }
                try{
                    int treatmentTime = ThreadLocalRandom.current().nextInt(2000,5001);
                    Thread.sleep(treatmentTime);
                }finally {
                    treatmentRoom.release();
                }


            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
