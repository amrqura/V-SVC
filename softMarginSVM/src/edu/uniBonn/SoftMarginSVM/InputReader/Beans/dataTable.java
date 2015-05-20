package edu.uniBonn.SoftMarginSVM.InputReader.Beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class dataTable {

	private List<DataAttributes> attributes;
	private double[] maxValues;
	private double[] minValues;
	
	
	public int getNumDim() {
		return numDim;
	}
	private int numDim;
	
	public dataTable() {
		// TODO Auto-generated constructor stub
	}
	public dataTable(int numDim){
		this.numDim=numDim;
		maxValues=new double[numDim+1]; // since 0 is for target
		minValues=new double[numDim+1];
		// initiate min and max values;
		for(int i=0;i<numDim+1;i++)
		{
			maxValues[i]=Double.MIN_VALUE;
			minValues[i]=Double.MAX_VALUE;
		}
	}
	
	public double[] getMaxValues() {
		return maxValues;
	}
	public double[] getMinValues() {
		return minValues;
	}
	public List<Double> getTargets() {
		if(targets==null)
			targets=new ArrayList<Double>();
		
		return targets;
	}
	public void setTargets(List<Double> targets) {
		this.targets = targets;
	}
	private List<Double> targets;
	
	public List<DataAttributes> getAttributes() {
		if(attributes==null)
		
			attributes= new ArrayList<DataAttributes>();
		
		
		return attributes;
	}
	public void setAttributes(List<DataAttributes> attributes) {
		this.attributes = attributes;
	}
	public List<dataExample> getExamples() {
		if(examples==null)
			examples=new ArrayList<dataExample>();
			
		return examples;
	}
	public void setExamples(List<dataExample> examples) {
		this.examples = examples;
	}
	private List<dataExample> examples;
	
	/**
	 * add the targets the existing targets
	 * @param value
	 */
	public void addTarget(Double value)
	{
		if(targets==null)
			targets=new ArrayList<Double>();
		
		
		targets.add(value);
		
	}
	
	public Map<dataExample, Integer> getIDS() {
		return IDS;
	}
	private Map<dataExample, Integer> IDS=new HashMap<dataExample, Integer>();
	
		
	public int getIDForExample(dataExample param)
	{
		int result=-1;
		if(IDS.get(param)>-1)
			result=IDS.get(param);
		return result;
	}
	
	public void addExample(List<String> param)
	{
		if(examples==null)
			examples=new ArrayList<dataExample>();
		if(IDS==null)
			IDS=new HashMap<dataExample, Integer>();
		dataExample newExample=new dataExample(param);	
		examples.add(newExample);
		IDS.put(newExample, IDS.size());
		
		// maintain the max and the min
		for(int i=0;i<param.size();i++)
		{
			double val=Double.parseDouble(param.get(i));
			if(val>maxValues[i+1])
				maxValues[i+1]=val;
			if(val<minValues[i+1])
				minValues[i+1]=val;
			
		}
		
	}
	/**
	 * return the data set and target as hashMap
	 * @return
	 */
	public Map<dataExample, Double> getExampleAsMap()
	{
		HashMap<dataExample, Double> result=new HashMap<dataExample, Double>();
		
		for(int i=0;i<examples.size();i++)
			result.put(examples.get(i), targets.get(i));
		
		
		return result;
	}
	
	
}
