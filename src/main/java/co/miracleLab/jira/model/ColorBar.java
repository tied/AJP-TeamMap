package com.atlassian.jira.model;

public class ColorBar {
	public static String[] colorNames={"green","red","yellow","blue","brown","gray"};
	private double percentage;
	private String info;
	private int colorIndex;
	
	public ColorBar(){	
	}
	
	public ColorBar(int colorIndex,double percentage,String info){	
		this.setColorIndex(colorIndex);
		this.setPercentage(percentage);
		this.setInfo(info);
	}
	
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public int getColorIndex() {
		return colorIndex;
	}

	public void setColorIndex(int colorIndex) {
		colorIndex=colorIndex%colorNames.length;
		this.colorIndex = colorIndex;
	}
	
	public String getColor(){
		return colorNames[colorIndex];
	}
	
	
}
