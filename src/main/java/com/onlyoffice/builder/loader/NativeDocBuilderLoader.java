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

import docbuilder.CDocBuilder;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A singleton loader for native DocBuilder libraries that manages the lifecycle of the underlying
 * CDocBuilder native implementation.
 *
 * <p>This class provides thread-safe initialization and disposal of the DocBuilder native library.
 * It ensures that the library is loaded only once and properly disposed of when no longer needed.
 *
 * <p>The loader implements {@link AutoCloseable} to support automatic resource management in
 * try-with-resources statements.
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 */
public class NativeDocBuilderLoader implements AutoCloseable {
  /** The singleton instance of this loader */
  private static volatile NativeDocBuilderLoader instance;

  /** Atomic flag indicating whether the native library has been loaded */
  private final AtomicBoolean loaded = new AtomicBoolean(false);

  /** The library loader responsible for loading native binaries */
  private final LibraryLoader libLoader;

  /**
   * Private constructor to enforce singleton pattern.
   *
   * @param libLoader the library loader instance responsible for loading native binaries
   */
  private NativeDocBuilderLoader(LibraryLoader libLoader) {
    this.libLoader = libLoader;
  }

  /**
   * Gets the singleton instance of the NativeDocBuilderLoader.
   *
   * <p>This method implements a thread-safe singleton pattern using double-checked locking. The
   * instance is created lazily when first requested.
   *
   * @param libLoader the library loader instance responsible for loading native binaries
   * @return the singleton instance of NativeDocBuilderLoader
   */
  public static NativeDocBuilderLoader getInstance(LibraryLoader libLoader) {
    if (instance == null) {
      synchronized (NativeDocBuilderLoader.class) {
        instance = new NativeDocBuilderLoader(libLoader);
      }
    }

    return instance;
  }

  /**
   * Loads the native DocBuilder library from the specified directory.
   *
   * <p>This method can only be called once per instance. Subsequent calls will have no effect. The
   * method first verifies that the underlying library loader has successfully loaded the required
   * binaries before initializing the DocBuilder.
   *
   * @param directory the directory path containing the native library files
   * @throws IllegalArgumentException if the library loader has not successfully loaded the binaries
   * @throws IllegalStateException if the library has already been loaded
   */
  public void load(String directory) {
    if (!libLoader.isLoaded())
      throw new IllegalArgumentException("Builder can't be initialized without the binaries");

    if (loaded.compareAndSet(false, true)) CDocBuilder.initialize(directory);
  }

  /**
   * Checks whether the native library has been successfully loaded.
   *
   * @return true if the library is loaded, false otherwise
   */
  public boolean isLoaded() {
    return loaded.get();
  }

  /**
   * Disposes of the native DocBuilder library and releases associated resources.
   *
   * <p>This method is called automatically when the loader is used in a try-with-resources
   * statement. It safely disposes of the native library only if it was previously loaded.
   *
   * @throws Exception if an error occurs during disposal
   */
  @Override
  public void close() throws Exception {
    if (loaded.get()) CDocBuilder.dispose();
  }
}
