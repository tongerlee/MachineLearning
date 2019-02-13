import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class decisionTree {

	public static void main(String[] args) {
		File trainfd = new File(args[0]);
		String[] labels_name = null;
		String[][] labels = null;
		int[][] labels_count = null;
		int[][][] conditional_labels_count = null;
		double minEntropy1 = Double.MAX_VALUE;
		double minEntropy2 = Double.MAX_VALUE;
		int firstAttrIndex = 0;
		int secAttrIndex = 0;
		int numberAtt = -1;
		boolean stop = false;
		String depth1 = "";
		String depth2l = "";
		String depth2r = "";
		boolean leftstop = false;
		boolean rightstop = false;
		int secLabels_count[][][] = null;
		int secLabels_count_result[][][][] = null;
		double entropy1 = 0;
		double entropy2 = 0;
		double minlEntropy = 0;
		double minrEntropy = 0;
		double secminlEntropy = Double.MAX_VALUE;
		double secminrEntropy = Double.MAX_VALUE;
		double prob1 = 0;
		boolean secstop= false;
		double leftentropy = 0;
		double rightentropy = 0;
		int secAttrIndexl = 0;
		int secAttrIndexr = 0;
		try {
			Scanner sc = new Scanner(trainfd);
			if(sc.hasNext()) {
				String[] tmp = sc.nextLine().split(","); // skip the first line
				labels = new String[tmp.length][2];
				labels_name = new String[tmp.length];
				for(int i = 0; i<tmp.length; i++) {
					labels[i][0] = "";
					labels[i][1] = "";
					labels_name[i] = tmp[i];
				}
				labels_count = new int[tmp.length][2];
				conditional_labels_count = new int [tmp.length][2][2];
				numberAtt = tmp.length;
			}
			while(sc.hasNext()){
				String[] tmp = sc.nextLine().split(",");
				if(labels[tmp.length - 1][0].isEmpty()) {
					labels[tmp.length - 1][0] = tmp[tmp.length - 1];
					labels_count[tmp.length - 1][0]++;
				} else if (tmp[tmp.length - 1].equals(labels[tmp.length - 1][0])) {
					labels_count[tmp.length - 1][0]++;
				} else if(labels[tmp.length - 1][1].isEmpty()) {
					labels[tmp.length - 1][1] = tmp[tmp.length - 1];
					labels_count[tmp.length - 1][1]++;
				} else {
					labels_count[tmp.length - 1][1]++;
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		// searching for the first attribute to split on
		try {
			Scanner sc = new Scanner(trainfd);
			if(sc.hasNext()) {
				sc.nextLine();
			}
			while(sc.hasNext()) {
				String[] tmp = sc.nextLine().split(",");
				for(int i = 0; i < tmp.length - 1; i++) {
					if(labels[i][0].isEmpty()) {
						labels[i][0] = tmp[i];
						labels_count[i][0]++;
					} else if (tmp[i].equals(labels[i][0])) {
						labels_count[i][0]++;
					} else if(labels[i][1].isEmpty()) {
						labels[i][1] = tmp[i];
						labels_count[i][1]++;
					} else {
						labels_count[i][1]++;
					}

					if(tmp[i].equals(labels[i][0])) {
						if(tmp[tmp.length-1].equals(labels[tmp.length - 1][0])) {
							conditional_labels_count[i][0][0]++;
						}
						else {
							conditional_labels_count[i][0][1]++;
						}
					} else {
						if(tmp[tmp.length-1].equals(labels[tmp.length - 1][0])) {
							conditional_labels_count[i][1][0]++;
						}
						else {
							conditional_labels_count[i][1][1]++;
						}
					}
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int total = labels_count[numberAtt - 1][0] 
				+ labels_count[numberAtt - 1][1];
		double rootProb = (double) Math.min(labels_count[numberAtt - 1][0], 
				labels_count[numberAtt - 1][1]) / (double)total;
		double rootEntropy = getEntropy(rootProb);
		for (int i = 0; i < numberAtt - 1; i++) {
			double prob11 = (double)conditional_labels_count[i][0][0] 
					/ (double)labels_count[i][0];
			entropy1 = getEntropy(prob11);

			double prob21 = (double)conditional_labels_count[i][1][0] 
					/ (double)labels_count[i][1];
			entropy2 = getEntropy(prob21);

			prob1 = (double) labels_count[i][0] / (double) total;
			double conditional_entropy = prob1 * entropy1 
					+ (1 - prob1) * entropy2;

			if(conditional_entropy < minEntropy1) {
				firstAttrIndex = i;
				minEntropy1 = conditional_entropy;
				minlEntropy = entropy1;
				minrEntropy = entropy2;
			}	
		}
		if(getMutual(rootEntropy, minEntropy1) >= 0.1) {
			stop = false;
			depth1 = labels_name[firstAttrIndex];
			if(conditional_labels_count[firstAttrIndex][0][0] == 0 || conditional_labels_count[firstAttrIndex][0][1] == 0) {
				leftstop = true;
			} 
			if (conditional_labels_count[firstAttrIndex][1][0] == 0 || conditional_labels_count[firstAttrIndex][1][1] == 0) {
				rightstop = true;
			}
		} else {
			stop = true;
			firstAttrIndex = 0;
		}

		// begin searching for the second attribute to split on
		if(!stop && !(leftstop && rightstop)) {
			secLabels_count = new int [numberAtt-1][2][2];
			secLabels_count_result = new int [numberAtt-1][2][2][2];
			try {
				Scanner sc = new Scanner(trainfd);
				if(sc.hasNext()) {
					sc.nextLine();
				}
				while(sc.hasNext()) {
					String[] tmp = sc.nextLine().split(",");
					int parentStatus = -1;
					if(tmp[firstAttrIndex].equals(labels[firstAttrIndex][0])) {
						parentStatus = 0;
					} else {
						parentStatus = 1;
					}
					for(int i = 0; i < tmp.length - 1; i++) {
						if(i == firstAttrIndex) {
							continue;
						} else {
							if(tmp[i].equals(labels[i][0])) {
								secLabels_count[i][parentStatus][0]++;
								if(tmp[tmp.length - 1].equals(labels[tmp.length-1][0])) {
									secLabels_count_result[i][parentStatus][0][0]++;
								}else {
									secLabels_count_result[i][parentStatus][0][1]++;
								}
							} else {
								secLabels_count[i][parentStatus][1]++;
								if(tmp[tmp.length - 1].equals(labels[tmp.length-1][0])) {
									secLabels_count_result[i][parentStatus][1][0]++;
								}else {
									secLabels_count_result[i][parentStatus][1][1]++;
								}
							}
						}
					}
				}
				sc.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < numberAtt - 1; i++) {
				if(i == firstAttrIndex) {
					continue;
				}
				double leftentropy11 = 0;
				double leftentropy21 = 0;

				double leftprob11 = 0;
				double leftprob21 = 0;
				double leftprob1 = 0;
				if(!leftstop) {
					leftprob11 =  (double) secLabels_count_result[i][0][0][0]
							/ (double) (secLabels_count_result[i][0][0][0] + 
									secLabels_count_result[i][0][0][1]);
					leftentropy11 = getEntropy(leftprob11);
					leftprob21 =  (double) secLabels_count_result[i][0][1][0]
							/ (double) (secLabels_count_result[i][0][1][0] + 
									secLabels_count_result[i][0][1][1]);
					leftentropy21 = getEntropy(leftprob21);
					leftprob1 = (double)secLabels_count[i][0][0] 
							/ (double)labels_count[firstAttrIndex][0];
					leftentropy = leftprob1 * leftentropy11 + (1-leftprob1) *
							leftentropy21;
				} else {
					leftentropy = 0;
				}
				double rightentropy11 = 0;
				double rightentropy21 = 0;
				double rightprob11 = 0;
				double rightprob21 = 0;
				double rightprob1 = 0;
				if(!rightstop) {
					rightprob11 =  (double) secLabels_count_result[i][1][0][0]
							/ (double) (secLabels_count_result[i][1][0][0] + 
									secLabels_count_result[i][1][0][1]);
					rightentropy11 = getEntropy(rightprob11);
					rightprob21 =  (double) secLabels_count_result[i][1][1][0]
							/ (double) (secLabels_count_result[i][1][1][0] + 
									secLabels_count_result[i][1][1][1]);
					rightentropy21 = getEntropy(rightprob21);
					rightprob1 = (double)secLabels_count[i][1][0] 
							/ (double)labels_count[firstAttrIndex][1];
					rightentropy = rightprob1 * rightentropy11 + (1-rightprob1) *
							rightentropy21;
				} else {
					rightentropy = 0;
				}
				if(!leftstop && leftentropy < secminlEntropy) {
					secminlEntropy = leftentropy;
					secAttrIndexl = i;
				}
				if(!rightstop && rightentropy < secminrEntropy) {
					secminrEntropy = rightentropy;
					secAttrIndexr = i;
				}
			}
			if(getMutual(minlEntropy, secminlEntropy) < 0.1) {
				leftstop = true;
			} else {
				depth2l = labels_name[secAttrIndexl];
			}
			if(getMutual(minrEntropy, secminrEntropy) < 0.1) {
				rightstop = true;
			} else {
				depth2r = labels_name[secAttrIndexr];
			}
			if(leftstop && rightstop) {
				secstop = true;
			}
		}

		int postiveLC = -1;
		int negativeLC = -1;
		int posIndex = -1;
		int negIndex = -1;
		if(labels[numberAtt-1][0].equals("democrat") ||
				labels[numberAtt-1][0].equals("A") || labels[numberAtt-1][0].equals("yes")) {
			postiveLC = labels_count[numberAtt-1][0];
			negativeLC = labels_count[numberAtt-1][1];
			posIndex = 0;
			negIndex = 1;
		}else {
			postiveLC = labels_count[numberAtt-1][1];
			negativeLC = labels_count[numberAtt-1][0];
			posIndex = 1;
			negIndex = 0;
		}

		System.out.println("[" + postiveLC + "+/" + negativeLC +"-]");
		if(!stop) {

			System.out.println(labels_name[firstAttrIndex].trim() + " = " + 
					labels[firstAttrIndex][0]
							+": [" + 
							conditional_labels_count[firstAttrIndex][0][posIndex] 
									+ "+/" + conditional_labels_count[firstAttrIndex][0][negIndex] + "-]");
			if(!secstop && !leftstop) {
				System.out.println("| "+labels_name[secAttrIndexl].trim() + " = " + 
						labels[secAttrIndexl][0]
								+": [" + secLabels_count_result[secAttrIndexl][0][0][posIndex]
										+ "+/" + secLabels_count_result[secAttrIndexl][0][0][negIndex] + "-]");
				System.out.println("| "+labels_name[secAttrIndexl].trim() + " = " + 
						labels[secAttrIndexl][1]
								+": [" + secLabels_count_result[secAttrIndexl][0][1][posIndex]
										+ "+/" + secLabels_count_result[secAttrIndexl][0][1][negIndex] + "-]");
			}
			System.out.println(labels_name[firstAttrIndex].trim() + " = " + 
					labels[firstAttrIndex][1]
							+": [" + 
							conditional_labels_count[firstAttrIndex][1][posIndex] 
									+ "+/" + conditional_labels_count[firstAttrIndex][1][negIndex] + "-]");
			if(!secstop && !rightstop) {
				System.out.println("| "+labels_name[secAttrIndexr].trim() + " = " + 
						labels[secAttrIndexr][0]
								+": [" + secLabels_count_result[secAttrIndexr][1][0][posIndex]
										+ "+/" + secLabels_count_result[secAttrIndexr][1][0][negIndex] + "-]");
				System.out.println("| "+labels_name[secAttrIndexr].trim() + " = " + 
						labels[secAttrIndexr][1]
								+": [" + secLabels_count_result[secAttrIndexr][1][1][posIndex]
										+ "+/" + secLabels_count_result[secAttrIndexr][1][1][negIndex] + "-]");
			}
		}
		double errorTrain = getError(args[0],stop, secstop, depth1, 
				depth2l, depth2r, labels[firstAttrIndex][0],
				labels[firstAttrIndex][1],labels[secAttrIndexl][0],
				labels[secAttrIndexl][1], labels[secAttrIndexr][0],
				labels[secAttrIndexr][1], leftstop, rightstop,
				conditional_labels_count[firstAttrIndex][0][posIndex],
				conditional_labels_count[firstAttrIndex][0][negIndex],
				conditional_labels_count[firstAttrIndex][1][posIndex],
				conditional_labels_count[firstAttrIndex][1][negIndex],
				secLabels_count_result[secAttrIndexl][0][0][posIndex],
				secLabels_count_result[secAttrIndexl][0][0][negIndex],
				secLabels_count_result[secAttrIndexl][0][1][posIndex],
				secLabels_count_result[secAttrIndexl][0][1][negIndex],
				secLabels_count_result[secAttrIndexr][1][0][posIndex],
				secLabels_count_result[secAttrIndexr][1][0][negIndex],
				secLabels_count_result[secAttrIndexr][1][1][posIndex],
				secLabels_count_result[secAttrIndexr][1][1][negIndex]
				);
		double errorTest = getError(args[1], stop, secstop, depth1, 
				depth2l, depth2r, labels[firstAttrIndex][0],
				labels[firstAttrIndex][1],labels[secAttrIndexl][0],
				labels[secAttrIndexl][1], labels[secAttrIndexr][0],
				labels[secAttrIndexr][1], leftstop, rightstop,
				conditional_labels_count[firstAttrIndex][0][posIndex],
				conditional_labels_count[firstAttrIndex][0][negIndex],
				conditional_labels_count[firstAttrIndex][1][posIndex],
				conditional_labels_count[firstAttrIndex][1][negIndex],
				secLabels_count_result[secAttrIndexl][0][0][posIndex],
				secLabels_count_result[secAttrIndexl][0][0][negIndex],
				secLabels_count_result[secAttrIndexl][0][1][posIndex],
				secLabels_count_result[secAttrIndexl][0][1][negIndex],
				secLabels_count_result[secAttrIndexr][1][0][posIndex],
				secLabels_count_result[secAttrIndexr][1][0][negIndex],
				secLabels_count_result[secAttrIndexr][1][1][posIndex],
				secLabels_count_result[secAttrIndexr][1][1][negIndex]);
		System.out.println("error(train): " + errorTrain);
		System.out.println("error(test): " + errorTest);
	}

	private static double getEntropy(double probability) {
		double entropy = 0;
		if (probability == 0) {
			entropy = ((1-probability) * Math.log(1 / (1-probability)) 
					/ Math.log(2)); 
		} else if (probability == 1) {
			entropy = (probability * Math.log(1/probability) / Math.log(2)); 
		}else {
			entropy = (probability * Math.log(1/probability) / Math.log(2)) 
					+ ((1-probability) * Math.log(1 / (1-probability)) 
							/ Math.log(2));
		}	
		return entropy;
	}

	private static double getMutual(double parentEntropy, double entropy) {
		double mutual = parentEntropy - entropy;
		return mutual;
	}

	private static double getError(String filename, boolean stop,
			boolean secstop, String firstAtt
			, String secAttl, String secAttr, String firstAttL1, String firstAttL2,
			String secAttL1l, String secAttL2l,String secAttL1r, String secAttL2r , boolean leftstop,
			boolean rightstop,
			int firstAtt1pos, int firstAtt1neg, 
			int firstAtt2pos, int firstAtt2neg, int secAttl1pos, 
			int secAttl1neg, int secAttl2pos, int secAttl2neg, 
			int secAttr1pos, int secAttr1neg, int secAttr2pos,
			int secAttr2neg) {
		double total = 0;
		double error = -1;
		double wrong_count = 0;
		int correct_result = -1;
		File checkFile = new File(filename);
		int firstAttIndex = -1;
		int secAttIndexr = -1;
		int secAttIndexl = -1;
		try {
			Scanner sc = new Scanner(checkFile);
			if(sc.hasNextLine()) {
				String[] tmp = sc.nextLine().split(",");
				for(int i = 0; i<tmp.length; i++) {
					if(tmp[i].equals(firstAtt)) {
						firstAttIndex = i;
					} 
					if (tmp[i].equals(secAttl)) {
						secAttIndexl = i;
					}
					if (tmp[i].equals(secAttr)) {
						secAttIndexr = i;
					}
				}
			}
			while(sc.hasNextLine()) {
				total++;
				String[] tmp = sc.nextLine().split(",");
				if(tmp[tmp.length - 1].equals("democrat") ||
						tmp[tmp.length - 1].equals("A") || 
						tmp[tmp.length - 1].equals("yes") ) {
					correct_result = 1;
				} else {
					correct_result = 0;
				}
				if(!stop) {
					if(tmp[firstAttIndex].equals(firstAttL1)) {
						if(leftstop) {
							if(firstAtt1pos > firstAtt1neg) {
								if(correct_result == 0) {
									wrong_count++;
								}
							} else {
								if(correct_result == 1) {
									wrong_count++;
								}
							}
						} else {
							if(!secstop) {
								if(tmp[secAttIndexl].equals(secAttL1l)) {
									if(secAttl1pos > secAttl1neg) {
										if(correct_result == 0) {
											wrong_count++;
										}
									} else {
										if(correct_result == 1) {
											wrong_count++;
										}
									}
								} else {
									if(secAttl2pos > secAttl2neg) {
										if(correct_result == 0) {
											wrong_count++;
										}
									} else {
										if(correct_result == 1) {
											wrong_count++;
										}
									}
								}
							}
						}
					} else {
						if(rightstop) {
							if(firstAtt2pos > firstAtt2neg) {
								if(correct_result == 0) {
									wrong_count++;
								}
							} else {
								if(correct_result == 1) {
									wrong_count++;
								}
							}
						} else {
							if(!secstop) {
								if(tmp[secAttIndexr].equals(secAttL1r)) {
									if(secAttr1pos > secAttr1neg) {
										if(correct_result == 0) {
											wrong_count++;
										}
									} else {
										if(correct_result == 1) {
											wrong_count++;
										}
									}
								} else {
									if(secAttr2pos > secAttr2neg) {
										if(correct_result == 0) {
											wrong_count++;
										}
									} else {
										if(correct_result == 1) {
											wrong_count++;
										}
									}
								}
							}
						}
					}
				}
			}
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		error = wrong_count / (double) total;
		return error;
	}

}
