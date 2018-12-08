public class sProcess {
    public int id;
    public int cputime;
    public int ioblocking;
    public int cpudone;
    public int ionext;
    public int numblocked;
    public int arrivalTime;
    public int averageBlockingTime;
    public int blockedTime;
    public boolean isCompleted = false;

    public sProcess(int id, int cputime, int ioblocking, int cpudone, int ionext, int numblocked, int arrivalTime,
                    int averageBlockingTime) {
        this.id = id;
        this.cputime = cputime;
        this.ioblocking = ioblocking;
        this.cpudone = cpudone;
        this.ionext = ionext;
        this.numblocked = numblocked;
        this.arrivalTime = arrivalTime;
        this.averageBlockingTime = averageBlockingTime;
        this.blockedTime = -1;
    }
}
