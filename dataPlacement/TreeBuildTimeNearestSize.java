//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import workflow.Workflow;

public class TreeBuildTimeNearestSize extends BuildTimeAlgorithm {
    HashMap<DataCenter, Double> space;
    PartitionTree fTree;
    private Clusterer fCluster = new BEA();

    public TreeBuildTimeNearestSize(ArrayList<DataCenter> aDatacenters) {
        super(aDatacenters);
    }

    public ArrayList<DataCenter> distribute(Workflow aWorkflow) throws DistributionException {
        return this.distribute(aWorkflow.getDatasets());
    }

    public ArrayList<DataCenter> distribute(ArrayList<DataSet> aDatasets) throws DistributionException {
        this.resetDataCenters();
        System.out.println("Starting Tree Nearest Size distribution");
        this.space = new HashMap();
        this.preOrganiseFixedData(aDatasets);
        this.fTree = new PartitionTree(aDatasets, this.fDataCenters);
        this.sortFixedData(aDatasets);

        while(!this.fTree.distributionCompleted()) {
            this.doTheRealWork(aDatasets);
        }

        Iterator var2 = this.fDataCenters.iterator();

        while(var2.hasNext()) {
            DataCenter d = (DataCenter)var2.next();
            d.setMaxCapacity(true);
        }

        return this.fDataCenters;
    }

