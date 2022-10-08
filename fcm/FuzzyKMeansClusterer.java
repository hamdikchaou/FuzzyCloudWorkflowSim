//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package fcm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.util.MathUtils;

public class FuzzyKMeansClusterer<T extends Clusterable> extends Clusterer<T> {
    private static final double DEFAULT_EPSILON = 0.001;
    private final int k;
    private final int maxIterations;
    private final double fuzziness;
    private final double epsilon;
    private final RandomGenerator random;
    private double[][] membershipMatrix;
    private List<T> points;
    private List<CentroidCluster<T>> clusters;

    public FuzzyKMeansClusterer(final int k, final double fuzziness) throws NumberIsTooSmallException {
        this(k, fuzziness, -1, new EuclideanDistance());
    }

    public FuzzyKMeansClusterer(final int k, final double fuzziness, final int maxIterations, final DistanceMeasure measure) throws NumberIsTooSmallException {
        this(k, fuzziness, maxIterations, measure, 0.001, new JDKRandomGenerator());
    }

    public FuzzyKMeansClusterer(final int k, final double fuzziness, final int maxIterations, final DistanceMeasure measure, final double epsilon, final RandomGenerator random) throws NumberIsTooSmallException {
        super(measure);
        if (fuzziness <= 1.0) {
            throw new NumberIsTooSmallException(fuzziness, 1.0, false);
        } else {
            this.k = k;
            this.fuzziness = fuzziness;
            this.maxIterations = maxIterations;
            this.epsilon = epsilon;
            this.random = random;
            this.membershipMatrix = null;
            this.points = null;
            this.clusters = null;
        }
    }

    public int getK() {
        return this.k;
    }

    public double getFuzziness() {
        return this.fuzziness;
    }

    public int getMaxIterations() {
        return this.maxIterations;
    }

    public double getEpsilon() {
        return this.epsilon;
    }

    public RandomGenerator getRandomGenerator() {
        return this.random;
    }

    public RealMatrix getMembershipMatrix() {
        if (this.membershipMatrix == null) {
            throw new MathIllegalStateException();
        } else {
            return MatrixUtils.createRealMatrix(this.membershipMatrix);
        }
    }

    public List<T> getDataPoints() {
        return this.points;
    }

    public List<CentroidCluster<T>> getClusters() {
        return this.clusters;
    }

    public double getObjectiveFunctionValue() {
        if (this.points != null && this.clusters != null) {
            int i = 0;
            double objFunction = 0.0;

            for(Iterator var4 = this.points.iterator(); var4.hasNext(); ++i) {
                T point = (Clusterable)var4.next();
                int j = 0;

                for(Iterator var7 = this.clusters.iterator(); var7.hasNext(); ++j) {
                    CentroidCluster<T> cluster = (CentroidCluster)var7.next();
                    double dist = this.distance(point, cluster.getCenter());
                    objFunction += dist * dist * FastMath.pow(this.membershipMatrix[i][j], this.fuzziness);
                }
            }

            return objFunction;
        } else {
            throw new MathIllegalStateException();
        }
    }

    public List<CentroidCluster<T>> cluster(final Collection<T> dataPoints) throws MathIllegalArgumentException {
        MathUtils.checkNotNull(dataPoints);
        int size = dataPoints.size();
        if (size < this.k) {
            throw new NumberIsTooSmallException(size, this.k, false);
        } else {
            this.points = Collections.unmodifiableList(new ArrayList(dataPoints));
            this.clusters = new ArrayList();
            this.membershipMatrix = new double[size][this.k];
            double[][] oldMatrix = new double[size][this.k];
            if (size == 0) {
                return this.clusters;
            } else {
                this.initializeMembershipMatrix();
                int pointDimension = ((Clusterable)this.points.get(0)).getPoint().length;

                int iteration;
                for(iteration = 0; iteration < this.k; ++iteration) {
                    this.clusters.add(new CentroidCluster(new DoublePoint(new double[pointDimension])));
                }

                iteration = 0;
                int max = this.maxIterations < 0 ? Integer.MAX_VALUE : this.maxIterations;
                double difference = 0.0;

                do {
                    this.saveMembershipMatrix(oldMatrix);
                    this.updateClusterCenters();
                    this.updateMembershipMatrix();
                    difference = this.calculateMaxMembershipChange(oldMatrix);
                    if (!(difference > this.epsilon)) {
                        break;
                    }

                    ++iteration;
                } while(iteration < max);

                return this.clusters;
            }
        }
    }

