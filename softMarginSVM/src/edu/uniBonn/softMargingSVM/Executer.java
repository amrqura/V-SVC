package edu.uniBonn.softMargingSVM;

import edu.uniBonn.SoftMarginSVM.InputReader.dataReader;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataTable;
import edu.uniBonn.softMargingSVM.Util.SVMScaler;


public class Executer {

	private final static String USAGE = "Usage: [datafile] [C=Penality Value] [delimiter] [Comma separated field Names] \n For Exaple: Executer \"dataFile.txt \" 0.3, \",\", \" age,Salary \" *";

	// main function to execute the algorithm
	public static void main(String[] args) {
		commmandLineAttributes attributeReaders;
		try
		{
			if(args==null || args.length!=4)
				throw new Exception();
			attributeReaders=new commmandLineAttributes();
			attributeReaders.readCommandLineAttributes(args);
			// read the data
			System.out.println("reading the Data");
			dataTable data=dataReader.readDataFromCSV(attributeReaders.getDataFileName(), attributeReaders.getDelimiter(),attributeReaders.getInterestedFieldPositions());
			System.out.println("scaling the data");
			SVMScaler scaler=new SVMScaler();
			data=scaler.getScaledVersion(data);
			
			// start scaling the data
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
			
			System.err.println(USAGE);
			
		}
		
		
	}
}
