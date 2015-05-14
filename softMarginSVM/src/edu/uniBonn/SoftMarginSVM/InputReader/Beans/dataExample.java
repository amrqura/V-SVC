package edu.uniBonn.SoftMarginSVM.InputReader.Beans;

import java.util.ArrayList;
import java.util.List;

public class dataExample {

	public List<Double> getExampleData() {
		return exampleData;
	}

	public void setExampleData(List<Double> exampleData) {
		this.exampleData = exampleData;
	}

	public dataExample() {
		// TODO Auto-generated constructor stub
	}
	public dataExample(List<String> param)
	{
		this.exampleData=new ArrayList<Double>();
		
		for(String tmpData:param)
			if(isNumeric(tmpData))
				exampleData.add(Double.parseDouble(tmpData));
			else
				exampleData.add(Double.NaN);
		
		
	}
	private List<Double> exampleData;
	
	public  boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
}
