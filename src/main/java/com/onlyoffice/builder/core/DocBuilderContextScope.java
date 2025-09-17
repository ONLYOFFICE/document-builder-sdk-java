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
package com.onlyoffice.builder.core;

import docbuilder.CDocBuilderContextScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper class for {@link CDocBuilderContextScope} that provides a Java-friendly interface for
 * managing document builder context scopes.
 *
 * <p>This class is designed to be used exclusively through the DocumentSession Fluent API. Direct
 * instantiation is restricted to enforce proper resource management and the Fluent API pattern.
 *
 * <p>This class implements {@link AutoCloseable} to ensure proper resource cleanup when used in
 * try-with-resources statements. It wraps the native C++ implementation and provides logging for
 * debugging purposes.
 *
 * <p>The context scope is used to manage the lifecycle and state of document builder operations,
 * ensuring proper resource management and cleanup.
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 * @see DocumentSession
 * @see CDocBuilderContextScope
 * @see AutoCloseable
 */
public class DocBuilderContextScope implements AutoCloseable {
  private static final Logger logger = LoggerFactory.getLogger(DocBuilderContextScope.class);

  private final CDocBuilderContextScope delegate;

  /**
   * Package-private constructor for DocBuilderContextScope.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderContextScope instances.
   *
   * @param scope the existing native context scope to wrap (must not be null)
   * @throws IllegalArgumentException if the scope parameter is null
   */
  DocBuilderContextScope(CDocBuilderContextScope scope) {
    if (scope == null) throw new IllegalArgumentException("Scope cannot be null");
    logger.debug("Creating DocBuilderContextScope with existing CDocBuilderContextScope");
    this.delegate = scope;
  }

  /**
   * Package-private constructor for DocBuilderContextScope.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderContextScope instances.
   *
   * <p>This constructor creates a fresh context scope with default settings.
   */
  DocBuilderContextScope() {
    logger.debug("Creating new DocBuilderContextScope with default constructor");
    this.delegate = new CDocBuilderContextScope();
  }

  /**
   * Package-private constructor for DocBuilderContextScope by copying an existing scope.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderContextScope instances.
   *
   * <p>This constructor creates a deep copy of the provided scope, allowing you to work with a
   * separate instance while maintaining the same initial state. The original scope remains
   * unchanged and can continue to be used independently.
   *
   * @param scope the existing scope to copy from (must not be null)
   * @throws IllegalArgumentException if the scope parameter is null
   */
  DocBuilderContextScope(DocBuilderContextScope scope) {
    if (scope == null) throw new IllegalArgumentException("Scope cannot be null");
    logger.debug("Creating DocBuilderContextScope by copying existing scope");
    this.delegate = new CDocBuilderContextScope(scope.delegate);
  }

  /**
   * Closes this context scope and releases any associated resources.
   *
   * <p>This method is called automatically when using try-with-resources statements. It delegates
   * the cleanup to the underlying native implementation and logs the action for debugging purposes.
   *
   * <p>After calling this method, the scope should not be used further as it may lead to undefined
   * behavior.
   */
  @Override
  public void close() {
    logger.debug("Closing DocBuilderContextScope");
    delegate.close();
  }
}
