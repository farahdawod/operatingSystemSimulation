import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;

public class Memory {
    private static final int MEMORY_SIZE = 40;
    public static final ArrayList<Word> memory = new ArrayList<>(MEMORY_SIZE);

    Queue<Process> readyQueue, blockedQueue;

    public Memory() {
        Word processes=new Word("Processes",new ArrayList<>());
        memory.add(processes);
    }


    public int[] assignPlace(int processID){
        //if (memory.get(0).getData().size()<MEMORY_SIZE/3){
        Iterator<Word> memoryIrt=memory.iterator();
        int i=0;
        while (memoryIrt.hasNext()){
            i++;
            memoryIrt.next();
        }
        return new int[]{i,i+1,i+2};

    }

    public void addToReadyQueue(Process process) {
        readyQueue.add(process);
    }
}
