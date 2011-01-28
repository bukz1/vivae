package vivae.example.ceamath;

import vivae.arena.Arena;
import vivae.arena.parts.Active;
import vivae.fitness.AverageSpeed;
import vivae.fitness.FitnessFunction;
import vivae.util.FrictionBuffer;
import vivae.util.Util;

import javax.swing.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: bukz1
 * Date: 1/27/11
 * Time: 12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class JLinkCEAExperiment {

    private Arena arena = null;
    private JFrame frame = null;
    private Vector<Active> agents = null;
    private String scenario;

    private final double MAX_DISTANCE = 300;
    private final double FRICTION_DISTANCE = 25;

    public JLinkCEAExperiment(String scenario) {
        super();
        this.scenario = scenario;
    }

    public String test(String input) {
        return "Echo: " + input;
    }

    public double evaluateExperiment(double[][][] wm, boolean visible) {
        FitnessFunction avg;

        createArena(this.scenario, visible);
        setupExperiment(wm, this.MAX_DISTANCE, this.FRICTION_DISTANCE);
        avg = new AverageSpeed(arena);

        startExperiment();

        System.out.printf("%f%n", avg.getFitness());

        return avg.getFitness();
    }

    public void createArena(String svgFilename, boolean visible) {
        frame = new JFrame("JLinkCEAExperiment");
        arena = new Arena(frame);

        arena.loadScenario(svgFilename);
        arena.setAllArenaPartsAntialiased(true);
        arena.isVisible = visible;
        arena.frictionBuffer = new FrictionBuffer(arena);
        if (!visible)
            arena.setLoopSleepTime(0);

        frame.setBounds(50, 0, arena.screenWidth, arena.screenHeight + 30);
        frame.setResizable(false);
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(arena);
        frame.setVisible(visible);

        agents = arena.getActives();
    }

    public void startExperiment() {
        arena.start();
    }


//    public double evaluate() {
//        avg = new AverageSpeed(arena);
//        startExperiment();
//        return avg.getFitness();
//    }


    //JLink does not allow to call System.exit()

    /**
     * @param number           number of the agent/robot
     * @param wm               composed weight matrix of size neurons*(inputs+neurons+1)
     * @param maxDistance      maximum distance of distance sensors
     * @param frictionDistance distance of friction point sensors
     *                         <p/>
     *                         Number of sensors as well as number of neurons is determined from the size
     *                         of the weight matrix. You can use either this function called number of agents time, or
     *                         use setupExperiment function, which distributs the weight matrices evenly.
     */
    public void setupAgent(int number, double[][] wm, double maxDistance, double frictionDistance) {

        Active agent = agents.get(number);

        int neurons = wm.length;
        int snum = (wm[0].length - neurons - 1);

        double sangle = -Math.PI / 2;
        double ai = Math.PI / (snum / 2 /* 3 */ - 1);

        CEAExperimentFRNNController frnnc = new CEAExperimentFRNNController();

        frnnc.initFRNN(Util.subMat(wm, 0, snum - 1),
                Util.subMat(wm, snum, snum + neurons - 1),
                Util.flatten(Util.subMat(wm, snum + neurons, snum + neurons)));

        arena.registerController(agent, frnnc);

//        System.out.println("agent = " + agent.getClass());

        if (agent instanceof CEAExperimentRobot) {
//            System.out.println("CEAExperimentRobot");
//            ((CEAExperimentRobot) agent).setDistanceSensors(snum / 2, sangle, ai, maxDistance, frictionDistance);
//            ((CEAExperimentRobot) agent).setFrictionSensors(snum / 2, sangle, ai, maxDistance, frictionDistance);
            ((CEAExperimentRobot) agent).setSensors(snum / 2 /* 3 */, sangle, ai, maxDistance, frictionDistance);
        }
    }

    /**
     * @param wm               Weight matrices Sto be setup to the controllers. The weight matrices are evenly distributed among the agents.
     * @param maxDistance
     * @param frictionDistance
     */
    public void setupExperiment(double[][][] wm, double maxDistance, double frictionDistance) {
        int agentnum = agents.size();
        for (int i = 0; i < agentnum; i++) {
            setupAgent(i, wm[i % wm.length], maxDistance, frictionDistance);
        }
    }

    public String getScenario() {
        return scenario;
    }

    public static void main(String[] args) {

//        JLinkCEAExperiment exp = new JLinkCEAExperiment("cfg/vivae/scenarios/ceamath_oval1.svg");
        JLinkCEAExperiment exp = new JLinkCEAExperiment("cfg/vivae/scenarios/ceamath_simple2.svg");

        // random weight matrices as 3D array
        // 3 robots,
        int sensors = 5; // 5 for distance and 5 for surface
        int neurons = 2;
        int robots = 1;

        double[][][] wm = Util.randomArray3D(robots, neurons, /* 3 */ 2 * sensors + neurons + 1, -5, 5);

        double fitness = exp.evaluateExperiment(wm, true);

        System.out.println("fitness = " + fitness);
    }
}
