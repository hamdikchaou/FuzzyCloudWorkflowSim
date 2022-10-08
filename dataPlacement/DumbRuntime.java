//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import workflow.Workflow;

public class DumbRuntime extends RuntimeAlgorithm {
    public DumbRuntime(BuildTimeAlgorithm aBuilder) {
        super(aBuilder);
    }

    public void run(ArrayList<DataCenter> aDatacenters, Workflow aWorkflow) throws DistributionException {
        this.fTasks = aWorkflow.getTasks();
        this.fDatacenters = aDatacenters;
        this.fUsedDatacenters = new ArrayList();

        int i;
        for(i = 0; i < this.fDatacenters.size(); ++i) {
            if (((DataCenter)this.fDatacenters.get(i)).getDatasets().size() > 0) {
                this.fUsedDatacenters.add((DataCenter)this.fDatacenters.get(i));
            }
        }

        this.fReport = "";
        i = 1;
        this.fTotalDataSent = 0;
        this.fTotalDataRetrieved = 0;
        this.fTotalDataReschedules = 0;
        this.fTotalDataRescheduled = 0;
        this.fTotalDataSentSize = 0.0;
        this.fTotalDataRetrievedSize = 0.0;

        for(this.fTotalDataRescheduledSize = 0.0; this.fTasks.size() > 0; this.fReport = this.fReport + "\r\n") {
            System.out.println("New Round!");
            this.fReport = this.fReport + "Round " + i++ + "\r\n";
            String lScheduledTasks = "Scheduled Tasks:    ";
            String lDatasetsRetrieved = "Datasets Retrieved: ";
            String lDatasetsSent = "Datasets Sent:      ";
            boolean scheduleChanged = false;
            String lScheduleChanges = "";
            int lRetrievedCount = 0;
            int lSentCount = 0;
            double lRetrievedSize = 0.0;
            double lSentSize = 0.0;
            this.fReport = this.fReport + this.cleanupFootprint();
            ArrayList<Task> lReadyTasks = new ArrayList();

            int i;
            for(i = 0; i < this.fTasks.size(); ++i) {
                if (((Task)this.fTasks.get(i)).isReady()) {
                    lReadyTasks.add((Task)this.fTasks.get(i));
                }
            }

            for(i = 0; i < lReadyTasks.size(); ++i) {
                DataCenter bestCenter = null;
                int bestCenterCount = Integer.MAX_VALUE;
                ArrayList<DataSet> lMissingSets = new ArrayList();

                int j;
                for(j = 0; j < this.fUsedDatacenters.size(); ++j) {
                    ArrayList<DataSet> lTheseMissingSets = this.findMissingDataSets((Task)lReadyTasks.get(i), (DataCenter)this.fUsedDatacenters.get(j));
                    if (lTheseMissingSets.size() < bestCenterCount) {
                        bestCenter = (DataCenter)this.fUsedDatacenters.get(j);
                        lMissingSets = lTheseMissingSets;
                        bestCenterCount = lTheseMissingSets.size();
                        if (lTheseMissingSets.size() == 0) {
                            break;
                        }
                    }
                }

                lScheduledTasks = lScheduledTasks + lReadyTasks.get(i) + ":" + bestCenter + "; ";

                try {
                    for(j = 0; j < lMissingSets.size(); ++j) {
                        DataCenter lSource = ((DataSet)lMissingSets.get(j)).getDC();
                        System.out.println("Retrieving dataset " + ((DataSet)lMissingSets.get(j)).getName() + " from " + lSource.getName() + " to " + bestCenter.getName());
                        lDatasetsRetrieved = lDatasetsRetrieved + lReadyTasks.get(i) + ":" + lMissingSets.get(j) + ":" + bestCenter + "; ";
                        ++lRetrievedCount;
                        lRetrievedSize += ((DataSet)lMissingSets.get(j)).getSize();
                    }

                    ArrayList<DataSet> lNewData = bestCenter.execute((Task)lReadyTasks.get(i));

                    for(int j = 0; j < lNewData.size(); ++j) {
                        if (bestCenter.freeSpace() < ((DataSet)lNewData.get(j)).getSize()) {
                            lScheduleChanges = lScheduleChanges + this.adjustSchedule(lNewData);
                            scheduleChanged = true;
                        }

                        bestCenter.addDataset((DataSet)lNewData.get(j));
                        ((DataSet)lNewData.get(j)).setDC(bestCenter);
                    }

                    if (!scheduleChanged) {
                        this.fTasks.remove(lReadyTasks.get(i));
                    }
                } catch (Exception var22) {
                    System.out.println("ERROR : " + var22);
                    System.out.println(var22.getMessage());
                    if (var22 instanceof DistributionException) {
                        throw new DistributionException(var22.getMessage());
                    }

                    return;
                }
            }

            this.fReport = this.fReport + lScheduledTasks + "\r\n";
            this.fReport = this.fReport + lDatasetsRetrieved + "Total: " + lRetrievedCount + " (" + lRetrievedSize + ")\r\n";
            this.fTotalDataRetrieved += lRetrievedCount;
            this.fTotalDataRetrievedSize += lRetrievedSize;
            this.fReport = this.fReport + lDatasetsSent + "Total: " + lSentCount + " (" + lSentSize + ")\r\n";
            this.fTotalDataSent += lSentCount;
            this.fTotalDataSentSize += lSentSize;
            if (scheduleChanged) {
                this.fReport = this.fReport + lScheduleChanges;
                scheduleChanged = false;
            }
        }

        this.fReport = this.fReport + "Total Retrieved: " + this.fTotalDataRetrieved + " (" + this.fTotalDataRetrievedSize + ")\r\n";
        this.fReport = this.fReport + "Total Sent:      " + this.fTotalDataSent + " (" + this.fTotalDataSentSize + ")\r\n";
        this.fReport = this.fReport + "                 -------------\r\n";
        this.fReport = this.fReport + "Total Movement:  " + (this.fTotalDataRetrieved + this.fTotalDataSent) + " (" + (this.fTotalDataRetrievedSize + this.fTotalDataSentSize) + ")\r\n";
    }

