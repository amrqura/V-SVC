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
import edu.uniBonn.softMargingSVM.Util.BaseMatrix;

/**
 * An SMO algorithm
 */

public class quadraticProgrammingProblemSolver {

	private static final int MAXITERATION = 50000;

	private final static supportVector[] EMPTY_SV_ARRAY = new supportVector[0];

	float eps;
	boolean unshrink = false;
	boolean shrinking;

	private final List<supportVector> allExamples;
	private supportVector[] active;
	private supportVector[] inactive;
	private final float Cp, Cn;
	private final int numExamples;

	BaseMatrix matrix;
	float[] matrix_svA;
	float[] matrix_svB;
	float[] matrix_all;

	public SVMModel solveEquation() {
		makeOptimize();

		SVMModel model = new SVMModel();

		// calculate rho
		calculate_rho(model);

		// calculate objective value

		float v = 0;
		for (supportVector svC : allExamples) {
			v += svC.alpha * (svC.Grade + svC.linearTerm);
		}

		// the objective function
		model.obj = v / 2;

		model.supportVectors = new HashMap<dataExample, Double>();
		for (supportVector svC : allExamples) {
			model.supportVectors.put(svC.point, svC.alpha);

		}

		return model;
	}

	public quadraticProgrammingProblemSolver(
			List<supportVector> solutionVectors, BaseMatrix Q, float Cp,
			float Cn, float eps, boolean shrinking) throws Exception {

		if (eps <= 0) {
			throw new Exception("eps <= 0");
		}

		this.matrix = Q;
		this.Cp = Cp;
		this.Cn = Cn;
		this.eps = eps;
		this.shrinking = shrinking;

		this.allExamples = solutionVectors;

		this.numExamples = allExamples.size();
		matrix_all = new float[numExamples];
	}

	private void calculate_rho(SVMModel model) {
		int nr_free1 = 0, nr_free2 = 0;
		double ub1 = Double.POSITIVE_INFINITY, ub2 = Double.POSITIVE_INFINITY;
		double lb1 = Double.NEGATIVE_INFINITY, lb2 = Double.NEGATIVE_INFINITY;
		double sum_free1 = 0, sum_free2 = 0;

		for (supportVector sv : allExamples) {
			if (sv.targetValue == 1) {
				if (sv.isLowerBound()) {
					ub1 = Math.min(ub1, sv.Grade);
				} else if (sv.isUpperBound()) {
					lb1 = Math.max(lb1, sv.Grade);
				} else {
					++nr_free1;
					sum_free1 += sv.Grade;
				}
			} else {
				if (sv.isLowerBound()) {
					ub2 = Math.min(ub2, sv.Grade);
				} else if (sv.isUpperBound()) {
					lb2 = Math.max(lb2, sv.Grade);
				} else {
					++nr_free2;
					sum_free2 += sv.Grade;
				}
			}
		}

		double r1, r2;
		if (nr_free1 > 0) {
			r1 = sum_free1 / nr_free1;
		} else {
			r1 = (ub1 + lb1) / 2;
		}

		if (nr_free2 > 0) {
			r2 = sum_free2 / nr_free2;
		} else {
			r2 = (ub2 + lb2) / 2;
		}

		model.r = (float) ((r1 + r2) / 2);
		model.rho = (float) ((r1 - r2) / 2);
	}

