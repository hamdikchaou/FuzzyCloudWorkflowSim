//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;

public class Task {
    protected String fName;
    protected ArrayList<DataSet> fInput = new ArrayList();
    protected ArrayList<DataSet> fOutput = new ArrayList();
    protected ArrayList<Task> fParents = new ArrayList();
    protected ArrayList<Task> fChildren = new ArrayList();

    public Task() {
    }

    public ArrayList<Task> getChildren() {
        return this.fChildren;
    }

    public ArrayList<Task> getParents() {
        return this.fParents;
    }

    public ArrayList<DataSet> getInput() {
        return this.fInput;
    }

    public void setInput(ArrayList<DataSet> aInput) {
        this.fInput = aInput;
    }

    public String getName() {
        return this.fName;
    }

    public void setName(String aName) {
        this.fName = aName;
    }

    public ArrayList<DataSet> getOutput() {
        return this.fOutput;
    }

    public void setOutput(ArrayList<DataSet> aOutput) {
        this.fOutput = aOutput;
    }

    public void addInput(DataSet aDataset) {
        for(int i = 0; i < this.fInput.size(); ++i) {
            if (((DataSet)this.fInput.get(i)).getName().compareTo(aDataset.getName()) == 0) {
                return;
            }
        }

        this.fInput.add(aDataset);
    }

    public void removeInput(DataSet aDataset) {
        for(int i = 0; i < this.fInput.size(); ++i) {
            if (((DataSet)this.fInput.get(i)).getName().equals(aDataset.getName()) && ((DataSet)this.fInput.get(i)).getSize() == aDataset.getSize()) {
                this.fInput.remove(this.fInput.get(i));
            }
        }

    }

    public void addOutput(DataSet aDataset) {
        for(int i = 0; i < this.fOutput.size(); ++i) {
            if (((DataSet)this.fOutput.get(i)).getName().compareTo(aDataset.getName()) == 0) {
                return;
            }
        }

        this.fOutput.add(aDataset);
    }

    public void removeOutput(DataSet aDataset) {
        for(int i = 0; i < this.fOutput.size(); ++i) {
            if (((DataSet)this.fOutput.get(i)).getName().equals(aDataset.getName()) && ((DataSet)this.fOutput.get(i)).getSize() == aDataset.getSize()) {
                this.fOutput.remove(aDataset);
            }
        }

    }

    public boolean isReady() {
        for(int i = 0; i < this.fInput.size(); ++i) {
            if (!((DataSet)this.fInput.get(i)).exists()) {
                return false;
            }
        }

        return true;
    }

    public void addParent(Task aTask) {
        if (!this.fParents.contains(aTask)) {
            this.fParents.add(aTask);
        }

        aTask.addAsChild(this);
    }

    protected void addAsParent(Task aTask) {
        if (!this.fParents.contains(aTask)) {
            this.fParents.add(aTask);
        }

    }

    public void addChild(Task aTask) {
        if (!this.fChildren.contains(aTask)) {
            this.fChildren.add(aTask);
        }

        aTask.addAsParent(this);
    }

    protected void addAsChild(Task aTask) {
        if (!this.fChildren.contains(aTask)) {
            this.fChildren.add(aTask);
        }

    }

    public int getChildCount() {
        return this.fChildren.size();
    }

    public boolean canParent(Task aChild) {
        if (this.fParents.size() == 0) {
            return true;
        } else if (this.fParents.contains(aChild)) {
            return false;
        } else {
            for(int i = 0; i < this.fParents.size(); ++i) {
                if (!((Task)this.fParents.get(i)).canParent(aChild)) {
                    return false;
                }
            }

            return true;
        }
    }

    public String toString() {
        return this.fName;
    }
}
