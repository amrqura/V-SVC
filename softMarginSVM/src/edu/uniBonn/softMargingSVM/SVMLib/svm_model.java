package edu.uniBonn.softMargingSVM.SVMLib;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.SVMModel;


public class svm_model extends SVMModel {

	public svmConfiguration param; 
	public int l; // total #SV
	public double[] rho; // constants in decision functions (rho[k*(k-1)/2])
	public double[] probA; // pariwise probability information
	public double[] probB;

	// for classification only

	public int[] nSV; // number of SVs for each class (nSV[k])

	public double obj; 
	public double[] alphas;
}
