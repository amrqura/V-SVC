package edu.uniBonn.softMargingSVM.Util;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;


public class MathOperation {


		public static double powi(double base, int times)
			{
			assert times >= 0;
			double tmp = base, ret = 1.0f;

			for (int t = times; t > 0; t /= 2)
				{
				if (t % 2 != 0)
					{
					ret *= tmp;
					}
				tmp = tmp * tmp;
				}
			return ret;
			}

	

		public static double dotProduct(dataExample x, dataExample y)
			{

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
			 Double[] yValues=new Double[ylen];
			 yValues=(y.getExampleData().toArray(yValues));;

			double sum = 0;
			int i = 0;
			int j = 0;
			int xIndex = xIndexes[0];
			int yIndex = yIndexes[0];

			while (i < xlen && j < ylen)
				{
				if (xIndex == yIndex)
					{
					sum += (double) xValues[i] * (double) yValues[j];

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


		
}
