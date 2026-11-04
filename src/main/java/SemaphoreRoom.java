public class SemaphoreRoom {
    private long permits;

    public SemaphoreRoom(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Permits cannot be negative");
        }
        permits = value;
    }

    public synchronized void acquire() throws InterruptedException {
        while(permits <= 0){
            wait();
        }
        this.permits--;
    }

    public synchronized  void release(){
        this.permits++;
        notifyAll();
    }

    public synchronized boolean tryAcquire(long timeoutMs) throws InterruptedException {
        if (timeoutMs < 0) {
            throw new IllegalArgumentException("Invalid timeoutMs");
        }

        long deadline = System.nanoTime() + timeoutMs * 1_000_000L;

        while (this.permits <= 0) {

            long remainingNanos = deadline - System.nanoTime();

            if (remainingNanos <= 0) {
                return false;
            }

            long remainingMs = remainingNanos / 1_000_000;
            int remainingNano = (int) (remainingNanos % 1_000_000);

            wait(remainingMs, remainingNano);
        }

        this.permits--;
        return true;
    }

    public synchronized void setPermits(Long permits) {
        if (permits < 0) {
            throw new IllegalArgumentException("Permits cannot be negative");
        }
        this.permits = permits;
        notifyAll();


    }
}
