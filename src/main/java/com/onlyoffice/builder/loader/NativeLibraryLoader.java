/**
 * (c) Copyright Ascensio System SIA 2025
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.onlyoffice.builder.loader;

import com.onlyoffice.builder.util.OSChecker;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link LibraryLoader} that loads native libraries for the document builder.
 *
 * <p>This class provides platform-specific native library loading functionality. It supports
 * Windows, macOS, and Linux operating systems. The class uses a singleton pattern to ensure that
 * libraries are loaded only once per JVM instance.
 *
 * <p>Supported platforms and their library extensions:
 *
 * <ul>
 *   <li><strong>Windows:</strong> .dll files
 *   <li><strong>macOS:</strong> .dylib files
 *   <li><strong>Linux:</strong> .so files
 * </ul>
 *
 * <p>This class is thread-safe and can be safely used in multi-threaded environments.
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 * @see LibraryLoader
 * @see OSChecker
 */
public class NativeLibraryLoader implements LibraryLoader {

  /** Singleton instance of the NativeLibraryLoader */
  private static volatile NativeLibraryLoader instance;

  /** Flag indicating whether the libraries have been loaded */
  private final AtomicBoolean loaded = new AtomicBoolean(false);

  /** List of native libraries to load, excluding platform-specific extensions */
  private final List<String> libraries =
      List.of(
          "UnicodeConverter",
          "kernel",
          "kernel_network",
          "graphics",
          "PdfFile",
          "XpsFile",
          "DjVuFile",
          "DocxRenderer",
          "doctrenderer",
          "docbuilder.jni");

  /** Operating system checker for platform-specific behavior */
  private final OSChecker checker;

  /**
   * Private constructor to enforce singleton pattern.
   *
   * @param checker the operating system checker to use for platform detection
   */
  private NativeLibraryLoader(OSChecker checker) {
    this.checker = checker;
  }

  /**
   * Gets the singleton instance of NativeLibraryLoader.
   *
   * <p>This method implements a thread-safe singleton pattern using double-checked locking. The
   * instance is created lazily when first requested.
   *
   * @param checker the operating system checker to use for platform detection
   * @return the singleton instance of NativeLibraryLoader
   */
  public static NativeLibraryLoader getInstance(OSChecker checker) {
    if (instance == null) {
      synchronized (NativeLibraryLoader.class) {
        instance = new NativeLibraryLoader(checker);
      }
    }

    return instance;
  }

  /**
   * Determines the default library path based on the location of this class.
   *
   * <p>This method extracts the directory containing the JAR file or class files from the
   * protection domain of this class.
   *
   * @return the default path where native libraries should be located
   * @throws RuntimeException if the URI cannot be converted to a file path
   */
  private Path getDefaultLibPath() {
    var location = NativeLibraryLoader.class.getProtectionDomain().getCodeSource().getLocation();
    try {
      var file = new File(location.toURI());
      return file.toPath().getParent();
    } catch (URISyntaxException var4) {
      throw new RuntimeException("Cannot convert URI of the NativeLibraryLoader.class to URL");
    }
  }

  /**
   * Loads core ICU libraries for Windows platform.
   *
   * <p>Loads the essential ICU data and Unicode conversion libraries that are required before
   * loading other native libraries on Windows.
   *
   * @param location the directory containing the native libraries
   */
  private void loadCoreWindows(Path location) {
    System.load(location.resolve("icudt58.dll").toString());
    System.load(location.resolve("icuuc58.dll").toString());
  }

  /**
   * Loads core ICU libraries for macOS platform.
   *
   * <p>Loads the essential ICU data and Unicode conversion libraries that are required before
   * loading other native libraries on macOS.
   *
   * @param location the directory containing the native libraries
   */
  private void loadCoreMac(Path location) {
    System.load(location.resolve("libicudata.58.dylib").toString());
    System.load(location.resolve("libicuuc.58.dylib").toString());
  }

  /**
   * Loads core ICU libraries for Linux platform.
   *
   * <p>Loads the essential ICU data and Unicode conversion libraries that are required before
   * loading other native libraries on Linux.
   *
   * @param location the directory containing the native libraries
   */
  private void loadCoreLinux(Path location) {
    System.load(location.resolve("libicudata.so.58").toString());
    System.load(location.resolve("libicuuc.so.58").toString());
  }

  /**
   * Gets the system-specific library file extension.
   *
   * <p>Returns the appropriate file extension based on the detected operating system.
   *
   * @return the file extension for native libraries on the current platform
   * @throws RuntimeException if the current operating system is not supported
   */
  private String getSystemSpecificLibraryExtension() {
    if (checker.isMac()) return ".dylib";
    else if (checker.isLinux()) return ".so";
    else if (checker.isWindows()) return ".dll";
    else throw new RuntimeException("Current OS is not supported");
  }

  /**
   * Loads all the required native libraries from the specified location.
   *
   * <p>This method loads libraries with the appropriate platform-specific naming conventions. On
   * Linux and macOS, libraries are prefixed with "lib". The method iterates through the list of
   * required libraries and loads each one using {@link System#load(String)}.
   *
   * @param location the directory containing the native libraries
   */
  private void loadLibraries(Path location) {
    var prefix = (checker.isLinux() || checker.isMac()) ? "lib" : "";
    var extension = getSystemSpecificLibraryExtension();
    for (var library : libraries)
      System.load(location.resolve(prefix + library + extension).toString());
  }

  /**
   * Loads native libraries from the specified location.
   *
   * <p>This method first loads the platform-specific core ICU libraries, then loads all other
   * required native libraries. If the libraries are already loaded, this method does nothing. The
   * method is idempotent and thread-safe.
   *
   * <p>The loading process follows this sequence:
   *
   * <ol>
   *   <li>Load core ICU libraries (platform-specific)
   *   <li>Load all other required native libraries
   *   <li>Mark libraries as loaded
   * </ol>
   *
   * @param location the path to the directory containing the native libraries
   * @throws RuntimeException if the current operating system is not supported or if libraries
   *     cannot be loaded from the specified location
   */
  @Override
  public void load(Path location) {
    if (loaded.get()) return;

    if (checker.isWindows()) loadCoreWindows(location);
    else if (checker.isMac()) loadCoreMac(location);
    else if (checker.isLinux()) loadCoreLinux(location);
    else throw new RuntimeException("Current OS is not supported");

    loadLibraries(location);
    loaded.set(true);
  }

  /**
   * Loads native libraries from the default location.
   *
   * <p>This method determines the default library path and delegates to {@link #load(Path)}. The
   * default path is typically the directory containing the JAR file or class files.
   *
   * @throws RuntimeException if the libraries cannot be loaded from the default location
   */
  @Override
  public void load() {
    load(getDefaultLibPath());
  }

  /**
   * Checks if the native libraries have been successfully loaded.
   *
   * @return {@code true} if the libraries are loaded, {@code false} otherwise
   */
  @Override
  public boolean isLoaded() {
    return loaded.get();
  }
}
