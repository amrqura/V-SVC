package edu.uniBonn.softMargingSVM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class commmandLineAttributes {

	private String dataFileName;
	public String getModelFileName() {
		return modelFileName;
	}


	private String modelFileName;
	
	public String getDataFileName() {
		return dataFileName;
	}

	public String getDelimiter() {
		return delimiter;
	}


	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
	}


	private double epsilon;
	public int getCashSize() {
		return cashSize;
	}


	private int cashSize;
	

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	private String delimiter;
	

	public double getRegularizationParameter() {
		return regularizationParameter;
	}

	private double regularizationParameter;
	



	public List<Integer> getInterestedFieldPositions() {
		return interestedFieldPositions;
	}

	private List<Integer> interestedFieldPositions=new ArrayList<Integer>();
	
	
	public void readCommandLineAttributes(String[] param) throws Exception
	{
		dataFileName=param[0];
		modelFileName=param[1];
		regularizationParameter=Double.parseDouble(param[2]);
		epsilon=Double.parseDouble(param[3]);
		delimiter=param[4];
		
		for(String interestedIndex:param[5].split(","))
			interestedFieldPositions.add(Integer.parseInt(interestedIndex));
		
		// optional , the cashe size
		if(param.length>=7)
			cashSize=Integer.parseInt(param[6]);
		else
			cashSize=100; // defualt size
		
		
		
	}
}
