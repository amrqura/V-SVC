package edu.uniBonn.softMargingSVM.SVMLib.Kernel;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.softMargingSVM.Util.MathOperation;

public class PolynomialKernelFunction extends GammaKernelFunctions {

	public int degree;
	public float coef0;
	
	public PolynomialKernelFunction(int degree, float gamma, float coef0) 
	{
	super(gamma);
	if (degree < 0)
		{
			//throw new Exception("degree of polynomial kernel < 0");
			System.err.println("degree of polynomial kernel < 0");
		}

	this.degree = degree;
	this.coef0 = coef0;
	}
	
	
	@Override
	public double computeKernelFunction(dataExample x, dataExample y) {
		// TODO Auto-generated method stub
		return MathOperation.powi(gamma * MathOperation.dot(x, y) + coef0, degree);
	}

}
