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

import docbuilder.CDocBuilderContext;
import java.util.function.Consumer;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A fluent API wrapper around the native CDocBuilderContext for building documents.
 *
 * <p>This class is designed to be used exclusively through the DocumentSession Fluent API. Direct
 * instantiation is restricted to enforce proper resource management and the Fluent API pattern.
 *
 * <p>The class provides higher-level abstractions for common document building operations and
 * implements AutoCloseable to ensure proper cleanup of native resources.
 *
 * <p><strong>Thread Safety:</strong> This class is not thread-safe. Instances should not be shared
 * between threads without proper synchronization.
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 * @see DocumentSession
 * @see DocBuilderValue
 * @see DocBuilderContextScope
 */
public class DocBuilderContext implements AutoCloseable {
  private static final Logger logger = LoggerFactory.getLogger(DocBuilderContext.class);

  private final CDocBuilderContext delegate;

  /**
   * Package-private constructor for DocBuilderContext.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderContext instances.
   *
   * @param delegate the native CDocBuilderContext to wrap (must not be null)
   * @throws IllegalArgumentException if the delegate parameter is null
   * @throws RuntimeException if the native delegate cannot be properly initialized
   */
  DocBuilderContext(CDocBuilderContext delegate) {
    if (delegate == null) throw new IllegalArgumentException("Delegate cannot be null");
    logger.debug("Creating DocBuilderContext with CDocBuilderContext delegate");
    this.delegate = delegate;
  }

  /**
   * Package-private constructor for DocBuilderContext.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderContext instances.
   *
   * @throws RuntimeException if the native CDocBuilderContext cannot be created
   */
  DocBuilderContext() {
    logger.debug("Creating empty DocBuilderContext");
    this.delegate = new CDocBuilderContext();
  }

  /**
   * Package-private constructor for DocBuilderContext by copying an existing one.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilderContext instances.
   *
   * @param value the DocBuilderContext to copy from (must not be null)
   * @throws IllegalArgumentException if the value parameter is null
   * @throws RuntimeException if the native delegate cannot be properly copied
   */
  DocBuilderContext(DocBuilderContext value) {
    if (value == null) throw new IllegalArgumentException("Value cannot be null");
    logger.debug("Creating DocBuilderContext copy from existing value");
    this.delegate = new CDocBuilderContext(value.delegate);
  }

  /**
   * Gets the underlying native CDocBuilderContext delegate.
   *
   * <p>This method provides access to the native delegate for advanced operations that require
   * direct access to the underlying C++ implementation. Use with caution as this bypasses the Java
   * wrapper's safety checks.
   *
   * <p><strong>Warning:</strong> Modifying the returned delegate directly may lead to inconsistent
   * state and should be avoided unless absolutely necessary.
   *
   * @return the native delegate (never null)
   */
  CDocBuilderContext getDelegate() {
    logger.debug("getDelegate() called");
    return delegate;
  }

  /**
   * Creates an undefined value in the document builder context.
   *
   * <p>Creates a new DocBuilderValue representing an undefined value. This is useful for
   * representing uninitialized or missing values in the document structure.
   *
   * @return a new DocBuilderValue representing undefined (never null)
   * @throws RuntimeException if the native operation fails
   */
  public DocBuilderValue createUndefined() {
    logger.debug("createUndefined() called");
    return new DocBuilderValue(delegate.createUndefined());
  }

  /**
   * Creates a null value in the document builder context.
   *
   * <p>Creates a new DocBuilderValue representing a null value. This is useful for representing
   * explicitly null values in the document structure.
   *
   * @return a new DocBuilderValue representing null (never null)
   * @throws RuntimeException if the native operation fails
   */
  public DocBuilderValue createNull() {
    logger.debug("createNull() called");
    return new DocBuilderValue(delegate.createNull());
  }

  /**
   * Creates an empty object in the document builder context.
   *
   * <p>Creates a new DocBuilderValue representing an empty object. This object can be populated
   * with properties and methods using the fluent API.
   *
   * @return a new DocBuilderValue representing an empty object (never null)
   * @throws RuntimeException if the native operation fails
   */
  public DocBuilderValue createObject() {
    logger.debug("createObject() called");
    return new DocBuilderValue(delegate.createObject());
  }

  /**
   * Creates an array with the specified length in the document builder context.
   *
   * <p>Creates a new DocBuilderValue representing an array with the specified number of elements.
   * The array elements are initially undefined and can be populated using array access methods.
   *
   * @param length the length of the array to create (must be non-negative)
   * @return a new DocBuilderValue representing an array (never null)
   * @throws IllegalArgumentException if length is negative
   * @throws RuntimeException if the native operation fails
   */
  public DocBuilderValue createArray(int length) {
    if (length < 0)
      throw new IllegalArgumentException("Array length cannot be negative: " + length);
    logger.debug("createArray() called with length: {}", length);
    return new DocBuilderValue(delegate.createArray(length));
  }

  /**
   * Gets the global object from the document builder context.
   *
   * <p>The global object contains the root namespace and provides access to all available document
   * building functionality.
   *
   * @return a DocBuilderValue representing the global object (never null)
   * @throws RuntimeException if the native operation fails
   */
  public DocBuilderValue getGlobal() {
    logger.debug("getGlobal() called");
    return new DocBuilderValue(delegate.getGlobal());
  }

