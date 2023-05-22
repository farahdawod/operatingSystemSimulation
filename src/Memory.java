import java.util.*;

public class Memory {
    private static final int MEMORY_SIZE = 40;
    private static final int PROCESS_SIZE=3;

    public static final ArrayList<Word> memory = new ArrayList<>(MEMORY_SIZE);

    Queue<Process> readyQueue;
    Hashtable <Process, Resource> blockedQueue;

    public Memory() {
        Word processes=new Word("Processes",new ArrayList<>());
        memory.add(processes);
        readyQueue= new LinkedList<>();
        blockedQueue=new Hashtable<>() ;
    }

    public Queue<Process> getReadyQueue(){
        return readyQueue;
    }

    public boolean isThereEnoughSpace(){
        Iterator<Word> memoryIrt=memory.iterator();
        int i=0;
        while (memoryIrt.hasNext()){
            i++;
            memoryIrt.next();
        }
        return i < (MEMORY_SIZE / PROCESS_SIZE);
    }

    public int[] assignPlaceForProcess(){
        Iterator<Word> memoryIrt=memory.iterator();
        int i=0;
        while (memoryIrt.hasNext()){
            i++;
            memoryIrt.next();
        }
        return new int[]{i,i+1,i+2};

    }

    public void add(String descriptionOfData,Object data, int index){
        Word newData=new Word(descriptionOfData,data);
        memory.add(index, newData);
    }

    public void addToReadyQueue(Process process) {
        readyQueue.add(process);
        process.changeProcessState(processState.READY);
    }

    public void addToBlockedQueue(Process process, Resource azma) {
        blockedQueue.put(process, azma);
        process.changeProcessState(processState.BLOCKED);
        System.out.println("Processes in Ready Queue: ");
        for (Process p1 : readyQueue) {
            System.out.print("Process" + p1.getProcessID() + ", ");
        }
        System.out.println("Processes in Blocked Queue: ");
        Iterator<Process> itrB=blockedQueue.keys().asIterator();
        while (itrB.hasNext()) {
            Process p2=itrB.next();
            System.out.print("Process" + p2.getProcessID() + ", ");
        }

    }

    public void removeFromBlockedQueue(Resource faka){
        for (Map.Entry<Process, Resource> finder : blockedQueue.entrySet()) {
            if (finder.getValue().compareTo(faka) == 0) {
                blockedQueue.remove(finder.getKey(), finder.getValue());
                addToReadyQueue(finder.getKey());
                return;
            }
        }
    }
    public Object getFromMemory(int index){
        return memory.get(index).data;
    }

}
