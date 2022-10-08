//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dataPlacement;

import fuzzy_param.Fuzzy_function;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FcmPSOPartition {
    private static int essaie_number;
    private static int num_cluster;
    private static double fuzziness = 10.0;
    private static double after_dot = 3.0;
    private static double epsilon = 0.001;
    private double[][] matrix;
    private Matrix mat;
    private double dc_size;
    int compt = 0;
    private ArrayList<DataCenter> fUsedDatacenters;
    private ArrayList<DataCenter> fuzzyUsedDatacenters;
    private ArrayList<DataSet> alldataset;
    private List<FuzzyPoint> data_point;
    public List<FuzzyCenter> cluster_point;
    private List<FuzzyPoint>[] cluster;

    public FcmPSOPartition(ArrayList<DataCenter> fDatacenters, Matrix mat) throws Exception {
        this.fUsedDatacenters = fDatacenters;
        num_cluster = this.fUsedDatacenters.size();
        this.mat = mat;
        this.matrix = new double[essaie_number][num_cluster];
        essaie_number = mat.getData().length;
        this.dc_size = ((DataCenter)fDatacenters.get(0)).getSize() * ((DataCenter)fDatacenters.get(0)).getP_ini();
        this.Fuzzy();
        System.out.println("---------------Init_fuzzy_before_membership-------------");

        int i;
        for(i = 0; i < num_cluster; ++i) {
            System.out.println(((FuzzyCenter)this.cluster_point.get(i)).toString3());
        }

        System.out.println("");
        this.cluster = new List[num_cluster];

        for(i = 0; i < num_cluster; ++i) {
            this.cluster[i] = new LinkedList();
        }

        this.cendroid_converge();
        this.alldataset = new ArrayList();

        for(i = 0; i < this.fUsedDatacenters.size(); ++i) {
            DataCenter thisCenter = (DataCenter)this.fUsedDatacenters.get(i);

            for(int j = 0; j < thisCenter.getDatasets().size(); ++j) {
                this.alldataset.add((DataSet)thisCenter.getDatasets().get(j));
            }
        }

        if (this.compt != num_cluster) {
            System.out.println("--the first solution---");
            this.fuzzyUsedDatacenters = this.fUsedDatacenters;
        } else {
            System.out.println("i am here+++++++++++++++++++++++");
            this.fuzzyUsedDatacenters = this.fUsedDatacenters;

            for(i = 0; i < num_cluster; ++i) {
                ((DataCenter)this.fuzzyUsedDatacenters.get(i)).getDatasets().clear();
            }

            for(i = 0; i < essaie_number; ++i) {
                int max = 0;
                double value = 0.0;

                for(int j = 0; j < num_cluster; ++j) {
                    if (this.matrix[i][j] > value) {
                        max = j;
                        value = this.matrix[i][j];
                    }
                }

                this.cluster[max].add((FuzzyPoint)this.data_point.get(i));
                System.out.println("---/////max=" + max + " cluster[max]=" + ((FuzzyPoint)this.data_point.get(i)).toString3());
                ((DataCenter)this.fuzzyUsedDatacenters.get(max)).addDataset((DataSet)this.alldataset.get(i));
            }
        }

    }

    public ArrayList<DataCenter> getFuzzyDC() {
        return this.fuzzyUsedDatacenters;
    }

    private void Fuzzy() {
        this.matrix = new double[essaie_number][num_cluster];
        this.data_point = new LinkedList();
        this.cluster_point = new LinkedList();

        int start;
        double center;
        for(start = 0; start < essaie_number; ++start) {
            String name = ((DataSet)this.mat.getDatasets().get(start)).getName();
            Double size = ((DataSet)this.mat.getDatasets().get(start)).getSize();
            center = (double)this.mat.dep(this.mat, start, start);
            FuzzyPoint dataset = new FuzzyPoint(center, name, size);
            this.data_point.add(dataset);
        }

        start = 0;
        int end = 0;

        for(int i = 0; i < num_cluster; ++i) {
            center = 0.0;
            int n_dataset = ((DataCenter)this.fUsedDatacenters.get(i)).getDatasets().size();
            end += n_dataset;

            for(int j = start; j < end; ++j) {
                center += (double)this.mat.dep(this.mat, j, j);
            }

            start = end;
            center /= (double)n_dataset;
            String nameDC = ((DataCenter)this.fUsedDatacenters.get(i)).getName();
            Double sizeDC = this.dc_size - ((DataCenter)this.fUsedDatacenters.get(i)).freeSpace();
            FuzzyCenter data = new FuzzyCenter(center, nameDC, sizeDC);
            this.cluster_point.add(data);
        }

    }

    public void init() {
        for(int i = 0; i < essaie_number; ++i) {
            System.out.println();

            for(int j = 0; j < num_cluster; ++j) {
                this.matrix[i][j] = this.dependency_diff(((FuzzyPoint)this.data_point.get(i)).dep, ((FuzzyCenter)this.cluster_point.get(j)).dep);
                System.out.print("|" + this.matrix[i][j] + " ");
            }
        }

    }

    public double dependency_diff(double x1, double x2) {
        return Math.abs(x1 / x2);
    }

    public void membership_matrix() {
        for(int i = 0; i < essaie_number; ++i) {
            System.out.println();
            double result = 0.0;

            int j;
            for(j = 0; j < num_cluster; ++j) {
                result += Math.pow(1.0 / this.matrix[i][j], 2.0 / (fuzziness - 1.0));
            }

            for(j = 0; j < num_cluster; ++j) {
                this.matrix[i][j] = Fuzzy_function.floorconvert(Math.pow(1.0 / this.matrix[i][j], 2.0 / (fuzziness - 1.0)) / result);
                System.out.print("|" + this.matrix[i][j] + " ");
            }
        }

        System.out.println();
    }

    public List<FuzzyCenter> new_cendroid() {
        List<FuzzyCenter> center_point = new LinkedList();

        for(int j = 0; j < num_cluster; ++j) {
            double result = 0.0;
            double x = 0.0;

            int i;
            for(i = 0; i < essaie_number; ++i) {
                result += Math.pow(this.matrix[i][j], fuzziness);
            }

            for(i = 0; i < essaie_number; ++i) {
                x += Math.pow(this.matrix[i][j], fuzziness) * ((FuzzyPoint)this.data_point.get(i)).dep;
            }

            center_point.add(new FuzzyCenter(x / result, ((FuzzyCenter)this.cluster_point.get(j)).name, 0.0));
            System.out.println("Cluster(DC) " + j + "     X/R=" + x / result);
        }

        return center_point;
    }

    public void cendroid_converge() {
        boolean cond = true;
        int ind = 1;
        System.out.println("---------------------------------------------------------- ");
        this.init();

        while(cond) {
            System.out.println("FCM Matrix");
            System.out.println("\n");
            System.out.println("\nCalling Membership function");
            this.membership_matrix();
            System.out.println("-----------New centroid---N� " + ind);
            ++ind;
            List<FuzzyCenter> center = this.new_cendroid();
            center = this.cluster_space(center);

            int i;
            for(i = 0; i < num_cluster; ++i) {
                Double x1 = ((FuzzyCenter)center.get(i)).dep;
                if (x1.isNaN()) {
                    return;
                }

                if (((FuzzyCenter)center.get(i)).size <= this.dc_size) {
                    ++this.compt;
                    if (((FuzzyCenter)center.get(i)).dep - ((FuzzyCenter)this.cluster_point.get(i)).dep < epsilon) {
                        cond = false;
                        System.out.println(((FuzzyCenter)center.get(i)).name + " diff(<eps)=" + (((FuzzyCenter)center.get(i)).dep - ((FuzzyCenter)this.cluster_point.get(i)).dep));
                    } else {
                        System.out.println(((FuzzyCenter)center.get(i)).name + " diff(>=eps)=" + (((FuzzyCenter)center.get(i)).dep - ((FuzzyCenter)this.cluster_point.get(i)).dep));
                        cond = true;
                        if (ind == 25) {
                            cond = false;
                            this.cluster_point = center;
                        }
                    }
                } else {
                    System.out.println(((FuzzyCenter)center.get(i)).name + " size>dc_size");
                    cond = false;
                }
            }

            if (this.compt == num_cluster) {
                for(i = 0; i < essaie_number; ++i) {
                    int max = 0;
                    double value = 0.0;

                    for(int j = 0; j < num_cluster; ++j) {
                        if (this.matrix[i][j] > value) {
                            max = j;
                            value = this.matrix[i][j];
                        }
                    }

                    this.cluster[max].add((FuzzyPoint)this.data_point.get(i));
                }

                this.cluster_point = center;
            }
        }

    }

    public List<FuzzyCenter> cluster_space(List<FuzzyCenter> fc) {
        int i;
        for(i = 0; i < essaie_number; ++i) {
            int max = 0;
            double value = 0.0;

            for(int j = 0; j < num_cluster; ++j) {
                if (this.matrix[i][j] > value) {
                    max = j;
                    value = this.matrix[i][j];
                }
            }

            ((FuzzyCenter)fc.get(max)).setSize(((FuzzyCenter)fc.get(max)).size + ((FuzzyPoint)this.data_point.get(i)).size);
        }

        for(i = 0; i < num_cluster; ++i) {
            System.out.println(((FuzzyCenter)fc.get(i)).toString3());
        }

        return fc;
    }
}
