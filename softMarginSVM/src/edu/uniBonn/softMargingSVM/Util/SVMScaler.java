package edu.uniBonn.softMargingSVM.Util;

import java.util.ArrayList;
import java.util.List;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataTable;

public class SVMScaler {

	// scale the data
	public dataTable getScaledVersion(dataTable param)
	{
		dataTable result=new dataTable(param.getNumDim());
		// same attributes
		result.setAttributes(param.getAttributes());
		result.setTargets(param.getTargets());
		for(int i=0;i<param.getExamples().size();i++)
		{
			List<String> tmpEntry=new ArrayList<String>();
			
			for(int j=0;j<param.getNumDim();j++)
			{
				double tmpValue=param.getExamples().get(i).getExampleData().get(j);
				double scaledTargetValue=(tmpValue-param.getMinValues()[j+1]) / (param.getMaxValues()[j+1]-param.getMinValues()[j+1]);
				tmpEntry.add(scaledTargetValue+"");
				
			}
			//result.getExamples().add(tmpExample);
			result.addExample(tmpEntry);
		
		}
		return result;
	}
}
