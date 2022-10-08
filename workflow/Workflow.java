//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package workflow;

import dataPlacement.DataSet;
import dataPlacement.Task;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import realworkflow.Log;

public class Workflow {
    private ArrayList<DataSet> fDatasets;
    private ArrayList<Task> fTasks;
    private ArrayList<DataSet> fGeneratedDatasets;

    public Workflow() {
        this.fDatasets = new ArrayList();
        this.fGeneratedDatasets = new ArrayList();
        this.fTasks = new ArrayList();
    }

    public Workflow(Workflow aWorkflow) {
        this.fDatasets = new ArrayList(aWorkflow.getDatasets());
        this.fGeneratedDatasets = new ArrayList(aWorkflow.getGeneratedDatasets());
        this.fTasks = new ArrayList(aWorkflow.getTasks());
    }

    public ArrayList<DataSet> getDatasets() {
        return this.fDatasets;
    }

    public void setDatasets(ArrayList<DataSet> datasets) {
        this.fDatasets = datasets;
    }

    public ArrayList<DataSet> getGeneratedDatasets() {
        return this.fGeneratedDatasets;
    }

    public void setGeneratedDatasets(ArrayList<DataSet> generatedDatasets) {
        this.fGeneratedDatasets = generatedDatasets;
    }

    public ArrayList<Task> getTasks() {
        return this.fTasks;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.fTasks = tasks;
    }

    public void save(String fileName) throws IOException {
        PrintStream out = new PrintStream(fileName);
        out.println("datasets intilales");

        int i;
        for(i = 0; i < this.fDatasets.size(); ++i) {
            out.println(this.fDatasets.get(i) + ":" + ((DataSet)this.fDatasets.get(i)).getSize());
        }

        out.println("");
        out.println("datasets g�n�r�es");

        for(i = 0; i < this.fGeneratedDatasets.size(); ++i) {
            out.println(this.fGeneratedDatasets.get(i) + ":" + ((DataSet)this.fGeneratedDatasets.get(i)).getSize());
        }

        out.println("");
        out.println("tasks");

        for(i = 0; i < this.fTasks.size(); ++i) {
            out.print(this.fTasks.get(i) + "-->");
            out.print("OutputDatasets=");

            int j;
            for(j = 0; j < ((Task)this.fTasks.get(i)).getOutput().size(); ++j) {
                out.print(((Task)this.fTasks.get(i)).getOutput().get(j) + ";");
            }

            out.print(" |InputDatasets=");

            for(j = 0; j < ((Task)this.fTasks.get(i)).getInput().size(); ++j) {
                out.print(((Task)this.fTasks.get(i)).getInput().get(j) + ";");
            }

            out.println();
            out.print("predecessors tasks=" + ((Task)this.fTasks.get(i)).getParents());
            out.println(" |successors tasks=" + ((Task)this.fTasks.get(i)).getChildren());
            out.println();
        }

        out.println();
    }

    public void reset() {
        for(int i = 0; i < this.fGeneratedDatasets.size(); ++i) {
            ((DataSet)this.fGeneratedDatasets.get(i)).setExists(false);
        }

    }

    public ArrayList<DataSet> renameinitdatasets(ArrayList<DataSet> data) {
        for(int i = 0; i < data.size(); ++i) {
            ((DataSet)data.get(i)).setName("d" + (i + 1));
        }

        return data;
    }

    public ArrayList<DataSet> renamegendatasets(ArrayList<DataSet> data) {
        for(int i = 0; i < data.size(); ++i) {
            ((DataSet)data.get(i)).setName("du" + (i + 1));
        }

        return data;
    }

    public ArrayList<Task> renametasks(ArrayList<Task> t) {
        for(int i = 0; i < t.size(); ++i) {
            ((Task)t.get(i)).setName("t" + (i + 1));
        }

        return t;
    }

    public void afficheTasks(ArrayList<Task> fTasks) {
        for(int i = 0; i < fTasks.size(); ++i) {
            Log.print("Task:" + ((Task)fTasks.get(i)).getName());
            Log.print(" Childs:" + ((Task)fTasks.get(i)).getChildCount());
            Log.printLine(" Parents:" + ((Task)fTasks.get(i)).getParents().size());
            Log.printLine("Input datasets:" + ((Task)fTasks.get(i)).getInput());
            Log.printLine("Output datasets:" + ((Task)fTasks.get(i)).getOutput());
            Log.printLine();
        }

    }

    public void afficheDatasets(ArrayList<DataSet> ffdatasets) {
        for(int i = 0; i < ffdatasets.size(); ++i) {
            Log.print("DataSet:" + ((DataSet)ffdatasets.get(i)).getName());
            Log.printLine("-->Used Tasks:" + ((DataSet)ffdatasets.get(i)).getTasks());
            Log.printLine();
        }

    }

    public void affiche(Workflow file) {
        ArrayList<Task> fTasks = file.getTasks();
        System.out.println("tasks");

        for(int i = 0; i < fTasks.size(); ++i) {
            System.out.print(fTasks.get(i) + "-->");
            System.out.print("OutputDatasets=");

            int j;
            for(j = 0; j < ((Task)fTasks.get(i)).getOutput().size(); ++j) {
                System.out.print(((Task)fTasks.get(i)).getOutput().get(j) + ";");
            }

            System.out.print(" |InputDatasets=");

            for(j = 0; j < ((Task)fTasks.get(i)).getInput().size(); ++j) {
                System.out.print(((Task)fTasks.get(i)).getInput().get(j) + ";");
            }

            System.out.println();
            System.out.print("predecessors tasks=" + ((Task)fTasks.get(i)).getParents());
            System.out.println(" |successors tasks=" + ((Task)fTasks.get(i)).getChildren());
            System.out.println();
        }

        System.out.println();
    }
}
