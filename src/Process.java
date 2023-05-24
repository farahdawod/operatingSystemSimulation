public class Process {

    private static int nextProcessID = 1;
    private Object[] PCB;
    private int arrivalTime;
    private int remainingInstructions;


    public Process (){
        PCB= new Object[4];
        PCB[0]=nextProcessID++; //ProcessID
        PCB[1]=processState.NEW;
        PCB[2]=0; //program counter
        //this.arrivalTime = arrivalTime;
        //this.remainingInstructions = 2;
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

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getRemainingInstructions() {
        return remainingInstructions;
    }

    public void setRemainingInstructions(int remainingInstructions) {
        this.remainingInstructions = remainingInstructions;
    }
    public int getPC(){
        return (int) getPCB()[2];
    }
    public void setPC(int PC){
        getPCB()[2]=PC;
    }
}
