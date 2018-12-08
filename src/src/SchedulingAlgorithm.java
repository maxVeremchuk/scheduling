// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.Vector;
import java.io.*;
import java.util.Collections;
import java.util.Comparator;

public class SchedulingAlgorithm {
    public static Results RunSJF(int runtime, Vector<sProcess> processVector, Vector<sProcess> notArrived, Results result) {
        int i;
        int comptime = 0;
        sProcess previousProcess;
        sProcess currentProcess;
        int size;
        int processSize = processVector.size() + notArrived.size();
        int completed = 0;
        boolean isAdded = false;
        boolean allBlocked = false;
        Vector<sProcess> newProcess = new Vector<>();
        Vector<sProcess> bufferProcesses = new Vector<>();
        String resultsFile = "Summary-Processes";

        //debug:
        //processVector.elementAt(0).cputime = 30;
        //processVector.elementAt(1).cputime = 930;
        //notArrived.elementAt(0).cputime = 30;
        //notArrived.elementAt(1).cputime = 850;

        try {
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));

            sort(processVector);
            currentProcess = processVector.elementAt(0);
            out.println("Process: " + currentProcess.id + " registered... (" + currentProcess.cputime + " " + currentProcess.ioblocking + " " + currentProcess.cpudone + " "  + currentProcess.arrivalTime + ")");
            while (comptime < runtime) {



                for (i = 0; i < notArrived.size(); i++) {
                    if (notArrived.elementAt(i).arrivalTime == comptime) {
                        sProcess newProc = notArrived.remove(i);
                        processVector.addElement(newProc);
                        newProcess.addElement(newProc); // arrived at that iteration
                        isAdded = true;
                    }
                }
                size = processVector.size();

                if (newProcess.size() > 0) {
                    if(isAdded) {
                        sort(processVector);
                    }
                    previousProcess = currentProcess;
                    for (i = 0; i < newProcess.size(); i++) {
                        sProcess proc = newProcess.elementAt(i);
                        if(currentProcess.isCompleted){
                            currentProcess = proc;
                        }
                        else if ((proc.cputime - proc.cpudone) < (currentProcess.cputime - currentProcess.cpudone)
                                && proc.blockedTime == -1) {
                            currentProcess = proc;
                        }
                    }
                    if(allBlocked){
                            out.println("Process: " + currentProcess.id + " starts from all blocking(" + currentProcess.cputime + " " + currentProcess.ioblocking + " " + currentProcess.cpudone + " " + currentProcess.arrivalTime + ")");
                        allBlocked = false;
                    }
                    else{
                        if (previousProcess != currentProcess) {
                            previousProcess.numblocked++;
                            out.println("Process: " + previousProcess.id + " blocked due to another proc... (" + previousProcess.cputime + " " + previousProcess.ioblocking + " " + previousProcess.cpudone + " " + previousProcess.arrivalTime + ")");
                            out.println("Process: " + currentProcess.id + " proc that blocked prev... (" + currentProcess.cputime + " " + currentProcess.ioblocking + " " + currentProcess.cpudone +  " " + currentProcess.arrivalTime + ")");
                        }
                    }

                    isAdded = false;
                }

                if (newProcess.size() > 0) {
                    newProcess.clear();
                }

                if (currentProcess.cpudone == currentProcess.cputime && !allBlocked) {
                    completed++;
                    currentProcess.isCompleted = true;
                    //currentProcess.cpudone++;
                    currentProcess.blockedTime = 0;
                    for(i = 0; i < processVector.size(); i++){
                        if(processVector.elementAt(i) == currentProcess){
                            bufferProcesses.add(processVector.remove(i));
                            break;
                        }
                    }
                    size--;
                    out.println("Process: " + currentProcess.id + " completed... (" + currentProcess.cputime + " " + currentProcess.ioblocking + " " + currentProcess.cpudone + " " + currentProcess.arrivalTime + ")");
                    if (completed == processSize) {
                        result.compuTime = comptime;
                        out.close();
                       result.processes = bufferProcesses;
                        return result;
                    }

                    previousProcess = currentProcess;
                    currentProcess = findNext(processVector, currentProcess);
                    if (previousProcess != currentProcess) {
                        out.println("Process: " + currentProcess.id + " registered... (" + currentProcess.cputime + " " + currentProcess.ioblocking + " " + currentProcess.cpudone + " " + currentProcess.arrivalTime + ")");
                    }
                    else{
                        out.println("All processes are blocked");
                        allBlocked = true;
                    }
                }
                if (currentProcess.ioblocking == currentProcess.ionext && !allBlocked) {
                    out.println("Process: " + currentProcess.id + " I/O blocked... (" + currentProcess.cputime + " " + currentProcess.ioblocking + " " + currentProcess.cpudone + " "  + currentProcess.arrivalTime + ")");
                    currentProcess.numblocked++;
                    currentProcess.ionext = 0;
                    currentProcess.blockedTime = 0;

                    previousProcess = currentProcess;
                    currentProcess = findNext(processVector, currentProcess);
                    if(previousProcess != currentProcess) {
                        out.println("Process: " + currentProcess.id + " registered... (" + currentProcess.cputime + " " + currentProcess.ioblocking + " " + currentProcess.cpudone + " " + currentProcess.arrivalTime + ")");
                    }else{
                        out.println("All processes are blocked");
                        allBlocked = true;
                    }
                }
                if( currentProcess.blockedTime == -1) {
                    currentProcess.cpudone++;
                    if (currentProcess.ioblocking > 0) {
                        currentProcess.ionext++;
                    }
                }
                for(i = 0; i < size; i++){
                    sProcess proc = processVector.elementAt(i);
                    if(proc.blockedTime == proc.averageBlockingTime){
                        proc.blockedTime = -1;
                        newProcess.add(proc);
                    }
                    else if(proc.blockedTime >= 0){
                        proc.blockedTime++;
                    }
                }
                comptime++;
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        result.compuTime = comptime;
        /*for(i = 0; i< processVector.size(); i++){
            processVector.elementAt(i).cpudone--;
        }*/
        result.processes = bufferProcesses;
        //processVector = bufferProcesses;
        return result;
    }

    public static void sort(Vector vector) {
        Collections.sort(vector, new Comparator<sProcess>() {
            @Override
            public int compare(sProcess p1, sProcess p2) {
                return (p1.cputime - p1.cpudone) - (p2.cputime - p2.cpudone);
            }
        });
    }

    public static sProcess findNext(Vector<sProcess> processVector, sProcess currentProcess) {
        for (int i = 0; i < processVector.size(); i++) {
            sProcess process = processVector.elementAt(i);
            if (!process.isCompleted && process != currentProcess && process.blockedTime == -1) {
                return process;
            }
        }
        return currentProcess;
    }
}

