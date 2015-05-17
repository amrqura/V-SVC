package edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import edu.uniBonn.softMargingSVM.SVMLib.svmConfiguration;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.KernelFunction;

public class BinaryModel extends AlphaModel {
	// protected final would be nice, but the Solver constructs the Model
	// without knowing about param so we have to set it afterwards.
	/**
	 * a thing that is confusing here: if a grid search was done, then the
	 * specific point that was the optimum should be recorded here. That works
	 * for binary and multiclass models when the grid search is done at the top
	 * level. But when param.gridsearchBinaryMachinesIndependently, there is no
	 * one point that makes sense. Really we should just leave it null and refer
	 * to the subsidiary BinaryModels.
	 */
	public svmConfiguration param;

	// ------------------------------ FIELDS ------------------------------


	public float obj;
	public float upperBoundPositive;
	public float upperBoundNegative;
	
	
}
