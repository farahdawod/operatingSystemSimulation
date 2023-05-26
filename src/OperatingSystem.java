import java.util.*;
import java.io.*;
public class OperatingSystem {
    Memory memory = new Memory();
    List<Integer> unloadedProcesses;
    Hashtable<Integer,String> processes;
    Queue<Integer> readyQueue;
    Hashtable <Integer, Resource> blockedQueue;
    Mutex input = new Mutex(Resource.USERINPUT);
    Mutex output = new Mutex(Resource.USEROUTPUT);
    Mutex file = new Mutex(Resource.FILE);
    int timeSlice;     // Time slice for each process
    SystemCalls systemCalls;
    int currentTime=0;
    public OperatingSystem (int timeSlice){
        this.timeSlice=timeSlice;
        systemCalls=new SystemCalls(memory);
        readyQueue= new LinkedList<>();
        blockedQueue=new Hashtable<>() ;
        unloadedProcesses=new ArrayList<>();
        processes=new Hashtable<>(){
            @Override
            public synchronized String put(Integer key, String value) {
                String r=super.put(key, value);
                checkIfProcessArrived();
                return r;
            }
        };
    }
    public Queue<Integer> getReadyQueue(){
        return readyQueue;
    }
    public void addToReadyQueue(int processID) {
        readyQueue.add(processID);
        Object[] PCB=getPCBFromMemory(processID);
        PCB[1]=processState.READY;
    }
    public void changeProcessState(int processID, processState state){
        Object[] PCB=getPCBFromMemory(processID);
        PCB[1]=state;
    }
    public processState getProcessState(int processID){
        return (processState) getPCBFromMemory(processID)[1];
    }
    public void addToBlockedQueue(int processID, Resource azma) {
        readyQueue.remove(processID);
        blockedQueue.put(processID, azma);
        changeProcessState(processID,processState.BLOCKED);
        System.out.println("Processes in Ready Queue: ");
        for (int p1=0;p1<readyQueue.size();p1++) {
            if (p1!=readyQueue.size()-1)
                System.out.print("Process " + readyQueue.toArray()[p1] + ", ");
            else System.out.print("Process "+readyQueue.toArray()[p1]);
        }
        System.out.println("");
        System.out.println("Processes in Blocked Queue: ");
        Iterator<Integer> itrB=blockedQueue.keys().asIterator();
        while (itrB.hasNext()) {
            int p2=itrB.next();
            if (itrB.hasNext())
                System.out.print("Process " + p2 + ", ");
            else System.out.print("Process " + p2);
        }
        System.out.println("");

    }
    public void addToMemory(String descriptionOfData,Object data, int index){
        memory.add(descriptionOfData,data,index);
    }

    public void removeFromBlockedQueue(Resource faka){
        for (Map.Entry<Integer, Resource> finder : blockedQueue.entrySet()) {
            if (finder.getValue().compareTo(faka) == 0) {
                blockedQueue.remove(finder.getKey(), finder.getValue());
                addToReadyQueue(finder.getKey());
                return;
            }
        }
    }
    public Object removeFromMemory(int index){
        return memory.remove(index);
    }
    public Object [] getPCBFromMemory(int ProcessID){
        return memory.getPCB(ProcessID);
    }
    public Object getFromMemory(int index){
        return memory.getFromMemory(index);
    }
    public int [] getMemoryBoundaries (int processID){return (int[]) getPCBFromMemory(processID)[3];}
    public int[] setMemoryBoundaries (){
        return memory.findSpots();
        //getPCBFromMemory(processID)[3]=memoryBoundaries;
    }
    public void setPCB(int processID,int[]memoryB){
        Object[] PCB= new Object[4];
        PCB[0]=processID;
        PCB[1]=processState.NEW;
        PCB[2]=0; //program counter
        PCB[3]=memoryB;
        addToMemory("PCB of Process "+processID,PCB,memoryB[0]);
        memory.addP(PCB);
    }
    public ArrayList<String> getProcessInstructions (int processID){
        return (ArrayList<String>) memory.getFromMemory(getMemoryBoundaries(processID)[1]);
    }
    public Hashtable<String,String> getProcessVars(int processID){
        return (Hashtable<String,String>) memory.getFromMemory(getMemoryBoundaries(processID)[2]);
    }
    public void incPC(int processID){
        Object[] PCB=getPCBFromMemory(processID);
        PCB[2]=(int) PCB[2]+1;
    }
    public void checkIfProcessArrived(){
        if (processes.isEmpty())
            return;
        else
            if (processes.containsKey(currentTime)){
                createProcess(processes.get(currentTime));
                processes.remove(currentTime);
            }
    }