    private String adjustSchedule(ArrayList<DataSet> aNewDatasets) throws Exception {
        ++this.fTotalDataReschedules;
        String result = "";
        result = result + this.cleanupFootprint();
        result = result + "State before rescheduling:\r\n";
        Matrix lNewMatrix = new Matrix();

        for(int i = 0; i < aNewDatasets.size(); ++i) {
            lNewMatrix.addDataset((DataSet)aNewDatasets.get(i));
        }

        result = result + this.getDataCenterState(lNewMatrix);
        return result + this.redistribute(lNewMatrix);
    }

    private String redistribute(Matrix lNewMatrix) throws Exception {
        int totalMoves = 0;
        double totalMoved = 0.0;
        String lMoveSummary = "";
        HashMap<String, ArrayList<DataSet>> lOldAllocation = new HashMap();

        int i;
        for(i = 0; i < this.fDatacenters.size(); ++i) {
            lOldAllocation.put(((DataCenter)this.fDatacenters.get(i)).getName(), new ArrayList(((DataCenter)this.fDatacenters.get(i)).getDatasets()));
        }

        Iterator var11 = this.fBuilder.fDataCenters.iterator();

        DataCenter d;
        while(var11.hasNext()) {
            d = (DataCenter)var11.next();
            d.setMaxCapacity(false);
        }

        var11 = this.fDatacenters.iterator();

        while(var11.hasNext()) {
            d = (DataCenter)var11.next();
            d.setMaxCapacity(false);
        }

        this.fUsedDatacenters = this.fBuilder.distribute(lNewMatrix.getDatasets());

        for(i = 0; i < this.fUsedDatacenters.size(); ++i) {
            ArrayList<DataSet> lOldList = (ArrayList)lOldAllocation.get(((DataCenter)this.fUsedDatacenters.get(i)).getName());
            ArrayList<DataSet> lNewList = ((DataCenter)this.fUsedDatacenters.get(i)).getDatasets();

            for(int j = 0; j < lNewList.size(); ++j) {
                if (!lOldList.contains(lNewList.get(j))) {
                    ++totalMoves;
                    totalMoved += ((DataSet)lNewList.get(j)).getSize();
                    lMoveSummary = lMoveSummary + " - Dataset " + lNewList.get(j) + " moved to " + this.fUsedDatacenters.get(i) + "\r\n";
                }
            }
        }

        String result = "State after rescheduling:\r\n";
        result = result + this.getDataCenterState();
        result = result + lMoveSummary;
        result = result + "Total movement during this redistribution:\r\n\t" + totalMoves + " (" + totalMoved + ")\r\n";
        this.fTotalDataRescheduled += totalMoves;
        this.fTotalDataRescheduledSize += totalMoved;
        Iterator var14 = this.fBuilder.fDataCenters.iterator();

        DataCenter d;
        while(var14.hasNext()) {
            d = (DataCenter)var14.next();
            d.setMaxCapacity(true);
        }

        var14 = this.fDatacenters.iterator();

        while(var14.hasNext()) {
            d = (DataCenter)var14.next();
            d.setMaxCapacity(true);
        }

        return result;
    }

    private String getDataCenterState(Matrix lNewMatrix) {
        String result = "";

        for(int i = 0; i < this.fUsedDatacenters.size(); ++i) {
            DataCenter thisCenter = (DataCenter)this.fUsedDatacenters.get(i);
            result = result + thisCenter.getName() + " : ";

            for(int j = 0; j < thisCenter.getDatasets().size(); ++j) {
                result = result + ((DataSet)thisCenter.getDatasets().get(j)).getName() + " (" + ((DataSet)thisCenter.getDatasets().get(j)).getSize() + "); ";
                lNewMatrix.addDataset((DataSet)thisCenter.getDatasets().get(j));
            }

            result = result + "\r\n";
        }

        return result;
    }

    private String getDataCenterState() {
        return this.getDataCenterState(new Matrix());
    }
}