  /**
   * Creates a new scope in the document builder context.
   *
   * <p>Scopes provide isolated execution contexts for document building operations, allowing for
   * better resource management and preventing variable name conflicts.
   *
   * @return a new DocBuilderContextScope (never null)
   * @throws RuntimeException if the native operation fails
   */
  public DocBuilderContextScope createScope() {
    logger.debug("createScope() called");
    return new DocBuilderContextScope(delegate.createScope());
  }

  /**
   * Checks if an error has occurred in the document builder context.
   *
   * <p>This method should be called after operations to check if any errors occurred during
   * document building. If an error is detected, the context may be in an inconsistent state.
   *
   * @return true if an error has occurred, false otherwise
   */
  public boolean isError() {
    var result = delegate.isError();
    logger.debug("isError() called, result: {}", result);
    return result;
  }

  /**
   * Executes API operations on the global API object.
   *
   * <p>This method provides a convenient way to execute API operations within a properly managed
   * resource scope. The API object is automatically retrieved and disposed of.
   *
   * @param apiOperations the consumer that will receive the API object for operations
   * @return this DocBuilderContext for method chaining
   * @throws IllegalArgumentException if apiOperations is null
   * @throws RuntimeException if the API operations fail or if the native operations fail
   */
  public DocBuilderContext withAPI(Consumer<DocBuilderValue> apiOperations) {
    if (apiOperations == null)
      throw new IllegalArgumentException("API operations consumer cannot be null");

    try (var global = getGlobal();
        var api = global.getProperty("Api")) {
      apiOperations.accept(api);
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute API operations", e);
    }

    return this;
  }

  /**
   * Executes document operations on the current document.
   *
   * <p>This method provides a convenient way to execute document operations within a properly
   * managed resource scope. Both the API and document objects are automatically retrieved and
   * disposed of.
   *
   * @param documentOperations the consumer that will receive the document object for operations
   * @return this DocBuilderContext for method chaining
   * @throws IllegalArgumentException if documentOperations is null
   * @throws RuntimeException if the document operations fail or if the native operations fail
   */
  public DocBuilderContext withDocument(Consumer<DocBuilderValue> documentOperations) {
    if (documentOperations == null)
      throw new IllegalArgumentException("Document operations consumer cannot be null");

    try (var global = getGlobal();
        var api = global.getProperty("Api");
        var document = api.call("GetDocument")) {
      documentOperations.accept(document);
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute document operations", e);
    }

    return this;
  }

  /**
   * Executes operations on both the API and document objects.
   *
   * <p>This method provides a convenient way to execute operations on both the API and document
   * objects within a properly managed resource scope. All objects are automatically retrieved and
   * disposed of.
   *
   * @param operations the bi-consumer that will receive both the API and document objects
   * @return this DocBuilderContext for method chaining
   * @throws IllegalArgumentException if operations is null
   * @throws RuntimeException if the operations fail or if the native operations fail
   */
  public DocBuilderContext withDocument(BiConsumer<DocBuilderValue, DocBuilderValue> operations) {
    if (operations == null)
      throw new IllegalArgumentException("Operations bi-consumer cannot be null");

    try (var global = getGlobal();
        var api = global.getProperty("Api");
        var document = api.call("GetDocument")) {
      operations.accept(api, document);
    } catch (Exception e) {
      throw new RuntimeException("Failed to execute API and document operations", e);
    }

    return this;
  }

  /**
   * Executes a function on this context and returns the result.
   *
   * <p>This method allows for complex operations to be executed while maintaining the fluent API
   * pattern. The function receives this context and can perform any operations on it.
   *
   * @param <T> the type of the result
   * @param operation the function to execute on this context
   * @return the result of the operation
   * @throws IllegalArgumentException if operation is null
   * @throws RuntimeException if the operation fails
   */
  public <T> T execute(Function<DocBuilderContext, T> operation) {
    if (operation == null) throw new IllegalArgumentException("Operation function cannot be null");
    return operation.apply(this);
  }

  /**
   * Executes a consumer operation on this context.
   *
   * <p>This method allows for operations to be executed while maintaining the fluent API pattern.
   * The consumer receives this context and can perform any operations on it.
   *
   * @param operation the consumer to execute on this context
   * @return this DocBuilderContext for method chaining
   * @throws IllegalArgumentException if operation is null
   * @throws RuntimeException if the operation fails
   */
  public DocBuilderContext execute(Consumer<DocBuilderContext> operation) {
    if (operation == null) throw new IllegalArgumentException("Operation consumer cannot be null");
    operation.accept(this);
    return this;
  }

  /**
   * Closes this DocBuilderContext and releases all associated native resources.
   *
   * <p>This method should be called when the context is no longer needed to prevent resource leaks.
   * The context should not be used after calling this method.
   *
   * @throws Exception if an error occurs while closing the native resources
   */
  @Override
  public void close() throws Exception {
    delegate.close();
  }

  /**
   * A functional interface for operations that consume two values.
   *
   * <p>This interface is provided for convenience and follows the same pattern as Java's built-in
   * BiConsumer interface.
   *
   * @param <T> the type of the first argument
   * @param <U> the type of the second argument
   */
  @FunctionalInterface
  public interface BiConsumer<T, U> {
    /**
     * Performs the operation on the given arguments.
     *
     * @param t the first argument
     * @param u the second argument
     */
    void accept(T t, U u);
  }
}
