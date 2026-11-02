public class EventLogger{
    public synchronized void log (String msg){
        System.out.println(msg);
    }
}