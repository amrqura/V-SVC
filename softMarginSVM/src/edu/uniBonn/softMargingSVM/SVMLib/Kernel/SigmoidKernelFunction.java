package edu.uniBonn.softMargingSVM.SVMLib.Kernel;


import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.softMargingSVM.Util.MathOperation;

public class SigmoidKernelFunction extends GammaKernelFunctions {

	public float coef0;
	
	@Override
	public double computeKernelFunction(dataExample x, dataExample y) {
		// TODO Auto-generated method stub
		return Math.tanh(gamma * MathOperation.dot(x, y) + coef0);
	}
	
	

public SigmoidKernelFunction(float gamma, float coef0)
	{
	super(gamma);
	this.coef0 = coef0;
	}


}
