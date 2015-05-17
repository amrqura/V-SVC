package edu.uniBonn.SoftMarginSVM.InputReader.Beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class dataExample implements Comparable<dataExample>{

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
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		// because we use the objects of this clsss in the hashMap
		final int prime = 31;
		int result=1;
		Random rand = new Random();
		for(Double tmpDouble:exampleData)
			result+=result+(prime*tmpDouble.hashCode());
		result*=super.hashCode();
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		dataExample target=(dataExample) obj;
		for(int i=0;i<exampleData.size();i++)
			if(exampleData.get(i)!=target.getExampleData().get(i))
				return false;
		return true;
		
	}

	@Override
	public int compareTo(dataExample o) {
		// TODO Auto-generated method stub
		
		for(int i=0;i<exampleData.size();i++)
		{
			if(exampleData.get(i)>o.getExampleData().get(i))
				return 1;
			if(exampleData.get(i)<o.getExampleData().get(i))
				return -1;
		}
		return 0;
	}
	
	
}
