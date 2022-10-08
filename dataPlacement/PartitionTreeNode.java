//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class PartitionTreeNode implements Comparable {
    ArrayList<DataSet> fDataSet;
    protected PartitionTreeNode lChild;
    protected PartitionTreeNode rChild;
    protected PartitionTreeNode fParent;
    protected PartitionTreeNode fSibling;
    private boolean assigned;
    private boolean hasChildren;
    PartitionTree fTree;
    int size;
    int fixedSize;
    private int fHorizontalPosition;
    ArrayList<DataCenter> myHome;
    private DataCenter myDataCentre;
    public int dependancyValue;
    public HashSet<PartitionTreeNode> myChildren;
    private Clusterer fCluster;

    public PartitionTreeNode(ArrayList<DataSet> aDataSet, PartitionTree aTree) throws DistributionException {
        this(aDataSet, (PartitionTreeNode)null, aTree, 0);
    }

    protected PartitionTreeNode(ArrayList<DataSet> fDataSet, PartitionTreeNode aParent, PartitionTree aTree, int aHorizontalPosition) throws DistributionException {
        this.dependancyValue = 0;
        this.fCluster = new BEA();
        System.out.println("Making a new node " + fDataSet.size());
        this.myChildren = new HashSet();
        this.myDataCentre = new DataCenter();
        this.fParent = aParent;
        this.fTree = aTree;
        this.fHorizontalPosition = aHorizontalPosition;
        this.fDataSet = new ArrayList(fDataSet);
        this.myHome = new ArrayList();
        Iterator var5 = fDataSet.iterator();

        while(var5.hasNext()) {
            DataSet s = (DataSet)var5.next();
            this.size = (int)((double)this.size + s.getSize());
            if (s.getFixedAddress() != null) {
                this.fixedSize = (int)((double)this.fixedSize + s.getSize());
            }

            if (s.getFixedAddress() != null && !this.myHome.contains(s.getFixedAddress())) {
                this.myHome.add(s.getFixedAddress());
            }
        }

        this.myHome.trimToSize();
        System.out.println(this.myHome.size() + "   " + fDataSet.size());

        try {
            if (this.myHome.size() > 1) {
                this.split();
            }
        } catch (DistributionException var7) {
            var7.printStackTrace();
        }

        System.out.println("Returning " + fDataSet.size());
    }

    public boolean split() throws DistributionException {
        if (this.rChild == null && this.lChild == null) {
            if (this.fDataSet.size() == 1) {
                return false;
            } else {
                ArrayList<DataSet> lChildData = new ArrayList();
                ArrayList<DataSet> rChildData = new ArrayList();
                int l = false;
                Matrix lMatrix = new Matrix();
                int bestPoint = 0;
                int bestPointScore = 0;

                int i;
                for(i = 0; i < this.fDataSet.size(); ++i) {
                    lMatrix.addDataset((DataSet)this.fDataSet.get(i));
                }

                lMatrix = this.fCluster.cluster(lMatrix);

                for(i = 1; i < this.fDataSet.size() - 1; ++i) {
                    int thisPointScore = this.calculatePartitionPointScore(lMatrix, i);
                    if (thisPointScore > bestPointScore) {
                        bestPointScore = thisPointScore;
                        bestPoint = i;
                    }
                }

                for(i = 0; i < bestPoint; ++i) {
                    lChildData.add((DataSet)lMatrix.getDatasets().get(i));
                }

                for(i = bestPoint; i < lMatrix.getDatasets().size(); ++i) {
                    rChildData.add((DataSet)lMatrix.getDatasets().get(i));
                }

                if (this.fDataSet.size() == 2) {
                    lChildData = new ArrayList();
                    lChildData.add((DataSet)this.fDataSet.get(0));
                    rChildData = new ArrayList();
                    rChildData.add((DataSet)this.fDataSet.get(1));
                }

                if (this.fDataSet.size() <= 1) {
                    throw new DistributionException("Trying to split a node that has one or less datasets");
                } else if (lChildData.size() != 0 && rChildData.size() != 0) {
                    this.lChild = new PartitionTreeNode(lChildData, this, this.fTree, this.fHorizontalPosition);
                    this.rChild = new PartitionTreeNode(rChildData, this, this.fTree, this.fHorizontalPosition + 1);
                    this.lChild.fSibling = this.rChild;
                    this.rChild.fSibling = this.lChild;
                    this.fTree.addLeaves(this.lChild, this.rChild);
                    Iterator var10 = this.fTree.fLeaves.values().iterator();

                    while(var10.hasNext()) {
                        PartitionTreeNode p = (PartitionTreeNode)var10.next();
                        System.out.println(p.toString() + " " + p.size + " " + p.getDataSets().size());
                    }

                    this.hasChildren = true;
                    PartitionTreeNode tmp = this.fParent;
                    this.myChildren.add(this.lChild);
                    this.myChildren.add(this.rChild);

                    while(tmp != null && tmp.fParent != null) {
                        tmp.myChildren.add(this.lChild);
                        tmp.myChildren.add(this.rChild);
                        tmp = tmp.fParent;
                    }

                    return true;
                } else {
                    throw new DistributionException("Trying to split a node such that it will have an empty child");
                }
            }
        } else {
            throw new DistributionException("Trying to split a node which already has children");
        }
    }

    public int getHorizontalPosition() {
        return this.fHorizontalPosition;
    }

    public void setHorizontalPosition(int horizontalPosition) {
        this.fHorizontalPosition = horizontalPosition;
    }

    public ArrayList<DataSet> getDataSets() {
        return this.fDataSet;
    }

    public int compareTo(Object o) {
        if (o instanceof PartitionTreeNode) {
            return this.size - ((PartitionTreeNode)o).size;
        } else {
            throw new ClassCastException("Comparison failure with PartionionTreeNode!");
        }
    }

    public boolean isAssigned() {
        return this.assigned;
    }

    public void setAssigned(boolean assigned) {
        System.out.println(this.toString() + " has been assigned");
        this.assigned = assigned;
        if (assigned && this.fParent != null && this.fSibling.isAssigned()) {
            this.fParent.setAssigned(true);
        }

    }

    public boolean assign(DataCenter dc) throws Exception {
        if (this.isAssigned()) {
            throw new Exception("Trying to assign an already assigned node!");
        } else if (this.myHome.size() == 1 && this.myHome.get(0) != dc) {
            throw new Exception("Tried to assign to the wrong datacentre! " + ((DataCenter)this.myHome.get(0)).getName() + " " + dc.getName());
        } else {
            this.myDataCentre = dc;
            Iterator var2 = this.fDataSet.iterator();

            while(var2.hasNext()) {
                DataSet ds = (DataSet)var2.next();
                ds.setDC(dc);
                if (!dc.getDatasets().contains(ds)) {
                    dc.addDataset(ds);
                }
            }

            this.setAssigned(true);
            this.myHome.add(dc);
            this.fParent.myHome.add(dc);
            this.myDataCentre = dc;
            System.out.println(this.toString() + " has assigned successfully");
            return true;
        }
    }

    public PartitionTreeNode getGreatestDependency() {
        return !this.fSibling.isAssigned() ? this.fSibling : this.fParent.getGreatestDependency();
    }

    public boolean hasChildren() {
        return this.hasChildren;
    }

    public DataCenter getMyDataCentre() {
        return this.myDataCentre;
    }

    public PartitionTreeNode getNearestUnassignedNode(HashSet<PartitionTreeNode> aNode) {
        HashSet<PartitionTreeNode> newANode = new HashSet(aNode);
        newANode.add(this);
        PartitionTreeNode result = null;
        if (this.lChild != null && !newANode.contains(this.lChild)) {
            result = this.lChild.getNearestUnassignedNode(newANode);
        }

        if (result != null) {
            return result;
        } else {
            if (this.rChild != null && !newANode.contains(this.rChild)) {
                result = this.rChild.getNearestUnassignedNode(newANode);
            }

            if (result != null) {
                return result;
            } else if (!aNode.contains(this) && !this.isAssigned() && !this.hasChildren()) {
                return this;
            } else {
                return aNode.contains(this.fParent) ? null : this.fParent.getNearestUnassignedNode(newANode);
            }
        }
    }

    public int GetPartitionDistanceFromMe(PartitionTreeNode p) {
        return this.fTree.getDistanceFromPartion(this, p);
    }

    public void fixFData() {
        if (this.fTree.fData.containsValue(this) && this.hasChildren()) {
            Iterator var1 = this.rChild.getDataSets().iterator();

            DataSet ds;
            while(var1.hasNext()) {
                ds = (DataSet)var1.next();
                this.fTree.fData.put(ds, this.rChild);
            }

            this.rChild.fixFData();
            var1 = this.lChild.getDataSets().iterator();

            while(var1.hasNext()) {
                ds = (DataSet)var1.next();
                this.fTree.fData.put(ds, this.lChild);
            }

            this.lChild.fixFData();
        }

    }

    public void fixFLeaves() {
        if (this.fTree.fLeaves.containsValue(this) && this.hasChildren()) {
            this.fTree.fLeaves.remove(this);
            if (!this.rChild.hasChildren()) {
                if (!this.fTree.fLeaves.containsValue(this.rChild)) {
                    this.fTree.fLeaves.put(this.fHorizontalPosition, this);
                } else {
                    this.rChild.fixFLeaves();
                }
            }

            if (!this.lChild.hasChildren()) {
                if (!this.fTree.fLeaves.containsValue(this.lChild)) {
                    this.fTree.fLeaves.put(this.fHorizontalPosition, this);
                } else {
                    this.lChild.fixFLeaves();
                }
            }
        }

    }

    private int calculatePartitionPointScore(Matrix aMatrix, int aPoint) {
        int topLeft = 0;

        int bottomRight;
        int excludedPoints;
        for(bottomRight = 0; bottomRight <= aPoint; ++bottomRight) {
            for(excludedPoints = 0; excludedPoints <= aPoint; ++excludedPoints) {
                topLeft += aMatrix.getData()[bottomRight][excludedPoints];
            }
        }

        bottomRight = 0;

        int i;
        for(excludedPoints = aPoint + 1; excludedPoints < aMatrix.getData().length; ++excludedPoints) {
            for(i = aPoint + 1; i < aMatrix.getData().length; ++i) {
                bottomRight += aMatrix.getData()[excludedPoints][i];
            }
        }

        excludedPoints = 0;

        for(i = 0; i <= aPoint; ++i) {
            for(int j = aPoint + 1; j < aMatrix.getData().length; ++j) {
                excludedPoints += aMatrix.getData()[i][j];
            }
        }

        i = topLeft * bottomRight;
        i = (int)((double)i - Math.pow((double)excludedPoints, 2.0));
        return i;
    }
}
