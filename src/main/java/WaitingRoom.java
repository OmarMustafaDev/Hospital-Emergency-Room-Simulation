public class WaitingRoom {

    private final int capacity;
    private final Patient[] buffer;
    private int inPtr = 0;
    private int outPtr = 0;

    private final SemaphoreRoom emptySeats;
    private final SemaphoreRoom waitingPatients;

    private final Object prodLock=new Object();
    private final Object consLock=new Object();

    public WaitingRoom(int capacity){

        this.capacity = capacity;
        this.buffer = new Patient[capacity];
        this.emptySeats = new SemaphoreRoom(capacity);
        this.waitingPatients = new SemaphoreRoom(0);
    }

    public void addPatient(Patient patient) throws InterruptedException{

        if (!emptySeats.tryAcquire(0)){

            System.out.println("The waiting room is full");
            System.out.println(patient + " is waiting outside for an available seat");

            emptySeats.acquire();
        }


        synchronized (prodLock){
            buffer[inPtr] = patient;
            inPtr = (inPtr + 1) % capacity;
            System.out.println(patient + " entered the waiting room");

        }
        waitingPatients.release();
    }

    public Patient getNextPatient() throws InterruptedException{
        waitingPatients.acquire();


        Patient patient;
        synchronized (consLock){
            patient = buffer[outPtr];
            buffer[outPtr] =null;
            outPtr= (outPtr+1) % capacity;
        }

        emptySeats.release();
        return patient;
    }
}