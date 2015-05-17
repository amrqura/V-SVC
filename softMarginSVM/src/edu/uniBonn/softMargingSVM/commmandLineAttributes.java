package edu.uniBonn.softMargingSVM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class commmandLineAttributes {

	private String dataFileName;
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
	

	public double getPenaltyValue() {
		return penaltyValue;
	}

	private double penaltyValue;
	



	public List<Integer> getInterestedFieldPositions() {
		return interestedFieldPositions;
	}

	private List<Integer> interestedFieldPositions=new ArrayList<Integer>();
	
	
	public void readCommandLineAttributes(String[] param) throws Exception
	{
		dataFileName=param[0];
		penaltyValue=Double.parseDouble(param[1]);
		epsilon=Double.parseDouble(param[2]);
		delimiter=param[3];
		
		for(String interestedIndex:param[4].split(","))
			interestedFieldPositions.add(Integer.parseInt(interestedIndex));
		
		// optional , the cashe size
		if(param.length>=6)
			cashSize=Integer.parseInt(param[5]);
		else
			cashSize=100; // defualt size
		
		
		
	}
}
