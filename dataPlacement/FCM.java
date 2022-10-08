//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import fuzzy_param.Fuzzy_function;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import outil.Center;

public class FCM {
    private static int essaie_number = 1;
    private static int num_cluster;
    private static double fuzziness = 2.0;
    private static double epsilon = 0.009;
    private ArrayList<DataCenter> fUsedDatacenters;
    private DataSet aNewDataset;
    private double[][] matrix;

    public FCM(DataSet aNewDataset, ArrayList<DataCenter> fUsedDatacenters) {
        this.fUsedDatacenters = fUsedDatacenters;
        this.aNewDataset = aNewDataset;
        num_cluster = fUsedDatacenters.size();
        this.matrix = new double[essaie_number][num_cluster];
    }

    public void init() {
        for(int i = 0; i < essaie_number; ++i) {
            for(int j = 0; j < num_cluster; ++j) {
                this.matrix[i][j] = (double)this.calculateClustering(this.aNewDataset, (DataCenter)this.fUsedDatacenters.get(j));
            }
        }

    }

    protected int calculateClustering(DataSet aDataset, DataCenter aDatacenter) {
        int dependancy = 0;

        for(int i = 0; i < aDatacenter.getDatasets().size(); ++i) {
            dependancy += Matrix.calculateDependancyFuzzy(aDataset, (DataSet)aDatacenter.getDatasets().get(i));
        }

        return dependancy;
    }

    public void membership_matrix() {
        for(int i = 0; i < essaie_number; ++i) {
            double result = 0.0;

            int j;
            for(j = 0; j < num_cluster; ++j) {
                System.out.print(this.matrix[i][j] + "|");
                result += Math.pow(1.0 / this.matrix[i][j], 1.0 / (fuzziness - 1.0));
            }

            System.out.println();

            for(j = 0; j < num_cluster; ++j) {
                this.matrix[i][j] = Fuzzy_function.floorconvert(Math.pow(1.0 / this.matrix[i][j], 1.0 / (fuzziness - 1.0)) / result);
                System.out.print(this.matrix[i][j] + "|");
            }

            System.out.println();
        }

    }

    public void cendroid_converge() {
        boolean cond = true;

        while(cond) {
            this.init();
            this.membership_matrix();
            List<Center> center = this.new_cendroid();

            for(int j = 0; j < num_cluster; ++j) {
                Double x1 = ((Center)center.get(j)).getX();
                if (x1.isNaN()) {
                    return;
                }

                if (Math.abs(((Center)center.get(j)).getX() - (double)num_cluster) < epsilon) {
                    cond = false;
                } else {
                    cond = true;
                }
            }
        }

    }

    public List<Center> new_cendroid() {
        List<Center> center_point = new LinkedList();

        for(int j = 0; j < num_cluster; ++j) {
            double result = 0.0;
            double x = 0.0;

            int i;
            for(i = 0; i < essaie_number; ++i) {
                result += Math.pow(this.matrix[i][j], fuzziness);
            }

            for(i = 0; i < essaie_number; ++i) {
                x += Math.pow(this.matrix[i][j], fuzziness) * this.matrix[i][j];
            }

            center_point.add(new Center(x / result));
        }

        return center_point;
    }

    public DataCenter cluster(DataCenter aDataCenterSource) {
        DataCenter bestcenter = null;
        int[] clust = new int[num_cluster];

        for(int i = 0; i < num_cluster; ++i) {
            clust[i] = num_cluster;
        }

        double value = 0.0;

        int i;
        for(int i = 0; i < essaie_number; ++i) {
            int max = false;

            for(i = 0; i < num_cluster; ++i) {
                if (this.matrix[i][i] > value) {
                    value = this.matrix[i][i];
                }
            }
        }

        Double thisutilisation = aDataCenterSource.utilisation();
        Double bestutilisation = thisutilisation;

        for(i = 0; i < essaie_number; ++i) {
            for(int j = 0; j < num_cluster; ++j) {
                if (this.matrix[i][j] == value && ((DataCenter)this.fUsedDatacenters.get(j)).utilisation() < bestutilisation) {
                    bestutilisation = ((DataCenter)this.fUsedDatacenters.get(j)).utilisation();
                    bestcenter = (DataCenter)this.fUsedDatacenters.get(j);
                }
            }
        }

        if (bestutilisation == thisutilisation) {
            bestcenter = aDataCenterSource;
        }

        return bestcenter;
    }
}
