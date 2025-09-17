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
 * Default implementation of the {@link OSChecker} interface that provides operating system
 * detection based on system properties.
 *
 * <p>This class uses the {@code os.name} system property to determine the current operating system
 * platform. It provides a reliable and lightweight way to detect the OS without external
 * dependencies.
 *
 * <p>The detection logic is based on common naming conventions:
 *
 * <ul>
 *   <li>Windows: contains "win"
 *   <li>macOS: contains "mac"
 *   <li>Linux: contains "nix", "nux", or "aix"
 * </ul>
 *
 * <p>This implementation is thread-safe and can be safely used across multiple threads.
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 * @see OSChecker
 */
public class SystemOSChecker implements OSChecker {

  /**
   * The operating system name retrieved from system properties. This field is initialized once when
   * the class is loaded and converted to lowercase for case-insensitive comparison.
   */
  private static final String osName = System.getProperty("os.name").toLowerCase();

  /**
   * Checks if the current operating system is Windows.
   *
   * <p>This method checks if the OS name contains "win" to identify Windows variants including
   * Windows 10, Windows 11, Windows Server, and other Windows-based operating systems.
   *
   * @return {@code true} if running on Windows, {@code false} otherwise
   */
  @Override
  public boolean isWindows() {
    return osName.contains("win");
  }

  /**
   * Checks if the current operating system is macOS.
   *
   * <p>This method checks if the OS name contains "mac" to identify macOS variants including macOS
   * Monterey, macOS Ventura, macOS Sonoma, and other Apple operating systems.
   *
   * @return {@code true} if running on macOS, {@code false} otherwise
   */
  @Override
  public boolean isMac() {
    return osName.contains("mac");
  }

  /**
   * Checks if the current operating system is Linux.
   *
   * <p>This method checks if the OS name contains "nix", "nux", or "aix" to identify various Linux
   * distributions and Unix-like operating systems including Ubuntu, CentOS, Red Hat, Debian, and
   * others.
   *
   * @return {@code true} if running on Linux, {@code false} otherwise
   */
  @Override
  public boolean isLinux() {
    return osName.contains("nix") || osName.contains("nux") || osName.indexOf("aix") > 0;
  }
}
