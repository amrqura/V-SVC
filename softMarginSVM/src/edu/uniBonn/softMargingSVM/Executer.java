package edu.uniBonn.softMargingSVM;

import java.util.Scanner;

import javax.swing.text.Position.Bias;

import edu.uniBonn.SoftMarginSVM.InputReader.dataReader;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataTable;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.BinaryModel;
import edu.uniBonn.softMargingSVM.SVMLib.SVMTrainer;
import edu.uniBonn.softMargingSVM.SVMLib.svmConfiguration;
import edu.uniBonn.softMargingSVM.SVMLib.svm_model;
import edu.uniBonn.softMargingSVM.Util.SVMScaler;


public class Executer {

	private final static String USAGE = "Usage: [datafile] [C=Penality Value] [eps epsilonValue] [delimiter] [Comma separated field Names] \n For Exaple: Executer \"dataFile.txt \" 0.3, \",\", \" age,Salary \" *";

	// main function to execute the algorithm
	public static void main(String[] args) {
		commmandLineAttributes attributeReaders;
		try
		{
			if(args==null || args.length<5)
				throw new Exception();
			attributeReaders=new commmandLineAttributes();
			attributeReaders.readCommandLineAttributes(args);
			// read the data
			System.out.println("reading the Data");
			dataTable data=dataReader.readDataFromCSV(attributeReaders.getDataFileName(), attributeReaders.getDelimiter(),attributeReaders.getInterestedFieldPositions());
			System.out.println("scaling the data");
			SVMScaler scaler=new SVMScaler();
			Scanner in = new Scanner(System.in);
			data=scaler.getScaledVersion(data);
			int choise;
			while(true)
			{
				System.out.println("trainging the Data ....");
				System.out.println("which Kernel function you want to use? please Enter your choise");
				System.out.println("1- Linear Kernel function");
				System.out.println("2- polynomial Kernel function");
				System.out.println("3- RBF kernel function");
				System.out.println("4- sigmoid Kernel function");
				choise = in.nextInt();
				if(choise>=1 && choise <=4)
					break;
				else
					System.out.println("wrong choise , please enter the choise again");
			}
			svmConfiguration config=new svmConfiguration();
			config.svm_type=choise;
			config.eps=(float)attributeReaders.getEpsilon();
			config.cache_size=attributeReaders.getCashSize();
			
			SVMTrainer trainer=new SVMTrainer();
			config.C=attributeReaders.getPenaltyValue();
			
			BinaryModel model=trainer.trainData(data, config);
			
			
			
			// start scaling the data
			
		}catch(Exception ex)
		{
			ex.printStackTrace();
			
			System.err.println(USAGE);
			
		}
		
		
	}
}
