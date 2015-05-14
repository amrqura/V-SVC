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
		delimiter=param[2];
		
		for(String interestedIndex:param[3].split(","))
			interestedFieldPositions.add(Integer.parseInt(interestedIndex));
		
		
		
		
	}
}
