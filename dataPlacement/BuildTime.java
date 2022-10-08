//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import fuzzy.Fuzzy;
import java.util.ArrayList;
import java.util.Iterator;
import workflow.Workflow;

public class BuildTime extends BuildTimeAlgorithm {
    private Matrix fDependancyMatrix;
    private Matrix fClusteredMatrix;
    private Clusterer fClusterer = new BEA();
    private Fuzzy fcm;

    public BuildTime(ArrayList<DataCenter> aDatacenters) {
        super(aDatacenters);
    }

    public ArrayList<DataCenter> getDatacenters() {
        return this.fDataCenters;
    }

    public void setDatacenters(ArrayList<DataCenter> aDatacenters) {
        this.fDataCenters = aDatacenters;
    }

    public Matrix getDependancyMatrix() {
        return this.fDependancyMatrix;
    }

    public Matrix getDependancyMatrixClustered() {
        return this.fClusteredMatrix;
    }

    public void setDependancyMatrix(Matrix aDependancyMatrix) {
        this.fDependancyMatrix = aDependancyMatrix;
    }

    public ArrayList<DataCenter> distributeFuzzy(Matrix fuzzyMatrix) {
        ArrayList<DataCenter> result = new ArrayList();
        return result;
    }

    public ArrayList<DataCenter> distribute(Workflow aWorkflow) throws DistributionException {
        System.out.println("-->Distribution_from BuildTime Class");
        this.fDependancyMatrix = new Matrix(aWorkflow);

        try {
            return this.distribute();
        } catch (Exception var3) {
            throw new DistributionException(var3.getMessage());
        }
    }

    public ArrayList<DataCenter> distribute(ArrayList<DataSet> aDatasets) throws DistributionException {
        this.fDependancyMatrix = new Matrix();

        for(int i = 0; i < aDatasets.size(); ++i) {
            this.fDependancyMatrix.addDataset((DataSet)aDatasets.get(i));
        }

        try {
            return this.distribute();
        } catch (Exception var3) {
            throw new DistributionException(var3.getMessage());
        }
    }

    private ArrayList<DataCenter> distribute() throws Exception {
        this.fClusteredMatrix = this.fClusterer.cluster(this.fDependancyMatrix);

        for(int i = 0; i < this.fDataCenters.size(); ++i) {
            ((DataCenter)this.fDataCenters.get(i)).getDatasets().clear();
        }

        this.partitionAndAssign(this.fClusteredMatrix, new ArrayList(this.fDataCenters));
        ArrayList<DataCenter> result = new ArrayList();

        for(int i = 0; i < this.fDataCenters.size(); ++i) {
            if (((DataCenter)this.fDataCenters.get(i)).getDatasets().size() > 0) {
                result.add((DataCenter)this.fDataCenters.get(i));
            }
        }

        return result;
    }

    private void partitionAndAssignFuzzy(Matrix aMatrix, ArrayList<DataCenter> aDatacenters) throws Exception {
        if (aMatrix.getDatasets().size() == 0) {
            throw new Exception("Can't repartition an empty matrix!");
        } else {
            this.fcm = new Fuzzy();
            int[][] mats = aMatrix.getData();
            double[][] mat = new double[mats.length][mats.length];

            for(int i = 0; i < mats.length; ++i) {
                for(int j = 0; j < mats.length; ++j) {
                    mat[i][j] = (double)mats[i][j];
                }
            }

            this.fcm.setMatrix(mat);
        }
    }

    private void partitionAndAssign(Matrix aMatrix, ArrayList<DataCenter> aDatacenters) throws Exception {
        if (aMatrix.getDatasets().size() == 0) {
            throw new Exception("Can't repartition an empty matrix!");
        } else {
            int bestPoint = 0;
            int bestPointScore = 0;

            for(int i = 1; i < aMatrix.getDatasets().size(); ++i) {
                int thisPointScore = this.calculatePartitionPointScore(aMatrix, i);
                if (thisPointScore > bestPointScore) {
                    bestPointScore = thisPointScore;
                    bestPoint = i;
                }
            }

            System.out.println("-->best point= " + bestPoint);
            this.assignDatasets(aMatrix, aDatacenters, 0, bestPoint);
            this.assignDatasets(aMatrix, aDatacenters, bestPoint + 1, aMatrix.getDatasets().size() - 1);
        }
    }

