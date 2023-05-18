import java.util.ArrayList;

public class Process {

    private static int nextProcessID = 1;
    Object[] PCB;


    public Process (){
        PCB= new Object[4];
        PCB[0]=nextProcessID++;
        PCB[1]=processState.NEW;
        PCB[2]=0;
    }

    public int getProcessID(){
        return (int) PCB[0];
    }

    public static void changeProcessState(int ID,processState processState){


    }

}
