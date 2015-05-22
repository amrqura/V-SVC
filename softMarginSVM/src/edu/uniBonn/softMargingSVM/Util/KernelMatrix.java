package edu.uniBonn.softMargingSVM.Util;

import java.util.Arrays;
import java.util.Collection;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.supportVector;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.baseKernelFunction;


// normally this matrix should be PSD = Positive Semi Definite
public class KernelMatrix implements BaseMatrix{

	private baseKernelFunction kernel;

	public KernelMatrix(baseKernelFunction function, int numExamples, int cacheRows) {
		// TODO Auto-generated constructor stub
		this.kernel=function;
		this.cache = new ActivityCache(numExamples, cacheRows);
		
	}
	
	public float computeValue(supportVector a, supportVector b)
	{
		// the kernel matrix should be positive semi definite
		// the diagonal should equals (y_i * y_j) * K(x_i*x_j) 
		return (float) (((a.targetValue == b.targetValue) ? 1 : -1) * kernel.computeKernelFunction(a.point, b.point));
	}

	@Override
	public float evaluateDiagonal(supportVector a) {
		// TODO Auto-generated method stub
		return cache.getDiagonal(a);
	}

	private ActivityCache cache;

	@Override
	/**
	 * will fill the array buf with the entries from the kernel matrix that corresponds to the svA position
	 */
	public void fillArrayWithSupportVectorEntries(supportVector svA, supportVector[] active, float[] buf) {
		// TODO Auto-generated method stub
		//System.out.println("getQ");
		cache.get(svA, active, buf);
		
	}

	@Override
	public void getQ(supportVector svA, supportVector[] active,
			supportVector[] inactive, float[] buf) {
		// TODO Auto-generated method stub

		cache.get(svA, active, inactive, buf);

	}

	@Override
	public void initOrders(Collection<supportVector> allExamples) {
		// TODO Auto-generated method stub
		int c = 0;
		for (supportVector a : allExamples)
			{
			a.supportVectorOrder = c++;
			}
		
	}

	@Override
	public void maintainCache(supportVector[] active,
			supportVector[] newlyInactive) {
		// TODO Auto-generated method stub

		cache.maintainCache(active, newlyInactive);
		
	}

	@Override
	public String perfString() {
		// TODO Auto-generated method stub
		return cache.toString();

	}

	
	
	private class ActivityCache {

		public final static float NOTCACHED = Float.NEGATIVE_INFINITY;
		float[][] data;

		float[] diagonal;

		int maxCachedRank;

		


		public ActivityCache(int numExamples, int cacheRows) {
			maxCachedRank = Math.min(numExamples, cacheRows);

	
			data = new float[maxCachedRank][];
			for (int i = 0; i < maxCachedRank; i++) {
				data[i] = new float[maxCachedRank];
				Arrays.fill(data[i], NOTCACHED);
			}

			
			diagonal = new float[numExamples];
			Arrays.fill(diagonal, NOTCACHED);
		}

		

		public float get(supportVector a, supportVector b) {
			
			if (a == b) {
				return getDiagonal(a);
			}

			

			if (a.supportVectorOrder >= maxCachedRank || b.supportVectorOrder >= maxCachedRank) {
				
				return computeValue(a, b);
			}

			float result = data[a.supportVectorOrder][b.supportVectorOrder];
			if (result == NOTCACHED) {
				result = computeValue(a, b);
				data[a.supportVectorOrder][b.supportVectorOrder] = result;
				data[b.supportVectorOrder][a.supportVectorOrder] = result;
			} 
			return result;
		}

		public float getDiagonal(supportVector a) {
			float result = diagonal[a.supportVectorOrder];
			if (result == NOTCACHED) {
				result = computeValue(a, a);
				diagonal[a.supportVectorOrder] = result;
			} 
			return result;
		}

		
		public void get(supportVector a, supportVector[] active, float[] buf) {
			// active array is in rank order

			if (a.supportVectorOrder >= maxCachedRank) {
				for (int i = 0; i < active.length; i++) {
					buf[i] = computeValue(a, active[i]);
				}
				return;
			}

			float[] row = data[a.supportVectorOrder];

			int cachedAndActive = Math.min(row.length, active.length);

			for (int i = 0; i < cachedAndActive; i++) {
				if (row[i] == NOTCACHED) {
					final supportVector b = active[i];
					
					row[i] = computeValue(a, b);

					data[b.supportVectorOrder][a.supportVectorOrder] = row[i];
				
				} else {
					supportVector b = active[i];
					
					
				}
			}

			System.arraycopy(row, 0, buf, 0, cachedAndActive); 

			for (int i = cachedAndActive; i < active.length; i++) {
				final supportVector b = active[i];
				buf[i] = computeValue(a, b);
			}
		}

		
		public void get(supportVector a, supportVector[] active,
				supportVector[] inactive, float[] buf) {
		
			get(a, active, buf);

			if (a.supportVectorOrder >= maxCachedRank) {
				int i = active.length;
				for (supportVector b : inactive) {
					buf[i] = computeValue(a, b);
					i++;
				}
			} else {
				float[] row = data[a.supportVectorOrder];

				int i = active.length;
				for (supportVector b : inactive) {
					if (b.supportVectorOrder >= maxCachedRank) {
						buf[i] = computeValue(a, b);
					} else {
						if (row[b.supportVectorOrder] == NOTCACHED) {
							row[b.supportVectorOrder] = computeValue(a, b);
							data[b.supportVectorOrder][a.supportVectorOrder] = row[b.supportVectorOrder];
						} 
						buf[i] = row[b.supportVectorOrder];
					}
					i++;
				}
			}
		}

		
		public void maintainCache(supportVector[] active,
				supportVector[] newlyInactive) 
		{
			

			int partitionRank = active.length;

			int i = 0;
			int j = 0;

			while (true) {
				
				while (i < active.length && active[i].supportVectorOrder < partitionRank) {
					
					i++;
				}

				
				while (j < newlyInactive.length
						&& newlyInactive[j].supportVectorOrder >= partitionRank) {
					
					j++;
				}

				if (i < active.length && j < newlyInactive.length) {
					

					swapBySolutionVector(active[i], newlyInactive[j]);
					

					i++;
					j++;
				} else {
					break;
				}
			}
			
		}

		private void swapBySolutionVector(supportVector svA,
				supportVector svB) {
			swapByRank(svA.supportVectorOrder, svB.supportVectorOrder);
			int tmp = svA.supportVectorOrder;
			svA.supportVectorOrder = svB.supportVectorOrder;
			svB.supportVectorOrder = tmp;
		}

		
		private void swapByRank(int rankA, int rankB) {
			float tmp = diagonal[rankA];
			diagonal[rankA] = diagonal[rankB];
			diagonal[rankB] = tmp;

			if (rankA >= maxCachedRank && rankB >= maxCachedRank) {
				// do nothing
			} else if (rankA < maxCachedRank && rankB < maxCachedRank) {
				float[] dtmp = data[rankA];
				data[rankA] = data[rankB];
				data[rankB] = dtmp;

				for (float[] drow : data) {
					float d = drow[rankA];
					drow[rankA] = drow[rankB];
					drow[rankB] = d;
				}
			} else if (rankA < maxCachedRank) 
			{
				Arrays.fill(data[rankA], NOTCACHED);
			} else 
			{
				Arrays.fill(data[rankB], NOTCACHED);
			}
		}
	}
}
