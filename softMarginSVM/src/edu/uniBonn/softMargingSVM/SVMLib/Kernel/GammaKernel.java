package edu.uniBonn.softMargingSVM.SVMLib.Kernel;


public abstract class GammaKernel implements KernelFunction
{
//------------------------------ FIELDS ------------------------------

public double gamma;


//--------------------------- CONSTRUCTORS ---------------------------

public GammaKernel(double gamma)
	{
	this.gamma = gamma;
	}

//--------------------- GETTER / SETTER METHODS ---------------------

public double getGamma()
	{
	return gamma;
	}

public void setGamma(double gamma)
	{
	this.gamma = gamma;
	}
}
