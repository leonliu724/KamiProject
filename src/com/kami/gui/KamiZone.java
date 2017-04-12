package com.kami.gui;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.paint.Color;

public class KamiZone implements Comparable<KamiZone> {
	private String name;
	private int index;
	private KamiTriangle element;
	private Color color;
	private Set<KamiZone> connected;
	private Set<Color> connectedColor;
	private boolean merged;
	
	public KamiZone() {
		connected = new HashSet<KamiZone>();
		connectedColor = new HashSet<Color>();
		merged = false;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public KamiTriangle getElement() {
		return element;
	}
	public void setElement(KamiTriangle element) {
		this.element = element;
	}
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Set<KamiZone> getConnected() {
		return connected;
	}
	public void setConnected(Set<KamiZone> connected) {
		this.connected = connected;
	}
	public Set<Color> getConnectedColor() {
		return connectedColor;
	}
	public void setConnectedColor(Set<Color> connectedColor) {
		this.connectedColor = connectedColor;
	}
	public boolean isMerged() {
		return merged;
	}
	public void setMerged(boolean merged) {
		this.merged = merged;
	}

	public int compareTo(KamiZone o) {
		return o.getConnected().size() - this.getConnected().size();
	}
}
