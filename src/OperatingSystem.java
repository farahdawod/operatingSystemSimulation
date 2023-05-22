import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class OperatingSystem {
    Memory memory;
    List<Process> processes;

    public void createProcess(String programPath){
        //each process has 3 words in memory assigned to it.
        Process process= new Process();
        process.setArrivalTime(currentTime);
        //checking if there's place in main memory to give the process its 3 words
        if (memory.isThereEnoughSpace())
            process.setMemoryBoundaries(memory.assignPlaceForProcess());
        //PCB of process will be placed in the first word that the process owns
        memory.add("PCB of Process"+process.getProcessID(),process.getPCB(),process.getMemoryBoundaries()[0]);
        //lines of code of process will be placed in the second word that the process owns
        memory.add("Lines of code of the process",
                interpreter(programPath, process),process.getMemoryBoundaries()[1]);
        processes.add(process);
    }

    public ArrayList<String> interpreter (String programPath, Process process){
        ArrayList<String> instructions=new ArrayList<>();
        //7oty el variables ely hatla'eeha fel program hena ya mayar
        //if u wanna change the datastructure, go ahead
        Hashtable<String,Object> hashtableVariableValue=new Hashtable<>();
        try {
            FileReader p1 = new FileReader(programPath);
            BufferedReader reader = new BufferedReader(p1);
            String line = reader.readLine();
            String translation="";
            while(line != null){
                String[]instruction= line.split(" ");
                switch (instruction[0]) {
                    case "print" -> translation = "System.out.print(" + instruction[1] + ");";
                    case "assign" -> {
                        if (instruction[2].compareTo("input") == 0)
                            translation = "System.out.print(\"Please enter a value\");" + '\n'
                                    + "Scanner sc = new Scanner(System.in);" + '\n'
                                    + "if (sc.hasNextInt()) { int " + instruction[1] + " = sc.nextInt(); } \n" +
                                    "else { String" + instruction[1] + "= sc.nextLine(); }";
                        else if (instruction[2].compareTo("readFile") == 0)
                            translation = "try { BufferedReader br = new BufferedReader(new FileReader(" + instruction[3] + "));\n" +
                                    "String " + instruction[1] + "= br.readLine(); } \n" +
                                    " catch (FileNotFoundException e) {throw new RuntimeException(e);\n" +
                                    "} catch( IOException e) {System.out.println(\"Error reading program file: \" + e.getMessage());\n" +
                                    "}";
                    }
                    case "writeFile" ->
                            translation = " try { BufferedWriter wr = new BufferedWriter(new FileWriter(" + instruction[1] + ")); \n" +
                                    "wr.write(" + instruction[2] + ");\n" +
                                    "} catch (IOException e) {\n" +
                                    "throw new RuntimeException(e);}";
                    case "printFromTo" ->
                            translation = "if (" + instruction[1] + ".compareTo(" + instruction[2] + ")<0) {\n" +
                                    "                    for (int i = Integer.parseInt(" + instruction[1] + "); i <= Integer.parseInt(" + instruction[2] + "); i++) {\n" +
                                    "                        System.out.println(i);\n" +
                                    "                    }\n" +
                                    "                }\n" +
                                    "                else {\n" +
                                    "                    for (int i = Integer.parseInt(" + instruction[1] + "); i >= Integer.parseInt(" + instruction[2] + "); i--) {\n" +
                                    "                        System.out.println(i);\n" +
                                    "                    }\n" +
                                    "                }";
                    default -> translation = line;
                }
                instructions.add(translation);
                line = reader.readLine();
            }
            memory.addToReadyQueue(process);
            process.setRemainingInstructions(instructions.size());

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch( IOException e) {
            System.out.println("Error reading program file: " + e.getMessage());
        }

        return instructions;
    }

    public int howManyInstructions (Process process){
        ArrayList<String> instructions= (ArrayList<String>) memory.getFromMemory(process.getMemoryBoundaries()[1]);
        return instructions.size();
    }
    public ArrayList<String> getProcessInstructions (Process process){
        return (ArrayList<String>) memory.getFromMemory(process.getMemoryBoundaries()[1]);
    }

//    public void aykhara(){
//        RoundRobin schedueler=new RoundRobin(memory.readyQueue,processes );
//    }

    public void execute(Process process) {
        process.changeProcessState(processState.RUNNING);
        ArrayList<String> instructions = getProcessInstructions(process);
        int index = process.getPC();
        System.out.print("The instruction currently executing is " + instructions.get(process.getPC()));
        String[] value = instructions.get(index).split(" ");

        switch (value[0]) {
            /*case "print":
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
                break;*/
            case "semWait" -> {
                switch (value[1]) {
                    case "userInput" -> {
                        if (input.getProcessID() == 0) {
                            input.setProcessID(process.getProcessID());
                            input.semWait();
                            process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            process.setPC(process.getPC()+1);
                        } else {
                            System.out.printf(input.getProcessID() + " is using the userInput semaphore");
                            memory.addToBlockedQueue(process,Resource.USERINPUT);
                        }
                    }
                    case "userOutput" -> {
                        if (output.getProcessID() == 0) {
                            output.setProcessID(process.getProcessID());
                            output.semWait();
                            process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            process.setPC(process.getPC()+1);
                        } else {
                            System.out.printf(output.getProcessID() + " is using the userOutput semaphore");
                            memory.addToBlockedQueue(process, Resource.USEROUTPUT);
                        }
                    }
                    default -> {
                        if (file.getProcessID() == 0) {
                            file.setProcessID(process.getProcessID());
                            file.semWait();
                            process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            process.setPC(process.getPC()+1);
                        } else {
                            System.out.printf(file.getProcessID() + " is using the file semaphore");
                            memory.addToBlockedQueue(process, Resource.FILE);
                        }
                    }
                }
            }
            case "semSignal" -> {
                switch (value[1]) {
                    case "userInput" -> {
                        if (input.getProcessID() == process.getProcessID()) {
                            input.semSignal();
                            memory.removeFromBlockedQueue(Resource.USERINPUT);
                            process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            process.setPC(process.getPC()+1);
                        } else {
                            System.out.printf(input.getProcessID() + " is using the userInput semaphore");
                        }
                    }
                    case "userOutput" -> {
                        if (output.getProcessID() == process.getProcessID()) {
                            output.semSignal();
                            memory.removeFromBlockedQueue(Resource.USEROUTPUT);
                            process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            process.setPC(process.getPC()+1);
                        } else {
                            System.out.printf(output.getProcessID() + " is using the userOutput semaphore");
                            //process.changeProcessState(processState.BLOCKED);
                            //memory.addToBlockedQueue(process);
                        }
                    }
                    default -> {
                        if (file.getProcessID() == process.getProcessID()) {
                            file.semSignal();
                            memory.removeFromBlockedQueue(Resource.FILE);
                            process.setRemainingInstructions(process.getRemainingInstructions()-1);
                            process.setPC(process.getPC()+1);
                        } else {
                            System.out.printf(file.getProcessID() + " is using the file semaphore");
                            //process.changeProcessState(processState.BLOCKED);
                            //memory.addToBlockedQueue(process);
                        }
                    }

                }
            }
            default -> {
                /*JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                OutputStream outputStream = new ByteArrayOutputStream();
                int compilationResult = compiler.run(null, null, outputStream, instructions.get(index));*/
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("java");
                try {
                    engine.eval(instructions.get(index));
                    process.setRemainingInstructions(process.getRemainingInstructions()-1);
                    process.setPC(process.getPC()+1);
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void RoundRobin() {
        Queue<Process> readyQueue = memory.getReadyQueue();
        Queue<Process> completedProcesses = new LinkedList<>();
        Process currentProcess;

        while (!readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
            System.out.println("Process"+currentProcess.getProcessID()+"is being executed");
            System.out.println("Processes in Ready Queue: ");
            for (Process p1 : readyQueue) {
                System.out.print("Process" + p1.getProcessID() + ", ");
            }
            System.out.println("Processes in Blocked Queue: ");
            Iterator<Process> itrB=memory.blockedQueue.keys().asIterator();
            while (itrB.hasNext()) {
                Process p2=itrB.next();
                System.out.print("Process" + p2.getProcessID() + ", ");
            }
            for (int i = 0; i < timeSlice; i++) {
                if (currentProcess.getRemainingInstructions()==0) {
                    completedProcesses.add(currentProcess);
                    System.out.println("Processes in Ready Queue: ");
                    for (Process p1 : readyQueue) {
                        System.out.print("Process" + p1.getProcessID() + ", ");
                    }
                    System.out.println("Processes in Blocked Queue: ");
                    itrB=memory.blockedQueue.keys().asIterator();
                    while (itrB.hasNext()) {
                        Process p2=itrB.next();
                        System.out.print("Process" + p2.getProcessID() + ", ");
                    }

                    break;
                }
                execute(currentProcess);
                currentTime++;
            }
            if(currentProcess.getRemainingInstructions()==0){
                completedProcesses.add(currentProcess);
                System.out.println("Processes in Ready Queue: ");
                for (Process p1 : readyQueue)
                    System.out.print("Process" + p1.getProcessID() +", ");

                System.out.println("Processes in Blocked Queue: ");
                itrB=memory.blockedQueue.keys().asIterator();
                while (itrB.hasNext()) {
                    Process p2=itrB.next();
                    System.out.print("Process" + p2.getProcessID() + ", ");
                }
            }

            else {
                readyQueue.add(currentProcess);
            }
        }
    }

}
