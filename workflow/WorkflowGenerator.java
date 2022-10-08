//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package workflow;

import dataPlacement.DataSet;
import dataPlacement.Task;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class WorkflowGenerator {
    public double maxDataSize;
    public double minDataSize;
    public double maxGeneratedDataSize;
    public double minGeneratedDataSize;
    public int dataCount;
    public int taskCount;
    public int maxDataUsage;
    public int maxTaskBranch;
    private Random fRandom;
    private ArrayList<Task> fTasks;
    private ArrayList<DataSet> fDataSets;
    private ArrayList<DataSet> fGeneratedDataSets;

    public WorkflowGenerator(int aDatasetCount, int aTaskCount, int aDataUsageFactor, int aTaskBranchFactor) {
        this.maxDataSize = 128.0;
        this.minDataSize = 1.0;
        this.maxGeneratedDataSize = 128.0;
        this.minGeneratedDataSize = 1.0;
        this.dataCount = aDatasetCount;
        this.taskCount = aTaskCount;
        this.maxDataUsage = aDataUsageFactor;
        this.maxTaskBranch = aTaskBranchFactor;
        this.fRandom = new Random();
    }

    public WorkflowGenerator() {
        this(5, 5, 4, 3);
    }

    public Workflow generate() {
        this.fTasks = new ArrayList();
        this.fDataSets = new ArrayList();
        this.fGeneratedDataSets = new ArrayList();
        Workflow result = new Workflow();
        System.out.print("Generating tasks... ");
        result.setTasks(this.generateTasks());
        System.out.println("Done");
        System.out.print("Generating Datasets... ");
        result.setDatasets(this.generateDatasets());
        System.out.println("Done");
        System.out.print("Linking tasks... ");
        this.linkTasks();
        System.out.println("Done");
        result.setGeneratedDatasets(this.fGeneratedDataSets);
        return result;
    }

    private ArrayList<Task> generateTasks() {
        for(int i = 0; i < this.taskCount; ++i) {
            Task thisTask = new Task();
            thisTask.setName("t" + (i + 1));
            this.fTasks.add(thisTask);
        }

        return this.fTasks;
    }

    private ArrayList<DataSet> generateDatasets() {
        for(int i = 0; i < this.dataCount; ++i) {
            DataSet thisDataset = new DataSet();
            this.fDataSets.add(thisDataset);
            thisDataset.setName("d" + (i + 1));
            thisDataset.setSize(this.fRandom.nextDouble() * (this.maxDataSize - this.minDataSize) + this.minDataSize);
            thisDataset.setExists(true);
            int thisDatasetUsage = this.fRandom.nextInt(this.maxDataUsage) + 1;

            while(thisDataset.getTasks().size() < thisDatasetUsage) {
                Task lTask = (Task)this.fTasks.get(this.fRandom.nextInt(this.fTasks.size()));
                if (!thisDataset.getTasks().contains(lTask)) {
                    lTask.getInput().add(thisDataset);
                    thisDataset.addTask(lTask);
                }
            }
        }

        return this.fDataSets;
    }

    private void linkTasks() {
        LinkedList<Task> lTaskQueue = new LinkedList();
        ArrayList<Task> lGeneratedTasks = new ArrayList();
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

            int lThisTasksChildCount = this.fRandom.nextInt(this.maxTaskBranch) + 1;
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
                if (!lGeneratedTasks.contains(lThisChild)) {
                    lTaskQueue.add(lThisChild);
                    lGeneratedTasks.add(lThisChild);
                }
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
        lDataset.setSize(this.fRandom.nextDouble() * (this.maxGeneratedDataSize - this.minGeneratedDataSize) + this.minGeneratedDataSize);
        lDataset.setExists(false);
        lDataset.setWasGenerated(true);
        this.fGeneratedDataSets.add(lDataset);
        return lDataset;
    }

    private ArrayList<Task> getPotentialChildren(Task aTask) {
        ArrayList<Task> result = new ArrayList();

        for(int i = 0; i < this.fTasks.size(); ++i) {
            if (aTask.canParent((Task)this.fTasks.get(i))) {
                result.add((Task)this.fTasks.get(i));
            }
        }

        if (result.contains(aTask)) {
            result.remove(aTask);
        }

        return result;
    }
}
