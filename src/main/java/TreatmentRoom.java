public class TreatmentRoom {
    private final Semaphore rooms ;
    public TreatmentRoom() {
        rooms = new Semaphore(2);
    }
    public boolean tryAcquire()throws InterruptedException{
        
        return rooms.tryAcquire(3000);
    }
    public void release(){
        rooms.release();
    }
}