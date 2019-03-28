package hmm;

import java.util.ArrayList;

public class HMM {
	private void normalizeData(ArrayList<ArrayList<ArrayList<Integer>>> data) {
		ArrayList<ArrayList<ArrayList<Double>>> normalizedData = new ArrayList<ArrayList<ArrayList<Double>>>();
		//Iterate through all of the characters in the list
		for (ArrayList<ArrayList<Integer>> character : data) {
			int sumX = 0;
			int sumY = 0;
			//Find the sum of all of the points in each character
			for (ArrayList<Integer> point : character) {
				sumX += point.get(0);
				sumY += point.get(1);
			}
			//Find the average of all the points in each character
			float meanX = sumX/character.size();
			float meanY = sumY/character.size();

			//Instantiate the variable to hold the summation used in a standard
			//deviation calculation
			int stdNumeratorX = 0;
			int stdNumeratorY = 0;
			/*Sum the numbers in the numerator of the std deviation formula.
			Multiply the number by itself to square it rather than using Math.pow
			since machine commands are O(1)*/
			for (ArrayList<Integer> point : character) {
				stdNumeratorX += ((point.get(0) - meanX) * (point.get(0) - meanX));
				stdNumeratorY += ((point.get(1) - meanY) * (point.get(1) - meanY));
			}
			
			//Calculate the standard deviation for x and y
			double stdX = Math.sqrt(stdNumeratorX / character.size());
			double stdY = Math.sqrt(stdNumeratorY / character.size());
			
			//Normalize the points and add them to a new character
			ArrayList<ArrayList<Double>> normalizedCharacter = new ArrayList<ArrayList<Double>>();
			for (ArrayList<Integer> point : character) {
				ArrayList<Double> normalizedPoint = new ArrayList<Double>();
				normalizedPoint.add(((point.get(0) - meanX) / stdX));
				normalizedPoint.add(((point.get(1) - meanY) / stdY));
				normalizedCharacter.add(normalizedPoint);
			}
			//Add the normalized character to the normalized dataset
			normalizedData.add(normalizedCharacter);
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HMM hMM = new HMM();
		hMM.normalizeData(null);
	}

}
