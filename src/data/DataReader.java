package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class DataReader {
	public ArrayList<ArrayList<ArrayList<Integer>>> readIn() {
		File f = new File("C:\\Users\\madde\\Desktop\\CSC Notes\\Algorithms\\TrainingData\\0_2.txt");
		ArrayList<ArrayList<ArrayList<Integer>>> data = new ArrayList<ArrayList<ArrayList<Integer>>>();
		ArrayList<ArrayList<Integer>> chara = new ArrayList<ArrayList<Integer>>();
		try {
			Scanner scan = new Scanner(f);
			while(scan.hasNextLine()) {
				String[] x = scan.nextLine().split(" ");
				if (!x[0].equals("\"x\"")) {
					ArrayList<Integer> point = new ArrayList<Integer>();
					point.add(Integer.parseInt(x[0]));
					point.add(Integer.parseInt(x[1]));
					chara.add(point);
				}
			}
			scan.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data.add(chara);
		return data;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataReader readData = new DataReader();
		ArrayList<ArrayList<ArrayList<Integer>>> data = readData.readIn();
		System.out.println(data.toString());
	}

}
