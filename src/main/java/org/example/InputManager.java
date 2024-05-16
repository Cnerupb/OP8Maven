package org.example;

import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Class for getting User Input
 *
 * @param <T> Final type of value, which will be converted from String
 */
public class InputManager<T> {

  private final String inpTip;
  private final Function<String, T> converter;
  private final Predicate<T> validator;

  public InputManager(String inpTip, Function<String, T> converter, Predicate<T> validator) {
    this.inpTip = inpTip;
    this.converter = converter;
    this.validator = validator;
  }


  /**
   * Gets User value and converts it to type T.
   *
   * @return Converted to T value wrapped in Optional, else empty Optional
   * @throws ExitException if 'exit' is typed. Exits program
   */
  public Optional<T> getValue() throws ExitException {
    T value;
    String strValue;
    Scanner scanner = new Scanner(System.in);

    while (true) {
      try {
        System.out.println(this.inpTip);
        strValue = scanner.nextLine();
        if (strValue.equals("exit")) {
          throw new ExitException("Exiting...");
        }
        value = converter.apply(strValue);
        if (!validator.test(value)) {
          throw new Exception("Invalid input!");
        }
        return Optional.ofNullable(value);
      } catch (ExitException exitException) {
        throw exitException;
      } catch (Exception ex) {
        System.out.println("Invalid input!");
      }
    }
  }
}
