//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package test;

import dataPlacement.BuildTime;
import dataPlacement.DataCenter;
import dataPlacement.DataSet;
import dataPlacement.FuzzyRuntime;
import dataPlacement.FuzzyRuntimePSO;
import dataPlacement.Task;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import realworkflow.Log;
import workflow.MultiWorkflowGenerator;
import workflow.Workflow;
import workflow.WorkflowGenerator;
import workflow.WorkflowParser;

public class Test {
    private ArrayList<DataCenter> fDatacenters = new ArrayList();
    public static int dataCenterCount = 3;
    public static double dataCenterSize = 240.0;
    public static double dataCenterPercent = 0.0;
    public static double dataCenterSizeShift = 0.0;
    public static double dataCenterPIni = 0.6;
    public static double dataCenterPMax = 1.0;
    public static int dataSetCount;
    public static int dataSetCountInit;
    public static int dataSetCountGen;
    public static double dataSetMaxSize = 1024.0;
    public static double dataSetMinSize = 1.0;
    public static int dataSetMaxUse = 3;
    public static int taskCount = 80;
    public static double taskResultMaxSize = 1024.0;
    public static double taskResultMinSize = 1.0;
    public static int taskMaxBranch = 4;
    public static double FIXED_DATASETS = 0.0;
    private int fTestCount;
    public static int fFailedTestCount = 0;
    public static double datasetsSize;
    private ArrayList<CloudSimulator> fSimulators;
    private WorkflowGenerator fGenerator;
    public static WorkflowParser wp;
    public static String daxPath;

    public Test() {
        this.configureDataCenters();
        this.fSimulators = this.generateSimulators();
        this.configureGenerator();
        this.fTestCount = 0;
    }

    public static void main(String[] args) throws Exception {
        Chronometer ch = new Chronometer();
        ch.start();
        MultiWorkflowGenerator lGenerator = new MultiWorkflowGenerator();
        Workflow lWork = lGenerator.generate();
        daxPath = "/C:/dax/Montage_5.xml";
        File daxFile = new File(daxPath);
        if (!daxFile.exists()) {
            Log.printLine("Warning: Please replace daxPath with the physical path in your working environment!");
        } else {
            wp = new WorkflowParser(daxPath);
            lWork.save("workflow.original");
            dataSetCount = lWork.getDatasets().size() + lWork.getGeneratedDatasets().size();
            dataSetCountInit = lWork.getDatasets().size();
            dataSetCountGen = lWork.getGeneratedDatasets().size();
            Log.printLine("Initial datasets number=" + lWork.getDatasets().size());
            Log.printLine("Generated datasets number=" + lWork.getGeneratedDatasets().size());
            datasetsSize = computeDatasetsSize(lWork);
            Log.printLine("datasetsSize=" + datasetsSize);
            Test t = new Test();
            ArrayList<DataSet> datasets = lWork.getDatasets();
            datasets = lWork.renameinitdatasets(datasets);
            lWork.setDatasets(datasets);
            ArrayList<DataSet> gdatasets = lWork.getGeneratedDatasets();
            gdatasets = lWork.renamegendatasets(gdatasets);
            lWork.setGeneratedDatasets(gdatasets);
            ArrayList<Task> tasks = lWork.getTasks();
            tasks = lWork.renametasks(tasks);
            lWork.setTasks(tasks);

            try {
                lWork.save("workflow.work");
            } catch (IOException var15) {
                System.out.println("ERROR: " + var15.getMessage());
                ++fFailedTestCount;
            }

            try {
                t.doTest("Hamdi.Final.Workflow", lWork);
            } catch (Exception var14) {
                System.out.println("error_here");
            }

            t.saveFinalReport("workflow.final");
            ArrayList<CloudSimulator> fSimulators = new ArrayList();

            for(int i = 0; i < fSimulators.size(); ++i) {
                lWork.reset();
                String report = ((CloudSimulator)fSimulators.get(i)).simulateWorkflow(new Workflow(lWork));

                try {
                    PrintStream out = new PrintStream(args[0] + "-" + ((CloudSimulator)fSimulators.get(i)).toString() + ".out");
                    out.println(report);
                    out.flush();
                    out.close();
                } catch (Exception var13) {
                    System.out.println("ERROR: " + var13.getMessage());
                }
            }

            t.saveFinalReport("workflow.final");
            ch.stop();
            System.out.println("\nExecution time: [" + ch.getTime() + "] milliseconds ");
        }
    }

    static Double computeDatasetsSize(Workflow w) {
        Double d = 0.0;

        int i;
        for(i = 0; i < w.getDatasets().size(); ++i) {
            d = d + ((DataSet)w.getDatasets().get(i)).getSize();
        }

        for(i = 0; i < w.getGeneratedDatasets().size(); ++i) {
            d = d + ((DataSet)w.getGeneratedDatasets().get(i)).getSize();
        }

        return d;
    }