    private void updateClusterCenters() {
        int j = 0;
        List<CentroidCluster<T>> newClusters = new ArrayList(this.k);

        for(Iterator var3 = this.clusters.iterator(); var3.hasNext(); ++j) {
            CentroidCluster<T> cluster = (CentroidCluster)var3.next();
            Clusterable center = cluster.getCenter();
            int i = 0;
            double[] arr = new double[center.getPoint().length];
            double sum = 0.0;

            for(Iterator var10 = this.points.iterator(); var10.hasNext(); ++i) {
                T point = (Clusterable)var10.next();
                double u = FastMath.pow(this.membershipMatrix[i][j], this.fuzziness);
                double[] pointArr = point.getPoint();

                for(int idx = 0; idx < arr.length; ++idx) {
                    arr[idx] += u * pointArr[idx];
                }

                sum += u;
            }

            MathArrays.scaleInPlace(1.0 / sum, arr);
            newClusters.add(new CentroidCluster(new DoublePoint(arr)));
        }

        this.clusters.clear();
        this.clusters = newClusters;
    }

    private void updateMembershipMatrix() {
        for(int i = 0; i < this.points.size(); ++i) {
            T point = (Clusterable)this.points.get(i);
            double maxMembership = Double.MIN_VALUE;
            int newCluster = -1;

            for(int j = 0; j < this.clusters.size(); ++j) {
                double sum = 0.0;
                double distA = FastMath.abs(this.distance(point, ((CentroidCluster)this.clusters.get(j)).getCenter()));
                double distB;
                if (distA != 0.0) {
                    for(Iterator var11 = this.clusters.iterator(); var11.hasNext(); sum += FastMath.pow(distA / distB, 2.0 / (this.fuzziness - 1.0))) {
                        CentroidCluster<T> c = (CentroidCluster)var11.next();
                        distB = FastMath.abs(this.distance(point, c.getCenter()));
                        if (distB == 0.0) {
                            sum = Double.POSITIVE_INFINITY;
                            break;
                        }
                    }
                }

                double membership;
                if (sum == 0.0) {
                    membership = 1.0;
                } else if (sum == Double.POSITIVE_INFINITY) {
                    membership = 0.0;
                } else {
                    membership = 1.0 / sum;
                }

                this.membershipMatrix[i][j] = membership;
                if (this.membershipMatrix[i][j] > maxMembership) {
                    maxMembership = this.membershipMatrix[i][j];
                    newCluster = j;
                }
            }

            ((CentroidCluster)this.clusters.get(newCluster)).addPoint(point);
        }

    }

    private void initializeMembershipMatrix() {
        for(int i = 0; i < this.points.size(); ++i) {
            for(int j = 0; j < this.k; ++j) {
                this.membershipMatrix[i][j] = this.random.nextDouble();
            }

            this.membershipMatrix[i] = MathArrays.normalizeArray(this.membershipMatrix[i], 1.0);
        }

    }

    private double calculateMaxMembershipChange(final double[][] matrix) {
        double maxMembership = 0.0;

        for(int i = 0; i < this.points.size(); ++i) {
            for(int j = 0; j < this.clusters.size(); ++j) {
                double v = FastMath.abs(this.membershipMatrix[i][j] - matrix[i][j]);
                maxMembership = FastMath.max(v, maxMembership);
            }
        }

        return maxMembership;
    }

    private void saveMembershipMatrix(final double[][] matrix) {
        for(int i = 0; i < this.points.size(); ++i) {
            System.arraycopy(this.membershipMatrix[i], 0, matrix[i], 0, this.clusters.size());
        }

    }
}
