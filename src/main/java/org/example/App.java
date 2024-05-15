package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Hello world!
 */
public class App {

  public static void main(String[] args) throws InterruptedException {
    int n;
    int counter;
    Optional<Integer> optN;
    List<MapSector> mapSectors;
    AtomicReferenceArray<Boolean> obtainedMapSectors;
    List<Thread> threadList;

    // Getting N from User
    System.out.println("To exit, type 'exit'.");
    while (true) {
      optN = Optional.empty();
      try {
        optN = new InputManager<>("Input N: ", Integer::parseInt, (val) -> val > 0).getValue();
      } catch (ExitException exitEx) {
        System.out.println(exitEx.getMessage());
        System.exit(0);
      }
      if (optN.isEmpty()) {
        throw new RuntimeException();
      }
      n = optN.get();

      // generating Sectors of Treasure map
      mapSectors = Stream.generate(App::createSector).limit(n).toList();
      // generating List to check if treasure in Sector is found/not found
      obtainedMapSectors = new AtomicReferenceArray<>(n);
      AtomicReferenceArray<Boolean> finalObtainedMapSectors = obtainedMapSectors;
      IntStream.range(0, n).forEach((i) -> finalObtainedMapSectors.set(i, false));
      for (int i = 0; i < n; i++) {
        obtainedMapSectors.set(i, false);
      }

      // generating Threads on each Sector
      threadList = new ArrayList<>();
      for (int i = 0; i < n; i++) {
        Thread thread = getThread(i, mapSectors, obtainedMapSectors);
        threadList.add(thread);
      }
      // starting each Thread
      threadList.forEach(Thread::start);
      for (Thread thread : threadList) {
        thread.join();
      }

      // printing results
      counter = 0;
      System.out.println("Treasures found in sectors: ");
      for (int i = 0; i < n; i++) {
        if (obtainedMapSectors.get(i).equals(true)) {
          System.out.println(i);
          counter += 1;
        }
      }
      if (counter == 0) {
        System.out.println("Not found.");
      }
    }
  }

  private static Thread getThread(int i, List<MapSector> mapSectors,
      AtomicReferenceArray<Boolean> obtainedMapSectors) {
    // Function that searches Sector on treasure having
    Runnable searchSector = () -> {
      MapSector mapSector = mapSectors.get(i);
      if (mapSector.isTreasure()) {
        obtainedMapSectors.set(i, true); // Make atomic
      }
    };
    return new Thread(searchSector);
  }

  public static MapSector createSector() {
    int randResult = new Random().nextInt(10);
    if (randResult != 0) {
      return new MapSector(false);
    }
    return new MapSector(true);
  }
}
