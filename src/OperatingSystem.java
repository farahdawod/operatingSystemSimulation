import java.util.ArrayList;
import java.util.List;

public class OperatingSystem {
    Memory memory;
    List<Process> processes;

    public void createProcess(String filePath){
        Process process= new Process();
        memory.addToReadyQueue(process);
    }

    public ArrayList<String> interpreter (String programPath){
        ArrayList<String> instructions=new ArrayList<>();

        //PCB[1]= processState.READY;


        return instructions;
    }

}
