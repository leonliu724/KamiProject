package com.kami.gui;

import java.util.List;

public class KamiZoneMap {
	List<KamiZone> zoneList;
	List<KamiZoneColorOption> zoneColorOptionList;
	boolean allDisconnected;
	
	public List<KamiZone> getZoneList() {
		return zoneList;
	}
	public void setZoneList(List<KamiZone> zoneList) {
		this.zoneList = zoneList;
	}
	public List<KamiZoneColorOption> getZoneColorOptionList() {
		return zoneColorOptionList;
	}
	public void setZoneColorOptionList(List<KamiZoneColorOption> zoneColorOptionList) {
		this.zoneColorOptionList = zoneColorOptionList;
	}
	public boolean isAllDisconnected() {
		return allDisconnected;
	}
	public void setAllDisconnected(boolean allDisconnected) {
		this.allDisconnected = allDisconnected;
	}
}
