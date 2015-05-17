package edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;


import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;

public class AlphaModel {

	// used only during training, then ditched
	public Map<dataExample, Double> supportVectors;

	// more compact representation used after training
	public int numSVs;
	public dataExample[] SVs;
	public double[] alphas;

	public float rho;
	
	public void compact()
	{
	// do this first so as to make the arrays the right size below
	for (Iterator<Map.Entry<dataExample, Double>> i = supportVectors.entrySet().iterator(); i.hasNext();)
		{
		Map.Entry<dataExample, Double> entry = i.next();
		if (entry.getValue() == 0)
			{
			i.remove();
			}
		}


	// put the keys and values in parallel arrays, to free memory and maybe make things a bit faster (?)

	numSVs = supportVectors.size();
	SVs = (dataExample[]) new Object[numSVs];
	alphas = new double[numSVs];

	int c = 0;
	for (Map.Entry<dataExample, Double> entry : supportVectors.entrySet())
		{
		SVs[c] = entry.getKey();
		alphas[c] = entry.getValue();
		c++;
		}

	supportVectors = null;
	}

protected void readSupportVectors(BufferedReader reader) throws IOException
	{
	//throw new NotImplementedException();
	List<Double> alphaList = new ArrayList<Double>();
	List<SparseVector> svList = new ArrayList<SparseVector>();

	String line;
	//int lineNo = 0;
	while ((line = reader.readLine()) != null)
		{
		StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");

		//alphas[lineNo] = Float.parseFloat(st.nextToken());
		alphaList.add(Double.parseDouble(st.nextToken()));

		// ** Read directly into SparseVector instead of generic P... bah

		int n = st.countTokens() / 2;
		SparseVector p = new SparseVector(n);
		//supportVectors[lineNo] = p;
		for (int j = 0; j < n; j++)
			{
			p.indexes[j] = Integer.parseInt(st.nextToken());
			p.values[j] = Float.parseFloat(st.nextToken());
			}
		svList.add(p);
		}

	//alphas = DSArrayUtils.toPrimitiveDoubleArray(alphaList);
	alphas=new double[alphaList.size()];
	for(int i=0;i<alphaList.size();i++)
		alphas[i]=alphaList.get(i);
	
	
	SVs = (dataExample[]) svList.toArray(new dataExample[0]);


	numSVs = SVs.length;

	supportVectors = null; // we read it directly to the compact representation
	}

protected void writeSupportVectors(DataOutputStream fp) throws IOException
	{
	fp.writeBytes("SV\n");

	for (int i = 0; i < numSVs; i++)
		{
		fp.writeBytes(alphas[i] + " ");

		//	P p = supportVectors.get(i);

		fp.writeBytes(SVs[i].toString());

		/*			if (kernel instanceof PrecomputedKernel)
		   {
		   fp.writeBytes("0:" + (int) (p.values[0]));
		   }
	   else
		   {
		   for (int j = 0; j < p.indexes.length; j++)
			   {
			   fp.writeBytes(p.indexes[j] + ":" + p.values[j] + " ");
			   }
		   }*/
		fp.writeBytes("\n");
		}
	}

public void writeToStream(DataOutputStream fp) throws IOException
	{
	//super.writeToStream(fp);

	fp.writeBytes("rho " + rho + "\n");
	fp.writeBytes("total_sv " + numSVs + "\n");
	}
	
}