	private int makeOptimize() {
		matrix.initOrders(allExamples); // write sequential ranks for all the
										// support vectors

		for (supportVector svA : allExamples) {
			svA.updateAlphaStatus(Cp, Cn);
		}

		initActiveSet();

		// initialize gradient

		for (supportVector svA : allExamples) {
			svA.Grade = svA.linearTerm;
			svA.Grade_bar = 0;
		}
		for (supportVector svA : allExamples) {
			if (!svA.isLowerBound()) {

				matrix.fillArrayWithSupportVectorEntries(svA, active,
						matrix_svA);
				for (supportVector svB : allExamples) {
					svB.Grade += svA.alpha * matrix_svA[svB.supportVectorOrder];

				}
				if (svA.isUpperBound()) {
					for (supportVector svB : allExamples) {
						svB.Grade_bar += svA.getC(Cp, Cn)
								* matrix_svA[svB.supportVectorOrder];

					}
				}
			}
		}

		// optimization step

		int iter = 0;
		int counter = Math.min(numExamples, 1000) + 1;

		supportVector svA;
		supportVector svB;

		while (true) {

			if (--counter == 0) {
				counter = Math.min(numExamples, 1000);
				if (shrinking) {
					shrink();
				}

			}
			SelectedPair pair = selectWorkingPair();

			if (pair.isOptimal) // pair already optimal
			{

				// reset active set size and check
				resetActiveSet();

				// 2- find a two elements working set B={i,j}
				pair = selectWorkingPair();
				if (pair.isOptimal) // pair already optimal
				{

					break;
				} else {
					counter = 1;

				}
			}
			svA = pair.svA;
			svB = pair.svB;

			++iter;

			if (iter > MAXITERATION) {
				break;
			}

			matrix.fillArrayWithSupportVectorEntries(svA, active, matrix_svA);
			matrix.fillArrayWithSupportVectorEntries(svB, active, matrix_svB);

			float C_i = svA.getC(Cp, Cn);
			float C_j = svB.getC(Cp, Cn);

			double old_alpha_i = svA.alpha;
			double old_alpha_j = svB.alpha;

			if (svA.targetValue != svB.targetValue) {

				float quad_coef = matrix.evaluateDiagonal(svA)
						+ matrix.evaluateDiagonal(svB) + 2
						* matrix_svA[svB.supportVectorOrder];

				if (quad_coef <= 0) {
					quad_coef = 1e-12f;
				}
				double delta = (-svA.Grade - svB.Grade) / quad_coef;
				double diff = svA.alpha - svB.alpha;
				// the change in the value of alpha(i) and alpha(j) depends on
				// the difference between approxomation error
				svA.alpha += delta;
				svB.alpha += delta;

				if (diff > 0) {
					if (svB.alpha < 0) {
						svB.alpha = 0;
						svA.alpha = diff;
					}
				} else {
					if (svA.alpha < 0) {
						svA.alpha = 0;
						svB.alpha = -diff;
					}
				}
				if (diff > C_i - C_j) {
					if (svA.alpha > C_i) {
						svA.alpha = C_i;
						svB.alpha = C_i - diff;
					}
				} else {
					if (svB.alpha > C_j) {
						svB.alpha = C_j;
						svA.alpha = C_j + diff;
					}
				}
			} else { // y_i=y_j
				float quad_coef = matrix.evaluateDiagonal(svA)
						+ matrix.evaluateDiagonal(svB) - 2
						* matrix_svA[svB.supportVectorOrder];

				if (quad_coef <= 0) {
					quad_coef = 1e-12f;
				}
				double delta = (svA.Grade - svB.Grade) / quad_coef;
				double sum = svA.alpha + svB.alpha;
				svA.alpha -= delta;
				svB.alpha += delta;

				if (sum > C_i) {
					if (svA.alpha > C_i) {
						svA.alpha = C_i;
						svB.alpha = sum - C_i;
					}
				} else {
					if (svB.alpha < 0) {
						svB.alpha = 0;
						svA.alpha = sum;
					}
				}
				if (sum > C_j) {
					if (svB.alpha > C_j) {
						svB.alpha = C_j;
						svA.alpha = sum - C_j;
					}
				} else {
					if (svA.alpha < 0) {
						svA.alpha = 0;
						svB.alpha = sum;
					}
				}
			}

			// update Grade

			double delta_alpha_i = svA.alpha - old_alpha_i;
			double delta_alpha_j = svB.alpha - old_alpha_j;

			if (delta_alpha_i == 0 && delta_alpha_j == 0) {

				break;
			}

			for (int i = 0; i < active.length; i++) {
				active[i].Grade += matrix_svA[i] * delta_alpha_i
						+ matrix_svB[i] * delta_alpha_j;
			}

			boolean ui = svA.isUpperBound();
			boolean uj = svB.isUpperBound();
			svA.updateAlphaStatus(Cp, Cn);
			svB.updateAlphaStatus(Cp, Cn);

			if (ui != svA.isUpperBound()) {
				matrix.getQ(svA, active, inactive, matrix_all);
				if (ui) {
					for (supportVector svC : allExamples) {
						svC.Grade_bar -= C_i
								* matrix_all[svC.supportVectorOrder];

					}
				} else {
					for (supportVector svC : allExamples) {
						svC.Grade_bar += C_i
								* matrix_all[svC.supportVectorOrder];

					}
				}
			}

			if (uj != svB.isUpperBound()) {
				matrix.getQ(svB, active, inactive, matrix_all);
				if (uj) {
					for (supportVector svC : allExamples) {
						svC.Grade_bar -= C_j
								* matrix_all[svC.supportVectorOrder];

					}
				} else {
					for (supportVector svC : allExamples) {

						svC.Grade_bar += C_j
								* matrix_all[svC.supportVectorOrder];

					}
				}
			}
		}

		return iter;
	}

	private void initActiveSet() {
		active = allExamples.toArray(EMPTY_SV_ARRAY);
		inactive = EMPTY_SV_ARRAY;
		matrix_svA = new float[active.length];
		matrix_svB = new float[active.length];
	}

