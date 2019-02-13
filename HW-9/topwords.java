import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class topwords {

	public static void main(String[] args) {
		// args[0] is the trainning file
		File train = new File(args[0]);
		HashMap<String, Double> conWords = new HashMap<String, Double>();
		HashMap<String, Double> libWords = new HashMap<String, Double>();
		double sum_conWords = 0.0;
		double sum_libWords = 0.0;
		double vocabulary = 0.0;
		try {
			Scanner trainsc = new Scanner(train);
			while(trainsc.hasNextLine()) {
				String currFile_name = trainsc.nextLine();
				File currFile = new File(currFile_name);	
				BufferedReader currFilebr = new BufferedReader(
						new FileReader(currFile));
				String curr_word;
				while((curr_word = currFilebr.readLine())!= null) {
					curr_word = curr_word.toLowerCase();
					if (!(conWords.containsKey(curr_word) 
							|| libWords.containsKey(curr_word))) {
						vocabulary++;
					}
					if(currFile_name.contains("con")) {
						sum_conWords++;
						if(conWords.containsKey(curr_word)) {
							conWords.put(curr_word, 
									conWords.get(curr_word)+1.0);
						} else {
							conWords.put(curr_word, 1.0);
						}
					} else {
						sum_libWords++;
						if(libWords.containsKey(curr_word)) {
							libWords.put(curr_word, 
									libWords.get(curr_word)+1.0);
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
		// System.out.println("Conservative count: " + con_count);
		// System.out.println("Liberal count: " + lib_count);

		HashMap<String, Double> sorted_con = sortByValues(conWords);
		HashMap<String, Double> sorted_lib = sortByValues(libWords);
		int twentycount = 0;
		//		System.out.println("Vocab :" + vocabulary);
		//		System.out.println("Read con :" + con_count);
		//		System.out.println("Read lib :" + lib_count);
		//		System.out.println("Read con words:" + sum_conWords);
		//		System.out.println("Read lib words:" + sum_libWords);
		//		System.out.println("Read con words:" + uni_conWords);
		//		System.out.println("Read lib words:" + uni_libWords);
		for(Map.Entry<String, Double> eachEntry : sorted_lib.entrySet()) {
			twentycount++;
			eachEntry.setValue((eachEntry.getValue() + 1.0) / 
					(sum_libWords + vocabulary));
			System.out.printf(eachEntry.getKey() + " %.4f\n", 
					eachEntry.getValue());
			if(twentycount == 20) {
				System.out.println();
				break;
			}
		}
		twentycount = 0;
		for(Map.Entry<String, Double> eachEntry : sorted_con.entrySet()) {
			twentycount++;
			eachEntry.setValue((eachEntry.getValue() + 1.0) / 
					(sum_conWords + vocabulary));
			System.out.printf(eachEntry.getKey() + " %.4f\n", 
					eachEntry.getValue());
			if(twentycount == 20) {
				break;
			}
		}

	}

	private static HashMap<String, Double> sortByValues
	(HashMap<String, Double> map) { 
		List<Map.Entry<String, Double>> list = 
				new LinkedList<Map.Entry<String, Double>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, 
					Map.Entry<String, Double> o2) {
				return (o2.getValue().compareTo(o1.getValue()));
			}
		});

		HashMap<String, Double> sortedHashMap = 
				new LinkedHashMap<String, Double>();
		for (Map.Entry<String, Double> each : list) {
			sortedHashMap.put(each.getKey(), each.getValue());
		} 
		return sortedHashMap;
	}

}
