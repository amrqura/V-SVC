package edu.uniBonn.softMargingSVM.SVMLib.Kernel;

import java.util.Properties;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.softMargingSVM.Util.MathSupport;

public class SigmoidKernel extends GammaKernel {

	public float coef0;
	
	@Override
	public double evaluate(dataExample x, dataExample y) {
		// TODO Auto-generated method stub
		return Math.tanh(gamma * MathSupport.dot(x, y) + coef0);
	}
	
	public SigmoidKernel(Properties props)
	{
	this(Float.parseFloat(props.getProperty("gamma")), Float.parseFloat(props.getProperty("coef0")));
	}

public SigmoidKernel(float gamma, float coef0)
	{
	super(gamma);
	this.coef0 = coef0;
	}


}
