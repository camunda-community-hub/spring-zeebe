package io.zeebe.spring.broker.fn;

import java.io.File;

/**
 * Collection class for various broker exceptions.
 */
public class ZeebeBrokerExceptions {

  public static IllegalArgumentException tomlFileNotReadable(final File tomlFile) {
    return new IllegalArgumentException(
      String.format("tomlFile not readable: %s", tomlFile.getAbsolutePath()));
  }
}
