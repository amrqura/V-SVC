package edu.uniBonn.softMargingSVM.Util;

import java.util.Arrays;
import java.util.Collection;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.supportVector;
import edu.uniBonn.softMargingSVM.SVMLib.Kernel.baseKernelFunction;


// normally this matrix should be PSD = Positive Semi Definite
public class KernelMatrix implements QMatrix{

	private baseKernelFunction kernel;

	public KernelMatrix(baseKernelFunction function, int numExamples, int cacheRows) {
		// TODO Auto-generated constructor stub
		this.kernel=function;
		this.cache = new RecentActivitySquareCache(numExamples, cacheRows);
		
	}
	
	public float computeValue(supportVector a, supportVector b)
	{
		// the kernel matrix should be positive semi definite
		// the diagoal should equals (y_i * y_j) * K(x_i*x_j) 
		return (float) (((a.targetValue == b.targetValue) ? 1 : -1) * kernel.computeKernelFunction(a.point, b.point));
	}

	@Override
	public float evaluateDiagonal(supportVector a) {
		// TODO Auto-generated method stub
		return cache.getDiagonal(a);
	}

	private RecentActivitySquareCache cache;

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

	
	
	private class RecentActivitySquareCache {
		// ------------------------------ FIELDS ------------------------------

		public final static float NOTCACHED = Float.NEGATIVE_INFINITY;
		float[][] data;

		float[] diagonal;

		int maxCachedRank;

		long hits = 0;
		long misses = 0;
		long widemisses = 0;
		long diagonalhits = 0;
		long diagonalmisses = 0;

		// --------------------------- CONSTRUCTORS ---------------------------

		public RecentActivitySquareCache(int numExamples, int cacheRows) {
			// how big should the cache really be
			maxCachedRank = Math.min(numExamples, cacheRows);

			// PERF maybe we don't need to preallocate the whole thing?

			// allocate square cache.
			data = new float[maxCachedRank][];
			for (int i = 0; i < maxCachedRank; i++) {
				data[i] = new float[maxCachedRank];
				Arrays.fill(data[i], NOTCACHED);
			}

			// allocate diagonal. Redundant with the square cache, but this way it
			// can sit in the processor cache sequentially.
			diagonal = new float[numExamples];
			Arrays.fill(diagonal, NOTCACHED);
		}

		// ------------------------ CANONICAL METHODS ------------------------

		public String toString() {
			return "QMatrix hits = "
					+ hits
					+ ", misses = "
					+ misses
					+ ", widemisses = "
					+ widemisses
					+ ", diagonalhits = "
					+ diagonalhits
					+ ", diagonalmisses = "
					+ diagonalmisses
					+ ", rate = "
					+ (float) (hits + diagonalhits)
					/ (float) (hits + diagonalhits + misses + widemisses + diagonalmisses)
					+ ", size = " + data.length;
		}

		// -------------------------- OTHER METHODS --------------------------

		public float get(supportVector a, supportVector b) {
			// assert a != b;
			// the diagonal entries should always stay empty; use getDiagonal
			// instead
			if (a == b) {
				return getDiagonal(a);
			}

			// note the use of the redundant sv.rank field here instead of
			// idToRankMap[sv.id]. This is just for cache locality.

			if (a.supportVectorOrder >= maxCachedRank || b.supportVectorOrder >= maxCachedRank) {
				// return NOTCACHED;
				widemisses++;
				return computeValue(a, b);
			}

			float result = data[a.supportVectorOrder][b.supportVectorOrder];
			if (result == NOTCACHED) {
				result = computeValue(a, b);
				data[a.supportVectorOrder][b.supportVectorOrder] = result;
				data[b.supportVectorOrder][a.supportVectorOrder] = result;
				misses++;
			} else {
				// assert result == computeQ(a, b);
				hits++;
			}
			return result;
		}

		public float getDiagonal(supportVector a) {
			float result = diagonal[a.supportVectorOrder];
			if (result == NOTCACHED) {
				result = computeValue(a, a);
				diagonal[a.supportVectorOrder] = result;
				diagonalmisses++;
			} else {
				diagonalhits++;
			}
			return result;
		}

		/**
		 * Get the kernel value from a given SV to all those provided in the active
		 * array, computing any that are not already cached. Requires that the
		 * active array is in rank order, including all ranks from 0 to n!
		 *
		 * @param a
		 * @param active
		 * @param buf
		 */
		public void get(supportVector a, supportVector[] active, float[] buf) {
			// active array is in rank order

			if (a.supportVectorOrder >= maxCachedRank) {
				for (int i = 0; i < active.length; i++) {
					buf[i] = computeValue(a, active[i]);
					widemisses++;
				}
				return;
			}

			float[] row = data[a.supportVectorOrder];

			int cachedAndActive = Math.min(row.length, active.length);

			for (int i = 0; i < cachedAndActive; i++) {
				if (row[i] == NOTCACHED) {
					final supportVector b = active[i];
					// assert b.rank == i;
					row[i] = computeValue(a, b);

					data[b.supportVectorOrder][a.supportVectorOrder] = row[i];
					/*
					 * if (a == b) { assert (row[a.rank] == getDiagonal(a)); }
					 */
					misses++;
				} else {
					supportVector b = active[i];
					// assert b.rank == i;
					// assert row[i] == computeQ(a, b);
					hits++;
				}
			}

			System.arraycopy(row, 0, buf, 0, cachedAndActive); // PERF test whether
																// this really helps
																// (cache locality?)

			for (int i = cachedAndActive; i < active.length; i++) {
				final supportVector b = active[i];
				buf[i] = computeValue(a, b);
				widemisses++;
			}
		}

