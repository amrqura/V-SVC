package edu.uniBonn.softMargingSVM.SVMLib;

import java.util.HashMap;
import java.util.List;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.BinaryModel;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.supportVector;
import edu.uniBonn.softMargingSVM.Util.KernelMatrix;



public class BinarySolver extends Solver {

	public BinarySolver(List<supportVector> solutionVectors, KernelMatrix Q,
			float Cp, float Cn, float eps, boolean shrinking) throws Exception {
		super(solutionVectors, Q, Cp, Cn, eps, shrinking);
	}

	// -------------------------- OTHER METHODS --------------------------

	public BinaryModel solve() {
		optimize();

		BinaryModel model = new BinaryModel();

		// calculate rho
		calculate_rho(model);

		// calculate objective value

		float v = 0;
		for (supportVector svC : allExamples) {
			v += svC.alpha * (svC.G + svC.linearTerm);
		}

		model.obj = v / 2;

		model.supportVectors = new HashMap<dataExample, Double>();
		for (supportVector svC : allExamples) {
			model.supportVectors.put(svC.point, svC.alpha);
			
			
		}

		// note at this point the solution includes _all_ vectors, even if their
		// alphas are zero

		// we can't do this yet because in the regression case there are twice
		// as many alphas as vectors
		// model.compact();

		model.upperBoundPositive = Cp;
		model.upperBoundNegative = Cn;

		// ** logging output disabled for now
		// logger.info("optimization finished, #iter = " + iter);

		return model;
	}
}
