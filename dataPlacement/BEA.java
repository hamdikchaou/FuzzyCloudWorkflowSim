//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import java.util.Iterator;
import java.util.LinkedList;

public class BEA extends Clusterer {
    public BEA() {
    }

    public Matrix cluster(Matrix aMatrix) {
        if (aMatrix.getData().length <= 2) {
            return aMatrix;
        } else {
            LinkedList<DataSet> lNewMatrixOrder = new LinkedList();
            lNewMatrixOrder.add((DataSet)aMatrix.getDatasets().get(0));
            lNewMatrixOrder.add((DataSet)aMatrix.getDatasets().get(1));

            for(int i = 2; i < aMatrix.getDatasets().size(); ++i) {
                int bestIndex = this.findBestNewIndex(lNewMatrixOrder, i, aMatrix);
                System.out.println("New Matrix" + lNewMatrixOrder.toString());
                System.out.println("bestIndex=== " + bestIndex);
                lNewMatrixOrder.add(bestIndex, (DataSet)aMatrix.getDatasets().get(i));
            }

            Matrix result = new Matrix();
            Iterator<DataSet> lIterator = lNewMatrixOrder.iterator();

            while(lIterator.hasNext()) {
                result.addDataset((DataSet)lIterator.next());
            }

            return result;
        }
    }

    private int findBestNewIndex(LinkedList<DataSet> lNewMatrixOrder, int i, Matrix aMatrix) {
        int bestIndex = 0;
        int bestCont = 0;
        int thisCont = this.boundaryContribution((DataSet)lNewMatrixOrder.get(0), (DataSet)aMatrix.getDatasets().get(i), aMatrix);
        if (thisCont > bestCont) {
            bestIndex = 0;
            bestCont = thisCont;
        }

        thisCont = this.boundaryContribution((DataSet)lNewMatrixOrder.get(i - 1), (DataSet)aMatrix.getDatasets().get(i), aMatrix);
        if (thisCont > bestCont) {
            bestIndex = i;
            bestCont = thisCont;
        }

        for(int j = 1; j < i; ++j) {
            thisCont = this.contribution((DataSet)lNewMatrixOrder.get(j - 1), (DataSet)aMatrix.getDatasets().get(i), (DataSet)lNewMatrixOrder.get(j), aMatrix);
            if (thisCont > bestCont) {
                bestIndex = j;
                bestCont = thisCont;
            }
        }

        return bestIndex;
    }

    private int contribution(DataSet aLeft, DataSet aNew, DataSet aRight, Matrix aMatrix) {
        int result = 2 * this.bond(aLeft, aNew, aMatrix);
        result += 2 * this.bond(aNew, aRight, aMatrix);
        result -= 2 * this.bond(aLeft, aRight, aMatrix);
        return result;
    }

    private int boundaryContribution(DataSet aBoundary, DataSet aNew, Matrix aMatrix) {
        return 2 * this.bond(aBoundary, aNew, aMatrix);
    }

    private int bond(DataSet d1, DataSet d2, Matrix aMatrix) {
        int result = 0;
        int d1Index = aMatrix.getDatasets().indexOf(d1);
        int d2Index = aMatrix.getDatasets().indexOf(d2);

        for(int i = 0; i < aMatrix.getData().length; ++i) {
            result += aMatrix.getData()[i][d1Index] * aMatrix.getData()[i][d2Index];
        }

        return result;
    }
}
