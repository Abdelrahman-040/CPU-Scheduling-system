import java.util.ArrayList;
import java.util.List;

class Process {
    String name;
    int arrivalTime;
    int burstTime;
    int remainingTime;
    int priority;
    int startTime;
    int endTime;
    int waitingTime;
    int turnaroundTime;
    List<Integer> quantumHistory;

    public Process(String name, int arrivalTime, int burstTime, int priority, int quantum) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
        this.startTime = -1; // Default value to indicate not started
        this.quantumHistory = new ArrayList<>();
        this.quantumHistory.add(quantum); // Add initial quantum to the history
    }

    public String getName() {
        return name;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }
}