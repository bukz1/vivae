package vivae.example.ceamath;

import robot.RobotWithMultipleSensors;
import vivae.controllers.RobotWithSensorController;
import vivae.controllers.nn.FRNN;
import vivae.util.Util;

/**
 * Created by IntelliJ IDEA.
 * User: bukz1
 * Date: 27.1.11
 * Time: 23:34
 * To change this template use File | Settings | File Templates.
 */
public class CEAExperimentFRNNController extends RobotWithSensorController {

    protected FRNN frnn = new FRNN();

    public void initFRNN(double[][] wIn, double[][] wRec, double[] wThr) {
        frnn.init(wIn, wRec, wThr);
    }

    @Override
    public void moveControlledObject() {

//        if (robot instanceof FRNNControlledRobot) {
//        double[] input = Util.flatten(((FRNNControlledRobot) robot).getSensoryData());
        double[] input = Util.flatten(((RobotWithMultipleSensors) robot).getSensoryData());

        double[] eval = frnn.evalNetwork(input);

        double lWheel = eval[0];
        double rWheel = eval[eval.length - 1];
        double angle;
        double acceleration = 5.0 * (lWheel + rWheel);
        if (acceleration < 0) {
            acceleration = 0; // negative speed causes problems, why?
        }
        double speed = Math.abs(robot.getSpeed() / robot.getMaxSpeed());
        speed = Math.min(Math.max(speed, -1), 1);
        if (rWheel > lWheel) {
            angle = 10 * (1.0 - speed);
        } else {
            angle = -10 * (1.0 - speed);
        }

        // log
//        for (int i = 0; i < input.length; i++)
//            System.out.printf("%f ", input[i]);
//        System.out.printf("%f %f%n", lWheel, rWheel);

        robot.rotate((float) angle);
        robot.accelerate((float) acceleration);

    }

    public void setFRNN(FRNN net) {
        this.frnn = net;
    }
}
