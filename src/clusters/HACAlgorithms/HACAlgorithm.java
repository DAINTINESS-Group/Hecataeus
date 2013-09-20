package clusters.HACAlgorithms;
import java.util.ArrayList;

import clusters.EngineConstructs.ClusterSet;
import clusters.GraphFacades.ClusterableObject;


public abstract class HACAlgorithm {
	public abstract void execute(ArrayList<ClusterableObject> inputObjects, ArrayList<ClusterSet> solutions, double [][] inputObjectsDistances);
}
