import java.io.*;
import java.util.*;

public class Memory{
    private static final int MEMORY_SIZE = 40;
    private static final int PROCESS_SIZE=3;

    public static final ArrayList<Word> memory = new ArrayList<>(MEMORY_SIZE);


    public Memory() {
        Word processes=new Word("Processes",new Hashtable<String, Integer>());
        memory.add(processes);
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
    public Object remove(int index){
       return memory.remove(index).getData();
    }

    public int[] findSpots(){
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

    public Object getFromMemory(int index){
        return memory.get(index).data;
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
