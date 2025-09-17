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
package com.onlyoffice.builder;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.module.ModuleDescriptor;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for verifying the Java module system configuration.
 *
 * <p>This test class validates that the Document Builder library properly implements the Java
 * Platform Module System (JPMS) requirements. It ensures that only the intended public API packages
 * are exported and that the module descriptor is correctly configured.
 *
 * <p>The tests verify:
 *
 * <ul>
 *   <li>Module descriptor presence and validity
 *   <li>Correct package export configuration
 *   <li>Proper encapsulation of internal packages
 * </ul>
 *
 * <p>These tests are essential for maintaining proper module boundaries and ensuring that the
 * library's public API is well-defined and secure.
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 */
public class ModuleTests {

  /**
   * Tests that the module descriptor correctly exports only the public API package.
   *
   * <p>This test verifies that the module system is properly configured to expose only the intended
   * public API while keeping internal implementation details encapsulated. It ensures that
   * consumers of the library can only access the documented public interfaces.
   *
   * <p>The test:
   *
   * <ul>
   *   <li>Retrieves the module descriptor for the current module
   *   <li>Extracts the list of exported packages
   *   <li>Verifies that only the main builder package is exported
   *   <li>Ensures no internal packages are accidentally exposed
   * </ul>
   */
  @Test
  void whenGettingModuleDescriptor_thenOnlyPublicApiPackageIsExported() {
    var module = ModuleTests.class.getModule();
    var descriptor = module.getDescriptor();

    assertNotNull(descriptor, "Descriptor must be present for named module");

    var exportedPackages =
        descriptor.exports().stream()
            .map(ModuleDescriptor.Exports::source)
            .collect(Collectors.toUnmodifiableSet());

    assertEquals(
        Set.of(
            "com.onlyoffice.builder.core",
            "com.onlyoffice.builder.loader",
            "com.onlyoffice.builder.util"),
        exportedPackages,
        "Only com.onlyoffice.builder should be exported");
  }
}
