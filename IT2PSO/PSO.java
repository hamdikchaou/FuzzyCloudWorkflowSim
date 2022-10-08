//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package IT2PSO;

import dataPlacement.DataCenter;
import dataPlacement.DataSet;
import dataPlacement.Task;
import java.util.ArrayList;
import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import net.sourceforge.jswarm_pso.Swarm;
import net.sourceforge.jswarm_pso.example_2.SwarmShow2D;

public class PSO {
    private ArrayList<Task> tasks;
    private ArrayList<DataCenter> dc;
    Task t;
    DataSet dd;
    public double[] psoAnswer;

    public PSO(ArrayList<Task> readytask, ArrayList<DataCenter> fUsedDatacenters) {
        this.tasks = readytask;
        this.dc = fUsedDatacenters;
    }

    public void setting() {
        int minPos = 1;
        int MaxPos = this.dc.size();
        int maxParticle = this.tasks.size();
        MyParticle.NUMBER_OF_DIMENTIONS = maxParticle;
        Swarm.DEFAULT_NUMBER_OF_PARTICLES = 50;
        Swarm swarm = new Swarm(Swarm.DEFAULT_NUMBER_OF_PARTICLES, new MyParticle(), new MyFitnessFunction(this.tasks, this.dc));
        Neighborhood neigh = new Neighborhood1D(Swarm.DEFAULT_NUMBER_OF_PARTICLES / 5, true);
        swarm.setNeighborhood(neigh);
        swarm.setNeighborhoodIncrement(0.9);
        swarm.setInertia(0.9);
        swarm.setMaxPosition((double)MaxPos);
        swarm.setMinPosition((double)minPos);
        swarm.setMaxMinVelocity(0.5);
        int numberOfIterations = 100;
        boolean showGraphics = false;
        int i;
        if (showGraphics) {
            i = numberOfIterations / 100 + 1;
            SwarmShow2D ss2d = new SwarmShow2D(swarm, numberOfIterations, i, true);
            ss2d.run();
        } else {
            for(i = 0; i < numberOfIterations; ++i) {
                swarm.evolve();
            }
        }

        this.psoAnswer = swarm.getBestPosition();
        System.out.println(swarm.toString());
    }

    public double[] getpsoAnswer() {
        return this.psoAnswer;
    }
}
