package com.seikoudoku2000.hatebumap.batch.player;

import net.geohex.GeoHex;
import net.geohex.GeoHex.Zone;

public class CreateHatena {

	public static void main (String[] args) {
		StringBuilder sb = new StringBuilder();
		for(int level = 9; level <= 14; level++) {
			Zone zone = GeoHex.getZoneByLocation(
					Double.parseDouble("35.011229"), Double.parseDouble("135.760792"), level);
			sb.append((int)zone.x).append(LocalSearcher.SPLITTER).append((int)zone.y).append(LocalSearcher.SPLITTER);
		}
		System.out.println(sb.toString());
	}
}