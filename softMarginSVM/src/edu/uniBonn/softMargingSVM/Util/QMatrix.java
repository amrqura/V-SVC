package edu.uniBonn.softMargingSVM.Util;

import java.util.Collection;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.supportVector;


public interface QMatrix {

//	float[] getQ(int column, int len);
//	float[] getQD();
//	void swapIndex(int i, int j);

//	List<P> getVectors();

	//float evaluate(SolutionVector<P> a, SolutionVector<P> b);

	float evaluateDiagonal(supportVector a);

	void getQ(supportVector svA, supportVector[] active, float[] buf);

	void getQ(supportVector svA, supportVector[] active, supportVector[] inactive, float[] buf);

//	void storeRanks(Collection<SolutionVector<P>> allExamples);

	void initRanks(Collection<supportVector> allExamples);

	void maintainCache(supportVector[] active, supportVector[] newlyInactive);

	public String perfString();
}
