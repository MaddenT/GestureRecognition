package hmm;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import data.DataReader;

public class Controller {
	private HMM zero;
	private HMM one;
	private HMM two;
	private HMM three;
	private HMM four;
	private HMM five;
	private HMM six;
	private HMM seven;
	private HMM eight;
	private HMM nine;
	private ArrayList<HMM> hmms;
	protected int[][] confusionMatrix;
	
	public Controller () {
		DataReader dr = new DataReader();
		this.zero = new HMM(dr.readInTraining("zero"), 4, "zero");//4 good setting
		this.one = new HMM(dr.readInTraining("one"), 3, "one");
		this.two = new HMM(dr.readInTraining("two"), 3, "two");
		this.three = new HMM(dr.readInTraining("three"), 3, "three");
		this.four = new HMM(dr.readInTraining("four"), 3, "four");//orig 3
		this.five = new HMM(dr.readInTraining("five"), 6, "five");//6 good setting
		this.six = new HMM(dr.readInTraining("six"), 4, "six");//4
		this.seven = new HMM(dr.readInTraining("seven"), 4, "seven");//4 good setting
		this.eight = new HMM(dr.readInTraining("eight"), 6, "eight");//6 good setting
		this.nine = new HMM(dr.readInTraining("nine"), 3, "nine");
		ArrayList<HMM> tempHMMs = new ArrayList<HMM>();
		tempHMMs.add(this.zero);
		tempHMMs.add(this.one);
		tempHMMs.add(this.two);
		tempHMMs.add(this.three);
		tempHMMs.add(this.four);
		tempHMMs.add(this.five);
		tempHMMs.add(this.six);
		tempHMMs.add(this.seven);
		tempHMMs.add(this.eight);
		tempHMMs.add(this.nine);
		this.hmms = tempHMMs;
	}
	
	public Controller (int[] states) {
		DataReader dr = new DataReader();
		this.zero = new HMM(dr.readInTraining("zero"), states[0], "zero");//4 good setting
		this.one = new HMM(dr.readInTraining("one"), states[1], "one");
		this.two = new HMM(dr.readInTraining("two"), states[2], "two");
		this.three = new HMM(dr.readInTraining("three"), states[3], "three");
		this.four = new HMM(dr.readInTraining("four"), states[4], "four");//orig 3
		this.five = new HMM(dr.readInTraining("five"), states[5], "five");//6 good setting
		this.six = new HMM(dr.readInTraining("six"), states[6], "six");//4
		this.seven = new HMM(dr.readInTraining("seven"), states[7], "seven");//4 good setting
		this.eight = new HMM(dr.readInTraining("eight"), states[8], "eight");//6 good setting
		this.nine = new HMM(dr.readInTraining("nine"), states[9], "nine");
		ArrayList<HMM> tempHMMs = new ArrayList<HMM>();
		tempHMMs.add(this.zero);
		tempHMMs.add(this.one);
		tempHMMs.add(this.two);
		tempHMMs.add(this.three);
		tempHMMs.add(this.four);
		tempHMMs.add(this.five);
		tempHMMs.add(this.six);
		tempHMMs.add(this.seven);
		tempHMMs.add(this.eight);
		tempHMMs.add(this.nine);
		this.hmms = tempHMMs;
	}
	
	public int testHMM() {
		ArrayList<String> names = new ArrayList<String>();
		for (HMM hmm : this.hmms) {
			names.add(hmm.name);
		}
		DataReader dr = new DataReader();
		int[][] confusion = new int[10][10];
		int count = 0;
		for (String curName : names) {
			System.out.println("Running test cases for: " + curName);
			ArrayList<ArrayList<ArrayList<Integer>>> curData = dr.readInTesting(curName);
			
			int[] rowMatrix = {0,0,0,0,0,0,0,0,0,0};
			for (ArrayList<ArrayList<Integer>> chara : curData) {
				double highestP = 0.0;
				String name = "";
				for (HMM hmm : this.hmms) {
					double localP = hmm.evaluate(chara);
					if (localP > highestP) {
						highestP = localP;
						name = hmm.name;
					}
				}
				if (name.equals("zero")) {
					int freq = rowMatrix[0];
					rowMatrix[0] = freq + 1;
				} else if (name.equals("one")) {
					int freq = rowMatrix[1];
					rowMatrix[1] = freq + 1;
				} else if (name.equals("two")) {
					int freq = rowMatrix[2];
					rowMatrix[2] = freq + 1;
				} else if (name.equals("three")) {
					int freq = rowMatrix[3];
					rowMatrix[3] = freq + 1;
				} else if (name.equals("four")) {
					int freq = rowMatrix[4];
					rowMatrix[4] = freq + 1;
				} else if (name.equals("five")) {
					int freq = rowMatrix[5];
					rowMatrix[5] = freq + 1;
				} else if (name.equals("six")) {
					int freq = rowMatrix[6];
					rowMatrix[6] = freq + 1;
				} else if (name.equals("seven")) {
					int freq = rowMatrix[7];
					rowMatrix[7] = freq + 1;
				} else if (name.equals("eight")) {
					int freq = rowMatrix[8];
					rowMatrix[8] = freq + 1;
				} else if (name.equals("nine")) {
					int freq = rowMatrix[9];
					rowMatrix[9] = freq + 1;
				}
			}
			confusion[count] = rowMatrix;
			count += 1;
		}
		
		int sumRight = 0;
		for (int i = 0; i < confusion.length ; i++) {
			sumRight += confusion[i][i];
		}
		this.confusionMatrix = confusion;
		return sumRight;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Instant start = Instant.now();
		
		Controller c = new Controller();
		int numRight = c.testHMM();
		
		Instant end = Instant.now();
		System.out.println("***********************************************************************************");
		System.out.println("***********************************************************************************");
		System.out.println("Exit, testing took: " + Duration.between(start, end) + " seconds.");
		System.out.println("Total number of correctly classified characters: " + numRight);
		double succRate = (double) numRight/1548;
		System.out.println("Success rate: " + Double.toString(succRate));
		System.out.println("Failure rate: " + Double.toString(1 - succRate));
		System.out.println("Confusion Matrix: ");
		for (int[] mat : c.confusionMatrix) {
			System.out.println(Arrays.toString(mat));
		}
	}

}

