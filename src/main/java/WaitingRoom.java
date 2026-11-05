public class WaitingRoom {

    private final int capacity;
    private final EventLogger logger;
    private final Patient[] buffer;
    private int inPtr = 0;
    private int outPtr = 0;

    private final Semaphore emptySeats;
    private final Semaphore waitingPatients;

    private final Object prodLock=new Object();
    private final Object consLock=new Object();

    public WaitingRoom(int capacity, EventLogger logger) {

        this.capacity = capacity;
        this.logger = logger;
        this.buffer = new Patient[capacity];
        this.emptySeats = new Semaphore(capacity);
        this.waitingPatients = new Semaphore(0);
    }

    public void addPatient(Patient patient) throws InterruptedException{

        if (!emptySeats.tryAcquire(0)){

            logger.log("The waiting room is full");
            logger.log(patient + " is waiting outside for an available seat");

            emptySeats.acquire();
        }


        synchronized (prodLock){
            buffer[inPtr] = patient;
            inPtr = (inPtr + 1) % capacity;
            logger.log(patient + " entered the waiting room");

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