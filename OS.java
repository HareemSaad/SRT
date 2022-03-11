import java.util.*;

public class OS {

    //class variable - to hold the adress of the ONE object of OS
    private static OS os = null;
    // data feilds
    private Process PC;
    private Process pd;
    private int IR;
    private int totalExecTime = 0;
    private Queue pq;
    private SortedQueue sq;
    // sub class - each object of Process represents a process
    static class Process {
        private String id;
        private int executionTime = 0; //time to finish execution -- here it is the same as instruction number
        private int startTime = 0;
        private int endTime = 0;
        private int turnAroundTime = 0;
        private int waitingTime = 0;
        private double utilizationTime = 0;
        private int instructionNumber; //size of process
        private int arrivalTime = -1; //when process enters the queue
        private int remainingTime = 0;
        private int resumeIndex = 0;

        private ArrayList<Integer> arr = new ArrayList<Integer>(); //a process is an array with above properties

        //constructor makes node and methodically sets their IDs
        public Process(int count) {
            this.id = "p" + count;
        }
    
        public String getId() {
            return id;
        }
    
        public int getExecutionTime() {
            return executionTime;
        }
    
        public void setExecutionTime(int executionTime) {
            this.executionTime = executionTime;
        }
    
        public int getArrivalTime() {
            return arrivalTime;
        }
    
        public void setArrivalTime(int arrivalTime) {
            this.arrivalTime = arrivalTime;
        }
    
        public ArrayList<Integer> getArray() {
            return arr;
        }
    
        public int getInstructionNumber() {
            return instructionNumber;
        }
    
        public void setInstructionNumber(int instructionNumber) {
            this.instructionNumber = instructionNumber;
        }
    
        public void setArray(ArrayList<Integer> ar) {
            System.out.println(id);
            this.arr = ar;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public int getTurnAroundTime() {
            return turnAroundTime;
        }

        public void setTurnAroundTime(int turnAroundTime) {
            this.turnAroundTime = turnAroundTime;
        }

        public int getWaitingTime() {
            return waitingTime;
        }

        public void setWaitingTime(int waitingTime) {
            this.waitingTime = waitingTime;
        }

        public double getUtilizationTime() {
            return utilizationTime;
        }

        public void setUtilizationTime(double utilizationTime) {
            this.utilizationTime = utilizationTime;
        }

        public int getRemainingTime() {
            return remainingTime;
        }

        public void setRemainingTime() {
            this.remainingTime = getExecutionTime();
        }

        public void decRemainingTime() {
            remainingTime--;
        }

        public int getResumeIndex() {
            return resumeIndex;
        }

        public void setResumeIndex(int resumeIndex) {
            this.resumeIndex = resumeIndex;
        }
    
        public void incResumeIndex() {
            resumeIndex++;
        }
    }
    //constructor - private constructor so it can oly be called from inside the class
    private OS() {
        pq = new Queue();
        sq = new SortedQueue();
    }
    // methods
    //at first call it creates an os object
    //at every other call it returns the same object
    //goal = to create only one object
    public static OS getOS() {
        if (os == null) {
            os = new OS();
        }
        return os;
    }

    private void createProcesses() {
        Scanner m = new Scanner(System.in);
        //creation of n process objects
        System.out.print("How many processes do you want to create: ");
        int times = m.nextInt();
        for (int i = 0; i < times; i++) {
            //make new Processs node
            Process newP = new Process(i+1);
            // set arrival time
            newP.setArrivalTime(i);
            // get size of process
            System.out.print("Enter size for process " + newP.getId() + ": ");
            //set IN and ET and RT
            newP.setInstructionNumber(m.nextInt());
            newP.setExecutionTime(newP.getInstructionNumber());
            newP.setRemainingTime();
            //add ET of the new process to totalExecTime
            totalExecTime = totalExecTime + newP.getExecutionTime();
            //enqueue process in main queue
            pq.EnQueue(newP);
            //enter array input
            for (int x = 0; x < newP.getInstructionNumber(); x++) {
                System.out.print("enter element " + x + ": ");
                newP.arr.add(m.nextInt());
            }
        }
        m.close();
    }

    //print all process arrays
    private void printarr() {
        Queue.QueueNode temp = pq.getFront();
        while (temp != null) {
            System.out.println(temp.getProcess().getId() + " ");
            for (int i = 0; i < temp.getProcess().arr.size(); i++) {
                System.out.print(temp.getProcess().arr.get(i) + " ");
            }
            System.out.println();
            temp = temp.getNext();
        }
        System.out.println();
    }

    //where everything runs
    public void run() {
        //make process print them
        createProcesses();
        printarr();
        //IR is the next executable instruction
        IR = pq.getProcessFront().getArray().get(0);
        //PC point to the front of the array
        PC = sq.getProcessFront();

        for (int i = 0; i < totalExecTime; i++) {
            
            if (pq.getProcessFront() != null && pq.getProcessFront().arrivalTime == i ) {
                //move first process to sorted queue
                sq.EnQueue(pq.Dequeue());
            }
            pd = sq.Dequeue();
            IR = pd.getResumeIndex();
            PC = sq.getProcessFront();
            //when a process starts
            if (pd.remainingTime == pd.executionTime) {
                pd.setStartTime(i);
                pd.setWaitingTime(pd.getStartTime() - pd.getArrivalTime());
            }
            System.out.print(pd.getId() + " at quantum " + i + " runs: " + pd.getArray().get(pd.getResumeIndex()) + "\t\t\tIR: " + IR);
            if (PC == null) {
                System.out.println("\t\tPC: " + PC);
            } else {
                System.out.println("\t\tPC: " + PC.id);
            }
            pd.incResumeIndex();
            pd.decRemainingTime();

            //when process ends
            if (pd.remainingTime == 0) {
                //set end time
                pd.setEndTime(i+1);
                // update turn around time --- t.t = end - arrival
                pd.setTurnAroundTime(pd.getEndTime() - pd.getArrivalTime());
                // update utilization -- utilization = exectime / endtime
                pd.setUtilizationTime((double)pd.getExecutionTime() / pd.getEndTime());
                //print PCB
                System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------------");
                System.out.println("|ID\t|Execution Time\t|Arrival Time\t|Start Time\t|End Time\t|Turn Around Time\t|Waiting Time\t|Utilization Time |");
                System.out.println("|"+pd.getId()+"\t|"+pd.getExecutionTime()+"\t\t|"+pd.getArrivalTime()+"\t\t|"+pd.getStartTime()+"\t\t|"+pd.getEndTime()+"\t\t|"+pd.getTurnAroundTime()+"\t\t\t|"+pd.getWaitingTime()+"\t\t|"+ String.format("%.2f",pd.getUtilizationTime())+"\t\t  |");
                System.out.println("-----------------------------------------------------------------------------------------------------------------------------------\n");

            } else {
                sq.EnQueue(pd);
            }
        }
    }
}