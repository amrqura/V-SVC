package edu.uniBonn.softMargingSVM.SVMLib;

import edu.uniBonn.softMargingSVM.SVMLib.Kernel.GaussianRBFKernelFunction;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.baseKernelFunction;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.LinearKernelFunction;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.PolynomialKernelFunction;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.SigmoidKernelFunction;

public class svmConfiguration {


	/* kernel_type */
	public static final int LINEAR = 1;
	public static final int POLY = 2;
	public static final int RBF = 3;
	public static final int SIGMOID = 4;

	public int svm_type;
	public int kernel_type;
	public int degree; // for poly
	public float gamma; // for poly/rbf/sigmoid
	public float coef0; // for poly/sigmoid

	// these are for training only
	public int cache_size; // in MB
	public float eps; // stopping criteria
	public double C; // regularization parameter
	public float V_Value; // for V-SVC 
	
	private baseKernelFunction kernel;

	
	public baseKernelFunction getKernel() {
		
		switch(svm_type)
		{
		case LINEAR:
			kernel=new LinearKernelFunction();
			break;
		case POLY:
			kernel=new PolynomialKernelFunction(degree, gamma, coef0);
			break;
		case RBF:
			
			kernel=new GaussianRBFKernelFunction(gamma);
			break;
		case SIGMOID:
			kernel=new SigmoidKernelFunction(gamma, coef0);
			break;
		default:
			kernel=new GaussianRBFKernelFunction(gamma);
			break;
			
		}
		
		return kernel;
	}


	
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