    public void configureDataCenters() {
        Random lRandom = new Random();

        for(int i = 0; i < dataCenterCount; ++i) {
            DataCenter thisDC = new DataCenter();
            thisDC.setName("dc" + (i + 1));
            double thisShift = dataCenterSizeShift * lRandom.nextDouble();
            if (lRandom.nextBoolean()) {
                thisShift *= -1.0;
            }

            double thisCenterSize = dataCenterSize + thisShift * dataCenterSize;
            thisDC.setSize(thisCenterSize);
            thisDC.setP_ini(dataCenterPIni);
            thisDC.setP_max(dataCenterPMax);
            this.fDatacenters.add(thisDC);
        }

    }

    private void configureGenerator() {
        this.fGenerator = new WorkflowGenerator();
        this.fGenerator.dataCount = dataSetCount;
        this.fGenerator.maxDataSize = dataSetMaxSize;
        this.fGenerator.minDataSize = dataSetMinSize;
        this.fGenerator.maxDataUsage = dataSetMaxUse;
        this.fGenerator.taskCount = taskCount;
        this.fGenerator.maxGeneratedDataSize = taskResultMaxSize;
        this.fGenerator.minGeneratedDataSize = taskResultMinSize;
        this.fGenerator.maxTaskBranch = taskMaxBranch;
    }

    public void doTest(String aTestPrefix, Workflow lWork) throws Exception {
        this.resetDataCenters();
        System.out.println("--------------Organising fixed data for every DataCenter---Randomly-----------");
        Workflow lTreeWork = new Workflow(lWork);
        ArrayList<DataSet> lDataSets = lWork.getDatasets();
        Random lRandom = new Random();
        HashMap<DataCenter, Double> space = new HashMap();
        Iterator var7 = this.fDatacenters.iterator();

        while(var7.hasNext()) {
            DataCenter d = (DataCenter)var7.next();
            space.put(d, d.freeSpace());
        }

        System.out.println("DataCenter_free_space:" + space);
        int q = 0;

        DataSet report;
        int i;
        for(i = 0; (double)i < (double)lDataSets.size() * FIXED_DATASETS; ++i) {
            report = (DataSet)lDataSets.get(i);
            DataCenter aDatacentre = (DataCenter)this.fDatacenters.get(Math.abs(lRandom.nextInt() % this.fDatacenters.size()));
            System.out.println("DataSet[" + report + "]=" + report.getSize() + " will be fixed at DataCenter:" + aDatacentre);
            if ((Double)space.get(aDatacentre) >= report.getSize()) {
                space.put(aDatacentre, (Double)space.get(aDatacentre) - report.getSize());
                report.setFixedAddress(aDatacentre);
                report.setName(report.getName() + "_f_" + report.getFixedAddress());
            } else {
                --i;
                ++q;
                if (q > 100) {
                    ++i;
                    q = 0;
                }
            }
        }

        System.out.println("DataCenter_free_space_after fixing datasets:" + space);
        System.out.println("--------------Fixed data has been arranged--------------");
        lTreeWork.setDatasets(lDataSets);

        for(i = 0; i < this.fSimulators.size(); ++i) {
            System.out.println("****************************************  " + ((CloudSimulator)this.fSimulators.get(i)).toString() + "  ****************************************");
            System.out.print("-->calling_class_FlexiSimulator");
            report = null;

            try {
                String report = ((CloudSimulator)this.fSimulators.get(i)).simulateWorkflow(new Workflow(lTreeWork));
                PrintStream out = new PrintStream(aTestPrefix + "-" + ((CloudSimulator)this.fSimulators.get(i)).toString() + ".out");
                out.println(report);
                out.flush();
                out.close();
            } catch (Exception var11) {
                System.out.println("ERROR : " + var11.getMessage());
            }
        }

        ++this.fTestCount;
    }

    private ArrayList<CloudSimulator> generateSimulators() {
        ArrayList<CloudSimulator> result = new ArrayList();
        BuildTime lBuilder12 = new BuildTime(this.fDatacenters);
        IT2FCMSimulator sim12 = new IT2FCMSimulator(this.fDatacenters, new FuzzyRuntime(lBuilder12), lBuilder12);
        sim12.name = "IT2DFCM";
        result.add(sim12);
        BuildTime lBuilder13 = new BuildTime(this.fDatacenters);
        IT2FCMSimulator sim13 = new IT2FCMSimulator(this.fDatacenters, new FuzzyRuntimePSO(lBuilder13), lBuilder13);
        sim13.name = "IT2FCMPSO";
        result.add(sim13);
        return result;
    }