    public void setDependancyMatrix(Matrix aMatrix) {
        throw new UnsupportedOperationException("Not supported yet.");
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

    public void preOrganiseFixedData(ArrayList<DataSet> aDatasets) {
        Iterator var2 = this.fDataCenters.iterator();

        DataCenter d;
        while(var2.hasNext()) {
            d = (DataCenter)var2.next();
            this.space.put(d, d.freeSpace());
            d.setDatasets(new ArrayList());
        }

        var2 = aDatasets.iterator();

        while(var2.hasNext()) {
            DataSet ds = (DataSet)var2.next();
            if (ds.getFixedAddress() != null) {
                ds.getFixedAddress().setHasFixedData(true);

                try {
                    if (this.space.get(ds.getFixedAddress()) == null) {
                        this.space.put(ds.getFixedAddress(), ds.getFixedAddress().freeSpace());
                    }

                    if (!((Double)this.space.get(ds.getFixedAddress()) - ds.getSize() > 0.0)) {
                        throw new DistributionException("Impossible amount of space required for fixed data.");
                    }

                    this.space.put(ds.getFixedAddress(), (Double)this.space.get(ds.getFixedAddress()) - ds.getSize());
                    System.out.println(ds.getName() + " added to " + ds.getFixedAddress().getName() + " leaving " + this.space.get(ds.getFixedAddress()) + " worth of free space tree");
                } catch (Exception var5) {
                    var5.printStackTrace();
                }
            }
        }

        var2 = this.fDataCenters.iterator();

        while(var2.hasNext()) {
            d = (DataCenter)var2.next();
            System.out.println(d.getName() + " has " + this.space.get(d) + " space left after required space for fixed datasets tree");
        }

    }

    public void sortFixedData(ArrayList<DataSet> aDatasets) throws DistributionException {
        int q = false;
        Iterator var3 = this.fDataCenters.iterator();

        while(var3.hasNext()) {
            DataCenter dc = (DataCenter)var3.next();
            dc.setDatasets(new ArrayList());
        }

        int oldI = 0;
        int loopCatch = 0;

        PartitionTreeNode lPartition;
        Iterator var7;
        DataSet s;
        for(int i = 0; i < aDatasets.size(); ++i) {
            if (oldI == i) {
                ++loopCatch;
            } else {
                loopCatch = 0;
            }

            oldI = i;
            if (loopCatch > 50) {
                break;
            }

            if (((DataSet)aDatasets.get(i)).getFixedAddress() != null) {
                System.out.println("Sorting out fixed data.");
                lPartition = this.fTree.getPartition((DataSet)aDatasets.get(i));
                if (lPartition.hasChildren()) {
                    System.out.println("Has children, continuing.");
                    lPartition.fixFData();
                    --i;
                } else if (lPartition.isAssigned()) {
                    System.out.println("Already assigned, continuing.");
                } else {
                    System.out.println("Trying to fit  " + lPartition.size + " " + lPartition.fixedSize + " into a " + ((DataSet)aDatasets.get(i)).getFixedAddress().freeSpace() + " sized space " + this.space.get(((DataSet)aDatasets.get(i)).getFixedAddress()) + " " + ((DataSet)aDatasets.get(i)).getFixedAddress().getName());
                    if ((double)(lPartition.size - lPartition.fixedSize) <= (Double)this.space.get(((DataSet)aDatasets.get(i)).getFixedAddress())) {
                        try {
                            System.out.println(lPartition.toString() + " is trying to assign to fixed dc " + ((DataSet)aDatasets.get(i)).getFixedAddress().getName());
                            lPartition.assign(((DataSet)aDatasets.get(i)).getFixedAddress());
                            this.space.put(((DataSet)aDatasets.get(i)).getFixedAddress(), (Double)this.space.get(((DataSet)aDatasets.get(i)).getFixedAddress()) - (double)(lPartition.size - lPartition.fixedSize));
                        } catch (Exception var10) {
                            --i;
                        }
                    } else {
                        System.out.println("Splitting a partition.");
                        var7 = ((DataSet)aDatasets.get(i)).getFixedAddress().getDatasets().iterator();

                        while(var7.hasNext()) {
                            s = (DataSet)var7.next();
                            PartitionTreeNode current = this.fTree.getPartition(s);
                            if (!current.isAssigned()) {
                                if (current.hasChildren()) {
                                    current.fixFData();
                                } else if (current.getDataSets().size() > 1 && current.size > lPartition.size) {
                                    lPartition = current;
                                }
                            }
                        }

                        System.out.println(lPartition.toString() + " should be splitting");
                        lPartition.split();
                        --i;
                    }
                }
            }
        }

        Iterator var13 = this.fTree.fLeaves.values().iterator();

        do {
            if (!var13.hasNext()) {
                return;
            }

            lPartition = (PartitionTreeNode)var13.next();
        } while(lPartition.isAssigned() || lPartition.myHome.size() <= 0);

        if (lPartition.hasChildren()) {
            throw new DistributionException("Fixed data allocation error! " + lPartition.toString() + " " + lPartition.size + " this was caused by a leaf/child error...");
        } else {
            System.out.println("Fixed data allocation error! ");
            System.out.println(((DataCenter)lPartition.myHome.get(0)).getName() + " " + ((DataCenter)lPartition.myHome.get(0)).freeSpace());
            var7 = ((DataCenter)lPartition.myHome.get(0)).getDatasets().iterator();

            while(var7.hasNext()) {
                s = (DataSet)var7.next();
                System.out.println("\t" + s.getName());
            }

            System.out.println(lPartition.toString() + " should contain ");
            var7 = lPartition.getDataSets().iterator();

            while(var7.hasNext()) {
                s = (DataSet)var7.next();
                System.out.println("\t" + s.getName());
            }

            throw new DistributionException("Fixed data allocation error! " + lPartition.toString() + " " + lPartition.size);
        }
    }

    public void doTheRealWork(ArrayList<DataSet> aDatasets) throws DistributionException {
        Iterator var2 = this.fTree.fLeaves.values().iterator();

        while(var2.hasNext()) {
            PartitionTreeNode p = (PartitionTreeNode)var2.next();
            if (!p.isAssigned()) {
                System.out.println(p.toString() + " hasn't been assigned... Nearest");
            }
        }

        PartitionTreeNode lGreatestDep = this.findTheGreatestDep(aDatasets);
        if (lGreatestDep == null) {
            Iterator var9 = this.fTree.fLeaves.values().iterator();

            while(var9.hasNext()) {
                PartitionTreeNode p = (PartitionTreeNode)var9.next();
                if (!p.isAssigned()) {
                    lGreatestDep = p;
                }
            }
        }

        if (lGreatestDep != null) {
            DataCenter greatestSpace = null;
            Iterator var11 = this.fDataCenters.iterator();

            while(true) {
                DataCenter d;
                do {
                    if (!var11.hasNext()) {
                        System.out.println("Greatest datacentre free space " + greatestSpace.getName() + " " + greatestSpace.freeSpace());
                        System.out.println("Greatest dep size " + lGreatestDep.toString() + " " + lGreatestDep.size + " " + lGreatestDep.isAssigned() + " " + lGreatestDep.myHome.size());
                        if ((double)lGreatestDep.size <= greatestSpace.freeSpace()) {
                            if (!lGreatestDep.fSibling.isAssigned()) {
                                this.mySiblingHasntBeenAssigned(greatestSpace, lGreatestDep);
                            } else if (lGreatestDep.fSibling.getMyDataCentre().freeSpace() >= (double)lGreatestDep.size) {
                                try {
                                    lGreatestDep.assign(greatestSpace);
                                } catch (Exception var7) {
                                    Logger.getLogger(TreeBuildTimeNearestSize.class.getName()).log(Level.SEVERE, (String)null, var7);
                                }
                            } else {
                                this.mySiblingHasBeenAssigned(greatestSpace, lGreatestDep);
                            }
                        } else {
                            if ((double)lGreatestDep.size > greatestSpace.freeSpace()) {
                                while((double)lGreatestDep.size > greatestSpace.freeSpace()) {
                                    if (lGreatestDep.getDataSets().size() == 1) {
                                        throw new DistributionException("Trying to partition a single dataset...");
                                    }

                                    lGreatestDep.split();
                                    if (lGreatestDep.lChild.getDataSets().size() > lGreatestDep.rChild.getDataSets().size()) {
                                        lGreatestDep = lGreatestDep.lChild;
                                    } else {
                                        lGreatestDep = lGreatestDep.rChild;
                                    }
                                }
                            }

                            try {
                                lGreatestDep.assign(greatestSpace);
                            } catch (Exception var6) {
                                Logger.getLogger(TreeBuildTimeNearestSize.class.getName()).log(Level.SEVERE, (String)null, var6);
                            }
                        }

                        return;
                    }

                    d = (DataCenter)var11.next();
                    System.out.println("DataCentre " + d.getName() + " " + d.freeSpace());
                } while(greatestSpace != null && !(greatestSpace.freeSpace() < d.freeSpace()));

                greatestSpace = d;
            }
        }
    }

    public PartitionTreeNode findTheGreatestDep(ArrayList<DataSet> aDatasets) {
        Matrix lMatrix = new Matrix();

        PartitionTreeNode lGreatestDep;
        for(Iterator var3 = this.fTree.fLeaves.values().iterator(); var3.hasNext(); lGreatestDep.dependancyValue = 0) {
            lGreatestDep = (PartitionTreeNode)var3.next();
        }

        int i;
        for(i = 0; i < aDatasets.size(); ++i) {
            lMatrix.addDataset((DataSet)aDatasets.get(i));
        }

        lMatrix = this.fCluster.cluster(lMatrix);

        for(i = 1; i < lMatrix.getDatasets().size(); ++i) {
            int thisPointScore = this.calculatePartitionPointScore(lMatrix, i);
            Integer newValue = this.fTree.getPartition((DataSet)aDatasets.get(i)).dependancyValue;
            newValue = newValue + thisPointScore;
            this.fTree.getPartition((DataSet)aDatasets.get(i)).dependancyValue = newValue;
        }

        TreeSet<PartitionTreeNode> sortedDeps = new TreeSet(new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((PartitionTreeNode)o1).dependancyValue - ((PartitionTreeNode)o2).dependancyValue;
            }
        });
        Iterator var11 = this.fTree.fLeaves.values().iterator();

