package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Stream;

public class DataReader {
	public ArrayList<ArrayList<ArrayList<Integer>>> readIn() {
		File folder = new File("C:\\Users\\madde\\git\\GestureRecognition\\src\\resources\\training-data\\zero");
		String[] files = folder.list();
		
		ArrayList<ArrayList<ArrayList<Integer>>> data = new ArrayList<ArrayList<ArrayList<Integer>>>();
		for (String file : files) {
			File f = new File("C:\\Users\\madde\\git\\GestureRecognition\\src\\resources\\training-data\\zero\\" + file);
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
		}	
		System.out.println("Data read");
		return data;
	}
	
	public void test() {
		File folder = new File("C:\\Users\\madde\\git\\GestureRecognition\\src\\resources\\training-data\\zero");
		String[] files = folder.list();
		System.out.println(files[0]);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DataReader readData = new DataReader();
		ArrayList<ArrayList<ArrayList<Integer>>> data = readData.readIn();
		System.out.println(data.size());
		//readData.test();
	}

}
