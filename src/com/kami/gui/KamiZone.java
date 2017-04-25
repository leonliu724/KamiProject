package com.kami.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.scene.paint.Color;

public class KamiZone implements Comparable<KamiZone> {
	private String name;
	private int index;
	private KamiTriangle element;
	private Color color;
	private Set<KamiZone> connected;
	private Set<Color> connectedColorSet;
	private Map<Color, Integer> connectedColor;
	private boolean merged;
	private int value;
	
	public KamiZone() {
		connected = new HashSet<KamiZone>();
		connectedColorSet = new HashSet<Color>();
		connectedColor = new HashMap<Color, Integer>();
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
	public Set<Color> getConnectedColorSet() {
		return connectedColorSet;
	}
	public void setConnectedColorSet(Set<Color> connectedColorSet) {
		this.connectedColorSet = connectedColorSet;
	}
	public boolean isMerged() {
		return merged;
	}
	public void setMerged(boolean merged) {
		this.merged = merged;
	}
	public Map<Color, Integer> getConnectedColor() {
		return connectedColor;
	}
	public void setConnectedColor(Map<Color, Integer> connectedColor) {
		this.connectedColor = connectedColor;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	public void addColor(Color color) {
		if (this.connectedColor.containsKey(color)) {
			int occurence = this.connectedColor.get(color);
			occurence++;
			this.connectedColor.put(color, occurence);
		} else {
			this.connectedColor.put(color, 1);
		}
	}
	
	public void addAllColors(Map<Color, Integer> colors) {
		for (Color color:colors.keySet()) {
			if (this.connectedColor.containsKey(color)) {
				int occurence = this.connectedColor.get(color);
				occurence += colors.get(color);
				this.connectedColor.put(color, occurence);
			} else {
				this.connectedColor.put(color, colors.get(color));
			}
		}
	}
	
	public void removeColor(Color color) {
		if (this.connectedColor.containsKey(color)) {
			int occurence = this.connectedColor.get(color);
			occurence--;
			if (occurence <= 0) {
				this.connectedColor.remove(color);
			} else {
				this.connectedColor.put(color, occurence);
			}
		}
	}

	public int compareTo(KamiZone obj) {
		return obj.getConnected().size() - this.getConnected().size();
	}
}
