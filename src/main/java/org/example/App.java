package org.example;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
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

  private int n;
  private int counter;
  private List<MapSector> mapSectors;
  private AtomicReferenceArray<Boolean> obtainedMapSectors;
  private List<Thread> threadList;
  private final Config config;

  public App() {
    this.n = 0;
    this.counter = 0;
    this.config = new Config();
  }

  public void printCommandsList() {
    String commandsList = """
        -3) Exit program;
        -2) Save N to config.xml;
        -1) Load N from config.xml;
        0) Show commands;
        1) Input N;
        2) Make calculations and show result.
        """;
    System.out.println(commandsList);
  }

  public void run() {
    this.printCommandsList();
    while (true) {
      try {
        Optional<Integer> comNum = new InputManager<>("\nInput command: ", Integer::parseInt,
            (val) -> (val > -4) && (val < 3)).getValue();
        System.out.println();
        comNum.ifPresent(this::runCommand);
      } catch (ExitException e) {
        System.exit(0);
      }
    }
  }

  public void runCommand(int num) {
    switch (num) {
      case -3 -> this.exitCommand();
      case -2 -> this.saveDataCommand();
      case -1 -> this.loadDataCommand();
      case 0 -> this.printCommandsList();
      case 1 -> {
        try {
          this.getNFromUsercommand();
        } catch (ExitException e) {
          System.out.println("Terminating...");
        }
      }
      case 2 -> {
        try {
          this.calculateAndShowResultCommand();
        } catch (InterruptedException ignored) {
        }
      }
      default -> System.out.println("Invalid number!");
    }
  }

  public void exitCommand() {
    System.out.println("Exiting...");
    System.exit(0);
  }

  /**
   * Creates new Map using N value
   */
  public void updateMap() {
    // generating Sectors of Treasure map
    this.mapSectors = Stream.generate(App::createSector).limit(n).toList();

    // generating List to check if treasure in Sector is found/not found
    this.obtainedMapSectors = new AtomicReferenceArray<>(n);
    AtomicReferenceArray<Boolean> finalObtainedMapSectors = this.obtainedMapSectors;
    IntStream.range(0, n).forEach((i) -> finalObtainedMapSectors.set(i, false));
    for (int i = 0; i < n; i++) {
      this.obtainedMapSectors.set(i, false);
    }
  }

  /**
   * Creates Treasure Map piece. Gold having in piece is defined randomly
   *
   * @return MapSector object
   */
  public static MapSector createSector() {
    int randResult = new Random().nextInt(10);
    if (randResult != 0) {
      return new MapSector(false);
    }
    return new MapSector(true);
  }

  public void getNFromUsercommand() throws ExitException {
    Optional<Integer> optN = new InputManager<>("Input N (0 < N < 2147483647):", Integer::parseInt,
        (val) -> val > 0).getValue();
    optN.ifPresent(n -> {
      this.n = n;
      this.updateMap();
    });
    System.out.println("Got N...");
  }

  public void calculateAndShowResultCommand() throws InterruptedException {
    if (this.n == 0) {
      System.out.println("N not found");
      return;
    }

    System.out.println("Calculating...");
    // generating Threads on each Sector
    threadList = new ArrayList<>();
    IntStream.range(0, n)
        .forEach((i) -> threadList.add(new MapSectorThread(i, mapSectors, obtainedMapSectors)));

    // starting each Thread and waiting its completion
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

  public void saveDataCommand() {
    try {
      this.saveData();
      System.out.println("Data saved");
    } catch (IOException e) {
      System.out.println("Error: " + e);
    } catch (NumberFormatException e) {
      System.out.println("Error: " + e);
      System.out.println(
          "N must be positive integer number greater than 0 and less than 2 147 483 647");
    }
  }

  /**
   * Saves N to config.xml N must be greater than 0
   */
  public void saveData() throws IOException, NumberFormatException {
    if (this.n == 0) {
      throw new NumberFormatException("N is 0");
    }
    this.config.saveConfig(this.n);
  }

  public void loadDataCommand() {
    try {
      this.loadConfig();
      System.out.println("config.xml loaded...");
      System.out.println("N is " + this.n);
    } catch (IOException e) {
      if (e instanceof UnsupportedEncodingException) {
        System.out.println("Encoding error. Encoding must be UTF-8");
      } else if (e instanceof InvalidPropertiesFormatException) {
        System.out.println("XML parse error. Check config.xml fields on syntax errors");
      }
//      System.out.println(e);
    } catch (NumberFormatException e) {
      System.out.println("Error: " + e);
      System.out.println(
          "N must be positive integer number greater than 0 and less than 2147483647.");
      System.out.println("Also check config.xml fields on having syntax errors");
    }
  }

  /**
   * Load config.xml data to Config class
   */
  public void loadConfig() throws IOException, NumberFormatException {
    this.config.loadConfig();
    this.loadN();
  }

  /**
   * Loads N from config and creates Treasure Map pieces
   * <p>
   * On error doing nothing
   */
  public void loadN() throws NumberFormatException {
    if (config.getN() == 0) {
      throw new NumberFormatException();
    }
    this.n = config.getN();
    this.updateMap();
  }

}
