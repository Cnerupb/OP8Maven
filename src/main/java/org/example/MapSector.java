package org.example;

/**
 * Class representing part of Map which is divided by N pieces. This class is one of its parts.
 */
public class MapSector {

  private final boolean isTreasure;

  public MapSector(boolean treasure) {
    this.isTreasure = treasure;
  }

  public boolean isTreasure() {
    return isTreasure;
  }
}
