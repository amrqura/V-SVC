package edu.uniBonn.softMargingSVM.SVMLib;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.SVMModel;
import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.supportVector;
import edu.uniBonn.softMargingSVM.Util.QMatrix;


/**
 * An SMO algorithm
 */

public  class quadraticProgrammingProblemSolver
	{


	private static final int MAXITERATION = 50000;

	protected final static supportVector[] EMPTY_SV_ARRAY = new supportVector[0];


	QMatrix matrix;
	float[] Q_svA;
	float[] Q_svB;
	float[] Q_all;

	float eps;
	boolean unshrink = false;
	boolean shrinking;

	protected final List<supportVector> allExamples;
	protected supportVector[] active;
	protected supportVector[] inactive;
	protected final float Cp, Cn;
	protected final int numExamples;




	public SVMModel solve() {
		optimize();

		SVMModel model = new SVMModel();

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
		
		model.upperBoundPositive = Cp;
		model.upperBoundNegative = Cn;


		return model;
	}
	
	public quadraticProgrammingProblemSolver( List<supportVector> solutionVectors,  QMatrix Q, float Cp, float Cn, float eps,
	              boolean shrinking) throws Exception
		{

		if (eps <= 0)
			{
			throw new Exception("eps <= 0");
			}

		this.matrix = Q;
		this.Cp = Cp;
		this.Cn = Cn;
		this.eps = eps;
		this.shrinking = shrinking;



		this.allExamples = solutionVectors;

		this.numExamples = allExamples.size();
		Q_all = new float[numExamples];
		}


	protected void calculate_rho(SVMModel model)
		{
		int nr_free1 = 0, nr_free2 = 0;
		double ub1 = Double.POSITIVE_INFINITY, ub2 = Double.POSITIVE_INFINITY;
		double lb1 = Double.NEGATIVE_INFINITY, lb2 = Double.NEGATIVE_INFINITY;
		double sum_free1 = 0, sum_free2 = 0;


		for (supportVector sv : allExamples)
			{
			if (sv.targetValue==1)
				{
				if (sv.isLowerBound())
					{
					ub1 = Math.min(ub1, sv.G);
					}
				else if (sv.isUpperBound())
					{
					lb1 = Math.max(lb1, sv.G);
					}
				else
					{
					++nr_free1;
					sum_free1 += sv.G;
					}
				}
			else
				{
				if (sv.isLowerBound())
					{
					ub2 = Math.min(ub2, sv.G);
					}
				else if (sv.isUpperBound())
					{
					lb2 = Math.max(lb2, sv.G);
					}
				else
					{
					++nr_free2;
					sum_free2 += sv.G;
					}
				}
			}

		double r1, r2;
		if (nr_free1 > 0)
			{
			r1 = sum_free1 / nr_free1;
			}
		else
			{
			r1 = (ub1 + lb1) / 2;
			}

		if (nr_free2 > 0)
			{
			r2 = sum_free2 / nr_free2;
			}
		else
			{
			r2 = (ub2 + lb2) / 2;
			}

		
		model.r = (float) ((r1 + r2) / 2);
		model.rho = (float) ((r1 - r2) / 2);
		}

	protected int optimize()
		{
		matrix.initOrders(allExamples); // write sequential ranks for all the suppor vectors

		for (supportVector svA : allExamples)			
			{
			svA.updateAlphaStatus(Cp, Cn);			
			}


		// initialize active set (for shrinking)

		initActiveSet();

		// initialize gradient

		for (supportVector svA : allExamples)
			{
			svA.G = svA.linearTerm;
			svA.G_bar = 0;
			}
		for (supportVector svA : allExamples)
			{
			if (!svA.isLowerBound()) 
				{
			

				matrix.fillArrayWithSupportVectorEntries(svA, active, Q_svA);
				for (supportVector svB : allExamples)
					{
					svB.G += svA.alpha * Q_svA[svB.supportVectorOrder];
	
					}
				if (svA.isUpperBound()) 
					{
					for (supportVector svB : allExamples)
						{
						svB.G_bar += svA.getC(Cp, Cn) * Q_svA[svB.supportVectorOrder];
						
						}
					}
				}
			}


		// optimization step

		int iter = 0;
		int counter = Math.min(numExamples, 1000) + 1;		

		supportVector svA;
		supportVector svB;


		while (true)
			{
			// show progress and do shrinking

			if (--counter == 0)
				{
				counter = Math.min(numExamples, 1000);
				if (shrinking)
					{
					do_shrinking();
					}

			
				}
			//oldPair = pair;
			SolutionVectorPair pair = selectWorkingPair();

			if (pair.isOptimal) // pair already optimal
				{
				// reconstruct the whole gradient
				reconstructGradient();

				// reset active set size and check
				resetActiveSet();


				// 2- find a two elements working set B={i,j}
				pair = selectWorkingPair();
				if (pair.isOptimal) // pair already optimal
					{
				
					break;
					}
				else
					{
					counter = 1;
				
					}
				}
			svA = pair.svA;
			svB = pair.svB;
			
			++iter;

			if (iter > MAXITERATION)
				{
				break;
				}



			matrix.fillArrayWithSupportVectorEntries(svA, active, Q_svA);
			matrix.fillArrayWithSupportVectorEntries(svB, active, Q_svB);

			float C_i = svA.getC(Cp, Cn); //getC(i);
			float C_j = svB.getC(Cp, Cn); //getC(j);

			double old_alpha_i = svA.alpha;
			double old_alpha_j = svB.alpha;

			if (svA.targetValue != svB.targetValue)
				{
			
				float quad_coef = matrix.evaluateDiagonal(svA) + matrix.evaluateDiagonal(svB)
						+ 2 * Q_svA[svB.supportVectorOrder]; 


				if (quad_coef <= 0)
					{
					quad_coef = 1e-12f;
					}
				double delta = (-svA.G - svB.G) / quad_coef;
				double diff = svA.alpha - svB.alpha;
				// the change in the value of alpha(i) and alpha(j) depends on the difference between approxomation error
				svA.alpha += delta;
				svB.alpha += delta;

				if (diff > 0)
					{
					if (svB.alpha < 0)
						{
						svB.alpha = 0;
						svA.alpha = diff;
						}
					}
				else
					{
					if (svA.alpha < 0)
						{
						svA.alpha = 0;
						svB.alpha = -diff;
						}
					}
				if (diff > C_i - C_j)
					{
					if (svA.alpha > C_i)
						{
						svA.alpha = C_i;
						svB.alpha = C_i - diff;
						}
					}
				else
					{
					if (svB.alpha > C_j)
						{
						svB.alpha = C_j;
						svA.alpha = C_j + diff;
						}
					}
				}
			else
				{
				float quad_coef = matrix.evaluateDiagonal(svA) + matrix.evaluateDiagonal(svB)
						- 2 * Q_svA[svB.supportVectorOrder]; 


				if (quad_coef <= 0)
					{
					quad_coef = 1e-12f;
					}
				double delta = (svA.G - svB.G) / quad_coef;
				double sum = svA.alpha + svB.alpha;
				svA.alpha -= delta;
				svB.alpha += delta;

				if (sum > C_i)
					{
					if (svA.alpha > C_i)
						{
						svA.alpha = C_i;
						svB.alpha = sum - C_i;
						}
					}
				else
					{
					if (svB.alpha < 0)
						{
						svB.alpha = 0;
						svA.alpha = sum;
						}
					}
				if (sum > C_j)
					{
					if (svB.alpha > C_j)
						{
						svB.alpha = C_j;
						svA.alpha = sum - C_j;
						}
					}
				else
					{
					if (svA.alpha < 0)
						{
						svA.alpha = 0;
						svB.alpha = sum;
						}
					}
				}

			// update G

			double delta_alpha_i = svA.alpha - old_alpha_i;
			double delta_alpha_j = svB.alpha - old_alpha_j;

			if (delta_alpha_i == 0 && delta_alpha_j == 0)
				{
			
				break;
				}

		
			for (int i = 0; i < active.length; i++)
				{
				active[i].G += Q_svA[i] * delta_alpha_i + Q_svB[i] * delta_alpha_j;
				}
	



			boolean ui = svA.isUpperBound(); 
			boolean uj = svB.isUpperBound(); 
			svA.updateAlphaStatus(Cp, Cn); 
			svB.updateAlphaStatus(Cp, Cn); 


			if (ui != svA.isUpperBound()) 
				{
				matrix.getQ(svA, active, inactive, Q_all);
				if (ui)
					{
					for (supportVector svC : allExamples)
						{
						svC.G_bar -= C_i * Q_all[svC.supportVectorOrder]; 
					
						}
					}
				else
					{
					for (supportVector svC : allExamples)
						{
						svC.G_bar += C_i * Q_all[svC.supportVectorOrder]; 
					
						}
					}
				}

			if (uj != svB.isUpperBound()) 
				{				
				matrix.getQ(svB, active, inactive, Q_all);
				if (uj)
					{
					for (supportVector svC : allExamples)
						{
						svC.G_bar -= C_j * Q_all[svC.supportVectorOrder];
	
						}
					}
				else
					{
					for (supportVector svC : allExamples)
						{
						
						svC.G_bar += C_j * Q_all[svC.supportVectorOrder]; 
						
						}
					}
				}
			}

		
		return iter;		// activeSet;
		}

	protected void initActiveSet()
		{
		active = allExamples.toArray(EMPTY_SV_ARRAY);
		inactive = EMPTY_SV_ARRAY;
		Q_svA = new float[active.length];
		Q_svB = new float[active.length];
		}

	void do_shrinking()
		{
		double Gmax1 = Double.NEGATIVE_INFINITY;
		double Gmax2 = Double.NEGATIVE_INFINITY;
		double Gmax3 = Double.NEGATIVE_INFINITY;
		double Gmax4 = Double.NEGATIVE_INFINITY;

		// find maximal violating pair first
		for (supportVector sv : active)
			{
			if (!sv.isUpperBound())
				{
				if (sv.targetValue==1)
					{
					if (-sv.G > Gmax1)
						{
						Gmax1 = -sv.G;
						}
					}
				else if (-sv.G > Gmax4)
					{
					Gmax4 = -sv.G;
					}
				}
			if (!sv.isLowerBound())
				{
				if (sv.targetValue==1)
					{
					if (sv.G > Gmax2)
						{
						Gmax2 = sv.G;
						}
					}
				else if (sv.G > Gmax3)
					{
					Gmax3 = sv.G;
					}
				}
			}

		if (!unshrink && Math.max(Gmax1 + Gmax2, Gmax3 + Gmax4) <= eps * 10)
			{
			unshrink = true;
			reconstructGradient();
			resetActiveSet();
			}


		// There was an extremely messy iteration here before, but I think it served only to separate the shrinkable vectors from the unshrinkable ones.

		Collection<supportVector> activeList =
				new ArrayList<supportVector>(Arrays.asList(active)); //Arrays.asList(active);
		Collection<supportVector> inactiveList = new ArrayList<supportVector>();

		// note the ordering: newly inactive SVs go at the beginning of the inactive list, maintaining order

		for (Iterator<supportVector> iter = activeList.iterator(); iter.hasNext();)
			{
			supportVector sv = iter.next();

			if (sv.isShrinkable(Gmax1, Gmax2, Gmax3, Gmax4))
				{
				iter.remove();
				inactiveList.add(sv);
				}
			}

		active = activeList.toArray(EMPTY_SV_ARRAY);
		supportVector[] newlyInactive = inactiveList.toArray(EMPTY_SV_ARRAY);
		matrix.maintainCache(active, newlyInactive);

		// previously inactive SVs come after that
		inactiveList.addAll(Arrays.asList(inactive));
		inactive = inactiveList.toArray(EMPTY_SV_ARRAY);
		}

	
	void reconstructGradient()
		{
		if (active.length == numExamples)
			{
			return;
			}

		int nr_free = 0;


		for (supportVector sv : inactive)
			{
			sv.G = sv.G_bar + sv.linearTerm;
			}

		for (supportVector sv : active)
			{
			if (sv.isFree())
				{
				nr_free++;
				}
			}

		int activeSize = active.length;


		if (nr_free * numExamples > 2 * activeSize * (numExamples - activeSize))
			{
			for (supportVector svA : inactive)
				{
				//float[] Q_i = Q.getQ(i, activeSize);
				matrix.fillArrayWithSupportVectorEntries(svA, active, Q_svA);
				for (supportVector svB : active)
					{
					if (svB.isFree()) //is_free(j))
						{
						//assert Q_svA[svB.rank] == Q.evaluate(svA, svB);
						svA.G += svB.alpha * Q_svA[svB.supportVectorOrder];
					
						}
					}
				}
			}
		else
			{
			for (supportVector svA : active)
				{
				if (svA.isFree()) 
					{
					
					matrix.getQ(svA, active, inactive, Q_all);
					for (supportVector svB : inactive)
						{
						//assert Q_all[svB.rank] == Q.evaluate(svA, svB);
						svB.G += svA.alpha * Q_all[svB.supportVectorOrder];
						
						}
					}
				}
			}
		}

	protected void resetActiveSet()
		{
		active = allExamples.toArray(EMPTY_SV_ARRAY);
		Arrays.sort(active);
		inactive = EMPTY_SV_ARRAY;
		Q_svA = new float[active.length];
		Q_svB = new float[active.length];
		}

	private SolutionVectorPair selectWorkingPair()
		{
		// we want to find the pair of points that maximize the difference in classification error is largest
		// we want a pair of points that make the decease of objective function maximized
		// return i,j such that y_i = y_j and
				// i: maximizes -y_i * grad(f)_i, i in I_up(\alpha)
				// j: minimizes the decrease of obj value
				//    (if quadratic coefficeint <= 0, replace it with tau)
				//    -y_j*grad(f)_j < -y_i*grad(f)_i, j in I_low(\alpha)

				double Gmaxp = Float.NEGATIVE_INFINITY;
				double Gmaxp2 = Float.NEGATIVE_INFINITY;

				double Gmaxn = Float.NEGATIVE_INFINITY;
				double Gmaxn2 = Float.NEGATIVE_INFINITY;

				supportVector GmaxnSV = null;
				supportVector GmaxpSV = null;

				supportVector GminSV = null;

				double obj_diff_min = Float.POSITIVE_INFINITY;

				// we want to iterate over all points that violate KKT condition
				// loop through points that neighter in upper bound nor lower bound
				for (supportVector sv : active)
					{
					if (sv.targetValue==1)
						{
						if (!sv.isUpperBound())
							{
							if (-sv.G >= Gmaxp)
								{
								Gmaxp = -sv.G;
								GmaxpSV = sv;
								}
							}
						}
					else
						{
						if (!sv.isLowerBound())
							{
							if (sv.G >= Gmaxn)
								{
								Gmaxn = sv.G;
								GmaxnSV = sv;
								}
							}
						}
					}

				matrix.fillArrayWithSupportVectorEntries(GmaxpSV, active, Q_svA);
				matrix.fillArrayWithSupportVectorEntries(GmaxnSV, active, Q_svB);

				for (supportVector sv : active)
					{
					/* in the second iteration , we want to find j 
					that will minimize the objective function
					
					*/
					if (sv.targetValue==1)
						{
						if (!sv.isLowerBound())
							{
							double grad_diff = Gmaxp + sv.G;
							if (sv.G >= Gmaxp2)
								{
								Gmaxp2 = sv.G;
								}
							if (grad_diff > 0)
								{
								double obj_diff;
								double quad_coef = matrix.evaluateDiagonal(GmaxpSV) + matrix.evaluateDiagonal(sv)
										- 2.0f * Q_svA[sv.supportVectorOrder]; 
								if (quad_coef > 0)
									{
									obj_diff = -(grad_diff * grad_diff) / quad_coef;
									}
								else
									{
									obj_diff = -(grad_diff * grad_diff) / 1e-12f;
									}

								if (obj_diff <= obj_diff_min)
									{
									GminSV = sv;
									obj_diff_min = obj_diff;
									}
								}
							}
						}
					else
						{
						if (!sv.isUpperBound())
							{
							double grad_diff = Gmaxn - sv.G;
							if (-sv.G >= Gmaxn2)
								{
								Gmaxn2 = -sv.G;
								}
							if (grad_diff > 0)
								{
								double obj_diff;


								double quad_coef = matrix.evaluateDiagonal(GmaxnSV) + matrix.evaluateDiagonal(sv)
										- 2.0f * Q_svB[sv.supportVectorOrder]; //Q_GmaxnSV[sv.rank];

								if (quad_coef > 0)
									{
									// the difference in the objective function
									obj_diff = -(grad_diff * grad_diff) / quad_coef;
									}
								else
									{
									obj_diff = -(grad_diff * grad_diff) / 1e-12f;
									}

								if (obj_diff <= obj_diff_min)
									{
									// we want to find the support vector that minimize the objective function
									GminSV = sv;
									obj_diff_min = obj_diff;
									}
								}
							}
						}
					}


				return new SolutionVectorPair(GminSV.targetValue==1 ? GmaxpSV : GmaxnSV, GminSV,
				                              Math.max(Gmaxp + Gmaxp2, Gmaxn + Gmaxn2) < eps);

		
		}


	protected class SolutionVectorPair
		{
// ------------------------------ FIELDS ------------------------------

		boolean isOptimal;
		supportVector svA;
		supportVector svB;


// --------------------------- CONSTRUCTORS ---------------------------

		protected SolutionVectorPair(supportVector svA, supportVector svB, boolean isOptimal)
			{
			this.svA = svA;
			this.svB = svB;
			this.isOptimal = isOptimal;
			}
		}
	}
