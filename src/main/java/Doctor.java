import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.CountDownLatch;

public class Doctor extends Thread {

    private final int id;
    private final WaitingRoom waitingRoom;
    private final TreatmentRoom treatmentRoom;
    private final EventLogger logger;
    private final SemaphoreRoom treated;

    public Doctor(int id, WaitingRoom waitingRoom,
                  TreatmentRoom treatmentRoom,
                  EventLogger logger,
                  SemaphoreRoom treated) {

        this.id = id;
        this.waitingRoom = waitingRoom;
        this.treatmentRoom = treatmentRoom;
        this.logger = logger;
        this.treated = treated;
    }

    @Override
    public void run() {

        while (!isInterrupted()) {

            try {

                Patient patient = waitingRoom.getNextPatient();

                logger.log("Doctor " + id + " is assigned to " + patient + ".");
                logger.log("Doctor " + id + " is trying to acquire a treatment room.");


                if (!treatmentRoom.tryAcquire()) {

                    logger.log("Doctor " + id + " could not acquire a treatment room.");


                    returnPatientToWaitingRoom(patient);

                    logger.log("Doctor " + id + " is now available.");

                    continue;
                }


                try {
                    logger.log("Doctor " + id + " acquired a treatment room.");
                    logger.log("Doctor " + id + " begins treating " + patient + ".");

                    int treatmentTime =
                            ThreadLocalRandom.current().nextInt(2000, 5001);

                    Thread.sleep(treatmentTime);

                    logger.log("Doctor " + id + " finished treating " + patient + ".");

                } finally {
                    treatmentRoom.release();

                    logger.log("Doctor " + id + " released the treatment room.");
                }


                logger.log("Doctor " + id + " is now available.");
                logger.log(patient + " left.");


                treated.release();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void returnPatientToWaitingRoom(Patient patient) {

        logger.log("Doctor " + id + " is sending " + patient +
                " back to the waiting room.");

        Thread requeueThread = new Thread(() -> {
            try {
                waitingRoom.addPatient(patient);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        requeueThread.start();
    }
}