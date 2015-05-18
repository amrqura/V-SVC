package edu.uniBonn.softMargingSVM.Util;

import java.util.Collection;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions.supportVector;


public interface QMatrix {



	float evaluateDiagonal(supportVector a);

	void fillArrayWithSupportVectorEntries(supportVector svA, supportVector[] active, float[] buf);

	void getQ(supportVector svA, supportVector[] active, supportVector[] inactive, float[] buf);


	void initOrders(Collection<supportVector> allExamples);

	void maintainCache(supportVector[] active, supportVector[] newlyInactive);

	public String perfString();
}
