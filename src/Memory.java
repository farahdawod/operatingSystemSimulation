import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class Memory {
    private static final int MEMORY_SIZE = 40;
    private static final int PROCESS_SIZE=3;

    public static final ArrayList<Word> memory = new ArrayList<>(MEMORY_SIZE);

    Queue<Process> readyQueue, blockedQueue;

    public Memory() {
        Word processes=new Word("Processes",new ArrayList<>());
        memory.add(processes);
        /* readyQueue,blockedQueue=new Queue<Process>() {
            @Override
            public boolean add(Process process) {
                return false;
            }

            @Override
            public boolean offer(Process process) {
                return false;
            }

            @Override
            public Process remove() {
                return null;
            }

            @Override
            public Process poll() {
                return null;
            }

            @Override
            public Process element() {
                return null;
            }

            @Override
            public Process peek() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @Override
            public Iterator<Process> iterator() {
                return null;
            }

            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @Override
            public <T> T[] toArray(T[] ts) {
                return null;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean addAll(Collection<? extends Process> collection) {
                return false;
            }

            @Override
            public boolean removeAll(Collection<?> collection) {
                return false;
            }

            @Override
            public boolean retainAll(Collection<?> collection) {
                return false;
            }

            @Override
            public void clear() {

            }
        }*/
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
    }

    public void addToBlockedQueue(Process process){
        blockedQueue.add(process);
    }
}
