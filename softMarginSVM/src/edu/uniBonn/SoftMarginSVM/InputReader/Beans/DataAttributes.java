package edu.uniBonn.SoftMarginSVM.InputReader.Beans;

public class DataAttributes {

	private String attibuteName;
	private double minValue;
	private double maxValue;
	public DataAttributes(String param)
	{
		attibuteName=param;
	}
	
	public String getAttibuteName() {
		return attibuteName;
	}
	public void setAttibuteName(String attibuteName) {
		this.attibuteName = attibuteName;
	}
	public double getMinValue() {
		return minValue;
	}
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	
}
