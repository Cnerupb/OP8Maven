package org.example;

import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MapSectorThread extends Thread {

  private final int i;
  private final List<MapSector> mapSectors;
  private final AtomicReferenceArray<Boolean> obtainedMapSectors;

  public MapSectorThread(int i, List<MapSector> mapSectors,
      AtomicReferenceArray<Boolean> obtainedMapSectors) {
    this.i = i;
    this.mapSectors = mapSectors;
    this.obtainedMapSectors = obtainedMapSectors;
  }

  public void run() {
    MapSector mapSector = mapSectors.get(this.i);
    if (mapSector.isTreasure()) {
      obtainedMapSectors.set(this.i, true);
    }
  }
}
