package edu.uniBonn.softMargingSVM.SVMLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataTable;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.SVMModel;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.supportVector;
import edu.uniBonn.softMargingSVM.Util.KernelMatrix;

public class SVMTrainer {

	public SVMModel trainData(dataTable data,svmConfiguration conf)
	{
		svm_model result=new svm_model();
		
		// wighted training
		float weightedCp = (float)conf.C;
		float weightedCn = (float)conf.C;
		float linearTerm = -1f;
		Map<dataExample, Double> examples = data.getExampleAsMap();
		List<supportVector> solutionVectors = new ArrayList<supportVector>(examples.size());

		for (Map.Entry<dataExample, Double> example : examples.entrySet())
		{
			supportVector sv =
					new supportVector(data.getIDForExample(example.getKey()), example.getKey(), example.getValue(),
					                      linearTerm);
			
			//sv.id = problem.getId(example.getKey());
			solutionVectors.add(sv);
		}
		KernelMatrix matrix=new KernelMatrix(conf.getKernel(),solutionVectors.size(),conf.cache_size);
		try {
			quadraticProgrammingProblemSolver s = new quadraticProgrammingProblemSolver(solutionVectors, matrix,weightedCp, weightedCn, conf.eps, true);
			SVMModel model = s.solve();
			
			model.param = conf;

			for (Map.Entry<dataExample, Double> entry : model.supportVectors.entrySet())
				{
				final dataExample key = entry.getKey();
				final double target = examples.get(key);
				if (target!=1)
					{
					entry.setValue(entry.getValue() * -1);
					}
				}

			model.fillParams();

			
			return model;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		
		
		return result;
		
	}
}
