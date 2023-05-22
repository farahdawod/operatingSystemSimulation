public class Process {

    private static int nextProcessID = 1;
    private Object[] PCB;


    public Process (){
        PCB= new Object[4];
        PCB[0]=nextProcessID++; //ProcessID
        PCB[1]=processState.NEW;
        PCB[2]=0; //program counter
    }

    public int getProcessID(){
        return (int) PCB[0];
    }

    public Object[] getPCB() {
        return PCB;
    }

    public void setMemoryBoundaries (int[] memoryBoundaries){PCB[3]=memoryBoundaries;}
    public int [] getMemoryBoundaries (){return (int[]) PCB[3];}

    public void changeProcessState(processState processState){PCB[1]=processState;}

    public processState getProcessState(){return (processState) PCB[1];}

    public boolean semWait(){
        return false;
    }

    public boolean semSignal(Mutex mutex){
        return false;
    }

}
