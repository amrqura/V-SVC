package edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;



// support vector solutions which will be the output from the training Step.

public class supportVector implements Comparable<supportVector>{

	// ------------------------------ FIELDS ------------------------------

	/**
	 * Used by the cacheing mechanism to keep track of which SVs are the most active.
	 */
	public int supportVectorOrder = -1;

	/**
	 * keep track of the sample id for mapping to ranks
	 */
	final public int id;
	final public dataExample point;
	public double targetValue;
	public double alpha;
	public double G;
	public float linearTerm;
	Status alphaStatus;
	public float G_bar;


// --------------------------- CONSTRUCTORS ---------------------------

	public supportVector(int id, dataExample key, Double targetValue, float linearTerm)
		{
		this.id = id;
		point = key;
		this.linearTerm = linearTerm;
		this.targetValue = targetValue;
		}

	public supportVector(int id,  dataExample key, Double value, float linearTerm, float alpha)
		{
		this(id, key, value, linearTerm);
		this.alpha = alpha;
		}

// ------------------------ CANONICAL METHODS ------------------------

	@Override
	public boolean equals(Object o)
		{
		if (this == o)
			{
			return true;
			}
		if (o == null || getClass() != o.getClass())
			{
			return false;
			}

		supportVector that = (supportVector) o;

		if (supportVectorOrder != that.supportVectorOrder)
			{
			return false;
			}

		return true;
		}

	// PERF hack for speed

	public int hashCode()
		{
		return id;
		}

	@Override
	public String toString()
		{
		return "SolutionVector{" + "point=" + point + ", targetValue=" + targetValue + ", alpha=" + alpha
		       + ", alphaStatus=" + alphaStatus + ", G=" + G + ", linearTerm=" + linearTerm + ", G_bar=" + G_bar + '}';
		}

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface Comparable ---------------------

	public int compareTo(supportVector b)
		{
		return supportVectorOrder < b.supportVectorOrder ? -1 : (supportVectorOrder > b.supportVectorOrder ? 1 : 0);
		}

// -------------------------- OTHER METHODS --------------------------

	public boolean isFree()
		{
		return alphaStatus == Status.FREE;
		}

	public boolean isShrinkable(double Gmax1, double Gmax2)
		{
		//return isShrinkable(Gmax1,Gmax2,Gmax1,Gmax2);

		if (isUpperBound())
			{
			if (targetValue==1)
				{
				return -G > Gmax1;
				}
			else
				{
				return -G > Gmax2;
				}
			}
		else if (isLowerBound())
			{
			if (targetValue==1)
				{
				return G > Gmax2;
				}
			else
				{
				return G > Gmax1;
				}
			}
		else
			{
			return false;
			}
		}

	public boolean isUpperBound()
		{
		return alphaStatus == Status.UPPER_BOUND;
		}

	public boolean isLowerBound()
		{
		return alphaStatus == Status.LOWER_BOUND;
		}

	public boolean isShrinkable(double Gmax1, double Gmax2, double Gmax3, double Gmax4)
		{
		if (isUpperBound())
			{
			if (targetValue==1)
				{
				return (-G > Gmax1);
				}
			else
				{
				return (-G > Gmax4);
				}
			}
		else if (isLowerBound())
			{
			if (targetValue==1)
				{
				return (G > Gmax2);
				}
			else
				{
				return (G > Gmax3);
				}
			}
		else
			{
			return false;
			}
		}

	public void updateAlphaStatus(float Cp, float Cn)
		{
		// upper pound: alpha<C,y=1 or alpha>0,y=-1
		if (alpha >= getC(Cp, Cn))
			{
			alphaStatus = Status.UPPER_BOUND;
			}
		// lower bound : alpha < C , y=-1 or alpha >0 , y=1
		else if (alpha <= 0)
			{
			alphaStatus = Status.LOWER_BOUND;
			}
		else
			{
			alphaStatus = Status.FREE;
			}
		}

	public float getC(float Cp, float Cn)
		{
			// return C positive or C Negative based on the target Value
			return targetValue==1 ? Cp : Cn;
		}

// -------------------------- ENUMERATIONS --------------------------

	public enum Status
		{
			LOWER_BOUND, UPPER_BOUND, FREE
		}
}
