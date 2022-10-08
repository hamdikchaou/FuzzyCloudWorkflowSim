//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import workflow.Workflow;

public class FuzzyRuntime2 extends RuntimeAlgorithm {
    public FuzzyRuntime2(BuildTimeAlgorithm aBuilder) {
        super(aBuilder);
    }

    public void setDatacenters(ArrayList<DataCenter> aDatacenters) {
        this.fDatacenters = aDatacenters;
    }

    public ArrayList<Task> getTasks() {
        return this.fTasks;
    }

    public void setTasks(ArrayList<Task> aTasks) {
        this.fTasks = aTasks;
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
        this.fTotalDataSentSize = 0.0;
        this.fTotalDataRetrievedSize = 0.0;
        this.fTotalDataRescheduled = 0;
        this.fTotalDataReschedules = 0;

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
            ArrayList<Task> lReadyTasks = this.getReadyTasks();

            for(int i = 0; i < lReadyTasks.size(); ++i) {
                DataCenter bestCenter = null;
                int bestCenterMissingSetCount = Integer.MAX_VALUE;
                ArrayList<DataSet> lMissingSets = new ArrayList();

                int j;
                for(j = 0; j < this.fUsedDatacenters.size(); ++j) {
                    ArrayList<DataSet> lTheseMissingSets = this.findMissingDataSets((Task)lReadyTasks.get(i), (DataCenter)this.fUsedDatacenters.get(j));
                    if (lTheseMissingSets.size() < bestCenterMissingSetCount) {
                        bestCenter = (DataCenter)this.fUsedDatacenters.get(j);
                        lMissingSets = lTheseMissingSets;
                        bestCenterMissingSetCount = lTheseMissingSets.size();
                        if (lTheseMissingSets.size() == 0) {
                            break;
                        }
                    } else if (lTheseMissingSets.size() == bestCenterMissingSetCount) {
                        int bestCenterClustering = 0;
                        int thisCenterClustering = 0;

                        for(int k = 0; k < ((Task)lReadyTasks.get(i)).getOutput().size(); ++k) {
                            bestCenterClustering += this.calculateClustering((DataSet)((Task)lReadyTasks.get(i)).getOutput().get(k), bestCenter);
                            thisCenterClustering += this.calculateClustering((DataSet)((Task)lReadyTasks.get(i)).getOutput().get(k), (DataCenter)this.fUsedDatacenters.get(j));
                        }

                        if (thisCenterClustering > bestCenterClustering) {
                            bestCenter = (DataCenter)this.fUsedDatacenters.get(j);
                            lMissingSets = lTheseMissingSets;
                            bestCenterMissingSetCount = lTheseMissingSets.size();
                        } else if (thisCenterClustering == bestCenterClustering && bestCenter.utilisation() > ((DataCenter)this.fUsedDatacenters.get(j)).utilisation()) {
                            bestCenter = (DataCenter)this.fUsedDatacenters.get(j);
                            lMissingSets = lTheseMissingSets;
                            bestCenterMissingSetCount = lTheseMissingSets.size();
                        }
                    }
                }

                lScheduledTasks = lScheduledTasks + lReadyTasks.get(i) + ":" + bestCenter + "; ";

                try {
                    for(j = 0; j < lMissingSets.size(); ++j) {
                        DataCenter lSource = ((DataSet)lMissingSets.get(j)).getDC();
                        lDatasetsRetrieved = lDatasetsRetrieved + lReadyTasks.get(i) + ":" + lMissingSets.get(j) + ":" + lSource + "->" + bestCenter + "; ";
                        ++lRetrievedCount;
                        lRetrievedSize += ((DataSet)lMissingSets.get(j)).getSize();
                    }

                    ArrayList<DataSet> lNewData = bestCenter.execute((Task)lReadyTasks.get(i));
                    this.fTasks.remove(lReadyTasks.get(i));

                    for(int j = 0; j < lNewData.size(); ++j) {
                        DataCenter lTargetCenter = this.distributeNewDataFuzzy((DataSet)lNewData.get(j), bestCenter);
                        if (lTargetCenter.freeSpace() < ((DataSet)lNewData.get(j)).getSize()) {
                            lScheduleChanges = bestCenter + " executing " + lReadyTasks.get(i) + " and sending the result (" + ((DataSet)lNewData.get(j)).getSize() + ") to " + lTargetCenter + " required a reschedule\r\n";
                            lScheduleChanges = lScheduleChanges + this.adjustSchedule(lNewData);
                            scheduleChanged = true;
                            break;
                        }

                        lTargetCenter.addDataset((DataSet)lNewData.get(j));
                        ((DataSet)lNewData.get(j)).setDC(lTargetCenter);
                        if (lTargetCenter != bestCenter) {
                            lDatasetsSent = lDatasetsSent + lNewData.get(j) + ":" + bestCenter + "->" + lTargetCenter + "; ";
                            ++lSentCount;
                            lSentSize += ((DataSet)lNewData.get(j)).getSize();
                        }
                    }

                    if (lReadyTasks.get(i) instanceof InstanceTask) {
                        lScheduleChanges = bestCenter + " executing " + lReadyTasks.get(i) + " triggered a new instance.\r\n";
                        lScheduleChanges = lScheduleChanges + this.startNewInstance((InstanceTask)lReadyTasks.get(i));
                        scheduleChanged = true;
                        break;
                    }

                    if (scheduleChanged) {
                        lReadyTasks.clear();
                    }
                } catch (Exception var25) {
                    System.out.println("ERROR : " + var25.getMessage());
                    if (var25 instanceof DistributionException) {
                        throw new DistributionException(var25.getMessage());
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
            }
        }

        this.fReport = this.fReport + "Total Retrieved:     " + this.fTotalDataRetrieved + " (" + this.fTotalDataRetrievedSize + ")\r\n";
        this.fReport = this.fReport + "Total Sent:          " + this.fTotalDataSent + " (" + this.fTotalDataSentSize + ")\r\n";
        this.fReport = this.fReport + "Total Redistributed: " + this.fTotalDataRescheduled + " (" + this.fTotalDataRescheduledSize + ")\r\n";
        this.fReport = this.fReport + "                     -------------\r\n";
        this.fReport = this.fReport + "Total Movement:      " + (this.fTotalDataRetrieved + this.fTotalDataSent + this.fTotalDataRescheduled) + " (" + (this.fTotalDataRetrievedSize + this.fTotalDataSentSize + this.fTotalDataRescheduledSize) + ")\r\n";
    }

    private String startNewInstance(InstanceTask aInstanceTask) throws Exception {
        String result = "";
        result = result + this.cleanupFootprint();
        result = result + "State before rescheduling:\r\n";
        Matrix lNewMatrix = new Matrix();
        result = result + this.getDataCenterState(lNewMatrix);

        for(int i = 0; i < aInstanceTask.getInstanceTasks().size(); ++i) {
            Task thisTask = (Task)aInstanceTask.getInstanceTasks().get(i);

            for(int j = 0; j < thisTask.getInput().size(); ++j) {
                ((DataSet)thisTask.getInput().get(j)).addTask(thisTask);
                if (((DataSet)thisTask.getInput().get(j)).exists()) {
                    lNewMatrix.addDataset((DataSet)thisTask.getInput().get(j));
                }
            }

            this.fTasks.add(thisTask);
        }

        return result + this.redistribute(lNewMatrix);
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

    private DataCenter distributeNewData(DataSet aNewDataset, DataCenter aDatasetSource) {
        int bestClustering = Integer.MIN_VALUE;
        DataCenter bestCenter = null;

        for(int i = 0; i < this.fUsedDatacenters.size(); ++i) {
            int thisClustering = this.calculateClustering(aNewDataset, (DataCenter)this.fUsedDatacenters.get(i));
            if (thisClustering > bestClustering) {
                bestCenter = (DataCenter)this.fUsedDatacenters.get(i);
                bestClustering = thisClustering;
            } else if (thisClustering == bestClustering) {
                if (this.fUsedDatacenters.get(i) != aDatasetSource && bestCenter != aDatasetSource) {
                    if (bestCenter.utilisation() > ((DataCenter)this.fUsedDatacenters.get(i)).utilisation()) {
                        bestCenter = (DataCenter)this.fUsedDatacenters.get(i);
                    }
                } else {
                    bestCenter = aDatasetSource;
                }
            }
        }

        return bestCenter;
    }

    private DataCenter distributeNewDataFuzzy(DataSet aNewDataset, DataCenter aDataCenterSource) {
        FCM fcm = new FCM(aNewDataset, this.fUsedDatacenters);
        fcm.init();
        fcm.membership_matrix();
        DataCenter bestCenter = fcm.cluster(aDataCenterSource);
        return bestCenter;
    }
}
