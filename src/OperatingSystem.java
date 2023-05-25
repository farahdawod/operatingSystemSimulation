import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.*;
import java.io.*;
public class OperatingSystem {
    Memory memory = new Memory();
    List<Integer> unloadedProcesses;
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

    public void addToBlockedQueue(int processID, Resource azma) {
        blockedQueue.put(processID, azma);
        changeProcessState(processID,processState.BLOCKED);
        System.out.println("Processes in Ready Queue: ");
        for (Integer p1 : readyQueue) {
            System.out.print("Process" + p1 + ", ");
        }
        System.out.println("Processes in Blocked Queue: ");
        Iterator<Integer> itrB=blockedQueue.keys().asIterator();
        while (itrB.hasNext()) {
            int p2=itrB.next();
            System.out.print("Process" + p2 + ", ");
        }

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
        return (Object[]) memory.getFromMemory(getMemoryBoundaries(ProcessID)[0]);

    }
    public int [] getMemoryBoundaries (int processID){return (int[]) getPCBFromMemory(processID)[3];}
    public void setMemoryBoundaries (int processID){
        int[] memoryBoundaries=memory.findSpots();
        getPCBFromMemory(processID)[3]=memoryBoundaries;
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

    public void createProcess(String programPath, int arrivalTime) {
        //each process has 3 words in memory assigned to it.
        if (arrivalTime==currentTime){
            Process process= new Process();
            //process.setArrivalTime(currentTime);
            //checking if there's place in main memory to give the process its 3 words
            if (!memory.isThereEnoughSpace())  unloadedProcesses.add(unloadFromMemory()[0]);
            setMemoryBoundaries(process.getProcessID());

            //PCB of process will be placed in the first word that the process owns
            addToMemory("PCB of Process "+process.getProcessID(),getPCBFromMemory(process.getProcessID())
                    ,getMemoryBoundaries(process.getProcessID())[0]);
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
            addToMemory("Lines of code",code,getMemoryBoundaries(process.getProcessID())[1]);
            //memory.add("Lines of code of the process",
            //interpreter(programPath, process),process.getMemoryBoundaries()[1]);
            //processes.add(process);
            RoundRobin();
        }
    }

    public void interpreter (int processID) {
        changeProcessState(processID,processState.RUNNING);
        ArrayList<String> instructions=getProcessInstructions(processID);
        Hashtable<String,String> hashtableVariableValue=new Hashtable<>();
        if (getProcessVars(processID)!=null)
            hashtableVariableValue=getProcessVars(processID);
        //for (String instructionf:instructions) {
        int index = (int) getPCBFromMemory(processID)[2];
        String instructionf=instructions.get(index);
        System.out.print("The instruction currently executing is " + instructions.get(index));
        String[]instruction= instructionf.split(" ");
        switch (instruction[0]) {
            case "print" -> {systemCalls.print(hashtableVariableValue.get(instruction[1]));}
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
                            System.out.printf(input.getProcessID() + " is using the userInput semaphore");
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
                            System.out.printf(output.getProcessID() + " is using the userOutput semaphore");
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
                            System.out.printf(file.getProcessID() + " is using the file semaphore");
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
                            System.out.printf(input.getProcessID() + " is using the userInput semaphore");
                        }
                    }
                    case "userOutput" -> {
                        if (output.getProcessID() == processID) {
                            output.semSignal();
                            removeFromBlockedQueue(Resource.USEROUTPUT);
                            //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            incPC(processID);
                        } else {
                            System.out.printf(output.getProcessID() + " is using the userOutput semaphore");
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
                            System.out.printf(file.getProcessID() + " is using the file semaphore");
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
                }catch (Exception e){
                    throw new RuntimeException();
                }

            }
        }
        addToMemory("VariablesOfProcess",hashtableVariableValue,getMemoryBoundaries(processID)[2]);
    }

    /*        }
        //instructions.add(translation);

        addToReadyQueue(processID);

        return instructions;
    }


    public void execute(int processID) {
        changeProcessState(processID,processState.RUNNING);
        ArrayList<String> instructions = getProcessInstructions(processID);
        int index = (int) getPCBFromMemory(processID)[2];
        System.out.print("The instruction currently executing is " + instructions.get(index));
        String[] value = instructions.get(index).split(" ");
        7ot el variables ely hatla'eeha fel program hena ya ahmed, awel 7aga fel hashtable esm el variable w tany 7aga
        el value bta3et el variable ma7tota as a string
        if u wanna change the datastructure, go ahead
        Hashtable<String,String> hashtableVariableValue=new Hashtable<>();
        actual execution
        switch (value[0]) {
            case "print":
                System.out.print(value[1]);
                break;
            case "assign":

                if (value[2].equals("input")) {
                    System.out.print("Please enter a value");
                    Scanner sc = new Scanner(System.in);
                    if (sc.hasNextInt()) {
                        int x = sc.nextInt();
                    } else { String x = sc.nextLine(); }
                }
                else {


                }
                break;
            case "writeFile":
                try {
                    BufferedWriter wr = new BufferedWriter(new FileWriter(value[1]));
                    wr.write(value[2]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "readFile":
                try { BufferedReader br = new BufferedReader(new FileReader(value[1]));}
                catch (FileNotFoundException e) {throw new RuntimeException(e); }
                break;
            case "printFromTo":
                if (value[1].compareTo(value[2])<0) {
                    for (int i = Integer.parseInt(value[1]); i <= Integer.parseInt(value[2]); i++) {
                        System.out.println(i);
                    }
                }
                else {
                    for (int i = Integer.parseInt(value[1]); i >= Integer.parseInt(value[2]); i--) {
                        System.out.println(i);
                    }
                }
                break;
            case "semWait" -> {
                switch (value[1]) {
                    case "userInput" -> {
                        if (input.getProcessID() == 0) {
                            input.setProcessID(processID);
                            input.semWait();
                            //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            incPC(processID);
                        } else {
                            System.out.printf(input.getProcessID() + " is using the userInput semaphore");
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
                            System.out.printf(output.getProcessID() + " is using the userOutput semaphore");
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
                            System.out.printf(file.getProcessID() + " is using the file semaphore");
                            addToBlockedQueue(processID, Resource.FILE);
                        }
                    }
                }
            }
            case "semSignal" -> {
                switch (value[1]) {
                    case "userInput" -> {
                        if (input.getProcessID() == processID) {
                            input.semSignal();
                            removeFromBlockedQueue(Resource.USERINPUT);
                            //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            incPC(processID);
                        } else {
                            System.out.printf(input.getProcessID() + " is using the userInput semaphore");
                        }
                    }
                    case "userOutput" -> {
                        if (output.getProcessID() == processID) {
                            output.semSignal();
                            removeFromBlockedQueue(Resource.USEROUTPUT);
                            //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            incPC(processID);
                        } else {
                            System.out.printf(output.getProcessID() + " is using the userOutput semaphore");
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
                            System.out.printf(file.getProcessID() + " is using the file semaphore");
                            //process.changeProcessState(processState.BLOCKED);
                            //memory.addToBlockedQueue(process);
                        }
                    }

                }
            }
            default -> {
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                OutputStream outputStream = new ByteArrayOutputStream();
                int compilationResult = compiler.run(null, null, outputStream, instructions.get(index));
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("java");
                try {
                    engine.eval(instructions.get(index));
                    //process.setRemainingInstructions(process.getRemainingInstructions()-1);
                    incPC(processID);
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }

        }
        memory.add("Variables of the process",hashtableVariableValue,process.getMemoryBoundaries()[3]);
    }*/

    public void RoundRobin() {
        Queue<Integer> readyQueue = getReadyQueue();
        //Queue<Process> completedProcesses = new LinkedList<>();
        int currentProcess;
        while (!readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
            if (unloadedProcesses.contains(currentProcess)) swapToMemory(currentProcess);
            System.out.println("Process "+currentProcess+" is being executed");
            System.out.println("Processes in Ready Queue: ");
            for (int p1 : readyQueue) {
                System.out.print("Process " + p1 + ", ");
            }
            System.out.println("Processes in Blocked Queue: ");
            Iterator<Integer> itrB=blockedQueue.keys().asIterator();
            while (itrB.hasNext()) {
                int p2=itrB.next();
                System.out.print("Process " + p2 + ", ");
            }
            for (int i = 0; i < timeSlice; i++) {
                if (getProcessInstructions(currentProcess).size()==(int)getPCBFromMemory(currentProcess)[2]+1) {
                    //completedProcesses.add(currentProcess);
                    System.out.println("Processes in Ready Queue: ");
                    for (int p1 : readyQueue) {
                        System.out.print("Process " + p1 + ", ");
                    }
                    System.out.println("Processes in Blocked Queue: ");
                    itrB=blockedQueue.keys().asIterator();
                    while (itrB.hasNext()) {
                        int p2=itrB.next();
                        System.out.print("Process " + p2 + ", ");
                    }
                    changeProcessState(currentProcess,processState.FINISHED);
                    break;
                }
                interpreter(currentProcess);
                currentTime++;
            }
            if(getProcessInstructions(currentProcess).size()==(int)getPCBFromMemory(currentProcess)[2]+1){
                //completedProcesses.add(currentProcess);
                System.out.println("Processes in Ready Queue: ");
                for (int p1 : readyQueue)
                    System.out.print("Process " + p1 +", ");

                System.out.println("Processes in Blocked Queue: ");
                itrB=blockedQueue.keys().asIterator();
                while (itrB.hasNext()) {
                    int p2=itrB.next();
                    System.out.print("Process " + p2 + ", ");
                }
            }

            else {
                readyQueue.add(currentProcess);
            }
        }
    }
    public int[] unloadFromMemory(){
        int p;
        BufferedWriter objectOut = null;
        try {
            objectOut = new BufferedWriter(new FileWriter("disk.text"));
            objectOut.write("Process ");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (blockedQueue.isEmpty()) p = readyQueue.stream().findFirst().get();
        else p=blockedQueue.keys().nextElement();
        int[] result=new int[4];
        result[0]=p;
        int k=1;
        Object [] PCB= (Object[]) removeFromMemory(getMemoryBoundaries(p)[0]);
        for (Object o:PCB){
            try {
                if (o instanceof Integer)
                    objectOut.write((Integer) o);
                else if (o instanceof processState)
                    objectOut.write(((processState) o).name());
                else if (o instanceof int[]) {
                    for (int i:(int[])o) {
                        result[k++]=i;
                        objectOut.write(i);
                    }
                }
                objectOut.write(",");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> instruct= (ArrayList<String>) removeFromMemory(getMemoryBoundaries(p)[1]);
        try {
            objectOut.newLine();
            for (String i:instruct) {
                objectOut.write(i+",");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        Hashtable<String,String> va= (Hashtable<String, String>) removeFromMemory(getMemoryBoundaries(p)[2]);
        try  {
            objectOut.newLine();
            Iterator<String> itr=va.elements().asIterator();
            Iterator<String> itrv=va.keys().asIterator();
            objectOut.write("Variables ");
            while (itr.hasNext()&&itrv.hasNext()) {
                objectOut.write(itrv.next()+"="+itr.next() +",");
                objectOut.close();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        unloadedProcesses.add(p);
    /*  removeFromMemory(p+1);
        removeFromMemory(p+2);
        removeFromMemory(p+3);*/

        return result;
    }

    public void swapToMemory(int processID){
        BufferedReader reader=null;
        ArrayList<String> temp=new ArrayList<>();
        ArrayList<String> code=new ArrayList<>();
        String line;
        String[] g,g2;
        Object[] PCB = new Object[4];
        boolean flag=false;
        try {
            reader=new BufferedReader(new FileReader("disk.txt"));
            line=reader.readLine();
            while (line!=null){
                g=line.split(" ");
                if (g[0].compareTo("Process")==0){
                    if (!flag){
                        g2=g[1].split(",");
                        if (Integer.parseInt(g2[0])==processID) {
                            flag = true;
                            //PCB = new Object[4];
                            PCB[0] = processID;
                            PCB[1] = processState.valueOf(g2[1]);
                            PCB[2] = Integer.parseInt(g2[2]);
                            PCB[3] = Arrays.copyOfRange(unloadFromMemory(),1,3);
                            addToMemory("PCB Of Process "+ processID,PCB,((int [])PCB[3])[0]);

                        }
                    }
                    else break;
                }
                if (flag&& g[0].compareTo("Variables ")!=0){
                    g2=g[0].split(",");
                    for (int i = 0; i < g2.length; i++) {
                        code.add(g2[i]);
                    }
                    addToMemory("Lines of code",code,((int [])PCB[3])[1]);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //return p;
    }

}
