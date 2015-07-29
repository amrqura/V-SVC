package edu.uniBonn.softMargingSVM;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.uniBonn.SoftMarginSVM.InputReader.dataReader;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataTable;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.SVMModel;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.baseKernelFunction;
import edu.uniBonn.softMargingSVM.predictor.modelReader;

/**
 * 
 * @author Amr Koura
 * class used to predict the the accuracy of learning process
 */
public class Predictor {

	/**
	 * parameter should be:
	 * 1- input file
	 * 2- model file
	 * 3- interested indecies
	 * 4- output file
	 * @param args
	 */
	public static void main(String [] args)
	{
		try
		{
			if(args==null || args.length<5)
				throw new Exception();
		
			String inputFilePath=args[0];
			String modelFilePath=args[1];
			String interestedIndecies=args[2];
			String delimiter=args[3];
			String outputFilePath=args[4];
			
			modelReader model_reader=new edu.uniBonn.softMargingSVM.predictor.modelReader(modelFilePath);
			SVMModel model=model_reader.readModel();
			
			
			BufferedReader input = new BufferedReader(new FileReader(inputFilePath));
			BufferedReader modelReader = new BufferedReader(new FileReader(modelFilePath));
			DataOutputStream output = new DataOutputStream(new FileOutputStream(outputFilePath));
			
			
			predict(model, inputFilePath,interestedIndecies,delimiter, output);
		}catch(Exception ex){
			ex.printStackTrace();
			System.exit(0);
			
		}
		
	}
	
	/**
	 * 
	 * @param model
	 * @param input
	 * @param output
	 */
	private static void predict(SVMModel model,String inputPath,String interestedPositions,String delimiter,DataOutputStream output)
	{
		// read values from input
		List<Integer> interestedPoistionsList=new ArrayList<Integer>();
		for(String tmpPosition:interestedPositions.split(","))
			interestedPoistionsList.add(Integer.parseInt(tmpPosition));
		dataTable data=dataReader.readDataFromCSV(inputPath, delimiter,interestedPoistionsList);
		int total=data.getExamples().size();
		int numOfMatch=0;
		for(int i=0;i<total;i++)
		{
			double target=data.getTargets().get(i);
			dataExample currentPoint=data.getExamples().get(i);
			if(predictOneExample(currentPoint, i, data, model)==target)
				numOfMatch++;
			
		}
		System.out.println(numOfMatch+"matches from "+total);
		
	}
	
	
	private static double predictOneExample(dataExample current,int position,dataTable all,SVMModel model)
	{
		double value=0;
		baseKernelFunction kernelFunction=model.param.getKernel();
		for(int i=0;i<all.getExamples().size();i++)
		{
			if(i!=position)
			{
				
				value+=all.getTargets().get(i)*model.alphas[i]*kernelFunction.computeKernelFunction(current,all.getExamples().get(i));
				//kernel
			}
		}
		value-=model.rho;
		if(value>=0)
		{
			return -1;
		}
		else
		{
			return 1;
		}
		
	}
	
	
	
	
	
}
