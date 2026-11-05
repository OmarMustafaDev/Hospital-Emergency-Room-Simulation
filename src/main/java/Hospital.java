import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class Hospital {
    public static void main(String[] args) throws InterruptedException {
        Scanner keyboard = new Scanner(System.in);
        System.out.println("Welcome to the Hospital Simulation!");
        System.out.println("Waiting room capacity: ");
        int capacity = keyboard.nextInt();
        System.out.println("Number of doctors: ");
        int numDoctors = keyboard.nextInt();
        System.out.println("Number of patients: ");
        int numPatients = keyboard.nextInt();

        if (capacity < 1 || numDoctors < 1 || numPatients < 1) {
            System.out.println("All inputs must be at least 1.");
            keyboard.close();
            return;
        }

        // shared resources
        EventLogger eventLogger = new EventLogger();
        WaitingRoom waitingRoom = new WaitingRoom(capacity, eventLogger);
        TreatmentRoom treatmentRoom = new TreatmentRoom();
        SemaphoreRoom treated = new SemaphoreRoom(0);

        Doctor[] doctors = new Doctor[numDoctors];
        Patient[] patients = new Patient[numPatients];

        StringBuilder names = new StringBuilder();
        for (int i = 1; i <= numPatients; i++) {
            names.append("P").append(i).append(" ");
        }
        eventLogger.log("Patients arriving:");
        eventLogger.log(names.toString().trim());

        for (int i = 0; i < numDoctors; i++) {
            doctors[i] = new Doctor(i + 1, waitingRoom, treatmentRoom, eventLogger, treated);
            doctors[i].start();
        }
        for (int i = 0; i < numPatients; i++) {
            patients[i] = new Patient(i + 1, waitingRoom, eventLogger);
            patients[i].start();
        }

        for (int i = 0; i < numPatients; i++) {
            treated.acquire();
        } // Wait for all patients to be treated

        for (Doctor doctor : doctors) {
            doctor.interrupt();
        }
        for (Patient patient : patients) {
            patient.join();
        }
        for (Doctor doctor : doctors) {
            doctor.join();
        }

        eventLogger.log("All patients have been treated.");
        eventLogger.log("Simulation completed.");

        keyboard.close();

    }

}
