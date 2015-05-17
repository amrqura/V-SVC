package edu.uniBonn.softMargingSVM.SVMLib.Kernel;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.softMargingSVM.Util.MathSupport;

public class LinearKernel implements KernelFunction {

	@Override
	public double evaluate(dataExample x, dataExample y) {
		// TODO Auto-generated method stub
		return MathSupport.dot(x, y);
	}
	

}
