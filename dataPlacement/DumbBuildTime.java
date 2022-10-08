//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.ArrayList;
import java.util.Iterator;
import workflow.Workflow;

public class DumbBuildTime extends BuildTimeAlgorithm {
    boolean ENABLE_FIXED_DATASETS = true;

    public DumbBuildTime(ArrayList<DataCenter> aDatacenters) {
        super(aDatacenters);
    }

    public void setDependancyMatrix(Matrix aMatrix) {
    }

    public ArrayList<DataCenter> distribute(Workflow aWorkflow) throws DistributionException {
        return this.distribute(aWorkflow.getDatasets());
    }

    public ArrayList<DataCenter> distribute(ArrayList<DataSet> aDatasets) throws DistributionException {
        for(int i = 0; i < this.fDataCenters.size(); ++i) {
            ((DataCenter)this.fDataCenters.get(i)).getDatasets().clear();
        }

        if (this.ENABLE_FIXED_DATASETS) {
            Iterator var8 = aDatasets.iterator();

            while(var8.hasNext()) {
                DataSet ds = (DataSet)var8.next();

                try {
                    if (ds.getFixedAddress() != null) {
                        ds.getFixedAddress().addDataset(ds);
                        ds.setDC(ds.getFixedAddress());
                    }
                } catch (Exception var7) {
                    throw new DistributionException("No space for fixed data!");
                }
            }
        }

        ArrayList<DataCenter> result = new ArrayList();

        for(int i = 0; i < aDatasets.size(); ++i) {
            if (this.ENABLE_FIXED_DATASETS && ((DataSet)aDatasets.get(i)).getFixedAddress() != null) {
                if (!result.contains(((DataSet)aDatasets.get(i)).getFixedAddress())) {
                    result.add(((DataSet)aDatasets.get(i)).getFixedAddress());
                }
            } else {
                DataCenter lTargetCenter = (DataCenter)this.fDataCenters.get(this.fRandom.nextInt(this.fDataCenters.size()));

                try {
                    lTargetCenter.addDataset((DataSet)aDatasets.get(i));
                    ((DataSet)aDatasets.get(i)).setDC(lTargetCenter);
                    if (!result.contains(lTargetCenter)) {
                        result.add(lTargetCenter);
                    }
                } catch (Exception var6) {
                    --i;
                }
            }
        }

        return result;
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
