package edu.uniBonn.SoftMarginSVM.InputReader.Beans;

import java.util.ArrayList;
import java.util.List;

public class dataTable {

	private List<DataAttributes> attributes;
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
	
	
}
