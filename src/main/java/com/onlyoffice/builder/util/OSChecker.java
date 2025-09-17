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
package com.onlyoffice.builder.util;

/**
 * Interface for checking the current operating system platform.
 *
 * <p>This interface provides methods to determine which operating system the Document Builder is
 * running on. This information is crucial for platform-specific operations such as loading native
 * libraries, handling file paths, and determining system capabilities.
 *
 * <p>Implementations of this interface should provide reliable platform detection based on system
 * properties or other platform-specific indicators.
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 * @see SystemOSChecker
 */
public interface OSChecker {

  /**
   * Checks if the current operating system is Windows.
   *
   * <p>This method should return {@code true} for all Windows variants including Windows 10,
   * Windows 11, Windows Server, etc.
   *
   * @return {@code true} if running on Windows, {@code false} otherwise
   */
  boolean isWindows();

  /**
   * Checks if the current operating system is macOS.
   *
   * <p>This method should return {@code true} for all macOS variants including macOS Monterey,
   * macOS Ventura, macOS Sonoma, etc.
   *
   * @return {@code true} if running on macOS, {@code false} otherwise
   */
  boolean isMac();

  /**
   * Checks if the current operating system is Linux.
   *
   * <p>This method should return {@code true} for all Linux distributions including Ubuntu, CentOS,
   * Red Hat, Debian, etc.
   *
   * @return {@code true} if running on Linux, {@code false} otherwise
   */
  boolean isLinux();
}
