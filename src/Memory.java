import java.io.*;
import java.util.*;

public class Memory{
    private static final int MEMORY_SIZE = 8;
    private static final int PROCESS_SIZE=3;

    public static ArrayList<Word> memory;


    public Memory() {
        memory=new ArrayList<>(MEMORY_SIZE);
        Word processes=new Word("Processes PCBs",new Hashtable<Integer,Object[]>());
        memory.add(processes);

    }

    public boolean isThereEnoughSpace(){
        Iterator<Word> memoryIrt=memory.iterator();
        int i=-1;
        while (memoryIrt.hasNext()){
            i++;
            memoryIrt.next();
        }
        return MEMORY_SIZE > i+3;
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
    public void replace(int index,String descriptionOfData){
        if (memory.get(index).getTypeOfDataStored().compareTo(descriptionOfData)==0){
            memory.remove(index);
        }
        return ;
    }

    public void add(String descriptionOfData,Object data, int index){
        Word newData=new Word(descriptionOfData,data);
        if (memory.size()>index)
            replace(index,descriptionOfData);
        memory.add(index, newData);
    }
    public String getW(List<Integer>onD){
        String result="";
        String s;
        String[] ss;
        for (int index=0;index< memory.size();index++){
            s=memory.get(index).getTypeOfDataStored();
            ss=s.split(" ");
            if (ss[0].compareTo("PCB")==0)
                continue;
            result=result+s+": \n";
            if (ss[0].compareTo("Processes")==-0){
                Hashtable<Integer,Object[]> b = (Hashtable<Integer, Object[]>) memory.get(index).getData();
                Iterator io=b.elements().asIterator();
                Object[] o;
                while (io.hasNext()){
                    o= (Object[]) io.next();
                    result+="Process "+o[0]+" PCB: ";
                    result+="Process State: " +((processState) o[1]).name();
                    result+=", Program Counter = "+o[2];
                    if (!onD.contains((int)o[0])){
                    result += ", Memory Boundaries: [";
                    int[] pb=(int[])o[3];
                    for (int i=0;i<pb.length;i++) {
                        if (i==pb.length-1)
                            result+=pb[i]+"]";
                        else
                            result +=pb[i]+",";
                    }
                }
                    else result+=", Saved on Disk";
                    result+="\n";
                }
            }
            else if (ss[0].compareTo("Lines")==0)
                for (String i:(ArrayList<String>)memory.get(index).getData()) result+=i+"\n";
            else if (ss[0].compareTo("Variables")==0){
                Hashtable<String,String> b = (Hashtable<String, String>) memory.get(index).getData();
                Iterator ivar=b.keys().asIterator();
                Iterator ival=b.elements().asIterator();
                String svar,sval;
                while (ivar.hasNext()&&ival.hasNext()){
                    svar= (String) ivar.next();
                    sval= (String) ival.next();
                    if (ivar.hasNext()&&ival.hasNext()) result+=svar+" = "+sval+", ";
                    else result+=svar+" = "+sval;
                }
            }
            result+="\n";
        }
            return result;
    }
    public Object getFromMemory(int index){
        if (index>= memory.size())
            return null;
        return memory.get(index).data;
    }
    public Object[] getPCB(int index){
        Hashtable<Integer,Object[]> b = (Hashtable<Integer, Object[]>) memory.get(0).getData();
        return b.get(index);
    }
    public void addP(Object[] p){
        Hashtable<Integer,Object[]> b = (Hashtable<Integer, Object[]>) memory.get(0).getData();
        b.put((Integer) p[0],p);
        memory.get(0).data=b;
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
