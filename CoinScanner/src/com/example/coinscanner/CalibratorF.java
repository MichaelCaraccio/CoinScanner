package com.example.coinscanner;

public class CalibratorF {
	
	public CalibratorF(IntervalF intervalA, IntervalF intervalB)
	{
		this.intervalA = intervalA;
		this.intervalB = intervalB;
	}
	
	public void setIntervalA(IntervalF interval)
	{
		this.intervalA = interval;
	}
	
	public void setIntervalB(IntervalF interval)
	{
		this.intervalB = interval;
	}
	
	public IntervalF getIntervalA()
	{
		return intervalA;
	}
	
	public IntervalF getIntervalB()
	{
		return intervalB;
	}
	
	public double calibrate(double value)
	{
		double a = intervalA.getA();
		double b = intervalA.getB();
		double c = intervalB.getA();
		double d = intervalB.getB();
		double pente = ((d-c)/(b-a));
		return pente * value + c - pente * a;
	}
	
	private IntervalF intervalA;
	private IntervalF intervalB;

}
