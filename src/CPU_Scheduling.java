import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CPU_Scheduling {
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Enter the number of processes: ");
        int n = sc.nextInt();
        System.out.print("Enter the context switching time: ");
        int contextSwitch = sc.nextInt();

        List<Process> processes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.printf("Enter details for Process %d (Name Arrival-Time Burst-Time Priority Quantum): ", i + 1);
            String name = sc.next();
            int arrivalTime = sc.nextInt();
            int burstTime = sc.nextInt();
            int priority = sc.nextInt();
            int quantum = sc.nextInt();
            processes.add(new Process(name, arrivalTime, burstTime, priority, quantum));
        }

        System.out.println("\nChoose the scheduling algorithm:");
        System.out.println("1. Non-preemptive Priority Scheduling");
        System.out.println("2. Non-Preemptive Shortest-Job First (SJF)");
        System.out.println("3. Shortest-Remaining Time First (SRTF)");
        System.out.println("4. FCAI Scheduling");
        int choice = sc.nextInt();

        switch (choice) {
            case 1 -> priorityScheduling(processes, contextSwitch);
            case 2 -> sjfScheduling(processes, contextSwitch);
            case 3 -> srtfScheduling(processes, contextSwitch);
            case 4 -> fcaiScheduling(processes, contextSwitch);
            default -> System.out.println("Invalid choice. Exiting...");
        }
    }

    // Non-preemptive Priority Scheduling
    private static void priorityScheduling(List<Process> processes, int contextSwitch) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.priority));
        List<String> executionOrder = new ArrayList<>();

        int currentTime = 0, index = 0;
        int agingFactor = 1;

        while (index < processes.size() || !readyQueue.isEmpty()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(index++));
            }

            // Apply aging
            List<Process> agedProcesses = new ArrayList<>();
            while (!readyQueue.isEmpty()) {
                Process p = readyQueue.poll();
                p.priority -= agingFactor;
                agedProcesses.add(p);
            }
            readyQueue.addAll(agedProcesses);

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process current = readyQueue.poll();
            executionOrder.add(current.name);

            if (current.startTime == -1) {
                current.startTime = currentTime;
            }

            currentTime += current.burstTime + contextSwitch;
            current.endTime = currentTime;
        }

        calculateTimes(processes);
        System.out.println("\nNon-preemptive Priority Scheduling (with Aging):");
        printExecutionOrder(executionOrder);
        printResults(processes, false);
        displayGraphicalExecutionOrder(executionOrder);
    }

    // Non-preemptive Shortest-Job First (SJF)
    private static void sjfScheduling(List<Process> processes, int contextSwitch) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> (p.burstTime + p.waitingTime)));
        List<String> executionOrder = new ArrayList<>();

        int currentTime = 0, index = 0;

        while (index < processes.size() || !readyQueue.isEmpty()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(index++));
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            // Update waiting time for all ready processes
            for (Process p : readyQueue) {
                p.waitingTime++;
            }

            Process current = readyQueue.poll();
            executionOrder.add(current.name);

            if (current.startTime == -1) {
                current.startTime = currentTime;
            }

            currentTime += current.burstTime + contextSwitch;
            current.endTime = currentTime;
        }

        calculateTimes(processes);
        System.out.println("\nNon-preemptive Shortest-Job First (SJF) with Wait-Time Adjustment:");
        printExecutionOrder(executionOrder);
        printResults(processes, false);
        displayGraphicalExecutionOrder(executionOrder);
    }

    // Shortest Remaining Time First (SRTF)
    private static void srtfScheduling(List<Process> processes, int contextSwitch) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        PriorityQueue<Process> readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.remainingTime));
        List<String> executionOrder = new ArrayList<>();

        int currentTime = 0, index = 0;

        while (index < processes.size() || !readyQueue.isEmpty()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(index++));
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process current = readyQueue.poll();
            executionOrder.add(current.name);

            if (current.startTime == -1) {
                current.startTime = currentTime;
            }

            current.remainingTime -= 1; // Simulate execution for 1 time unit
            currentTime++;

            if (current.remainingTime == 0) {
                current.endTime = currentTime + contextSwitch;
            } else {
                readyQueue.add(current);
            }
        }

        calculateTimes(processes);
        System.out.println("\nShortest Remaining Time First (SRTF):");
        printExecutionOrder(executionOrder);
        printResults(processes, false);
        displayGraphicalExecutionOrder(executionOrder);
    }

    // FCAI Scheduling
    private static void fcaiScheduling(List<Process> processes, int contextSwitch) {
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        int lastArrivalTime = processes.get(processes.size() - 1).arrivalTime;
        int maxBurstTime = processes.stream().mapToInt(p -> p.burstTime).max().orElse(1);
        double v1 = lastArrivalTime / 10.0;
        double v2 = maxBurstTime / 10.0;

        Queue<Process> readyQueue = new LinkedList<>();
        List<String> executionOrder = new ArrayList<>();

        int currentTime = 0, index = 0;

        while (index < processes.size() || !readyQueue.isEmpty()) {
            while (index < processes.size() && processes.get(index).arrivalTime <= currentTime) {
                readyQueue.add(processes.get(index++));
            }

            if (readyQueue.isEmpty()) {
                currentTime++;
                continue;
            }

            Process current = readyQueue.poll();
            executionOrder.add(current.name);

            if (current.startTime == -1) {
                current.startTime = currentTime;
            }

            int quantum = calculateFCAIQuantum(current, v1, v2);
            current.quantumHistory.add(quantum);

            int executeTime = Math.min(quantum, current.remainingTime);
            current.remainingTime -= executeTime;
            currentTime += executeTime;

            if (current.remainingTime > 0) {
                readyQueue.add(current);
            } else {
                current.endTime = currentTime + contextSwitch;
            }
        }

        calculateTimes(processes);
        System.out.println("\nFCAI Scheduling:");
        printExecutionOrder(executionOrder);
        printResults(processes, true);
        displayGraphicalExecutionOrder(executionOrder);
    }

    private static int calculateFCAIQuantum(Process p, double v1, double v2) {
        return (int) Math.ceil((10 - p.priority) + (p.arrivalTime / v1) + (p.remainingTime / v2));
    }

    private static void calculateTimes(List<Process> processes) {
        for (Process p : processes) {
            p.turnaroundTime = p.endTime - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;
        }
    }

    private static void printExecutionOrder(List<String> executionOrder) {
        System.out.println("Execution Order: " + String.join(" -> ", executionOrder));
    }

    private static void printResults(List<Process> processes, boolean isFCAI) {
        System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s\n", "Name", "Arrival", "Burst", "Waiting", "Turnaround", "Quantum");

        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        for (Process p : processes) {
            int lastQuantum = p.quantumHistory.get(p.quantumHistory.size() - 1);
            System.out.printf("%-10s %-10d %-10d %-10d %-10d %-10d\n",
                    p.getName(),
                    p.getArrivalTime(),
                    p.getBurstTime(),
                    p.getWaitingTime(),
                    p.getTurnaroundTime(),
                    lastQuantum);

            totalWaitingTime += p.waitingTime;
            totalTurnaroundTime += p.turnaroundTime;
        }

        double avgWaitingTime = totalWaitingTime / (double) processes.size();
        double avgTurnaroundTime = totalTurnaroundTime / (double) processes.size();
        System.out.printf("\nAverage Waiting Time: %.2f\n", avgWaitingTime);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTurnaroundTime);

        if (isFCAI) {
            System.out.println("\nQuantum History:");
            for (Process p : processes) {
                System.out.printf("%s: %s\n", p.getName(), p.quantumHistory);
            }
        }
    }

    private static void displayGraphicalExecutionOrder(List<String> executionOrder) {
        // Create a JFrame to display the execution order graphically
        JFrame frame = new JFrame("Execution Order");
        frame.setSize(800, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        for (String process : executionOrder) {
            JLabel label = new JLabel(process);
            label.setFont(new Font("Arial", Font.PLAIN, 16));
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            panel.add(label);
        }

        frame.add(panel);
        frame.setVisible(true);
    }
}

