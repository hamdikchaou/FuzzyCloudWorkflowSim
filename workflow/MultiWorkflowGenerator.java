//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package workflow;

import dataPlacement.DataCenter;
import dataPlacement.DataSet;
import dataPlacement.InstanceTask;
import dataPlacement.Task;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class MultiWorkflowGenerator {
    public ArrayList<DataCenter> fDatacenters;
    private final int MAX_DATA_SIZE;
    private final int MIN_DATA_SIZE;
    private int fDataCount;
    private int fTaskCount;
    private int fMaxDataUsageFactor;
    private int fMaxTaskBranchFactor;
    private int fMaxInstanceCount;
    private Random fRandom;
    private ArrayList<Task> fTasks;
    private LinkedList<ArrayList<Task>> fInstances;
    private ArrayList<DataSet> fDataSets;
    private ArrayList<DataSet> fGeneratedDataSets;

    public MultiWorkflowGenerator(int aDatasetCount, int aTaskCount, int aDataUsageFactor, int aTaskBranchFactor, int aMaxInstanceCount) {
        this.MAX_DATA_SIZE = 128;
        this.MIN_DATA_SIZE = 1;
        this.fDataCount = aDatasetCount;
        this.fTaskCount = aTaskCount;
        this.fMaxDataUsageFactor = aDataUsageFactor;
        this.fMaxTaskBranchFactor = aTaskBranchFactor;
        this.fMaxInstanceCount = aMaxInstanceCount;
        this.fRandom = new Random();
    }

    public MultiWorkflowGenerator() {
        this(5, 5, 3, 2, 2);
    }

    public Workflow generate() {
        this.fTasks = new ArrayList();
        this.fDataSets = new ArrayList();
        this.fGeneratedDataSets = new ArrayList();
        Workflow result = new Workflow();
        System.out.println("--------------Creation of a Workflow--------------");
        System.out.print("Generating tasks... ");
        result.setTasks(this.generateTasks());
        System.out.println("Done");
        System.out.println("Number of tasks instances = " + this.fInstances.size());
        System.out.println(this.fInstances.toString());
        System.out.print("Generating initial Datasets... ");
        result.setDatasets(this.generateDatasets());
        System.out.println("Done");
        System.out.println(this.fDataSets.toString());
        System.out.print("Linking tasks... ");

        for(int i = 0; i < this.fInstances.size(); ++i) {
            this.linkTasks((ArrayList)this.fInstances.get(i));
        }

        System.out.println("Done");
        System.out.print("New Generated Datasets... ");
        result.setGeneratedDatasets(this.fGeneratedDataSets);
        System.out.println("Done");
        System.out.println(this.fGeneratedDataSets.toString());
        return result;
    }

    private ArrayList<Task> generateTasks() {
        this.fInstances = new LinkedList();
        int instanceCount = 1;

        for(int j = instanceCount; j > 0; --j) {
            ArrayList<Task> thisInstance = new ArrayList();

            int lInstanceTaskIndex;
            for(lInstanceTaskIndex = 0; lInstanceTaskIndex < this.fTaskCount; ++lInstanceTaskIndex) {
                int thisTaskNumber = j * this.fTaskCount - (this.fTaskCount - 1) + lInstanceTaskIndex;
                Task thisTask = new Task();
                thisTask.setName("t" + thisTaskNumber++);
                this.fTasks.add(thisTask);
                thisInstance.add(thisTask);
            }

            if (this.fInstances.size() > 0) {
                lInstanceTaskIndex = this.fRandom.nextInt(thisInstance.size());
                Task lInstanceTask = (Task)thisInstance.get(lInstanceTaskIndex);
                int lTaskListIndex = this.fTasks.indexOf(lInstanceTask);
                InstanceTask lNewTask = new InstanceTask((ArrayList)this.fInstances.get(0));
                lNewTask.setName(lInstanceTask.getName());
                this.fTasks.set(lTaskListIndex, lNewTask);
                thisInstance.set(lInstanceTaskIndex, lNewTask);
            }

            this.fInstances.addFirst(thisInstance);
        }

        return (ArrayList)this.fInstances.get(0);
    }

    private ArrayList<DataSet> generateDatasets() {
        for(int i = 0; i < this.fDataCount; ++i) {
            DataSet thisDataset = new DataSet();
            this.fDataSets.add(thisDataset);
            thisDataset.setName("d" + (i + 1));
            thisDataset.setSize((double)(this.fRandom.nextInt(128) + 1));
            thisDataset.setExists(true);
            int thisDatasetUsage = this.fRandom.nextInt(this.fMaxDataUsageFactor) + 1;

            while(thisDataset.getTasks().size() < thisDatasetUsage) {
                Task lTask = (Task)this.fTasks.get(this.fRandom.nextInt(this.fTasks.size()));
                if (!thisDataset.getTasks().contains(lTask)) {
                    lTask.getInput().add(thisDataset);
                    if (((ArrayList)this.fInstances.get(0)).contains(lTask)) {
                        thisDataset.addTask(lTask);
                    }
                }
            }
        }

        return this.fDataSets;
    }

    private void linkTasks() {
        LinkedList<Task> lTaskQueue = new LinkedList();
        lTaskQueue.add((Task)this.fTasks.get(0));

        while(true) {
            Task lTask;
            ArrayList lPotentialChildren;
            do {
                if (lTaskQueue.size() <= 0) {
                    return;
                }

                lTask = (Task)lTaskQueue.pop();
                lPotentialChildren = this.getPotentialChildren(lTask);
            } while(lPotentialChildren.size() == 0);

            int lThisTasksChildCount = this.fRandom.nextInt(this.fMaxTaskBranchFactor) + 1;
            lThisTasksChildCount = Math.min(lThisTasksChildCount, lPotentialChildren.size());
            DataSet lDataset = this.getGeneratedDataset("du" + lTask.getName().substring(1));
            lTask.addOutput(lDataset);

            while(lTask.getChildCount() < lThisTasksChildCount) {
                Task lThisChild = (Task)lPotentialChildren.get(this.fRandom.nextInt(lPotentialChildren.size()));
                if (lTask.canParent(lThisChild)) {
                    lThisChild.addParent(lTask);
                    lPotentialChildren.remove(lThisChild);
                }

                lDataset.addTask(lThisChild);
                lThisChild.addInput(lDataset);
                lTaskQueue.add(lThisChild);
            }
        }
    }

    private void linkTasks(ArrayList<Task> aInstance) {
        LinkedList<Task> lTaskQueue = new LinkedList();
        lTaskQueue.add((Task)aInstance.get(0));

        while(true) {
            Task lTask;
            ArrayList lPotentialChildren;
            do {
                if (lTaskQueue.size() <= 0) {
                    return;
                }

                lTask = (Task)lTaskQueue.pop();
                lPotentialChildren = this.getPotentialChildren(lTask);
            } while(lPotentialChildren.size() == 0);

            int lThisTasksChildCount = this.fRandom.nextInt(this.fMaxTaskBranchFactor) + 1;
            lThisTasksChildCount = Math.min(lThisTasksChildCount, lPotentialChildren.size());
            DataSet lDataset = this.getGeneratedDataset("du" + lTask.getName().substring(1));
            lTask.addOutput(lDataset);

            while(lTask.getChildCount() < lThisTasksChildCount) {
                Task lThisChild = (Task)lPotentialChildren.get(this.fRandom.nextInt(lPotentialChildren.size()));
                if (lTask.canParent(lThisChild)) {
                    lThisChild.addParent(lTask);
                    lPotentialChildren.remove(lThisChild);
                }

                lDataset.addTask(lThisChild);
                lThisChild.addInput(lDataset);
                lTaskQueue.add(lThisChild);
            }
        }
    }

    private DataSet getGeneratedDataset(String aSetName) {
        for(int i = 0; i < this.fGeneratedDataSets.size(); ++i) {
            if (((DataSet)this.fGeneratedDataSets.get(i)).getName().compareTo(aSetName) == 0) {
                return (DataSet)this.fGeneratedDataSets.get(i);
            }
        }

        DataSet lDataset = new DataSet();
        lDataset.setName(aSetName);
        lDataset.setSize((double)(this.fRandom.nextInt(128) + 1));
        lDataset.setExists(false);
        lDataset.setWasGenerated(true);
        this.fGeneratedDataSets.add(lDataset);
        return lDataset;
    }

    private ArrayList<Task> getPotentialChildren(Task aTask) {
        ArrayList<Task> result = new ArrayList();
        ArrayList<Task> lThisTasksInstance = this.getInstance(aTask);

        for(int i = 0; i < lThisTasksInstance.size(); ++i) {
            if (aTask.canParent((Task)lThisTasksInstance.get(i))) {
                result.add((Task)lThisTasksInstance.get(i));
            }
        }

        if (result.contains(aTask)) {
            result.remove(aTask);
        }

        return result;
    }

    private ArrayList<Task> getInstance(Task aTask) {
        for(int i = 0; i < this.fInstances.size(); ++i) {
            if (((ArrayList)this.fInstances.get(i)).contains(aTask)) {
                return (ArrayList)this.fInstances.get(i);
            }
        }

        return null;
    }
}
