public class TreatmentRoom {
    private final SemaphoreRoom rooms ;
    public TreatmentRoom() {
        rooms = new SemaphoreRoom(2);
    }
    public boolean tryAcquire()throws InterruptedException{
        
        return rooms.tryAcquire(3000);
    }
    public void release(){
        rooms.release();
    }
}