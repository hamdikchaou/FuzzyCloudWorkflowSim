//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package IT2FCMPSO;

import net.sourceforge.jswarm_pso.Neighborhood;
import net.sourceforge.jswarm_pso.Neighborhood1D;
import net.sourceforge.jswarm_pso.Swarm;
import net.sourceforge.jswarm_pso.example_1.MyFitnessFunction;
import net.sourceforge.jswarm_pso.example_1.MyParticle;
import net.sourceforge.jswarm_pso.example_2.SwarmShow2D;

public class Example {
    public Example() {
    }

    public static void main(String[] args) {
        System.out.println("Begin: Example 1\n");
        Swarm swarm = new Swarm(Swarm.DEFAULT_NUMBER_OF_PARTICLES, new MyParticle(), new MyFitnessFunction());
        Neighborhood neigh = new Neighborhood1D(Swarm.DEFAULT_NUMBER_OF_PARTICLES / 5, true);
        swarm.setNeighborhood(neigh);
        swarm.setNeighborhoodIncrement(0.9);
        swarm.setInertia(0.95);
        swarm.setMaxPosition(1.0);
        swarm.setMinPosition(0.0);
        swarm.setMaxMinVelocity(0.1);
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

        System.out.println(swarm.toStringStats());
        System.out.println("End: Example 1");
    }
}
