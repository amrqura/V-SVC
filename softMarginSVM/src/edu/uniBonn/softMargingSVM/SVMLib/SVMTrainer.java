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

	public SVMModel trainData(dataTable data,svmConfiguration conf) throws Exception 
	{
		SVMModel result=new SVMModel();
		
		if(!feasible(data, conf))
		{
			System.err.println("the specified V Value is not feasible for training the data");
			throw new Exception();
			
			
			
		}
		// wighted training
		float weightedCp = (float)conf.C;
		float weightedCn = (float)conf.C;
		float linearTerm = 0f;
		
		
		
		
		Map<dataExample, Double> examples = data.getExampleAsMap();
		
		float l =examples.size();
		float sumPos = conf.V_Value * l / 2;
		float sumNeg = conf.V_Value * l / 2;
		
		
		List<supportVector> solutionVectors = new ArrayList<supportVector>(examples.size());

		for (Map.Entry<dataExample, Double> example : examples.entrySet())
		{
			
			float initAlpha;
			if (example.getValue()==1)
				{
				initAlpha = Math.min(1.0f, sumPos);
				sumPos -= initAlpha;
				}
			else
				{
				initAlpha = Math.min(1.0f, sumNeg);
				sumNeg -= initAlpha;
				}
			
			
			supportVector sv =
					new supportVector(data.getIDForExample(example.getKey()), example.getKey(), example.getValue(),
					                      linearTerm,initAlpha);
			
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

			
			float r = model.r;


			for (Map.Entry<dataExample, Double> entry : model.supportVectors.entrySet())
				{
					double value=examples.get(entry.getKey())/r;
					entry.setValue(value);
					
				}


			model.rho /= r;
			model.obj /= r * r;
			model.upperBoundPositive = 1 / r;
			model.upperBoundNegative = 1 / r;

			
			model.fillParams();

			
			return model;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		
		
		return result;
		
	}
	
	
	private boolean feasible(dataTable data,svmConfiguration conf)
	{
		List<Double> outPuts=data.getTargets();
		
		int numPos = 0;
		int numNeg = 0;
		for(Double tmpDouble:outPuts)
			if(tmpDouble==1.0)
				numPos++;
			else
				numNeg++;

		if (conf.V_Value * (numPos + numNeg) / 2 > Math.min(numPos, numNeg))
			{
			return false; 
			}

		
		return true;
	}
}
