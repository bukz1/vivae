package vivae.example;

import vivae.util.Util;
import vivae.fitness.FitnessFunction;
import vivae.fitness.MovablesOnTop;
import vivae.fitness.AverageSpeed;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Nov 11, 2009
 * Time: 3:03:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class FRNNRandomSearch {

    public double[][] search(String scenario, int sensors, int neurons, int evals){
        FRNNExperiment exp = new FRNNExperiment();
        double bestfit=0;
        double fit;
        double[][] res=null;
        for(int i=0;i<evals;i++){
            exp.createArena(scenario,false);
            // the 3D matrix has toplevel size of 1, thus it is copied to all controllers
            double[][][] wm = Util.randomArray3D(1,neurons,2*sensors+neurons+1,-5,5);
            exp.setupExperiment(wm,50,25);
            FitnessFunction avg = new AverageSpeed(exp.arena);
            exp.startExperiment();
            fit = avg.getFitness();
            System.out.println("fitness = "+ fit);
            if(fit>bestfit){
                bestfit = fit; res = wm[0];
            }
        }
        return res;
    }

    public void play(String scenario, double[][] wm){
         FRNNExperiment exp = new FRNNExperiment();
         exp.createArena(scenario,true);
         for(int i=0;i<exp.agents.size();i++){
             exp.setupAgent(i,wm,50,25);
         }
         FitnessFunction avg = new AverageSpeed(exp.arena);
         exp.startExperiment();
         System.out.println(" best fitness = "+ avg.getFitness());
    }

    public static void main(String[] arg){
        String scenario = "data/scenarios/arena1.svg";
        FRNNRandomSearch s = new FRNNRandomSearch();
        double[][] wmbest = s.search(scenario, 5, 2, 100); // 5 sensors of each type, 2 neurons, 20 evaluations
        s.play(scenario,wmbest); // play the best one
    }
}