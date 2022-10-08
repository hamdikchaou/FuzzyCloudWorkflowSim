//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PartitionTree {
    ArrayList<DataSet> fDataSet;
    ArrayList<DataCenter> fDataCentre;
    HashMap<Integer, PartitionTreeNode> fLeaves;
    PartitionTreeNode fTop;
    HashMap<DataSet, PartitionTreeNode> fData;

    public PartitionTree(ArrayList<DataSet> fDataSet, ArrayList<DataCenter> fDataCentre) throws DistributionException {
        this.fDataSet = fDataSet;
        this.fDataCentre = fDataCentre;
        this.fLeaves = new HashMap();
        this.fData = new HashMap();
        this.fTop = new PartitionTreeNode(fDataSet, this);
        if (!this.fTop.hasChildren()) {
            this.fLeaves.put(this.fTop.getHorizontalPosition(), this.fTop);
        }

        Iterator var3;
        if (this.fLeaves.containsValue(this.fTop) && this.fTop.hasChildren()) {
            var3 = this.fLeaves.keySet().iterator();

            while(var3.hasNext()) {
                Integer key = (Integer)var3.next();
                if (this.fLeaves.get(key) == this.fTop) {
                    this.fLeaves.remove(key);
                    break;
                }
            }
        }

        var3 = fDataSet.iterator();

        while(var3.hasNext()) {
            DataSet ds = (DataSet)var3.next();
            if (this.fData.get(ds) == null) {
                this.fData.put(ds, this.fTop);
            }
        }

    }

    public boolean addLeaves(PartitionTreeNode aNode, PartitionTreeNode anotherNode) {
        Iterator var3;
        Integer key;
        if (this.fLeaves.containsValue(aNode.fParent)) {
            var3 = this.fLeaves.keySet().iterator();

            while(var3.hasNext()) {
                key = (Integer)var3.next();
                if (this.fLeaves.get(key) == aNode.fParent) {
                    this.fLeaves.remove(key);
                    break;
                }
            }
        }

        if (this.fLeaves.containsValue(anotherNode.fParent)) {
            var3 = this.fLeaves.keySet().iterator();

            while(var3.hasNext()) {
                key = (Integer)var3.next();
                if (this.fLeaves.get(key) == anotherNode.fParent) {
                    this.fLeaves.remove(key);
                    break;
                }
            }
        }

        DataSet ds;
        PartitionTreeNode tmp;
        Iterator var7;
        if (!aNode.hasChildren()) {
            for(tmp = (PartitionTreeNode)this.fLeaves.put(aNode.getHorizontalPosition(), aNode); tmp != null; tmp = (PartitionTreeNode)this.fLeaves.put(tmp.getHorizontalPosition(), tmp)) {
                tmp.setHorizontalPosition(tmp.getHorizontalPosition() + 1);
            }

            var7 = aNode.getDataSets().iterator();

            while(var7.hasNext()) {
                ds = (DataSet)var7.next();
                this.fData.put(ds, aNode);
            }
        }

        if (!anotherNode.hasChildren()) {
            for(tmp = (PartitionTreeNode)this.fLeaves.put(anotherNode.getHorizontalPosition(), anotherNode); tmp != null; tmp = (PartitionTreeNode)this.fLeaves.put(tmp.getHorizontalPosition(), tmp)) {
                tmp.setHorizontalPosition(tmp.getHorizontalPosition() + 1);
            }

            var7 = anotherNode.getDataSets().iterator();

            while(var7.hasNext()) {
                ds = (DataSet)var7.next();
                this.fData.put(ds, anotherNode);
            }
        }

        return true;
    }

    public PartitionTreeNode getPartition(DataSet ds) {
        if (((PartitionTreeNode)this.fData.get(ds)).hasChildren()) {
            Iterator var2 = ((PartitionTreeNode)this.fData.get(ds)).rChild.getDataSets().iterator();

            DataSet d;
            while(var2.hasNext()) {
                d = (DataSet)var2.next();
                if (d == ds) {
                    this.fData.put(d, ((PartitionTreeNode)this.fData.get(ds)).rChild);
                }
            }

            var2 = ((PartitionTreeNode)this.fData.get(ds)).lChild.getDataSets().iterator();

            while(var2.hasNext()) {
                d = (DataSet)var2.next();
                if (d == ds) {
                    this.fData.put(d, ((PartitionTreeNode)this.fData.get(ds)).rChild);
                }
            }
        }

        return (PartitionTreeNode)this.fData.get(ds);
    }

    public boolean split(PartitionTreeNode node) throws DistributionException {
        node.split();
        return true;
    }

    public boolean distributionCompleted() {
        return this.fTop.isAssigned();
    }

    public PartitionTreeNode getGreatestDependency(DataSet ds) {
        return this.getGreatestDependency(this.getPartition(ds));
    }

    public PartitionTreeNode getGreatestDependency(PartitionTreeNode p) {
        return p.getGreatestDependency();
    }

    public int getDistanceFromPartion(PartitionTreeNode pa, PartitionTreeNode pb) {
        int result = false;
        int paIndex = 0;
        int pbIndex = 0;
        Iterator var6 = this.fLeaves.keySet().iterator();

        while(var6.hasNext()) {
            Integer i = (Integer)var6.next();
            if (((PartitionTreeNode)this.fLeaves.get(i)).equals(pb)) {
                pbIndex = i;
            }

            if (((PartitionTreeNode)this.fLeaves.get(i)).equals(pa)) {
                paIndex = i;
            }
        }

        int result;
        if (paIndex > pbIndex) {
            result = paIndex - pbIndex;
        } else {
            result = pbIndex - paIndex;
        }

        return result;
    }

    public int getNumberOfUnassignedNodes() {
        int i = 0;
        Iterator var2 = this.fLeaves.values().iterator();

        while(var2.hasNext()) {
            PartitionTreeNode p = (PartitionTreeNode)var2.next();
            if (!p.hasChildren() && !p.isAssigned()) {
                ++i;
            }
        }

        return i;
    }
}
