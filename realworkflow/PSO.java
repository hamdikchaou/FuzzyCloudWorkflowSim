//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package realworkflow;

import java.util.Random;

public class PSO {
    public PSO() {
    }

    public static double fitness(double[] x) {
        double retValue = 0.0;

        for(int i = 0; i < x.length; ++i) {
            retValue += Math.pow(x[i], 2.0);
        }

        return retValue;
    }

    public static void main(String[] args) {
        double w = 0.9;
        double c1 = 2.05;
        double c2 = 2.05;
        double r1 = 0.0;
        double r2 = 0.0;
        double xMin = -5.12;
        double xMax = 5.12;
        double vMin = 0.0;
        double vMax = 1.0;
        double wMin = 0.4;
        double wMax = 0.9;
        double phi = c1 + c2;
        double chi = 2.0 / Math.abs(2.0 - phi - Math.sqrt(Math.pow(phi, 2.0) - 4.0 * phi));
        double nInfinite = Double.NEGATIVE_INFINITY;
        double gBestValue = nInfinite;
        int Np = 100;
        int Nd = 2;
        int Nt = 1000;
        double[] pBestValue = new double[Np];
        double[] gBestPosition = new double[Nd];
        double[] bestFitnessHistory = new double[Nt];
        double[] M = new double[Np];
        double[][] pBestPosition = new double[Np][Nd];
        double[][] R = new double[Np][Nd];
        double[][] V = new double[Np][Nd];
        Random rand = new Random();

        int j;
        for(j = 0; j < Np; ++j) {
            pBestValue[j] = nInfinite;
        }

        int p;
        for(j = 0; j < Np; ++j) {
            for(p = 0; p < Nd; ++p) {
                R[j][p] = xMin + (xMax - xMin) * rand.nextDouble();
                V[j][p] = vMin + (vMax - vMin) * rand.nextDouble();
                if (rand.nextDouble() < 0.5) {
                    V[j][p] = -V[j][p];
                    R[j][p] = -R[j][p];
                }
            }
        }

        for(j = 0; j < Np; ++j) {
            M[j] = fitness(R[j]);
            M[j] = -M[j];
        }

        for(j = 0; j < Nt; ++j) {
            int i;
            for(p = 0; p < Np; ++p) {
                for(i = 0; i < Nd; ++i) {
                    R[p][i] += V[p][i];
                    if (R[p][i] > xMax) {
                        R[p][i] = xMax;
                    } else if (R[p][i] < xMin) {
                        R[p][i] = xMin;
                    }
                }
            }

            for(p = 0; p < Np; ++p) {
                M[p] = fitness(R[p]);
                M[p] = -M[p];
                if (M[p] > pBestValue[p]) {
                    pBestValue[p] = M[p];

                    for(i = 0; i < Nd; ++i) {
                        pBestPosition[p][i] = R[p][i];
                    }
                }

                if (M[p] > gBestValue) {
                    gBestValue = M[p];

                    for(i = 0; i < Nd; ++i) {
                        gBestPosition[i] = R[p][i];
                    }
                }
            }

            bestFitnessHistory[j] = gBestValue;
            w = wMax - (wMax - wMin) / (double)Nt * (double)j;

            for(p = 0; p < Np; ++p) {
                for(i = 0; i < Nd; ++i) {
                    r1 = rand.nextDouble();
                    r2 = rand.nextDouble();
                    V[p][i] = chi * w * (V[p][i] + r1 * c1 * (pBestPosition[p][i] - R[p][i]) + r2 * c2 * (gBestPosition[i] - R[p][i]));
                    if (V[p][i] > vMax) {
                        V[p][i] = vMax;
                    } else if (V[p][i] < vMin) {
                        V[p][i] = vMin;
                    }
                }
            }

            System.out.println("iteration: " + j + " BestValue " + gBestValue);
        }

    }
}
