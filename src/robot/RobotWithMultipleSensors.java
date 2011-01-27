package robot;

import vivae.arena.Arena;
import vivae.arena.parts.Robot;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: bukz1
 * Date: 1/27/11
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class RobotWithMultipleSensors extends Robot {

    public RobotWithMultipleSensors(float x, float y) {
        super(x, y);
    }

    public RobotWithMultipleSensors(Shape shape, int layer, Arena arena) {
        super(shape, layer, arena);
    }

    public RobotWithMultipleSensors(float x, float y, Arena arena) {
        super(x, y, arena);
    }

    abstract public double[][] getSensoryData();

}
