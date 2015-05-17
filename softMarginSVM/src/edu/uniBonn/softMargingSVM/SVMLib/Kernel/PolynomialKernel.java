package edu.uniBonn.softMargingSVM.SVMLib.Kernel;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.softMargingSVM.Util.MathSupport;

public class PolynomialKernel extends GammaKernel {

	public int degree;
	public float coef0;
	
	public PolynomialKernel(int degree, float gamma, float coef0) 
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
	public double evaluate(dataExample x, dataExample y) {
		// TODO Auto-generated method stub
		return MathSupport.powi(gamma * MathSupport.dot(x, y) + coef0, degree);
	}

}
