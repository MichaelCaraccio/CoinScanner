package com.example.coinscanner;


public class Coin {

	public Coin(String displayString, double value, double mmRadius)
	{
		this.displayString = displayString;
		this.value = value;
		this.mmRadius = mmRadius;
	}
	
	public double getValue()
	{
		return this.value;
	}
	
	public String getDisplay()
	{
		return this.displayString;
	}
	
	public double getRadius()
	{
		return this.mmRadius;
	}
	
	public boolean isEquals(Coin other)
	{
		return this.value == other.getValue() && this.mmRadius == other.mmRadius;
	}
	
	private String displayString;
	private double value;
	private double mmRadius;
}
