//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package IT2FCMPSO;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.sourceforge.jswarm_pso.FitnessFunction;

public class MyFitnessFunction extends FitnessFunction {
    List availableVMs;
    List avaliableTasks;
    List avalibaleTaskSorted;
    List Tasks;

    public List getVmList() {
        return this.availableVMs;
    }

    public List getTaskList() {
        return this.avaliableTasks;
    }

    public MyFitnessFunction(List TaskList, List VMList) {
        this.availableVMs = VMList;
        this.avaliableTasks = TaskList;
    }

    public boolean checkDuplicate(ArrayList<Integer> list, int value) {
        return list.contains(value);
    }

    public double evaluate(double[] position) {
        List<TaskTime> taskTime = new ArrayList();
        List<VM> vmTime = new ArrayList();
        Iterator it = this.getVmList().iterator();

        while(it.hasNext()) {
            Object vmList = it.next();
            VM v = new VM();
            v.VMBusyTime = 0.0;
            vmTime.add(v);
        }

        int p = false;
        Iterator it = this.getTaskList().iterator();
        if (it.hasNext()) {
            new TaskTime();
            throw new RuntimeException("Uncompilable source code - Erroneous tree type: Task");
        } else {
            double maxTaskTime = -1.0;
            Iterator var7 = taskTime.iterator();

            while(var7.hasNext()) {
                TaskTime task = (TaskTime)var7.next();
                if (task.stopTime > maxTaskTime) {
                    maxTaskTime += task.stopTime;
                }
            }

            maxTaskTime = ((TaskTime)taskTime.get(taskTime.size() - 1)).stopTime;
            return maxTaskTime * -1.0;
        }
    }

    private double getOutputSize(Task t) {
        throw new RuntimeException("Uncompilable source code - Erroneous tree type: Task");
    }

    private double getInputSize(Task t) {
        throw new RuntimeException("Uncompilable source code - Erroneous tree type: Task");
    }
}