    private void resetDataCenters() {
        for(int i = 0; i < this.fDatacenters.size(); ++i) {
            ((DataCenter)this.fDatacenters.get(i)).resetDataCenter();
        }

    }

    public void saveFinalReport(String reportName) {
        PrintWriter out;
        try {
            out = new PrintWriter(reportName);
        } catch (Exception var9) {
            System.out.println("Cannot save Final Report");
            System.out.println(var9);
            System.out.println(var9.getMessage());
            return;
        }

        out.println("Final Report");
        out.println("============");
        out.println("Tests: " + this.fTestCount);
        out.println("Tests Failed: " + fFailedTestCount);
        out.println("Parameters:");
        out.println("  Data Centers: " + dataCenterCount);
        out.println("  |  p_ini: " + dataCenterPIni);
        out.println("  |  p_max: " + dataCenterPMax);
        out.println("  |  size(one center):  " + dataCenterSize + "=|datasetsSize|*|" + dataCenterPercent + "|");
        out.println("  |  shift: " + dataCenterSizeShift * 100.0 + "%");
        out.println("  -----------");
        out.println();
        out.println("  Workflows:");
        out.println("  |  DataSets Count: " + dataSetCount + "  |  DataSets Size: " + datasetsSize);
        out.println("  |  Initial DataSets Count: " + dataSetCountInit);
        out.println("  |  Generated DataSets Count: " + dataSetCountGen);
        out.println("  |  Task Count: " + daxPath);
        out.println("  |  Fixed DataSet Percentage: " + FIXED_DATASETS * 100.0 + "%");
        out.println("  --------------------------");
        out.println();
        int maxSimNameLength = 0;

        int i;
        for(i = 0; i < this.fSimulators.size(); ++i) {
            if (((CloudSimulator)this.fSimulators.get(i)).toString().length() > maxSimNameLength) {
                maxSimNameLength = ((CloudSimulator)this.fSimulators.get(i)).toString().length();
            }
        }

        for(i = 0; i < this.fSimulators.size(); ++i) {
            double totalMoved = 0.0;
            double totalMovedSize = 0.0;
            out.print(padWithSpaces(((CloudSimulator)this.fSimulators.get(i)).toString(), maxSimNameLength) + ": ");
            out.println("Data Retrieved:   " + ((CloudSimulator)this.fSimulators.get(i)).getAverageDataRetrieved() + " (" + ((CloudSimulator)this.fSimulators.get(i)).getAverageDataRetrievedSize() + ")");
            totalMoved += ((CloudSimulator)this.fSimulators.get(i)).getAverageDataRetrieved();
            totalMovedSize += ((CloudSimulator)this.fSimulators.get(i)).getAverageDataRetrievedSize();
            out.print(padWithSpaces("", maxSimNameLength + 2));
            out.println("Data Sent:        " + ((CloudSimulator)this.fSimulators.get(i)).getAverageDataSent() + " (" + ((CloudSimulator)this.fSimulators.get(i)).getAverageDataSentSize() + ")");
            totalMoved += ((CloudSimulator)this.fSimulators.get(i)).getAverageDataSent();
            totalMovedSize += ((CloudSimulator)this.fSimulators.get(i)).getAverageDataSentSize();
            out.print(padWithSpaces("", maxSimNameLength + 2));
            out.println("Data Rescheduled: " + ((CloudSimulator)this.fSimulators.get(i)).getAverageDataRescheduled() + " (" + ((CloudSimulator)this.fSimulators.get(i)).getAverageDataRescheduledSize() + ")");
            out.print(padWithSpaces("", maxSimNameLength + 2));
            out.println("Number of Reschedules: " + ((CloudSimulator)this.fSimulators.get(i)).getAverageDataReschedules());
            totalMoved += ((CloudSimulator)this.fSimulators.get(i)).getAverageDataRescheduled();
            totalMovedSize += ((CloudSimulator)this.fSimulators.get(i)).getAverageDataRescheduledSize();
            out.print(padWithSpaces("", maxSimNameLength + 2));
            out.println("Total Movement:   " + totalMoved + " (" + totalMovedSize + ")");
            out.println();
            out.print(padWithSpaces("", maxSimNameLength + 2));
            out.println("Average Dataset Movement Std Dev: " + ((CloudSimulator)this.fSimulators.get(i)).getMovementSD());
            out.print(padWithSpaces("", maxSimNameLength + 2));
            out.println("Average Task Execution Std Dev: " + ((CloudSimulator)this.fSimulators.get(i)).getTaskExecutionSD());
            out.println();
        }

        out.flush();
        out.close();
    }

    public static String padWithSpaces(String aString, int length) {
        String result;
        for(result = aString; result.length() < length; result = " " + result) {
        }

        return result;
    }
}
