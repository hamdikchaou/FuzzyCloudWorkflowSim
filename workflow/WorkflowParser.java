//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package workflow;

import dataPlacement.DataSet;
import dataPlacement.Task;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import realworkflow.Log;

public class WorkflowParser {
    private ArrayList<DataSet> fDatasets;
    private ArrayList<Task> fTasks;
    private ArrayList<DataSet> fGeneratedDatasets;
    private Workflow wf;
    protected HashMap<String, DataSet> data;
    public String daxPath;

    public WorkflowParser(String daxPath) {
        this.daxPath = daxPath;
    }

    public Workflow parse() {
        Workflow w = new Workflow();
        if (this.daxPath != null) {
            w = this.parseXmlFile(this.daxPath);
        }

        return w;
    }

    private Workflow parseXmlFile(String path) {
        this.fTasks = new ArrayList();
        this.fDatasets = new ArrayList();
        this.fGeneratedDatasets = new ArrayList();
        this.data = new HashMap();

        try {
            SAXBuilder builder = new SAXBuilder();
            Document dom = builder.build(new File(path));
            Element root = dom.getRootElement();
            List<Element> list = root.getChildren();
            Iterator var8 = list.iterator();

            label105:
            while(true) {
                while(true) {
                    if (!var8.hasNext()) {
                        break label105;
                    }

                    Element node = (Element)var8.next();
                    String inout;
                    String parentName;
                    switch (node.getName().toLowerCase()) {
                        case "job":
                            String nodeName = node.getAttributeValue("id");
                            Task thisTask = new Task();
                            thisTask.setName(nodeName);
                            List<Element> fileList = node.getChildren();
                            Iterator var27 = fileList.iterator();

                            while(var27.hasNext()) {
                                Element file = (Element)var27.next();
                                if (file.getName().toLowerCase().equals("uses")) {
                                    DataSet thisDataset = new DataSet();
                                    String fileName = file.getAttributeValue("name");
                                    if (fileName == null) {
                                        fileName = file.getAttributeValue("file");
                                    }

                                    if (fileName == null) {
                                        Log.print("Error in parsing xml");
                                    }

                                    inout = file.getAttributeValue("link");
                                    double size = 0.0;
                                    parentName = file.getAttributeValue("size");
                                    if (parentName != null) {
                                        size = Double.parseDouble(parentName);
                                    } else {
                                        Log.printLine("File Size not found for " + fileName);
                                    }

                                    thisDataset.setName(fileName);
                                    thisDataset.setSize(size);
                                    thisDataset.setExists(true);
                                    if (inout.equals("output")) {
                                        thisDataset.setWasGenerated(true);
                                        thisTask.addOutput(thisDataset);
                                    } else {
                                        thisDataset.setWasGenerated(false);
                                        thisTask.addInput(thisDataset);
                                    }

                                    String val = thisDataset.getName() + thisDataset.getSize() + thisDataset;
                                    if (!this.data.containsKey(val)) {
                                        this.data.put(val, thisDataset);
                                    }
                                }
                            }

                            this.fTasks.add(thisTask);
                            break;
                        case "child":
                            List<Element> pList = node.getChildren();
                            String childName = node.getAttributeValue("ref");

                            for(int i = 0; i < this.fTasks.size(); ++i) {
                                inout = ((Task)this.fTasks.get(i)).getName();
                                if (inout.equals(childName)) {
                                    Iterator var18 = pList.iterator();

                                    while(var18.hasNext()) {
                                        Element parent = (Element)var18.next();
                                        parentName = parent.getAttributeValue("ref");

                                        for(int j = 0; j < this.fTasks.size(); ++j) {
                                            String parents = ((Task)this.fTasks.get(j)).getName();
                                            if (parents.equals(parentName)) {
                                                Task parentTask = (Task)this.fTasks.get(j);
                                                ((Task)this.fTasks.get(i)).addParent(parentTask);
                                            }
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        } catch (JDOMException var24) {
            Log.printLine("JDOM Exception;Please make sure your dax file is valid");
        } catch (IOException var25) {
            Log.printLine("IO Exception;Please make sure dax.path is correctly set in your config file");
        } catch (Exception var26) {
            var26.printStackTrace();
            Log.printLine("Parsing Exception");
        }

        this.fDatasets = this.extract(this.data);
        this.fTasks = this.adjustTasks(path);
        this.fGeneratedDatasets = this.generated(this.fDatasets);
        this.fDatasets = this.initialDatasets(this.fDatasets, this.fGeneratedDatasets);
        this.wf = new Workflow();
        this.wf.setTasks(this.fTasks);
        this.wf.setDatasets(this.fDatasets);
        this.wf.setGeneratedDatasets(this.fGeneratedDatasets);
        return this.wf;
    }

    private ArrayList<DataSet> extract(Map<String, DataSet> data) {
        ArrayList<DataSet> d = new ArrayList();
        Iterator var3 = data.entrySet().iterator();

        while(var3.hasNext()) {
            Map.Entry mapentry = (Map.Entry)var3.next();
            DataSet x = (DataSet)mapentry.getValue();
            d.add(x);
        }

        return d;
    }

    private boolean cherche(ArrayList<DataSet> data, DataSet d) {
        boolean b = false;

        for(int i = 0; i < data.size(); ++i) {
            if (((DataSet)data.get(i)).getName().equals(d.getName()) && ((DataSet)data.get(i)).getSize() == d.getSize()) {
                b = true;
            }
        }

        return b;
    }

    private DataSet trouveDataset(ArrayList<DataSet> data, DataSet d) {
        DataSet b = new DataSet();

        for(int i = 0; i < data.size(); ++i) {
            if (((DataSet)data.get(i)).getName().equals(d.getName()) && ((DataSet)data.get(i)).getSize() == d.getSize()) {
                b = (DataSet)data.get(i);
            }
        }

        return b;
    }

    public ArrayList<Task> adjustTasks(String path) {
        ArrayList<Task> fTasks = new ArrayList();

        try {
            SAXBuilder builder = new SAXBuilder();
            Document dom = builder.build(new File(path));
            Element root = dom.getRootElement();
            List<Element> list = root.getChildren();
            Iterator var9 = list.iterator();

            while(true) {
                while(var9.hasNext()) {
                    Element node = (Element)var9.next();
                    String inout;
                    String parentName;
                    switch (node.getName().toLowerCase()) {
                        case "job":
                            String nodeName = node.getAttributeValue("id");
                            Task thisTask = new Task();
                            thisTask.setName(nodeName);
                            List<Element> fileList = node.getChildren();
                            Iterator var28 = fileList.iterator();

                            while(var28.hasNext()) {
                                Element file = (Element)var28.next();
                                if (file.getName().toLowerCase().equals("uses")) {
                                    DataSet thisDataset = new DataSet();
                                    String fileName = file.getAttributeValue("name");
                                    if (fileName == null) {
                                        fileName = file.getAttributeValue("file");
                                    }

                                    if (fileName == null) {
                                        Log.print("Error in parsing xml");
                                    }

                                    inout = file.getAttributeValue("link");
                                    double size = 0.0;
                                    parentName = file.getAttributeValue("size");
                                    if (parentName != null) {
                                        size = Double.parseDouble(parentName);
                                    } else {
                                        Log.printLine("File Size not found for " + fileName);
                                    }

                                    thisDataset.setName(fileName);
                                    thisDataset.setSize(size);
                                    DataSet x = this.trouveDataset(this.fDatasets, thisDataset);
                                    if (inout.equals("output")) {
                                        thisTask.addOutput(x);
                                    } else {
                                        thisTask.addInput(x);
                                        x.addTask(thisTask);
                                    }
                                }
                            }

                            fTasks.add(thisTask);
                            break;
                        case "child":
                            List<Element> pList = node.getChildren();
                            String childName = node.getAttributeValue("ref");

                            for(int i = 0; i < fTasks.size(); ++i) {
                                inout = ((Task)fTasks.get(i)).getName();
                                if (inout.equals(childName)) {
                                    Iterator var19 = pList.iterator();

                                    while(var19.hasNext()) {
                                        Element parent = (Element)var19.next();
                                        parentName = parent.getAttributeValue("ref");

                                        for(int j = 0; j < fTasks.size(); ++j) {
                                            String parents = ((Task)fTasks.get(j)).getName();
                                            if (parents.equals(parentName)) {
                                                Task parentTask = (Task)fTasks.get(j);
                                                ((Task)fTasks.get(i)).addParent(parentTask);
                                            }
                                        }
                                    }
                                }
                            }
                    }
                }

                return fTasks;
            }
        } catch (JDOMException var25) {
            Log.printLine("JDOM Exception;Please make sure your dax file is valid");
        } catch (IOException var26) {
            Log.printLine("IO Exception;Please make sure dax.path is correctly set in your config file");
        } catch (Exception var27) {
            var27.printStackTrace();
            Log.printLine("Parsing Exception");
        }

        return fTasks;
    }

    public void adjustTasksDatasets(ArrayList<Task> fTasks) {
        for(int i = 0; i < fTasks.size(); ++i) {
            int j;
            DataSet x;
            int k;
            for(j = 0; j < ((Task)fTasks.get(i)).getInput().size(); ++j) {
                x = (DataSet)((Task)fTasks.get(i)).getInput().get(j);

                for(k = 0; k < this.fDatasets.size(); ++k) {
                    if (((DataSet)this.fDatasets.get(k)).getName().equals(x.getName()) && ((DataSet)this.fDatasets.get(k)).getSize() == x.getSize() && ((DataSet)this.fDatasets.get(k)).wasGenerated() == x.wasGenerated()) {
                        ((DataSet)this.fDatasets.get(k)).addTask((Task)fTasks.get(i));
                        ((Task)fTasks.get(i)).removeInput(x);
                        ((Task)fTasks.get(i)).addInput((DataSet)this.fDatasets.get(k));
                    }
                }

                for(k = 0; k < this.fGeneratedDatasets.size(); ++k) {
                    if (((DataSet)this.fGeneratedDatasets.get(k)).getName().equals(x.getName()) && ((DataSet)this.fGeneratedDatasets.get(k)).getSize() == x.getSize() && ((DataSet)this.fGeneratedDatasets.get(k)).wasGenerated() == x.wasGenerated()) {
                        ((DataSet)this.fGeneratedDatasets.get(k)).addTask((Task)fTasks.get(i));
                        ((Task)fTasks.get(i)).removeInput((DataSet)this.fGeneratedDatasets.get(k));
                        ((Task)fTasks.get(i)).addInput(x);
                    }
                }
            }

            for(j = 0; j < ((Task)fTasks.get(i)).getOutput().size(); ++j) {
                x = (DataSet)((Task)fTasks.get(i)).getOutput().get(j);

                for(k = 0; k < this.fDatasets.size(); ++k) {
                    if (((DataSet)this.fDatasets.get(k)).getName().equals(x.getName()) && ((DataSet)this.fDatasets.get(k)).getSize() == x.getSize()) {
                        ((Task)fTasks.get(i)).removeOutput(x);
                        ((Task)fTasks.get(i)).addOutput((DataSet)this.fDatasets.get(k));
                    }
                }

                for(k = 0; k < this.fGeneratedDatasets.size(); ++k) {
                    if (((DataSet)this.fGeneratedDatasets.get(k)).getName().equals(x.getName()) && ((DataSet)this.fGeneratedDatasets.get(k)).getSize() == x.getSize()) {
                        ((Task)fTasks.get(i)).removeOutput(x);
                        ((Task)fTasks.get(i)).addOutput((DataSet)this.fGeneratedDatasets.get(k));
                    }
                }
            }
        }

    }

    public ArrayList<DataSet> generated2() {
        ArrayList<DataSet> d = new ArrayList();

        for(int i = 0; i < this.fDatasets.size(); ++i) {
            if (((DataSet)this.fDatasets.get(i)).wasGenerated()) {
                DataSet x = new DataSet();
                x.setExists(true);
                x.setName(((DataSet)this.fDatasets.get(i)).getName());
                x.setSize(((DataSet)this.fDatasets.get(i)).getSize());
                d.add(x);
            }
        }

        return d;
    }

    public ArrayList<DataSet> generated(ArrayList<DataSet> fDatasets) {
        ArrayList<DataSet> gdata = new ArrayList();
        Map<String, DataSet> generated = new HashMap();

        for(int i = 0; i < fDatasets.size(); ++i) {
            generated.put(((DataSet)fDatasets.get(i)).getName() + ((DataSet)fDatasets.get(i)).getSize(), (DataSet)fDatasets.get(i));
        }

        Set cles = generated.keySet();
        Iterator it = cles.iterator();

        while(it.hasNext()) {
            Object cle = it.next();
            Object valeur = cle.toString();
            DataSet dataset = (DataSet)generated.get(valeur);
            if (dataset.wasGenerated()) {
                gdata.add(dataset);
            }
        }

        return gdata;
    }

    public ArrayList<DataSet> initialDatasets(ArrayList<DataSet> fDatasets, ArrayList<DataSet> fGeneratedDatasets2) {
        ArrayList<DataSet> fdata = new ArrayList();
        Map<String, DataSet> all = new HashMap();

        int i;
        for(i = 0; i < fDatasets.size(); ++i) {
            all.put(((DataSet)fDatasets.get(i)).getName() + ((DataSet)fDatasets.get(i)).getSize(), (DataSet)fDatasets.get(i));
        }

        for(i = 0; i < fGeneratedDatasets2.size(); ++i) {
            Object val = ((DataSet)fGeneratedDatasets2.get(i)).getName() + ((DataSet)fGeneratedDatasets2.get(i)).getSize();
            all.remove(val);
        }

        Set cles = all.keySet();
        Iterator it = cles.iterator();

        while(it.hasNext()) {
            Object cle = it.next();
            String valeur = cle.toString();
            DataSet dataset = (DataSet)all.get(valeur);
            fdata.add(dataset);
        }

        return fdata;
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

    public void afficheDatasets(ArrayList<DataSet> fdata) {
        for(int i = 0; i < fdata.size(); ++i) {
            Log.print("Dataset:" + ((DataSet)fdata.get(i)).getName());
            Log.print(" Size:" + ((DataSet)fdata.get(i)).getSize());
            Log.print(" Generated:" + ((DataSet)fdata.get(i)).wasGenerated());
            Log.printLine();
        }

    }

    public void afficheDataset(DataSet fdata) {
        Log.print("Dataset:" + fdata.getName());
        Log.print(" Size:" + fdata.getSize());
        Log.print(" Generated:" + fdata.wasGenerated());
        Log.printLine();
    }
}
