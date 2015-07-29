package edu.uniBonn.softMargingSVM.predictor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.SVMModel;
import edu.uniBonn.softMargingSVM.SVMLib.svmConfiguration;

public class modelReader {
	
	
	private String modelFilePath;
	
	
	
	public modelReader(String modelFilePath) {
		// TODO Auto-generated constructor stub
		this.modelFilePath=modelFilePath;
		
	}
	
	
	
	public SVMModel readModel()
	{
		SVMModel model=new SVMModel();
		model.param=new svmConfiguration();
		
		try{
			// read header
			BufferedReader fp = new BufferedReader(new FileReader(modelFilePath));
			Properties props = new Properties();
			// read the part that before the support vectors
			props.load(new StringBufferInputStream(readUpToSVs(fp)));
			model.rho=Float.parseFloat(props.getProperty("rho"));
			model.kernelType=Integer.parseInt(props.getProperty("kernel_type"));
			model.numSVs=Integer.parseInt(props.getProperty("total_sv"));
					
			if(model.kernelType==2)  // polynomail
			{
				model.param.degree=Integer.parseInt(props.getProperty("degree"));
			}
			else if(model.kernelType==3) //RBF Kernel
			{
				model.param.gamma=Float.parseFloat(props.getProperty("Gamma"));
				
			}
			else if(model.kernelType==4) //sigmoid Kernel
			{
				model.param.gamma=Float.parseFloat(props.getProperty("Gamma"));
				model.param.coef0=Float.parseFloat(props.getProperty("coef0"));

				
			}
			readAlphasAndSVS(fp);
			model.alphas=new double[alphaList.size()];
			// fill the data
			for(int i=0;i<alphaList.size();i++)
				model.alphas[i]=alphaList.get(i);
			
			model.SVs=new dataExample[examples.size()];
			for(int i=0;i<examples.size();i++)
				model.SVs[i]=examples.get(i);
			
			fp.close();
				
			// read 
		}catch(Exception ex){
			ex.printStackTrace();
			
		}
		return model;
	}

	private static String readUpToSVs(BufferedReader reader) throws IOException
	{
	StringBuffer sb = new StringBuffer();
	while (true)
		{
		String l = reader.readLine();
		if (l.startsWith("support Vectors"))
			{
			break;
			}
		sb.append(l).append("\n");
		}
	return sb.toString();
	}
	
	List<Double> alphaList;
	List<dataExample> examples;
	private void readAlphasAndSVS(BufferedReader reader) throws IOException
	{
		alphaList = new ArrayList<Double>();
		examples=new ArrayList<dataExample>();
		String line;
		while ((line = reader.readLine()) != null)
		{
			StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

			//alphas[lineNo] = Float.parseFloat(st.nextToken());
			alphaList.add(Double.parseDouble(st.nextToken()));
			int n = st.countTokens();
			List<Double> values=new ArrayList<Double>();
			for(int i=0;i<n;i++)
			{
				values.add(Double.parseDouble(st.nextToken()));
				
			}
			dataExample tmpExample=new dataExample();
			tmpExample.setExampleData(values);
			examples.add(tmpExample);
			
		}
		
	}
	
}
