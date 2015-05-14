package edu.uniBonn.SoftMarginSVM.InputReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CSV {

	public static List<List<String>> convertCSVStringToList(String csvFilePath,
			String delimiter,List<Integer> interestedPositions) {

		
		// read the CSV file into list of list of string
		BufferedReader br = null;
		String line = "";
		List<List<String>> result=new ArrayList<List<String>>();
		
		try
		{
			br = new BufferedReader(new FileReader(csvFilePath));
			while ((line = br.readLine()) != null) {
			
				if(line.split(delimiter).length<3)
				{
					continue;
				}
				List<String> tmpLine=new ArrayList<String>();
				for(Integer interestedPosition:interestedPositions)
					tmpLine.add((line.split(delimiter)[interestedPosition]));
				result.add( tmpLine);
				
			}
			
			
		}catch(Exception ex){
			
			ex.printStackTrace();
		}
		return result;
		
	}

}
