package hmm;

import java.util.ArrayList;
import org.apache.commons.math3.linear.RealMatrix;

public class State {
	private ArrayList<Double> means;
	private RealMatrix covariance;
	private double weight;
	
	public State (ArrayList<Double> means, RealMatrix covariance, double weight) {
		this.means = means;
		this.covariance = covariance;
		this.weight = weight;
	}
	
}