		/**
		 * pass active and inactive instead of allExamples to guarantee rank order.
		 * Requires that the active array is in rank order, including all ranks from
		 * 0 to n. Does not require that the inactive array has any particular
		 * order, but does return the results in buf to match the requested order.
		 *
		 * @param a
		 * @param active
		 * @param inactive
		 * @param buf
		 */
		public void get(supportVector a, supportVector[] active,
				supportVector[] inactive, float[] buf) {
			// first fill the active portion. Here the requested order must match
			// the rank order anyway
			get(a, active, buf);

			// then fill the inactive portion one element at a time in the requested
			// order, not the rank order

			if (a.supportVectorOrder >= maxCachedRank) {
				int i = active.length;
				for (supportVector b : inactive) {
					buf[i] = computeValue(a, b);
					widemisses++;
					i++;
				}
			} else {
				float[] row = data[a.supportVectorOrder];

				int i = active.length;
				for (supportVector b : inactive) {
					if (b.supportVectorOrder >= maxCachedRank) {
						buf[i] = computeValue(a, b);
						widemisses++;
					} else {
						if (row[b.supportVectorOrder] == NOTCACHED) {
							row[b.supportVectorOrder] = computeValue(a, b);
							data[b.supportVectorOrder][a.supportVectorOrder] = row[b.supportVectorOrder];
							misses++;
						} else {
							// assert result == computeQ(a, b);
							hits++;
						}
						buf[i] = row[b.supportVectorOrder];
					}
					i++;
				}
			}
		}

		/**
		 * Rearrange the ranks so that all active SVs come before all inactive SVs.
		 * Sort the data[][] and diagonal[] arrays according to the new ranking. The
		 * provided arrays are in the correct rank order already.
		 */
		public void maintainCache(supportVector[] active,
				supportVector[] newlyInactive) // , SolutionVector<P>[]
													// previouslyInactive)
		{
			// int rankTrav = 0;

			// the desired partitioning is provided by the arguments; the current
			// partitioning is buried inside each element as SV.rank.

			// note the ranks of the previously inactive SVs don't change, so we
			// don't have to touch them or their cache entries at all

			// the partitioning mechanism is similar to that used in quicksort:
			// find all elements of newlyInactive with prior rank less than the
			// partition rank
			// find all elements of active that with prior rank greater than the
			// partition rank
			// exchange these pairwise until done

			// it doesn't matter which pairs we choose to achieve partitioning, but
			// it may improve things some to maintain order as well as possible.
			// thus, we exchange them in order.

			// Once we're done with this we want the SVs to know their new ranks, so
			// we tkae this opportunity to reassign those.

			int partitionRank = active.length;

			int i = 0;
			int j = 0;

			while (true) {
				// find the first active element that was previously ranked too
				// poorly
				while (i < active.length && active[i].supportVectorOrder < partitionRank) {
					// this one is OK, leave it in place
					// active[i].rank = i;
					i++;
				}

				// find the first newly inactive element that was previously ranked
				// too well
				while (j < newlyInactive.length
						&& newlyInactive[j].supportVectorOrder >= partitionRank) {
					// this one is OK, leave it in place
					// newlyInactive[j].rank = partitionRank + j;
					j++;
				}

				if (i < active.length && j < newlyInactive.length) {
					// now we're pointing at the first available pair that should be
					// swapped

					swapBySolutionVector(active[i], newlyInactive[j]);
					// active[i].rank = i;
					// newlyInactive[j].rank = j + partitionRank;

					// now the pair is swapped, advance the counters past it

					i++;
					j++;
				} else {
					break;
				}
			}
			/*
			 * // Now the SV.rank entries should match the real ranks for the active
			 * list int count = 0; for (SolutionVector<P> b : active) { assert
			 * b.rank == count; count++; } for (SolutionVector<P> b : newlyInactive)
			 * { assert b.rank == count; count++; }
			 */
		}

		private void swapBySolutionVector(supportVector svA,
				supportVector svB) {
			swapByRank(svA.supportVectorOrder, svB.supportVectorOrder);
			int tmp = svA.supportVectorOrder;
			svA.supportVectorOrder = svB.supportVectorOrder;
			svB.supportVectorOrder = tmp;

			/*
			 * swapById(svA.id, svB.id); svA.rank = idToRankMap[svA.id]; svB.rank =
			 * idToRankMap[svB.id];
			 */
		}

		/*
		 * private void swapById(int idA, int idB) { swapByRank(idToRankMap[idA],
		 * idToRankMap[idB]); int tmp = idToRankMap[idA]; idToRankMap[idA] =
		 * idToRankMap[idB]; idToRankMap[idB] = tmp; }
		 */
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
			} else if (rankA < maxCachedRank) // && rankB > maxCachedRank
			{
				Arrays.fill(data[rankA], NOTCACHED);
			} else // if (rankB < maxCachedRank && rankA > maxCachedRank
			{
				Arrays.fill(data[rankB], NOTCACHED);
			}
		}
	}
}
