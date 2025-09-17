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

import java.nio.file.Path;

/**
 * Interface for loading native libraries required by the document builder.
 *
 * <p>This interface provides methods to load native libraries from a specific location or from the
 * default location, and to check if the libraries have been successfully loaded.
 *
 * <p>Implementations of this interface handle the platform-specific details of loading native
 * libraries, including proper library naming conventions and dependencies.
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 */
public interface LibraryLoader {

  /**
   * Loads native libraries from the specified location.
   *
   * <p>This method loads all required native libraries from the given path. The libraries are
   * loaded in the correct order to satisfy dependencies. If the libraries are already loaded, this
   * method does nothing.
   *
   * @param location the path to the directory containing the native libraries
   * @throws RuntimeException if the libraries cannot be loaded from the specified location
   */
  void load(Path location);

  /**
   * Loads native libraries from the default location.
   *
   * <p>This method loads native libraries from the default location, which is typically the
   * directory containing the JAR file or class files. The exact location is determined by the
   * implementation.
   *
   * @throws RuntimeException if the libraries cannot be loaded from the default location
   */
  void load();

  /**
   * Checks if the native libraries have been successfully loaded.
   *
   * @return {@code true} if the libraries are loaded, {@code false} otherwise
   */
  boolean isLoaded();
}
