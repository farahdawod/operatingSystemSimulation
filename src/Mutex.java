public class Mutex extends Process{
    private static boolean semaphore = true; //available
    private int processID;

    public static boolean isSemaphore() {
        return semaphore;
    }

    public static void setSemaphore(boolean semaphore) {
        Mutex.semaphore = semaphore;
    }

    @Override
    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    @Override
    public boolean semSignal(Mutex mutex) {
        if(!isSemaphore()){
            System.out.println("Another process is using this resource");
            return isSemaphore();
        }
        else{

            semaphore = true;
            processID = super.getProcessID();
            return semaphore;
        }
    }

    @Override
    public boolean semWait() {
        if(processID == super.getProcessID()){
            if(!semaphore){
                semaphore = true;
                processID = 0;
                return semaphore;
            }
            else{
                System.out.println("Resource is not taken");
                return false;
            }
        }
        else{
            System.out.println("Another process is using this resource");
            return false;
        }
    }
}
