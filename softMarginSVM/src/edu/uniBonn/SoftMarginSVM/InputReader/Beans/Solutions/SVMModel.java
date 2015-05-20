package edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.softMargingSVM.SVMLib.svmConfiguration;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.baseKernelFunction;

public class SVMModel {

	public svmConfiguration param;

	public float obj;
	public float upperBoundPositive;
	public float upperBoundNegative;

	// used only during training, then ditched
	public Map<dataExample, Double> supportVectors;

	
	public int numSVs;
	public dataExample[] SVs;
	public double[] alphas;

	public float rho;
	public float r;
	

	protected void writeSupportVectors(DataOutputStream fp) throws IOException {
		fp.writeBytes("SV\n");

		for (int i = 0; i < numSVs; i++) {
			fp.writeBytes(alphas[i] + " ");

			fp.writeBytes(SVs[i].toString());

			fp.writeBytes("\n");
		}
	}

	public void writeToStream(DataOutputStream fp) throws IOException {

		fp.writeBytes("rho " + rho + "\n");
		fp.writeBytes("total_sv " + numSVs + "\n");

	}

	// write to file
	public void writeModelToFile(String model_file_name) throws IOException {
		DataOutputStream fp = new DataOutputStream(new FileOutputStream(
				model_file_name));
		writeToStream(fp);
		fp.writeBytes("SV\n");

		for (int i = 0; i < numSVs; i++) {
			fp.writeBytes(alphas[i] + " ");

			fp.writeBytes(SVs[i].toString());

			fp.writeBytes("\n");
		}

	}
	
	
	//used to fill the parameters of the model to be printed to the file
	public void fillParams() {
		for (Iterator<Map.Entry<dataExample, Double>> i = supportVectors
				.entrySet().iterator(); i.hasNext();) {
			Map.Entry<dataExample, Double> entry = i.next();
			if (entry.getValue() == 0) {
				i.remove();
			}
		}

		numSVs = supportVectors.size();
		SVs = new dataExample[numSVs];
		alphas = new double[numSVs];

		int c = 0;
		for (Map.Entry<dataExample, Double> entry : supportVectors.entrySet()) {
			SVs[c] = entry.getKey();
			alphas[c] = entry.getValue();
			c++;
		}

		supportVectors = null;
	}

}
