//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;
import workflow.Workflow;

public abstract class RuntimeAlgorithm {
    protected ArrayList<Task> fTasks;
    protected ArrayList<DataCenter> fDatacenters;
    protected ArrayList<DataCenter> fUsedDatacenters;
    protected int fTotalDataRetrieved;
    protected int fTotalDataSent;
    protected int fTotalDataRescheduled;
    protected int fTotalDataReschedules;
    protected double fTotalDataRetrievedSize;
    protected double fTotalDataSentSize;
    protected double fTotalDataRescheduledSize;
    protected BuildTimeAlgorithm fBuilder;
    protected String fReport;

    public RuntimeAlgorithm(BuildTimeAlgorithm aBuildTimeAlgorithm) {
        this.fBuilder = aBuildTimeAlgorithm;
    }

    public String getReport() {
        return this.fReport;
    }

    public abstract void run(ArrayList<DataCenter> aDataCenters, Workflow aWorkflow) throws DistributionException;

    protected String cleanupFootprint() {
        String result = "";

        for(int i = 0; i < this.fUsedDatacenters.size(); ++i) {
            for(int j = 0; j < ((DataCenter)this.fUsedDatacenters.get(i)).getDatasets().size(); ++j) {
                DataSet thisDataset = (DataSet)((DataCenter)this.fUsedDatacenters.get(i)).getDatasets().get(j);
                if (thisDataset.wasGenerated()) {
                    boolean canDelete = true;

                    for(int k = 0; k < this.fTasks.size(); ++k) {
                        if (((Task)this.fTasks.get(k)).getInput().contains(thisDataset)) {
                            canDelete = false;
                        }
                    }

                    if (canDelete) {
                        ((DataCenter)this.fUsedDatacenters.get(i)).getDatasets().remove(thisDataset);
                        result = result + thisDataset + " (" + thisDataset.getSize() + ")  is no longer needed and was deleted from " + this.fUsedDatacenters.get(i) + "\r\n";
                        --j;
                    }
                }
            }
        }

        return result;
    }

    protected ArrayList<Task> getReadyTasks() {
        ArrayList<Task> result = new ArrayList();

        for(int i = 0; i < this.fTasks.size(); ++i) {
            if (((Task)this.fTasks.get(i)).isReady()) {
                result.add((Task)this.fTasks.get(i));
            }
        }

        return result;
    }

    protected ArrayList<DataSet> findMissingDataSets(Task aTask, DataCenter aDataCenter) {
        ArrayList<DataSet> result = new ArrayList();

        for(int i = 0; i < aTask.getInput().size(); ++i) {
            if (!aDataCenter.getDatasets().contains(aTask.getInput().get(i))) {
                result.add((DataSet)aTask.getInput().get(i));
            }
        }

        return result;
    }

    protected int calculateClustering(DataSet aDataset, DataCenter aDatacenter) {
        int dependancy = 0;

        for(int i = 0; i < aDatacenter.getDatasets().size(); ++i) {
            dependancy += Matrix.calculateDependancyFuzzy(aDataset, (DataSet)aDatacenter.getDatasets().get(i));
        }

        return dependancy;
    }

    public int getTotalDataRetrieved() {
        return this.fTotalDataRetrieved;
    }

    public double getTotalDataRetrievedSize() {
        return this.fTotalDataRetrievedSize;
    }

    public int getTotalDataSent() {
        return this.fTotalDataSent;
    }

    public double getTotalDataSentSize() {
        return this.fTotalDataSentSize;
    }

    public int getTotalDataRescheduled() {
        return this.fTotalDataRescheduled;
    }

    public int getTotalDataReschedules() {
        return this.fTotalDataReschedules;
    }

    public double getTotalDataRescheduledSize() {
        return this.fTotalDataRescheduledSize;
    }
}