    public void createProcess(String programPath) {
        //each process has 3 words in memory assigned to it.
            Process process= new Process();
            //process.setArrivalTime(currentTime);
            //checking if there's place in main memory to give the process its 3 words
            if (!memory.isThereEnoughSpace()) System.out.println("Process "+unloadFromMemory()[0]
                    +" is unloaded from disk to make space for Process "+process.getProcessID());
            int [] b=setMemoryBoundaries();
            setPCB(process.getProcessID(),b);
            //PCB of process will be placed in the first word that the process owns
            //addToMemory("PCB of Process "+process.getProcessID(),getPCBFromMemory(process.getProcessID())
                   // ,b[0]);
            //lines of code of process will be placed in the second word that the process owns
            FileReader p1; BufferedReader reader; String line;
            ArrayList<String> code=new ArrayList<>();
            try {
                p1 = new FileReader(programPath);
                reader = new BufferedReader(p1);
                line = reader.readLine();
                while(line != null) {
                    code.add(line);
                    line=reader.readLine();
                }
            }
            catch (IOException e) {
                System.out.println("Error reading program file: " + e.getMessage());
            }
            addToMemory("Lines of code of Process "+process.getProcessID(),code,getMemoryBoundaries(process.getProcessID())[1]);
            //memory.add("Lines of code of the process",
            //interpreter(programPath, process),process.getMemoryBoundaries()[1]);
            //processes.add(process);
            addToReadyQueue(process.getProcessID());
            //RoundRobin();

    }
    public void interpreter (int processID) {
        changeProcessState(processID,processState.RUNNING);
        System.out.println("Process "+processID+" is being executed");
        ArrayList<String> instructions=getProcessInstructions(processID);
        Hashtable<String,String> hashtableVariableValue=new Hashtable<>();
        if (getProcessVars(processID)!=null)
            hashtableVariableValue=getProcessVars(processID);
        //for (String instructionf:instructions) {
        int index = (int) getPCBFromMemory(processID)[2];
        String instructionf=instructions.get(index);
        System.out.println("The instruction currently executing is " + instructions.get(index));
        String[]instruction= instructionf.split(" ");
        switch (instruction[0]) {
            case "print" -> {
                systemCalls.print(hashtableVariableValue.get(instruction[1]));
                incPC(processID);}
            case "assign" -> {
                if (instruction[2].compareTo("input") == 0)
                    hashtableVariableValue.put(instruction[1],systemCalls.takeInput());

                else if (instruction[2].compareTo("readFile") == 0) {
                    try {
                    if (hashtableVariableValue.containsKey(instruction[3]))
                        hashtableVariableValue.put(instruction[1],
                                systemCalls.readFromDisk(hashtableVariableValue.get(instruction[3])));
                    else hashtableVariableValue.put(instruction[1], systemCalls.readFromDisk(instruction[3]));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                else if (hashtableVariableValue.containsKey(instruction[2]))
                    hashtableVariableValue.put(instruction[1], hashtableVariableValue.get(instruction[2]));
                else hashtableVariableValue.put(instruction[1],instruction[2]);
                incPC(processID);
            }
            case "writeFile" ->{
                if (hashtableVariableValue.containsKey(instruction[2])) {
                    if (hashtableVariableValue.containsKey(instruction[1]))
                        systemCalls.writeToDisk(hashtableVariableValue.get(instruction[2])
                                , hashtableVariableValue.get(instruction[1]));
                    else systemCalls.writeToDisk(hashtableVariableValue.get(instruction[2])
                            , instruction[1]);
                }
                else {
                    if (hashtableVariableValue.containsKey(instruction[1]))
                        systemCalls.writeToDisk(instruction[2]
                                , hashtableVariableValue.get(instruction[1]));
                    else systemCalls.writeToDisk(instruction[2]
                            , instruction[1]);
                }
                incPC(processID);
            }
            case "printFromTo" ->{
                int v1,v2;
                if (hashtableVariableValue.containsKey(instruction[2])) {
                    if (hashtableVariableValue.containsKey(instruction[1])){
                        v1=Integer.parseInt(hashtableVariableValue.get(instruction[1]));
                        v2=Integer.parseInt(hashtableVariableValue.get(instruction[2]));
                    }
                    else {
                        v1=Integer.parseInt(instruction[1]);
                        v2=Integer.parseInt(hashtableVariableValue.get(instruction[2]));
                    }
                }
                else{
                    if (hashtableVariableValue.containsKey(instruction[1])){
                        v1=Integer.parseInt(instruction[2]);
                        v2=Integer.parseInt(hashtableVariableValue.get(instruction[1]));
                    }
                    else {
                        v1=Integer.parseInt(instruction[2]);
                        v2=Integer.parseInt(instruction[1]);
                    }
                }
                if (v1>v2)
                    for (int i = v2; i <=v1 ; i++) systemCalls.print(String.valueOf(i));
                else for (int i = v1; i <=v2 ; i++) systemCalls.print(String.valueOf(i));
                incPC(processID);
            }
            case "semWait" -> {
                switch (instruction[1]) {
                    case "userInput" -> {
                        if (input.getProcessID() == 0) {
                            input.setProcessID(processID);
                            input.semWait();
                            //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            incPC(processID);
                        } else {
                            System.out.printf("Process "+input.getProcessID() + " is using the userInput semaphore \n");
                            addToBlockedQueue(processID,Resource.USERINPUT);
                        }
                    }
                    case "userOutput" -> {
                        if (output.getProcessID() == 0) {
                            output.setProcessID(processID);
                            output.semWait();
                            //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            incPC(processID);
                        } else {
                            System.out.printf("Process "+output.getProcessID() + " is using the userOutput semaphore \n");
                            addToBlockedQueue(processID, Resource.USEROUTPUT);
                        }
                    }
                    default -> {
                        if (file.getProcessID() == 0) {
                            file.setProcessID(processID);
                            file.semWait();
                            //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            incPC(processID);
                        } else {
                            System.out.printf("Process "+file.getProcessID() + " is using the file semaphore \n");
                            addToBlockedQueue(processID, Resource.FILE);
                        }
                    }
                }
            }
            case "semSignal" -> {
                switch (instruction[1]) {
                    case "userInput" -> {
                        if (input.getProcessID() == processID) {
                            input.semSignal();
                            removeFromBlockedQueue(Resource.USERINPUT);
                            //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            incPC(processID);
                        } else {
                            System.out.printf(input.getProcessID() + " is using the userInput semaphore\n");
                        }
                    }
                    case "userOutput" -> {
                        if (output.getProcessID() == processID) {
                            output.semSignal();
                            removeFromBlockedQueue(Resource.USEROUTPUT);
                            //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            incPC(processID);
                        } else {
                            System.out.printf(output.getProcessID() + " is using the userOutput semaphore\n");
                            //process.changeProcessState(processState.BLOCKED);
                            //memory.addToBlockedQueue(process);
                        }
                    }
                    default -> {
                        if (file.getProcessID() == processID) {
                            file.semSignal();
                            removeFromBlockedQueue(Resource.FILE);
                            //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            incPC(processID);
                        } else {
                            System.out.printf(file.getProcessID() + " is using the file semaphore \n");
                            //process.changeProcessState(processState.BLOCKED);
                            //memory.addToBlockedQueue(process);
                        }
                    }

                }
            }
            default -> {
                try {
                    if (hashtableVariableValue.containsKey(instruction[1]))
                        systemCalls.readFromDisk(hashtableVariableValue.get(instruction[1]));
                    else systemCalls.readFromDisk(instruction[1]);
                    incPC(processID);
                }catch (Exception e){
                    throw new RuntimeException();
                }

            }
        }
        if (!hashtableVariableValue.equals(getProcessVars(processID)))
            addToMemory("Variables Of Process "+processID,hashtableVariableValue,getMemoryBoundaries(processID)[2]);
        if (getProcessState(processID)==processState.RUNNING) changeProcessState(processID,processState.READY);
        if (getProcessState(processID)!=processState.BLOCKED)System.out.print("Instruction executed Successfully\n");
        else System.out.println("Couldn't execute instruction, Process got blocked\n");
    }
    public void printMemory(){
        System.out.print("Memory [ \n");
        int i=0;
        //while (true){
            //if (memory.getFromMemory(i)!=null)
        System.out.print(memory.getW(unloadedProcesses));
        //    else break;
        //    i++;
       // }
        System.out.print("]\n");
    }
    public void execute(){
        scheduler();
    }
    public void scheduler() {
        System.out.println("Current Clock Cycle: "+(currentTime));
        printMemory();
        Queue<Integer> readyQueue = getReadyQueue();
        //Queue<Process> completedProcesses = new LinkedList<>();
        int currentProcess;
        while (!readyQueue.isEmpty()) {
            currentProcess = readyQueue.peek();
            if (unloadedProcesses.contains(currentProcess)){
                System.out.println("Process "+currentProcess+" is swapped out of disk and Process "+
                        swapToMemory(currentProcess)+" is swapped into disk");
            }
            System.out.println("Processes in Ready Queue: ");
            for (int p1=0;p1<readyQueue.size();p1++) {
                if (p1!=readyQueue.size()-1)
                    System.out.print("Process " + readyQueue.toArray()[p1] + ", ");
                else System.out.print("Process "+readyQueue.toArray()[p1]);
            }
            System.out.println("");
            System.out.println("Processes in Blocked Queue: ");
            Iterator<Integer> itrB=blockedQueue.keys().asIterator();
            while (itrB.hasNext()) {
                int p2=itrB.next();
                if (itrB.hasNext())
                    System.out.print("Process " + p2 + ", ");
                else System.out.print("Process " + p2);
            }
            System.out.println("");
            for (int i = 0; i < timeSlice; i++) {
                if (getProcessState(currentProcess).compareTo(processState.READY)!=0) continue;
                interpreter(currentProcess);
                System.out.println("End of Clock Cycle: "+(currentTime++));
                if (getProcessInstructions(currentProcess).size()==(int)getPCBFromMemory(currentProcess)[2]) {
                    //completedProcesses.add(currentProcess);
                    readyQueue.remove(currentProcess);
                    System.out.println("Process "+currentProcess+" finished");
                    System.out.println("Processes in Ready Queue: ");
                    for (int p1=0;p1<readyQueue.size();p1++) {
                        if (p1!=readyQueue.size()-1)
                            System.out.print("Process " + readyQueue.toArray()[p1] + ", ");
                        else System.out.print("Process "+readyQueue.toArray()[p1]);
                    }
                    System.out.println("");
                    System.out.println("Processes in Blocked Queue: ");
                    itrB=blockedQueue.keys().asIterator();
                    while (itrB.hasNext()) {
                        int p2=itrB.next();
                        if (itrB.hasNext())
                            System.out.print("Process " + p2 + ", ");
                        else System.out.print("Process " + p2);
                    }
                    System.out.println("");
                    changeProcessState(currentProcess,processState.FINISHED);
                    processes.put(currentProcess,processState.FINISHED.name());
                }
                checkIfProcessArrived();
                printMemory();
            }
            if (getProcessState(currentProcess).compareTo(processState.FINISHED)!=0)
                if(getProcessInstructions(currentProcess).size()==(int)getPCBFromMemory(currentProcess)[2]){
                    //completedProcesses.add(currentProcess);
                    readyQueue.remove(currentProcess);
                    System.out.println("Process "+currentProcess+" finished");
                    System.out.println("Processes in Ready Queue: ");
                    for (int p1=0;p1<readyQueue.size();p1++) {
                        if (p1!=readyQueue.size()-1)
                            System.out.print("Process " + readyQueue.toArray()[p1] + ", ");
                        else System.out.print("Process "+readyQueue.toArray()[p1]);
                    }
                    System.out.println("");
                    System.out.println("Processes in Blocked Queue: ");
                    itrB=blockedQueue.keys().asIterator();
                    while (itrB.hasNext()) {
                        int p2=itrB.next();
                        if (itrB.hasNext())
                            System.out.print("Process " + p2 + ", ");
                        else System.out.print("Process " + p2);
                    }
                    System.out.println("");
                    changeProcessState(currentProcess,processState.FINISHED);
                    processes.put(currentProcess,processState.FINISHED.name());
                }
            else if (getProcessState(currentProcess).compareTo(processState.BLOCKED)!=0){
                readyQueue.poll();
                readyQueue.add(currentProcess);
            }
        }
    }
    public int[] unloadFromMemory(){
        int p=-1;
        BufferedWriter objectOut;
        try {
            objectOut = new BufferedWriter(new FileWriter("disk.txt",true));
            objectOut.write("Process ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!blockedQueue.isEmpty()) {
            Iterator<Integer> bitr=blockedQueue.keys().asIterator();
            while (bitr.hasNext()) {
                p = bitr.next();
                if (!unloadedProcesses.contains(p))
                    break;
                p=-1;
            }
            if (p==-1)
                for (Integer integer : readyQueue) {
                    p = integer;
                    if (!unloadedProcesses.contains(p))
                        break;
                }
        }
        else if (processes.containsValue(processState.FINISHED.name())){
            Iterator<Integer> itrp=processes.keys().asIterator();
            int pt;
            while (itrp.hasNext()){
                pt=itrp.next();
                if (processes.get(pt).compareTo(processState.FINISHED.name())==0){
                    p=pt;
                    break;
                }
            }
        }
        else{
            for (int integer=readyQueue.size()-1 ;integer>-1;integer--) {
                p = (int) readyQueue.toArray()[integer];
                if (!unloadedProcesses.contains(p))
                    break;
            }
        }
        int[] result=new int[4];
        result[0]=p;
        int k=1;
        Object [] PCB= (Object[]) getFromMemory(getMemoryBoundaries(p)[0]);
        for (Object o:PCB){
            try {
                if (o instanceof Integer)
                    objectOut.write(String.valueOf( o));
                else if (o instanceof processState)
                    objectOut.write(((processState) o).name());
                else if (o instanceof int[]) {
                    for (int i:(int[])o) {
                        result[k++]=i;
                        objectOut.write(String.valueOf(i));
                    }
                }
                objectOut.write(",");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> instruct= (ArrayList<String>) getFromMemory(getMemoryBoundaries(p)[1]);
        try {
            objectOut.newLine();
            for (String i:instruct) {
                objectOut.write(i);
                objectOut.newLine();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        Hashtable<String,String> va= (Hashtable<String, String>) getFromMemory(getMemoryBoundaries(p)[2]);
        try  {
            objectOut.newLine();
            Iterator<String> itr=va.elements().asIterator();
            Iterator<String> itrv=va.keys().asIterator();
            objectOut.write("Variables ");
            while (itr.hasNext()&&itrv.hasNext()) {
                objectOut.write(itrv.next()+"="+itr.next() +",");
            }
            objectOut.newLine();
            objectOut.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }
        unloadedProcesses.add(p);
        removeFromMemory(getMemoryBoundaries(p)[2]);
        removeFromMemory(getMemoryBoundaries(p)[1]);
        removeFromMemory(getMemoryBoundaries(p)[0]);
        return result;
    }

    public int swapToMemory(int processID){
        BufferedReader reader=null;
        ArrayList<String> temp=new ArrayList<>();
        ArrayList<String> code=new ArrayList<>();
        Hashtable<String,String> hashtable=new Hashtable<>();
        int[] premoved=unloadFromMemory();
        String line;
        String[] g,g2;
        Object[] PCB = new Object[4];
        boolean flag=false;
        try {
            reader=new BufferedReader(new FileReader("disk.txt"));
            line=reader.readLine();
            while (line!=null){
                if (line.compareTo("")!=0){
                g=line.split(" ");
                if (g[0].compareTo("Process")==0 && !flag){
                    g2=g[1].split(",");
                    if (Integer.parseInt(g2[0])==processID) {
                        flag = true;
                        //PCB = new Object[4];
                        PCB[0] = processID;
                        PCB[1] = processState.valueOf(g2[1]);
                        PCB[2] = Integer.parseInt(g2[2]);
                        PCB[3] = Arrays.copyOfRange(premoved,1,4);
                        addToMemory("PCB Of Process "+ processID,PCB,((int [])PCB[3])[0]);
                        getPCBFromMemory(processID)[3]=PCB[3];
                    }
                    else temp.add(line);
                }
                else if (flag&& g[0].compareTo("Variables")!=0){
                    //g2=g[0].split(",");
                    code.add(line);
                }
                else if(flag) {
                    if (g.length>=2){
                    g[1]= line.substring(10);
                    g2=g[1].split(",");
                    for (String s : g2) {
                        String[] var = s.split("=");
                        hashtable.put(var[0], var[1]);
                    }
                    }
                    addToMemory("Lines of code of Process "+processID,code,((int [])PCB[3])[1]);
                    addToMemory("Variables Of Process "+processID,hashtable,((int [])PCB[3])[2]);
                    break;
                }
                }
                else
                    if (line.length()>1) temp.add(line);
                line=reader.readLine();
            }
            line=reader.readLine();
            while (line!=null){
                temp.add(line);
                line=reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        unloadedProcesses.remove(Integer.valueOf(processID));
        try {
            FileWriter fileWriter=new FileWriter("disk.txt",false);
                for (String s:temp) {
                    if (s.toCharArray().length!=0) fileWriter.write(s + "\n");
                }
            fileWriter.close();
        }catch (IOException e){
            System.out.println("An error occurred while deleting the text file contents: " + e.getMessage());
        }
        getPCBFromMemory(premoved[0])[3]=null;
        return premoved[0];
    }

    public static void main(String[] args){
        OperatingSystem os=new OperatingSystem(2);
        os.processes.put(0,"Program_1.txt");
        os.processes.put(1,"Program_2.txt");
        os.processes.put(4,"Program_3.txt");
        os.execute();

        try {
            BufferedWriter objectOut = new BufferedWriter(new FileWriter("disk.txt",false));
            objectOut.write("");
            objectOut.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
