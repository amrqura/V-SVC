package edu.uniBonn.softMargingSVM.SVMLib;

import edu.uniBonn.softMargingSVM.SVMLib.Kernel.GaussianRBFKernel;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.KernelFunction;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.LinearKernel;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.PolynomialKernel;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.SigmoidKernel;

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
	public double C; // for C_SVC, EPSILON_SVR and NU_SVR
	public int nr_weight; // for C_SVC
	public int[] weight_label; // for C_SVC
	public double[] weight; // for C_SVC
	public double nu; // for NU_SVC, ONE_CLASS, and NU_SVR
	public double p; // for EPSILON_SVR
	public int shrinking; // use the shrinking heuristics
	public int probability; // do probability estimates
	
	
	
	public KernelFunction getKernel() {
		
		switch(svm_type)
		{
		case LINEAR:
			kernel=new LinearKernel();
			break;
		case POLY:
			kernel=new PolynomialKernel(degree, gamma, coef0);
			break;
		case RBF:
			kernel=new GaussianRBFKernel(gamma);
			break;
		case SIGMOID:
			kernel=new SigmoidKernel(gamma, coef0);
			break;
		default:
			kernel=new GaussianRBFKernel(gamma);
			break;
			
		}
		
		return kernel;
	}


	private KernelFunction kernel;
	
	
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
