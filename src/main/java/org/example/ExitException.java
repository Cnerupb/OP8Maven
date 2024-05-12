package org.example;

/**
 * Exception throws when user types 'exit' in some User Input scenarios
 */
public class ExitException extends Exception {

  public ExitException(String message) {
    super(message);
  }
}
