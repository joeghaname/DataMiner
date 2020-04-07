package project1;

import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MainProject {

	public static void main(String[] args) throws IOException {
		 
		try
		{	
			Scanner scan = new Scanner(new File("retail.txt"));
			System.out.println("File Found!");
			
			int total = 88162;
			
			// Calculating the amount of data to sample
			float userPercent = 100;
			int amountOfData = (int) Math.floor((userPercent/100)*total);
			
			// Calculating the amount of support needed
			float userSupport = 1;
			int support = (int) Math.floor((userSupport/100)*amountOfData); // Example: 1% Support = 881 instances
			
			ArrayList<ArrayList<Integer>> officialData = PreProcessor(scan, total);
			
			PrintWriter writer = new PrintWriter(new FileOutputStream(new File("runTimes-APriori.txt"), true));
			
			// A-Priori
			long startTime = System.currentTimeMillis(); // CPU Timer START 
			APriori(officialData, support, amountOfData);
			long taskTime = System.currentTimeMillis() - startTime; // CPU Timer END
			writer.append("\nSupport = " +userSupport +" Percent = "+userPercent  +"% RUNTIME = "+taskTime);
			
			writer.close();
			
			PrintWriter writer1 = new PrintWriter(new FileOutputStream(new File("runTimes-PCY.txt"), true));
			
			/* PCY // ** TESTING PURPOSES: UNCOMMENT TO TEST PCY ALGORITHM **
			long startTime1 = System.currentTimeMillis(); // CPU Timer START 
			PCY(officialData, support, amountOfData);
			long taskTime1 = System.currentTimeMillis() - startTime1; // CPU Timer END
			writer1.append("\nSupport = " +userSupport +" Percent = "+userPercent  +"% RUNTIME = "+taskTime1);
			*/
			writer1.close();
			
			
			/* TESTING PURPOSES
			for(int i = 0; i < 88162; i++) {
				System.out.print("Line "+i+": ");
				for(int j = 0; j < officialData.get(i).size(); j++) {
					System.out.print(officialData.get(i).get(j) + " ");
					
				}
				System.out.println();
			}
			*/
			
			// System.out.println("System Time Consumed by APriori: " +taskTime);
			// System.out.println("System Time Consumed by PCY: " +taskTime1);
			
		}
		catch (FileNotFoundException e)
		{
			System.out.println("File not found!");
		}
		
	}
	
	
	public static ArrayList<ArrayList<Integer>> PreProcessor(Scanner scan, int total) throws FileNotFoundException {
		
		ArrayList<String> dataSet = new ArrayList<String>();
		
		int[] rowSize = new int[88162]; // array to save the amount of items of each basket
		
		Scanner scan2 = new Scanner(new File("retail.txt"));
		while(scan2.hasNextLine())
		{
			dataSet.add(scan2.nextLine());	// Scans in each line as strings into dataSet ArrayList
		}
		
		scan2.close();
		
		// Calculates how many items are in each basket
		for(int i = 0; i < total; i++) {
			String[] tempRow = dataSet.get(i).split(" ");
				rowSize[i] = tempRow.length;
		}
		
		Scanner scan3 = new Scanner(new File("retail.txt"));
		
		ArrayList<ArrayList<Integer>> outside = new ArrayList<ArrayList<Integer>>(); // outer arrayList which holds all data
		ArrayList<Integer> inside = new ArrayList<Integer>(); // temporary arrayList variable for transferring each row of data 
 		
		// Process for saving all data in an (integer) ArrayList (of ArrayLists)
		for(int i = 0; i < total; i++) {
			for(int j = 0; j < rowSize[i]; j++) {
					inside.add(scan3.nextInt());
			}
			outside.add(inside);
			inside = new ArrayList<Integer>();
		}
		
		scan3.close();
		return outside;
	}
	
	// PCY Algorithm
	public static void PCY(ArrayList<ArrayList<Integer>> outside, int support, int amountOfData) throws FileNotFoundException{
		
		
		int[] arrCount = new int[20000];	// array for frequencies of each item
		ArrayList<Integer> freCount = new ArrayList<Integer>();
		
		int hashSize = 20000; // test with 20000
		
		Map<Integer, Integer> hashPCY = new HashMap<Integer, Integer>();
		
		// Retrieving all the frequencies of each item
		for(int i = 0; i < amountOfData; i++) {
			for(int j = 0; j < outside.get(i).size(); j++) {
				arrCount[outside.get(i).get(j)]++;
			}
		}
				
		// Saving the frequent items in freCount arrayList
		for(int i = 0; i < arrCount.length; i++) {
			if(arrCount[i] > support) {
				freCount.add(i);
			}
		}
		
		// PASS 1 - Part 1: Hashing the pairs into buckets
		for(int basket = 0; basket < amountOfData; basket++) {
			for(int i = 0; i < outside.get(basket).size(); i++) {
				for(int j = i + 1; j < outside.get(basket).size(); j++) {
					int itemI = outside.get(basket).get(i);
					int itemJ = outside.get(basket).get(j);
					
					int hashValue = (itemI + itemJ) % hashSize;
					
					if(hashPCY.containsKey(hashValue)) {
						int num = hashPCY.get(hashValue);
						num++;
						hashPCY.put(hashValue, num);
					}
					else {
						hashPCY.put(hashValue, 1);
					}
				}
			}
		}
		
		// PASS 1 - Part 2: Converting hashmap to bitmap 
		Map<Integer, Boolean> bitMap = new HashMap<Integer, Boolean>();
		
		for(Integer key: hashPCY.keySet()) {
			if(hashPCY.get(key) > support) {
				bitMap.put(key, true);
			}
			else {
				bitMap.put(key, false);
			}
		}
		
		// PASS 2
		
		HashMap<Integer, Integer> freqHash = new HashMap<Integer, Integer>();
		
		ArrayList<ArrayList<Integer>> freqPairs = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> tempPair = new ArrayList<Integer>();

			for(int i = 0; i < freCount.size(); i++) {
				for(int j = i + 1; j < freCount.size(); j++) {
					
					int itemI = freCount.get(i);
					int itemJ = freCount.get(j);
					
					int freqHashValue = (itemI+itemJ) % hashSize;
					
					if(bitMap.get(freqHashValue)) {
						tempPair.add(itemI);
						tempPair.add(itemJ);
						freqPairs.add(tempPair);
						tempPair = new ArrayList<Integer>();
					}
				}
			}
		/*	** TESTING PURPOSES: CHECKING FOR PAIRS IN THE CONSOLE
		for(int i = 0; i < freqPairs.size(); i++) {
			System.out.println("Pair #"+i+": "+freqPairs.get(i).get(0) +" "+freqPairs.get(i).get(1));
		}
		*/
	}
	
	// A-Priori Algorithm
	public static void APriori(ArrayList<ArrayList<Integer>> outside, int support, int amountOfData) throws FileNotFoundException
	{
	
		int[] arrCount = new int[20000];	// array for frequencies of each item
		int[][] frePair = new int[2500][2];
		ArrayList<Integer> freCount = new ArrayList<Integer>();
		
		// PASS 1 - Part 1: Retrieving all the frequencies of each item
		for(int i = 0; i < amountOfData; i++) {
			for(int j = 0; j < outside.get(i).size(); j++) {
				arrCount[outside.get(i).get(j)]++;
			}
		}
		
		// PASS 1 - Part 2: Checking if it is a frequent item
		for(int i = 0; i < arrCount.length; i++) {
			if(arrCount[i] > support) {
				freCount.add(i);
			}
		}
		
		// ** TESTING PURPOSES: Prints the frequent items **
		// System.out.println("The Frequent items are: ");
		/*
		 PrintWriter writer2 = new PrintWriter("results-FreqItems.txt");
		  for(int i = 0; i < freCount.size(); i++)
		{
			//System.out.println(freCount.get(i));
			writer2.println("Freq.Item No." + (i+1) +": "+freCount.get(i));		
		}
		writer2.close();
		 */
		
		PrintWriter writer = new PrintWriter("results-APriori.txt");
		
		// PASS 2: Search algorithm for looking for frequent pairs in each basket
		int numOfPairs = 0;
		boolean isPresent = false;
		
		for(int i = 0; i < amountOfData; i++) {
				
			for(int j = 0; j < freCount.size(); j++)
				
				if(outside.get(i).contains(freCount.get(j))) {
					
					for(int k = freCount.size() - 1; k > j; k--) {
						
						if(outside.get(i).contains(freCount.get(k)) && outside.get(i).contains(freCount.get(j))) {
							
							// Checking if pair is already present in the frePair array
							for(int l = 0; l < 2450; l++) {
								
								if(((frePair[l][0] == freCount.get(j) && frePair[l][1] ==  freCount.get(k)) ||  (frePair[l][1] == freCount.get(j) && frePair[l][0] ==  freCount.get(k)))) {
									isPresent = true;
								}
							}
							
							// Saving the pairs in frePair
							if(!isPresent) {
								frePair[numOfPairs][0] = freCount.get(j);
								frePair[numOfPairs][1] = freCount.get(k);
								writer.println("Pair No.: "+numOfPairs +" --> " +frePair[numOfPairs][0] +" "+frePair[numOfPairs][1]);
								numOfPairs++; // Increments the number of pairs in variable (numOfPairs) after writing to file
							}
							
							isPresent = false;
							
					}
				}
			}
		}
		writer.close();	
	}
}


