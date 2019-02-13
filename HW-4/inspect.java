import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class inspect {

	public static void main(String[] args) {
		String label1 = "";
		String label2 = "";
		int label1_count = 0;
		int label2_count = 0;
		File fd = new File(args[0]);
		try {
			Scanner sc = new Scanner(fd);
			if(sc.hasNext())
				sc.nextLine(); // skip the first line
			while(sc.hasNext()) {
				String[] tmp = sc.nextLine().split(",");
				if(label1.isEmpty()) {
					label1 = tmp[tmp.length - 1];
					label1_count++;
				} else if (tmp[tmp.length - 1].equals(label1)) {
					label1_count++;
				} else if(label2.isEmpty()) {
					label2 = tmp[tmp.length - 1];
					label2_count++;
				} else {
					label2_count++;
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		int total = label1_count + label2_count;
		double error = (double) Math.min(label1_count, label2_count) 
				/ (double)total;
		double entropy = 0;
		if(error == 0) {
			entropy = ((1-error) * Math.log(1 / (1-error)) / Math.log(2));
		}else if(error == 1) {
			entropy = (error * Math.log(1/error) / Math.log(2));
		}else {
			entropy = (error * Math.log(1/error) / Math.log(2)) +  
				((1-error) * Math.log(1 / (1-error)) / Math.log(2));
		}
		// System.out.println(label1 + ": " + label1_count);
		// System.out.println(label2 + ": " + label2_count);
		System.out.println("entropy: " + entropy);
		System.out.println("error: " + error);
	}
}
