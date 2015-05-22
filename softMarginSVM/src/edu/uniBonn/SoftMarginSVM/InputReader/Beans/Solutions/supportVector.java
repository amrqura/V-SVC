package edu.uniBonn.SoftMarginSVM.InputReader.Beans.Solutions;

import edu.uniBonn.SoftMarginSVM.InputReader.Beans.dataExample;




public class supportVector implements Comparable<supportVector>{


	public int supportVectorOrder = -1;

	/**
	 * keep track of the sample id for mapping to ranks
	 */
	final public int id;
	final public dataExample point;
	public double targetValue;
	public double alpha;
	public double Grade;
	public float linearTerm;
	Status alphaStatus;
	public float Grade_bar;



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
		       + ", alphaStatus=" + alphaStatus + ", G=" + Grade + ", linearTerm=" + linearTerm + ", G_bar=" + Grade_bar + '}';
		}



	public int compareTo(supportVector b)
		{
		return supportVectorOrder < b.supportVectorOrder ? -1 : (supportVectorOrder > b.supportVectorOrder ? 1 : 0);
		}


	public boolean isFree()
		{
		return alphaStatus == Status.FREE;
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
				return (-Grade > Gmax1);
				}
			else
				{
				return (-Grade > Gmax4);
				}
			}
		else if (isLowerBound())
			{
			if (targetValue==1)
				{
				return (Grade > Gmax2);
				}
			else
				{
				return (Grade > Gmax3);
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