    private void assignDatasets(Matrix aMatrix, ArrayList<DataCenter> aDatacenters, int startPoint, int endPoint) throws Exception {
        double lDataSize = 0.0;

        for(int i = startPoint; i <= endPoint; ++i) {
            lDataSize += ((DataSet)aMatrix.getDatasets().get(i)).getSize();
        }

        DataCenter lBestCenter = this.getBestDataCenter(lDataSize, new ArrayList(this.fDataCenters));
        if (lBestCenter == null) {
            if (aMatrix.getDatasets().size() == 1) {
                Iterator var11 = aDatacenters.iterator();

                while(var11.hasNext()) {
                    DataCenter d = (DataCenter)var11.next();
                    System.err.println(d.getName() + d.freeSpace());
                }

                System.err.println(aDatacenters.size() + " " + this.fDataCenters.size());
                throw new DistributionException("Can't assign dataset " + aMatrix.getDatasets().get(0) + " " + lDataSize + " to any datacenter");
            }

            Matrix lMatrix = new Matrix();

            for(int i = startPoint; i <= endPoint; ++i) {
                lMatrix.addDataset((DataSet)aMatrix.getDatasets().get(i));
            }

            this.partitionAndAssign(lMatrix, aDatacenters);
        } else {
            for(int i = startPoint; i <= endPoint; ++i) {
                lBestCenter.addDataset((DataSet)aMatrix.getDatasets().get(i));
                ((DataSet)aMatrix.getDatasets().get(i)).setDC(lBestCenter);
            }

            aDatacenters.remove(lBestCenter);
        }

    }

    private void assignLeftDatasets(Matrix aMatrix, ArrayList<DataCenter> aDatacenters, int partitionPoint) throws Exception {
        double leftDataSize = 0.0;

        for(int i = 0; i <= partitionPoint; ++i) {
            leftDataSize += ((DataSet)aMatrix.getDatasets().get(i)).getSize();
        }

        DataCenter lLeftDatacenter = this.getBestDataCenter(leftDataSize, new ArrayList(this.fDataCenters));
        if (lLeftDatacenter == null) {
            if (aMatrix.getDatasets().size() == 1) {
                throw new Exception("Can't assign dataset " + aMatrix.getDatasets().get(0) + " to any datacenter!");
            }

            Matrix lLeftMatrix = new Matrix();

            for(int i = 0; i <= partitionPoint; ++i) {
                lLeftMatrix.addDataset((DataSet)aMatrix.getDatasets().get(i));
            }

            this.partitionAndAssign(lLeftMatrix, aDatacenters);
        } else {
            for(int i = 0; i <= partitionPoint; ++i) {
                try {
                    lLeftDatacenter.addDataset((DataSet)aMatrix.getDatasets().get(i));
                    ((DataSet)aMatrix.getDatasets().get(i)).setDC(lLeftDatacenter);
                } catch (Exception var9) {
                    System.out.println("ERROR: " + var9.getMessage());
                }
            }

            aDatacenters.remove(lLeftDatacenter);
        }

    }

    private void assignRightDatasets(Matrix aMatrix, ArrayList<DataCenter> aDatacenters, int partitionPoint) throws Exception {
        double rightDataSize = 0.0;

        for(int i = partitionPoint + 1; i < aMatrix.getDatasets().size(); ++i) {
            rightDataSize += ((DataSet)aMatrix.getDatasets().get(i)).getSize();
        }

        DataCenter lRightDatacenter = this.getBestDataCenter(rightDataSize, new ArrayList(this.fDataCenters));
        if (lRightDatacenter == null) {
            if (aMatrix.getDatasets().size() == 1) {
                throw new Exception("Can't assign dataset " + aMatrix.getDatasets().get(0) + " to any datacenter!");
            }

            Matrix lRightMatrix = new Matrix();

            for(int i = partitionPoint + 1; i < aMatrix.getDatasets().size(); ++i) {
                lRightMatrix.addDataset((DataSet)aMatrix.getDatasets().get(i));
            }

            this.partitionAndAssign(lRightMatrix, aDatacenters);
        } else {
            for(int i = partitionPoint + 1; i < aMatrix.getDatasets().size(); ++i) {
                try {
                    lRightDatacenter.addDataset((DataSet)aMatrix.getDatasets().get(i));
                    ((DataSet)aMatrix.getDatasets().get(i)).setDC(lRightDatacenter);
                } catch (Exception var9) {
                    System.out.println("ERROR: " + var9.getMessage());
                }
            }

            aDatacenters.remove(lRightDatacenter);
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

    private DataCenter getBestDataCenter(double aDatasize, ArrayList<DataCenter> aDatacenters) {
        DataCenter result = null;

        for(int i = 0; i < aDatacenters.size(); ++i) {
            double thisCentersPotential = ((DataCenter)aDatacenters.get(i)).freeSpace();
            if (!(thisCentersPotential < aDatasize)) {
                if (result == null) {
                    result = (DataCenter)aDatacenters.get(i);
                } else if (thisCentersPotential < result.getP_ini() * result.getSize()) {
                    result = (DataCenter)aDatacenters.get(i);
                }
            }
        }

        if (result != null) {
            System.out.println("Free space " + result.freeSpace() + " at DC�=" + result + "\n");
        }

        return result;
    }
}
