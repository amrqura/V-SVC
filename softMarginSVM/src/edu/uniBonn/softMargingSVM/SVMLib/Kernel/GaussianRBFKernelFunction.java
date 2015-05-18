package edu.uniBonn.softMargingSVM.SVMLib.Kernel;

import java.util.Properties;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;




public class GaussianRBFKernelFunction extends GammaKernelFunctions{

	private static final double explicitSumOptimized(final dataExample x,  final dataExample y)
	{
	double sum = 0;

	
	final int[] xIndexes = new int[x.getExampleData().size()];
	for(int i=0;i<x.getExampleData().size();i++)
		xIndexes[i]=i;
	
	
	final int xlen = x.getExampleData().size();
	
	final int[] yIndexes = new int[y.getExampleData().size()];
	for(int i=0;i<y.getExampleData().size();i++)
		yIndexes[i]=i;
	
	final int ylen = yIndexes.length;
	
	Double[] xValues=new Double[xlen];
	 xValues =  (x.getExampleData().toArray(xValues));;
	//xValues=
	 Double[] yValues=new Double[ylen];
	 yValues=(y.getExampleData().toArray(yValues));;
	 //yValues= 
	 

	int i = 0;
	int j = 0;
	int xIndex = xIndexes[0];
	int yIndex = yIndexes[0];


	while (xIndex != Integer.MAX_VALUE || yIndex != Integer.MAX_VALUE)
		{
		if (xIndex == yIndex)
			{
			double d = (double) xValues[i] - (double) yValues[j];
			sum += d * d;

			i++;
			if (i >= xlen)
				{
				xIndex = Integer.MAX_VALUE;
				}
			else
				{
				xIndex = xIndexes[i];
				}

			j++;
			if (j >= ylen)
				{
				yIndex = Integer.MAX_VALUE;
				}
			else
				{
				yIndex = yIndexes[j];
				}
			}
		else
			{
			try
				{
				while (xIndex > yIndex)
					{

					sum += (double) yValues[j] * (double) yValues[j];
					j++;
					yIndex = yIndexes[j];
					}
				}
			catch (ArrayIndexOutOfBoundsException e)
				{
				yIndex = Integer.MAX_VALUE;
				}

			try
				{
				while (yIndex > xIndex)
					{

					sum += (double) xValues[i] * (double) xValues[i];
					i++;
					xIndex = xIndexes[i];
					}
				}
			catch (ArrayIndexOutOfBoundsException e)
				{
				xIndex = Integer.MAX_VALUE;
				}
			
			}
		}

	return sum;
	}

//--------------------------- CONSTRUCTORS ---------------------------

public GaussianRBFKernelFunction(Properties props)
	{
	this(Float.parseFloat(props.getProperty("gamma")));
	}

public GaussianRBFKernelFunction(float gamma)
	{
	super(gamma);
	}


@Override
public String toString()
	{
	return "RBF gamma=" + gamma;
	}


public String toFileOutputString()
	{
	StringBuilder sb = new StringBuilder();
	sb.append("kernel_type rbf\n");
	sb.append("gamma " + gamma + "\n");
	return sb.toString();
	}



public double computeKernelFunction( dataExample x,  dataExample y)
	{
	
	double sum = explicitSumOptimized(x, y);  

	double result = Math.exp(-gamma * sum);
	
	return result;
	}

}
