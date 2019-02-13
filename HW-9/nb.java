import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class nb {

	public static void main(String[] args) {
		// args[0] is the trainning file
		// args[1] is the test file
		File train = new File(args[0]);
		Map<String, Double> conWords = new HashMap<String, Double>();
		Map<String, Double> libWords = new HashMap<String, Double>();
		double sum_conWords = 0.0;
		double sum_libWords = 0.0;
		double con_count = 0.0;
		double lib_count = 0.0;
		double vocabulary = 0.0;
		try {
			Scanner trainsc = new Scanner(train);
			while(trainsc.hasNextLine()) {
				String currFile_name = trainsc.nextLine();
				File currFile = new File(currFile_name);	
				if(currFile_name.contains("con")) {
					con_count++;
				} else {
					lib_count++;
				}
				BufferedReader currFilebr = new BufferedReader(
						new FileReader(currFile));
				String curr_word;
				while((curr_word = currFilebr.readLine())!= null) {
					curr_word = curr_word.toLowerCase();
					if (!conWords.containsKey(curr_word) 
							&& !libWords.containsKey(curr_word)) {
						vocabulary++;
					}
					if(currFile_name.contains("con")) {
						sum_conWords++;
						if(conWords.containsKey(curr_word)) {
							double oldvalue = conWords.get(curr_word);
							conWords.replace(curr_word, 
									(oldvalue+1.0));
						} else {
							conWords.put(curr_word, 1.0);
						}
					} else {
						sum_libWords++;
						if(libWords.containsKey(curr_word)) {
							double oldvalue = libWords.get(curr_word);
							libWords.replace(curr_word, 
									(oldvalue+1.0));
						} else {
							libWords.put(curr_word, 1.0);
						}
					}
				}
				currFilebr.close();
			}
			trainsc.close();
		} catch (IOException e) {
			System.out.println("Error Openning Trainning File!");
			e.printStackTrace();
		}
		//		System.out.println(vocabulary);
		//		System.out.println(conWords.size());
		//		System.out.println(libWords.size());
		//		System.out.println(sum_conWords);
		for(Map.Entry<String, Double> eachEntry : conWords.entrySet()) {
			eachEntry.setValue((eachEntry.getValue() + 1.0) / 
					(sum_conWords + vocabulary));
		}
		for(Map.Entry<String, Double> eachEntry : libWords.entrySet()) {
			eachEntry.setValue((eachEntry.getValue() + 1.0) / 
					(sum_libWords + vocabulary));
		}

		double prior_con = con_count / (con_count + lib_count);
		double prior_lib = lib_count / (con_count + lib_count);
		File test = new File(args[1]);
		double log_prob_con = Math.log(prior_con);
		double log_prob_lib = Math.log(prior_lib);
		double correctness = 0.0;
		double count = 0.0;
		double unseen_con = 1.0 / (sum_conWords + vocabulary);
		double unseen_lib = 1.0 / (sum_libWords + vocabulary);
		try {
			Scanner testsc = new Scanner(test);
			while(testsc.hasNextLine()) {
				count++;
				String currFile_name = testsc.nextLine();
				File currFile = new File(currFile_name);
				BufferedReader currFilebr = new BufferedReader(
						new FileReader(currFile));
				log_prob_con = Math.log(prior_con);
				log_prob_lib = Math.log(prior_lib);
				String curr_word;
				while((curr_word = currFilebr.readLine()) != null) {
					curr_word = curr_word.toLowerCase();
					// Conservative
					if(conWords.containsKey(curr_word)) {
						log_prob_con += Math.log(conWords.get(curr_word));
					} else if(libWords.containsKey(curr_word)){
						log_prob_con += Math.log(unseen_con);
					} 
					// Liberal
					if(libWords.containsKey(curr_word)) {
						log_prob_lib += Math.log(libWords.get(curr_word));
					} else if(conWords.containsKey(curr_word)){
						log_prob_lib += Math.log(unseen_lib);
					}
				}
				currFilebr.close();
				if(log_prob_con >= log_prob_lib) {
					if(currFile_name.contains("con")) {
						correctness++;
					}
					System.out.println("C");
				} else {
					if(currFile_name.contains("lib")) {
						correctness++;
					}
					System.out.println("L");
				}
			}
			System.out.printf("Accuracy: %.4f\n", (correctness / count));
			testsc.close();
		} catch (IOException e) {
			System.out.println("Error Openning Test File!");
			e.printStackTrace();
		}

	}

}
