import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.CountDownLatch;

public class Doctor extends Thread {

    private final int id;
    private final WaitingRoom waitingRoom;
    private final TreatmentRoom treatmentRoom;
    private final EventLogger logger;
    private final CountDownLatch treated;

    public Doctor(int id, WaitingRoom waitingRoom,
                  TreatmentRoom treatmentRoom, EventLogger logger, CountDownLatch treated) {
        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRoom = treatmentRoom;
        this.logger = logger;
        this.treated = treated;
    }

    @Override
    public void run() {

        while (true){
            try {
                Patient patient = waitingRoom.getNextPatient();
                logger.log("Doctor " + id + " is assigned to " + patient + ".");
                logger.log("Doctor " + id + " is trying to acquire a treatment room.");


                if (!treatmentRoom.tryAcquire()) {
                    logger.log("Doctor " + id + " could not acquire a treatment room.");
                    logger.log("Doctor " + id + " is sending " + patient + " back to the waiting room.");
                    new Thread(() -> {
                        try {
                            waitingRoom.addPatient(patient);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                    logger.log("Doctor " + id + " is now available.");
                    continue;
                }
                try{
                    int treatmentTime = ThreadLocalRandom.current().nextInt(2000,5001);
                    Thread.sleep(treatmentTime);
                }finally {
                    treatmentRoom.release();
                }
                treated.countDown();

            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
