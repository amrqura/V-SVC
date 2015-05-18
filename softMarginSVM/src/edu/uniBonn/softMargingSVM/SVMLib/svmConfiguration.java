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
	public int nr_weight; // for C_SVC
	public int[] weight_label; // for C_SVC
	public double[] weight; // for C_SVC
	public double nu; // for NU_SVC, ONE_CLASS, and NU_SVR
	public double p; // for EPSILON_SVR
	public int shrinking; // use the shrinking heuristics
	public int probability; // do probability estimates
	
	
	
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


	private baseKernelFunction kernel;
	
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
