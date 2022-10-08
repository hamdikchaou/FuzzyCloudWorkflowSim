//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;

public class InstanceTask extends Task {
    protected ArrayList<Task> fNewInstanceTasks;
    protected String fInstanceName;

    public InstanceTask(ArrayList<Task> aInstanceTasks) {
        this.fNewInstanceTasks = aInstanceTasks;
    }

    public void setInstanceName(String aInstanceName) {
        this.fInstanceName = aInstanceName;
    }

    public String getInstanceName() {
        return this.fInstanceName;
    }

    public ArrayList<Task> getInstanceTasks() {
        return this.fNewInstanceTasks;
    }
}
