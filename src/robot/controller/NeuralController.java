package robot.controller;

import robot.IRobotInterface;
import vivae.controllers.nn.FRNN;
import vivae.util.Util;

/**
 * Created by IntelliJ IDEA.
 * User: bukz1
 * Date: 28.1.11
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
public class NeuralController implements IRobotController {

    final private IRobotInterface robot;
    private int stepCount = 0;

    private FRNN network;
    private int numNeurons;
    private int numInputs;

    public NeuralController(double[][] netW, IRobotInterface robot) {
        this.robot = robot;

        numNeurons = netW.length;
        numInputs = (netW[0].length - numNeurons - 1);

        network = new FRNN(
                Util.subMat(netW, 0, numInputs - 1),
                Util.subMat(netW, numInputs, numInputs + numNeurons - 1),
                Util.flatten(Util.subMat(netW, numInputs + numNeurons, numInputs + numNeurons))
        );
    }

    public void step() {

        double[] input = robot.getSensorData();
        double[] eval = network.evalNetwork(input);

        double lWheel = eval[0];
        double rWheel = eval[1];

        robot.setWheelSpeed(lWheel, rWheel);

        stepCount++;
    }

    public IRobotInterface getRobot() {
        return robot;
    }


}
