import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class partB {

	public static void main(String[] args) {
		// Q1
		// Since there are 9 binary attributes
		// input space is 2^9
		double inputSpace = Math.pow(2, 4);
		System.out.println((int)inputSpace);

		// Q2
		// number of possible concept = 2^(size of input space)
		double conceptSpace = Math.pow(2, inputSpace);
		System.out.println((int)conceptSpace);
		
		// Q3
		// Assume 
		// Age: "Young" = 1, "Old" = 0
		// Class: "1" = 1, "3" = 0
		// Embarked: "Southampton" = 1, "Queenstown" = 0
		// Sex: "Male" = 1, "Female" = 0
		// hypothesis[x][0] = not survived, hypothesis[x][1] = survived
		int[][] hypothesis = new int[16][2];
		for (int i =0; i<16; i++) {
			hypothesis[i][0] = hypothesis[i][1] = 1;
		}
		File train = new File("4Cat-Train.labeled");
		try {
			Scanner trainsc = new Scanner(train);
			while(trainsc.hasNextLine()) {
				String line = trainsc.nextLine();
				String[] attributes = line.split("\t");
				String fourth = attributes[0].split(" ")[1];
				String third = attributes[1].split(" ")[1];
				String second = attributes[2].split(" ")[1];
				String first = attributes[3].split(" ")[1];
				int fourthD = (fourth.equals("Young")) ? 1:0;
				int thirdD = (third.equals("1")) ? 1:0;
				int secondD = (second.equals("Southampton")) ? 1:0;
				int firstD = (first.equals("Male")) ? 1:0;
				int inputToIndex = fourthD*8 + thirdD*4 +secondD*2 + firstD;
				if(attributes[4].split(" ")[1].equals("Yes")) {
					hypothesis[inputToIndex][0] = 0;
				} else if (attributes[4].split(" ")[1].equals("No")) {
					hypothesis[inputToIndex][1] = 0;
				}
			}
			trainsc.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found during partB Q3");
			e.printStackTrace();
		}
		int size = 1;
		for (int i =0; i<16; i++) {
			if(hypothesis[i][0] == 0 && hypothesis[i][1] == 0) {
				size = 0;
				break;
			}
			else if (hypothesis[i][0] == 0 || hypothesis[i][1] == 0) {
				continue;
			}
			else {
				size*=2;
			}
		}
		System.out.println(size);
		
		
		// Q4
		File test = new File(args[0]);
		try {
			Scanner testsc = new Scanner(test);
			while(testsc.hasNextLine()) {
				String currLine = testsc.nextLine();
				String[] testAttributes = currLine.split("\t");
				String testFourth = testAttributes[0].split(" ")[1];
				String testThird = testAttributes[1].split(" ")[1];
				String testSecond = testAttributes[2].split(" ")[1];
				String testFirst = testAttributes[3].split(" ")[1];
				int fourthDtest = (testFourth.equals("Young")) ? 1:0;
				int thirdDtest = (testThird.equals("1")) ? 1:0;
				int secondDtest = (testSecond.equals("Southampton")) ? 1:0;
				int firstDtest = (testFirst.equals("Male")) ? 1:0;
				int inputToIndexTest = fourthDtest*8 + thirdDtest*4 +secondDtest*2 + firstDtest;
				if(hypothesis[inputToIndexTest][0] == 0) {
					System.out.println(size + " 0");
				} else if(hypothesis[inputToIndexTest][1] == 0) {
					System.out.println("0 " + size);
				}else {
					int half = size / 2;
					System.out.println(half + " " + half);
				}
			}
			testsc.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found during partB Q4");
			e.printStackTrace();
		}
	}

}
