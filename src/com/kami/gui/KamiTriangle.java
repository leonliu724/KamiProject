package com.kami.gui;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class KamiTriangle extends Polygon {
	private Color color;
	private Set<KamiTriangle> connected;
	private int row;
	private int column;
	private boolean changed;
	private boolean blank;
	private boolean border;

	public KamiTriangle() {
		super();
		connected = new HashSet<KamiTriangle>();
		changed = false;
		blank = false;
		border = false;
		color = Color.WHITESMOKE;
		this.setStroke(Color.color(0.75,0.75,0.75,0.05));
		this.setFill(Color.WHITESMOKE);
	}
	
	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public Set<KamiTriangle> getConnected() {
		return connected;
	}
	public void setConnected(Set<KamiTriangle> connected) {
		this.connected = connected;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public boolean isChanged() {
		return changed;
	}
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public boolean isBlank() {
		return blank;
	}
	public void setBlank(boolean blank) {
		this.blank = blank;
	}
	public boolean isBorder() {
		return border;
	}
	public void setBorder(boolean border) {
		this.border = border;
	}
}
