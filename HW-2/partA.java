import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class partA {

	public static void main(String[] args) {
		// Q1
		// Since there are 9 binary attributes
		// input space is 2^9
		double inputSpace = Math.pow(2, 9);
		System.out.println((int)inputSpace);

		// Q2
		// number of possible concept = 2^(size of input space)
		double conceptSpace = Math.pow(2, inputSpace);
		double digits = Math.log10(conceptSpace);
		digits = Math.ceil(digits);
		System.out.println((int)digits);

		// Q3
		double hypothesisSize = Math.pow(3, 9) + 1;
		System.out.println((int)hypothesisSize);

		// Q4
		double newHypothesisSize = Math.pow(3, 10) + 1;
		System.out.println((int)newHypothesisSize);

		// Q5
		double anotherHypothesisSize = 4 * Math.pow(3, 8) + 1;
		System.out.println((int)anotherHypothesisSize);

		// Q6
		File inputFile = new File("9Cat-Train.labeled");
		String[] h = new String[9];
		for(String eachh : h) {
			eachh = null;
		}
		try {
			Scanner sc = new Scanner(inputFile);
			PrintWriter writer;
			try {
				writer = new PrintWriter("partA6.txt", "UTF-8");
				int countEntries = 0;
				while(sc.hasNextLine()) {
					String currLine = sc.nextLine();
					countEntries++;
					String[] content = currLine.split("\t");
					int resultIndex = content.length - 1;
					String[] results = content[resultIndex].split(" ");
					if(results[1].equals("Yes")) {
						// check each attribute constraint
						for(int i = 0; i < resultIndex; i++) {
							String[] eachAttribute = content[i].split(" ");
							if(h[i] == null || ((!h[i].equals(eachAttribute[1])) && !(h[i].equals("?")))) {
								if(h[i] == null) {
									h[i] = eachAttribute[1];
								}
								else {
									h[i] = "?";
								}
							}
						}
					}
					if(countEntries % 20 == 0) {
						int temp = 0;
						for (String currh : h) {
							temp++;
							writer.print(currh);
							if(temp < 9) {
								writer.print("\t");
							}
						}
						writer.print("\n");
					}
				}
				writer.close();
			} catch (UnsupportedEncodingException e) {
				System.out.println("Failed Writing File in Q6");
				e.printStackTrace();
			}
			sc.close();
		} catch (FileNotFoundException e) {
			System.out.println("Failed Reading File in Q6");
			e.printStackTrace();
		}
		
		//Q7
		File inputDevFile = new File("9Cat-Dev.labeled");
		float error = 0;
		float counter = 0;
		try {
			Scanner scDev = new Scanner(inputDevFile);
			while(scDev.hasNextLine()) {
				counter++;
				String hypothesisResult = "Yes";
				String currDevLine = scDev.nextLine();
				String[] DevAttr = currDevLine.split("\t");
				int realResultIndex = DevAttr.length - 1;
				String[] realResults = DevAttr[realResultIndex].split(" ");
				for(int i = 0; i < realResultIndex; i++) {
					String[] eachAttribute = DevAttr[i].split(" ");
					if(eachAttribute[1].equals(h[i]) || h[i].equals("?") ) {
						continue;
					} else {
						hypothesisResult = "No";
						break;
					}
				}
				if(!hypothesisResult.equals(realResults[1])) {
					error++;
				}
			}
			float missclassificationR = error / counter;
			System.out.printf("%.2f\n", missclassificationR);
			scDev.close();
		} catch (FileNotFoundException e) {
			System.out.println("Failed Reading File in Q7");
			e.printStackTrace();
		}
		
		
		// Q8
		File inputTestFile = new File(args[0]);
		try {
			Scanner scTest = new Scanner(inputTestFile);
			while(scTest.hasNextLine()) {
				String hypothesisResultTest = "Yes";
				String currTestLine = scTest.nextLine();
				String[] testAttr = currTestLine.split("\t");
				for(int i = 0; i < 9; i++) {
					String[] eachAttribute = testAttr[i].split(" ");
					if(eachAttribute[1].equals(h[i]) || h[i].equals("?") ) {
						continue;
					} else {
						hypothesisResultTest = "No";
						break;
					}
				}
				System.out.println(hypothesisResultTest);
			}
			scTest.close();
		} catch (FileNotFoundException e) {
			System.out.println("Failed Reading File in Q7");
			e.printStackTrace();
		}
		
	}

}
