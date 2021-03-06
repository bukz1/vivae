/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */
package vivae.util;

import vivae.arena.*;
import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;
import vivae.arena.parts.ArenaPart;

/**
 * @author Petr Smejkal
 */
public class ArenaPartsGenerator {

    /**
     * Takes a map of shapes indexed by their type and creates Vector of ArenaParts.
     * @param shapesWithTypeMap a map of shapes the ArenaParts are to be made of.
     * @param layer Specifies which layer the ArenaParts that we've created are on.
     * @param arena An Arena Reference is required for Robot's constructor.
     * @return Vector of ArenaParts
     */
    public static Vector<ArenaPart> createParts(
            HashMap<String, Vector<Shape>> shapesWithTypeMap, int layer, Arena arena) {
        Vector<ArenaPart> parts = new Vector<ArenaPart>();
        Set<String> types = shapesWithTypeMap.keySet();
        for (String type : types) {
            for (Shape shape : shapesWithTypeMap.get(type)) {
                ArenaPart part = createPart(shape, type, layer, arena);
                if (part != null) {
                    parts.add(part);
                }
            }
        }
        return parts;
    }

    /**
     * Tries to create a child class of ArenaPart of exact name given as the second parameter.
     * @param shape Determines the shape.
     * @param type  Specifies the type of the future ArenaPart.
     * @param layer Number of the layer in the arena if it is a surface.
     * @param arena An Arena reference is required for Robot's constructor. 
     * @return A new ArenaPart child class.
     */
    public static ArenaPart createPart(Shape shape, String type, int layer, Arena arena) {
        Object[] constructorArgs = {shape, layer, arena};
        Class[] argTypes = {Shape.class, int.class, Arena.class};

        Object reflectedArenaPart = null;

        try {
            Constructor cons = null;
            //pokud jmeno nema balicek, tak se predpoklada vivae.arena.parts
            if(!type.contains(".")) {
                type = "vivae.arena.parts." + type;
            }
            cons = Class.forName(type).getConstructor(argTypes);
            reflectedArenaPart = cons.newInstance(constructorArgs);
            if (!ArenaPart.class.isAssignableFrom(Class.forName(type))) {
                System.out.println("Error: Unrecognized type of Object: " + type);
            }
            return (ArenaPart) reflectedArenaPart;
        } catch (Exception ex) {
            Logger.getLogger("vivae").info("Class " + type + " cannot be constructed by reflection.");
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Takes a shape and returns an array of the points it is made of.
     * @param shape Input shape
     * @return Array of Point2D coordinates of the traversal points.
     */
    public static Point2D.Float[] getTraversalPoints(Shape shape) {
        List<Point2D.Float> list = new ArrayList<Point2D.Float>();
        float centerX = new Float(shape.getBounds2D().getCenterX());
        float centerY = new Float(shape.getBounds2D().getCenterY());
        PathIterator pit = shape.getPathIterator(null);
        float[] coords = new float[6];
        int count = 0;
        while (!pit.isDone()) {
            int type = pit.currentSegment(coords);
            switch (type) {
                case PathIterator.SEG_MOVETO:
                // fall through
                case PathIterator.SEG_LINETO:
                    list.add(new Point2D.Float(coords[0] - centerX, coords[1] - centerY));
                    break;
                case PathIterator.SEG_CLOSE:
                    break;
                default:
            }
            count++;
            pit.next();
        }
        return list.toArray(new Point2D.Float[list.size() - 1]);
    }
}
