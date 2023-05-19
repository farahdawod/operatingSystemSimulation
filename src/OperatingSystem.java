import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class OperatingSystem {
    Memory memory;
    List<Process> processes;

    public void createProcess(String programPath){
        //each process has 3 words in memory assigned to it.
        Process process= new Process();
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


        process.changeProcessState(processState.READY);
        memory.addToReadyQueue(process);
        return instructions;
    }

}
