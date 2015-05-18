package edu.uniBonn.softMargingSVM.SVMLib.Kernel;


public abstract class GammaKernelFunctions implements baseKernelFunction
{

public double gamma;



public GammaKernelFunctions(double gamma)
	{
	this.gamma = gamma;
	}


public double getGamma()
	{
	return gamma;
	}

public void setGamma(double gamma)
	{
	this.gamma = gamma;
	}
}