        while(var11.hasNext()) {
            PartitionTreeNode p = (PartitionTreeNode)var11.next();
            sortedDeps.add(p);
        }

        lGreatestDep = (PartitionTreeNode)sortedDeps.pollLast();
        System.out.println("Trying as greatest deps: " + lGreatestDep.toString() + " haschildren: " + lGreatestDep.hasChildren() + " ass " + lGreatestDep.isAssigned());

        try {
            if (lGreatestDep.isAssigned()) {
                while(lGreatestDep.isAssigned()) {
                    System.out.println("Trying as greatest deps: " + lGreatestDep.toString() + " haschildren: " + lGreatestDep.hasChildren() + " ass " + lGreatestDep.isAssigned());
                    lGreatestDep = (PartitionTreeNode)sortedDeps.pollLast();
                }
            }

            return lGreatestDep;
        } catch (NullPointerException var6) {
            return null;
        } catch (NoSuchElementException var7) {
            return null;
        }
    }

    public void mySiblingHasntBeenAssigned(DataCenter greatestSpace, PartitionTreeNode lGreatestDep) {
        ArrayList<DataCenter> emptyDataCentres = new ArrayList();
        Iterator var4 = this.fDataCenters.iterator();

        DataCenter dc;
        while(var4.hasNext()) {
            dc = (DataCenter)var4.next();
            if (dc.getDatasets().size() == 0) {
                emptyDataCentres.add(dc);
            }
        }

        if (emptyDataCentres.size() != 0) {
            greatestSpace = (DataCenter)emptyDataCentres.get(0);
            var4 = emptyDataCentres.iterator();

            while(var4.hasNext()) {
                dc = (DataCenter)var4.next();
                if (dc.freeSpace() > greatestSpace.freeSpace()) {
                    greatestSpace = dc;
                }
            }

            if (greatestSpace.freeSpace() >= (double)lGreatestDep.size) {
                try {
                    lGreatestDep.assign(greatestSpace);
                } catch (Exception var10) {
                    Logger.getLogger(TreeBuildTimeNearestSize.class.getName()).log(Level.SEVERE, (String)null, var10);
                }
            }
        } else {
            System.out.println("Finding nearest sizewise");
            TreeMap<Double, HashSet<DataCenter>> whereToTry = new TreeMap();
            Iterator var6 = this.fDataCenters.iterator();

            while(var6.hasNext()) {
                DataCenter ds = (DataCenter)var6.next();
                if (ds.freeSpace() >= (double)lGreatestDep.size) {
                    HashSet tmp;
                    if (whereToTry.get(ds.freeSpace() - (double)lGreatestDep.size) == null) {
                        tmp = new HashSet();
                    } else {
                        tmp = (HashSet)whereToTry.get(ds.freeSpace() - (double)lGreatestDep.size);
                    }

                    tmp.add(ds);
                    whereToTry.put(ds.freeSpace() - (double)lGreatestDep.size, tmp);
                }
            }

            var6 = whereToTry.keySet().iterator();

            while(var6.hasNext()) {
                Double i = (Double)var6.next();

                try {
                    Iterator var8 = ((HashSet)whereToTry.get(i)).iterator();

                    while(var8.hasNext()) {
                        DataCenter d = (DataCenter)var8.next();
                        lGreatestDep.assign(d);
                    }
                } catch (Exception var11) {
                }

                if (lGreatestDep.isAssigned()) {
                    break;
                }
            }
        }

    }

    public void mySiblingHasBeenAssigned(DataCenter greatestSpace, PartitionTreeNode lGreatestDep) {
        ArrayList<DataCenter> emptyDataCentres = new ArrayList();
        Iterator var4 = this.fDataCenters.iterator();

        DataCenter dc;
        while(var4.hasNext()) {
            dc = (DataCenter)var4.next();
            if (dc.getDatasets().size() == 0) {
                emptyDataCentres.add(dc);
            }
        }

        if (emptyDataCentres.size() != 0) {
            greatestSpace = (DataCenter)emptyDataCentres.get(0);
            var4 = emptyDataCentres.iterator();

            while(var4.hasNext()) {
                dc = (DataCenter)var4.next();
                if (dc.freeSpace() > greatestSpace.freeSpace()) {
                    greatestSpace = dc;
                }
            }

            System.out.println("Trying to fit " + lGreatestDep.size + " into " + greatestSpace.freeSpace());
            if (greatestSpace.freeSpace() >= (double)lGreatestDep.size) {
                try {
                    lGreatestDep.assign(greatestSpace);
                } catch (Exception var12) {
                    Logger.getLogger(TreeBuildTimeNearestSize.class.getName()).log(Level.SEVERE, (String)null, var12);
                }
            }
        } else {
            System.out.println("Finding nearest sizewise");
            TreeMap<Double, HashSet<DataCenter>> whereToTry = new TreeMap();
            Iterator var6 = this.fDataCenters.iterator();

            while(var6.hasNext()) {
                DataCenter ds = (DataCenter)var6.next();
                if (ds.freeSpace() >= (double)lGreatestDep.size) {
                    HashSet tmp;
                    if (whereToTry.get(ds.freeSpace() - (double)lGreatestDep.size) == null) {
                        tmp = new HashSet();
                    } else {
                        tmp = (HashSet)whereToTry.get(ds.freeSpace() - (double)lGreatestDep.size);
                    }

                    tmp.add(ds);
                    whereToTry.put(ds.freeSpace() - (double)lGreatestDep.size, tmp);
                }
            }

            var6 = whereToTry.keySet().iterator();

            while(var6.hasNext()) {
                Double i = (Double)var6.next();
                Iterator var8 = ((HashSet)whereToTry.get(i)).iterator();

                while(var8.hasNext()) {
                    DataCenter d = (DataCenter)var8.next();

                    try {
                        lGreatestDep.assign(d);
                    } catch (Exception var11) {
                    }

                    if (lGreatestDep.isAssigned()) {
                        break;
                    }
                }

                if (lGreatestDep.isAssigned()) {
                    break;
                }
            }
        }

    }

    private void resetDataCenters() {
        for(int i = 0; i < this.fDataCenters.size(); ++i) {
            ((DataCenter)this.fDataCenters.get(i)).resetDataCenter();
        }

    }

    public Matrix getDependancyMatrix() {
        return null;
    }

    public Matrix getDependancyMatrixClustered() {
        return null;
    }

    public ArrayList<DataCenter> distributeFuzzy(Matrix fuzzyMatrix) {
        return null;
    }
}
