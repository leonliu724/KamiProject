package com.kami.gui;

import javafx.scene.paint.Color;

public class KamiZoneColorOption implements Comparable<KamiZoneColorOption> {
	private KamiZone zone;
	private Color color;
	private int occurence;
	
	public KamiZone getZone() {
		return zone;
	}
	public void setZone(KamiZone zone) {
		this.zone = zone;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public int getOccurence() {
		return occurence;
	}
	public void setOccurence(int occurence) {
		this.occurence = occurence;
	}

	public int compareTo(KamiZoneColorOption obj) {
		return obj.getOccurence() - this.getOccurence();
	}
}
