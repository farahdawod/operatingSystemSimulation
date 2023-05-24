import java.io.*;
import java.util.*;

public class Memory{
    private static final int MEMORY_SIZE = 40;
    private static final int PROCESS_SIZE=3;

    public static final ArrayList<Word> memory = new ArrayList<>(MEMORY_SIZE);

    Queue<Process> readyQueue;
    Hashtable <Process, Resource> blockedQueue;

    public Memory() {
        Word processes=new Word("Processes",new ArrayList<>());
        memory.add(processes);
        readyQueue= new LinkedList<Process>();
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
        if (memory.get(index)!=null)
            memory.remove(index);
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

    public Process unloadFromMemory(){
        Process p;
        if (blockedQueue.isEmpty()) p = readyQueue.stream().findFirst().get();
        else
            p=blockedQueue.keys().nextElement();

        Object [] PCB=p.getPCB();
        for (Object o:PCB){
            try (BufferedWriter objectOut = new BufferedWriter(new FileWriter("disk.text"))) {
                if (o instanceof Integer)
                    objectOut.write((Integer) o);
                else if (o instanceof processState)
                    objectOut.write(((processState) o).name());
                else if (o instanceof int[]) {
                    for (int i:(int[])o)
                        objectOut.write(i);
                }
                objectOut.write(",");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> instruct= (ArrayList<String>) getFromMemory(p.getMemoryBoundaries()[1]);
        try (BufferedWriter objectOut = new BufferedWriter(new FileWriter("disk.text"))) {
            objectOut.newLine();
            for (String i:instruct) {
                objectOut.write(i+",");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        Hashtable<String,String> va= (Hashtable<String, String>) getFromMemory(p.getMemoryBoundaries()[2]);
        try (BufferedWriter objectOut = new BufferedWriter(new FileWriter("disk.text"))) {
            objectOut.newLine();
            Iterator<String> itr=va.elements().asIterator();
            Iterator<String> itrv=va.keys().asIterator();
            while (itr.hasNext()&&itrv.hasNext()) {
                objectOut.write(itrv.next()+"="+itr.next() +",");
                objectOut.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        memory.remove(p.getMemoryBoundaries()[0]);
        memory.remove(p.getMemoryBoundaries()[1]);
        memory.remove(p.getMemoryBoundaries()[2]);
        return p;
    }

    public void readFromMemory(int processID){
        try (BufferedReader objectIn= new BufferedReader(new FileReader("disk.txt"))){
            String line=objectIn.readLine();
            String [] num;
            while (line!=null){
             num=line.split(",");
             int id= Integer.parseInt(num[0]);
             if (id==processID)
                 break;
             line= objectIn.readLine();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Object[] putPCB(String[] info){
        Object[] PCB=new Object[4];
        PCB[0]=Integer.parseInt(info[0]);
        PCB[1]=processState.valueOf(info[1]);
        PCB[2]=Integer.parseInt(info[2]);
        int[] memoryBounds=new int[3];
        memoryBounds[0]= Integer.parseInt(info[3]);
        memoryBounds[1]= Integer.parseInt(info[4]);
        memoryBounds[2]= Integer.parseInt(info[5]);
        PCB[3]=memoryBounds;
        return PCB;
    }

}
