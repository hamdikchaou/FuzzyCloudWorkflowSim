//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package fuzzy;

import fuzzy_param.Fuzzy_function;
import fuzzy_param.Fuzzy_parametre;
import java.util.LinkedList;
import java.util.List;
import outil.Center;
import outil.Point;

public class Fuzzy {
    Fuzzy_data fuzzy_data = new Fuzzy_data(Fuzzy_parametre.getFuzziness(), Fuzzy_parametre.getNum_cluster(), Fuzzy_parametre.getEssaie_number(), Fuzzy_parametre.getValmin_x(), Fuzzy_parametre.getValmax_x(), Fuzzy_parametre.getValmin_y(), Fuzzy_parametre.getValmax_y());
    private double[][] matrix = new double[Fuzzy_parametre.getEssaie_number()][Fuzzy_parametre.getNum_cluster()];

    public Fuzzy_data getFuzzy_data() {
        return this.fuzzy_data;
    }

    public void setFuzzy_data(Fuzzy_data fuzzy_data) {
        this.fuzzy_data = fuzzy_data;
    }

    public double[][] getMatrix() {
        return this.matrix;
    }

    public void setMatrix(double[][] matrix) {
        this.matrix = matrix;
    }

    public Fuzzy() {
    }

    public void init() {
        for(int i = 0; i < Fuzzy_parametre.getEssaie_number(); ++i) {
            for(int j = 0; j < Fuzzy_parametre.getNum_cluster(); ++j) {
                this.matrix[i][j] = this.euclidien_distance(this.fuzzy_data.getData_point(i).getX(), this.fuzzy_data.getData_point(i).getY(), this.fuzzy_data.getCluuster_point(j).getX(), this.fuzzy_data.getCluuster_point(j).getY());
            }
        }

    }

    public double euclidien_distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public void membership_matrix() {
        for(int i = 0; i < Fuzzy_parametre.getEssaie_number(); ++i) {
            double result = 0.0;

            int j;
            for(j = 0; j < Fuzzy_parametre.getNum_cluster(); ++j) {
                result += Math.pow(1.0 / this.matrix[i][j], 1.0 / (this.fuzzy_data.getFuzziness() - 1.0));
            }

            for(j = 0; j < Fuzzy_parametre.getNum_cluster(); ++j) {
                this.matrix[i][j] = Fuzzy_function.floorconvert(Math.pow(1.0 / this.matrix[i][j], 1.0 / (this.fuzzy_data.getFuzziness() - 1.0)) / result);
            }
        }

    }

    public List<Center> new_cendroid() {
        List<Center> center_point = new LinkedList();

        for(int j = 0; j < Fuzzy_parametre.getNum_cluster(); ++j) {
            double result = 0.0;
            double x = 0.0;
            double y = 0.0;

            int i;
            for(i = 0; i < Fuzzy_parametre.getEssaie_number(); ++i) {
                result += Math.pow(this.matrix[i][j], this.fuzzy_data.getFuzziness());
            }

            for(i = 0; i < Fuzzy_parametre.getEssaie_number(); ++i) {
                y += Math.pow(this.matrix[i][j], this.fuzzy_data.getFuzziness()) * this.fuzzy_data.getData_point(i).getY();
                x += Math.pow(this.matrix[i][j], this.fuzzy_data.getFuzziness()) * this.fuzzy_data.getData_point(i).getX();
            }

            center_point.add(new Center(x / result, y / result));
            System.out.println(j + "     X=" + x + "   " + result + "    " + x / result + "    Y=" + y / result);
        }

        return center_point;
    }

    public void cendroid_converge() {
        boolean cond = true;
        int ind = 1;
        System.out.println("---------------------------------------------------------- ");

        while(cond) {
            this.init();
            this.membership_matrix();
            List<Center> center = this.new_cendroid();
            System.out.println("-----------New centroid---N� " + ind);
            ++ind;

            for(int j = 0; j < Fuzzy_parametre.getNum_cluster(); ++j) {
                Double x1 = ((Center)center.get(j)).getX();
                Double y1 = ((Center)center.get(j)).getY();
                if (x1.isNaN() || y1.isNaN()) {
                    return;
                }

                if (Math.abs(((Center)center.get(j)).getX() - this.fuzzy_data.getCluuster_point(j).getX()) < Fuzzy_parametre.getEpsilon() && Math.abs(((Center)center.get(j)).getY() - this.fuzzy_data.getCluuster_point(j).getY()) < Fuzzy_parametre.getEpsilon()) {
                    cond = false;
                } else {
                    cond = true;
                }
            }

            this.fuzzy_data.setCluster_point(center);
        }

    }

    public List<Point>[] cluster() {
        List<Point>[] cluster = new List[Fuzzy_parametre.getNum_cluster()];

        int i;
        for(i = 0; i < Fuzzy_parametre.getNum_cluster(); ++i) {
            cluster[i] = new LinkedList();
        }

        for(i = 0; i < Fuzzy_parametre.getEssaie_number(); ++i) {
            int max = 0;
            double value = 0.0;

            for(int j = 0; j < Fuzzy_parametre.getNum_cluster(); ++j) {
                if (this.matrix[i][j] > value) {
                    max = j;
                    value = this.matrix[i][j];
                }
            }

            cluster[max].add(this.fuzzy_data.getData_point(i));
        }

        return cluster;
    }
}
