package edu.uniBonn.softMargingSVM.SVMLib.Kernel;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;

public interface baseKernelFunction {

	double computeKernelFunction(dataExample x, dataExample y);

}
