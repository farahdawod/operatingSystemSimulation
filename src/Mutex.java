public class Mutex {
    private boolean semaphore; //available
    private int processID;
    private Resource resource;

    public Mutex (Resource resource){
        semaphore = true;
        this.resource = resource;
        processID = 0;
    }

//    public Mutex(int processID, Resource resource){
//        semaphore = true;
//        this.processID = processID;
//        this.resource = resource;
//    }

    public boolean isSemaphore() {
        return semaphore;
    }

    public void setSemaphore(boolean semaphore) {
        this.semaphore = semaphore;
    }

    public int getProcessID() {
        return processID;
    }

    public void setProcessID(int processID) {
        this.processID = processID;
    }

    public void setResource(Resource resource){
        this.resource = resource;
    }

    public Resource getResource(){
        return this.resource;
    }

    //@Override
    public void semSignal(){
        semaphore = true;
        this.processID=0;
//        if(!isSemaphore()){
//            System.out.println("Another process is using this resource");
//            return isSemaphore();
//        }
//        else{
//            semaphore = true;
//            processID = super.getProcessID();
//            return semaphore;
//        }
    }

    //@Override
    public void semWait() {
        semaphore = false;
//        if(processID == super.getProcessID()){ //wait
//            if(!semaphore){
//                semaphore = true;
//                processID = 0;
//                return semaphore;
//            }
//            else{
//                System.out.println("Resource is not taken");
//                return false;
//            }
//        }
//        else{
//            System.out.println("Another process is using this resource");
//            return false;
//        }
    }
}