	void shrink() {
		double Gmax1 = Double.NEGATIVE_INFINITY;
		double Gmax2 = Double.NEGATIVE_INFINITY;
		double Gmax3 = Double.NEGATIVE_INFINITY;
		double Gmax4 = Double.NEGATIVE_INFINITY;

		// find maximal violating pair first
		for (supportVector sv : active) {
			if (!sv.isUpperBound()) {
				if (sv.targetValue == 1) {
					if (-sv.Grade > Gmax1) {
						Gmax1 = -sv.Grade;
					}
				} else if (-sv.Grade > Gmax4) {
					Gmax4 = -sv.Grade;
				}
			}
			if (!sv.isLowerBound()) {
				if (sv.targetValue == 1) {
					if (sv.Grade > Gmax2) {
						Gmax2 = sv.Grade;
					}
				} else if (sv.Grade > Gmax3) {
					Gmax3 = sv.Grade;
				}
			}
		}

		if (!unshrink && Math.max(Gmax1 + Gmax2, Gmax3 + Gmax4) <= eps * 10) {
			unshrink = true;
			resetActiveSet();
		}

		Collection<supportVector> activeList = new ArrayList<supportVector>(
				Arrays.asList(active));
		Collection<supportVector> inactiveList = new ArrayList<supportVector>();

		for (Iterator<supportVector> iter = activeList.iterator(); iter
				.hasNext();) {
			supportVector sv = iter.next();

			if (sv.isShrinkable(Gmax1, Gmax2, Gmax3, Gmax4)) {
				iter.remove();
				inactiveList.add(sv);
			}
		}

		active = activeList.toArray(EMPTY_SV_ARRAY);
		supportVector[] newlyInactive = inactiveList.toArray(EMPTY_SV_ARRAY);
		matrix.maintainCache(active, newlyInactive);

		inactiveList.addAll(Arrays.asList(inactive));
		inactive = inactiveList.toArray(EMPTY_SV_ARRAY);
	}

	private void resetActiveSet() {
		active = allExamples.toArray(EMPTY_SV_ARRAY);
		Arrays.sort(active);
		inactive = EMPTY_SV_ARRAY;
		matrix_svA = new float[active.length];
		matrix_svB = new float[active.length];
	}

	private SelectedPair selectWorkingPair() {
		// we want to find the pair of points that maximize the difference in
		// classification error is largest
		// we want a pair of points that make the decease of objective function
		// maximized
		// return i,j such that y_i = y_j and
		// i: maximizes -y_i * grad(f)_i, i in I_up(\alpha)
		// j: minimizes the decrease of obj value
		// (if quadratic coefficeint <= 0, replace it with tau)
		// -y_j*grad(f)_j < -y_i*grad(f)_i, j in I_low(\alpha)

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
		for (supportVector sv : active) {
			if (sv.targetValue == 1) {
				if (!sv.isUpperBound()) {
					if (-sv.Grade >= Gmaxp) {
						Gmaxp = -sv.Grade;
						GmaxpSV = sv;
					}
				}
			} else {
				if (!sv.isLowerBound()) {
					if (sv.Grade >= Gmaxn) {
						Gmaxn = sv.Grade;
						GmaxnSV = sv;
					}
				}
			}
		}

		matrix.fillArrayWithSupportVectorEntries(GmaxpSV, active, matrix_svA);
		matrix.fillArrayWithSupportVectorEntries(GmaxnSV, active, matrix_svB);

		for (supportVector sv : active) {
			/*
			 * in the second iteration , we want to find j that will minimize
			 * the objective function
			 */
			if (sv.targetValue == 1) {
				if (!sv.isLowerBound()) {
					double grad_diff = Gmaxp + sv.Grade;
					if (sv.Grade >= Gmaxp2) {
						Gmaxp2 = sv.Grade;
					}
					if (grad_diff > 0) {
						double obj_diff;
						double quad_coef = matrix.evaluateDiagonal(GmaxpSV)
								+ matrix.evaluateDiagonal(sv) - 2.0f
								* matrix_svA[sv.supportVectorOrder];
						if (quad_coef > 0) {
							obj_diff = -(grad_diff * grad_diff) / quad_coef;
						} else {
							obj_diff = -(grad_diff * grad_diff) / 1e-12f;
						}

						if (obj_diff <= obj_diff_min) {
							GminSV = sv;
							obj_diff_min = obj_diff;
						}
					}
				}
			} else {
				if (!sv.isUpperBound()) {
					double grad_diff = Gmaxn - sv.Grade;
					if (-sv.Grade >= Gmaxn2) {
						Gmaxn2 = -sv.Grade;
					}
					if (grad_diff > 0) {
						double obj_diff;

						double quad_coef = matrix.evaluateDiagonal(GmaxnSV)
								+ matrix.evaluateDiagonal(sv) - 2.0f
								* matrix_svB[sv.supportVectorOrder];

						if (quad_coef > 0) {
							// the difference in the objective function
							obj_diff = -(grad_diff * grad_diff) / quad_coef;
						} else {
							obj_diff = -(grad_diff * grad_diff) / 1e-12f;
						}

						if (obj_diff <= obj_diff_min) {
							// we want to find the support vector that minimize
							// the objective function
							GminSV = sv;
							obj_diff_min = obj_diff;
						}
					}
				}
			}
		}

		return new SelectedPair(GminSV.targetValue == 1 ? GmaxpSV : GmaxnSV,
				GminSV, Math.max(Gmaxp + Gmaxp2, Gmaxn + Gmaxn2) < eps);

	}

	private class SelectedPair {

		boolean isOptimal;
		supportVector svA;
		supportVector svB;

		private SelectedPair(supportVector svA, supportVector svB,
				boolean isOptimal) {
			this.svA = svA;
			this.svB = svB;
			this.isOptimal = isOptimal;
		}
	}
}
