package edu.uniBonn.softMargingSVM.SVMLib.Kernel;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.softMargingSVM.Util.MathOperation;

public class LinearKernelFunction implements baseKernelFunction {

	@Override
	public double computeKernelFunction(dataExample x, dataExample y) {
		// TODO Auto-generated method stub
		return MathOperation.dot(x, y);
	}
	

}
