package edu.uniBonn.softMargingSVM.SVMLib.Kernel;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;

public interface KernelFunction {

	double evaluate(dataExample x, dataExample y);

}
