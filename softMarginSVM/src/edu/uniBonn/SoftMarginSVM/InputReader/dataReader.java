package edu.uniBonn.SoftMarginSVM.InputReader;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.DataAttributes;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataTable;

public class dataReader {

	public static dataTable readDataFromCSV(String inputPath,String delimiter,List<Integer> interestedPositions)
	{
		dataTable result=new dataTable(interestedPositions.size());
		
		List<List<String>> inputDataEntries=CSV.convertCSVStringToList(inputPath, delimiter,interestedPositions);

		// set the attributes
		List<String> attributesNames=inputDataEntries.get(0);
		int featureSize=interestedPositions.size();
		for(String tmpAttribute: attributesNames)
		{
			result.getAttributes().add(new DataAttributes(tmpAttribute));
		}
		
		// fill the databidy
		for(int i=1;i<inputDataEntries.size();i++)
		{
			//read the target value
			//result.getTargets().add(Double.parseDouble(inputDataEntries.get(i).get(0)));
			result.addTarget(Double.parseDouble(inputDataEntries.get(i).get(0)));
			// read the data entry from index 1 to the end
			//result.getExamples().add(new dataExample(inputDataEntries.get(i).subList(1, featureSize+1)));
			result.addExample(inputDataEntries.get(i).subList(1, featureSize+1));
		}
			
		return result;
	}
	
	
	
	
}
