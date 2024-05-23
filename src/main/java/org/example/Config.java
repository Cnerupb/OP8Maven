package org.example;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class Config {

  private final Properties properties = new Properties();
  private final String configFilePath = "config.xml";

  public void loadConfig() throws IOException {
    try (FileInputStream fis = new FileInputStream(configFilePath)) {
      properties.loadFromXML(fis);
    }
  }

  public void saveConfig(int N) throws IOException {
    properties.setProperty("N", String.valueOf(N));
    try (FileOutputStream fos = new FileOutputStream(configFilePath)) {
      properties.storeToXML(fos, "Amount of map Pieces");
    }
  }

  public int getN() throws NumberFormatException {
    return (Integer.parseInt(properties.getProperty("N", "")));
  }
}
