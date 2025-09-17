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
package docbuilder;

/**
 * JNI wrapper class for the native document builder context.
 *
 * <p>This class provides a Java interface to the underlying C++ document builder context through
 * JNI (Java Native Interface). The context represents the current execution environment for
 * document operations and provides methods to create various data types and manage scopes.
 *
 * <p>The class implements {@link AutoCloseable} to ensure proper resource cleanup of native
 * resources when used in try-with-resources statements.
 *
 * <p><strong>Important:</strong> This class manages native memory resources. Always call {@link
 * #close()} or use try-with-resources to prevent memory leaks.
 *
 * <p><strong>Thread Safety:</strong> This class is not thread-safe. Each instance should be used by
 * a single thread.
 *
 * <p><strong>Usage:</strong> Context instances are typically obtained from a {@link CDocBuilder}
 * instance and provide the execution environment for document manipulation operations.
 *
 * @see CDocBuilder
 * @see CDocBuilderValue
 * @see CDocBuilderContextScope
 * @version 0.1.0
 * @since 0.1.0
 */
public class CDocBuilderContext implements AutoCloseable {
  /** Native handle to the underlying C++ document builder context instance */
  final long c_internal;

  /**
   * Package-private constructor for creating contexts from native handles.
   *
   * <p>This constructor is used internally by the library to create context instances from native
   * handles returned by other operations.
   *
   * @param value the native handle to the context
   */
  CDocBuilderContext(long value) {
    this.c_internal = value;
  }

  /**
   * Creates a new document builder context instance.
   *
   * <p>This constructor creates a new native context instance. The context provides the execution
   * environment for document operations.
   *
   * @throws RuntimeException if the native context cannot be created
   */
  public CDocBuilderContext() {
    this.c_internal = c_Create();
  }

  /**
   * Creates a copy of an existing document builder context.
   *
   * <p>This constructor creates a new context that is a copy of the provided context. This can be
   * useful for creating independent working copies of contexts.
   *
   * @param context the context to copy
   * @throws IllegalArgumentException if context is null
   */
  public CDocBuilderContext(CDocBuilderContext context) {
    this.c_internal = c_Copy(context.c_internal);
  }

  /**
   * Creates an undefined value in this context.
   *
   * <p>Undefined values represent uninitialized or missing data in the document builder's scripting
   * environment.
   *
   * @return a new {@link CDocBuilderValue} representing an undefined value
   */
  public CDocBuilderValue createUndefined() {
    return new CDocBuilderValue(c_CreateUndefined(this.c_internal));
  }

  /**
   * Creates a null value in this context.
   *
   * <p>Null values represent the absence of a value in the document builder's scripting
   * environment.
   *
   * @return a new {@link CDocBuilderValue} representing a null value
   */
  public CDocBuilderValue createNull() {
    return new CDocBuilderValue(c_CreateNull(this.c_internal));
  }

  /**
   * Creates an empty object in this context.
   *
   * <p>Objects can contain properties and methods for complex data structures in the document
   * builder's scripting environment.
   *
   * @return a new {@link CDocBuilderValue} representing an empty object
   */
  public CDocBuilderValue createObject() {
    return new CDocBuilderValue(c_CreateObject(this.c_internal));
  }

  /**
   * Creates an array with the specified length in this context.
   *
   * <p>Arrays provide indexed access to collections of values in the document builder's scripting
   * environment.
   *
   * @param length the initial length of the array
   * @return a new {@link CDocBuilderValue} representing an array
   * @throws IllegalArgumentException if length is negative
   */
  public CDocBuilderValue createArray(int length) {
    return new CDocBuilderValue(c_CreateArray(this.c_internal, length));
  }

  /**
   * Gets the global context object.
   *
   * <p>The global context provides access to built-in functions, constants, and other globally
   * available resources in the document builder's scripting environment.
   *
   * @return a new {@link CDocBuilderValue} representing the global context
   */
  public CDocBuilderValue getGlobal() {
    return new CDocBuilderValue(c_GetGlobal(this.c_internal));
  }

  /**
   * Creates a new scope within this context.
   *
   * <p>Scopes provide isolated execution environments for operations, allowing for better control
   * over variable visibility and lifetime.
   *
   * @return a new {@link CDocBuilderContextScope} instance
   */
  public CDocBuilderContextScope createScope() {
    return new CDocBuilderContextScope(c_CreateScope(this.c_internal));
  }

  /**
   * Checks if an error has occurred in this context.
   *
   * <p>This method can be used to check for errors after executing operations that might fail.
   *
   * @return true if an error has occurred, false otherwise
   */
  public boolean isError() {
    return c_IsError(this.c_internal);
  }

  /**
   * Closes this context instance and releases native resources.
   *
   * <p>This method is called automatically when using try-with-resources. It destroys the native
   * context instance and frees associated memory.
   *
   * <p><strong>Important:</strong> Always call this method or use try-with-resources to prevent
   * memory leaks from native resources.
   *
   * @throws Exception if an error occurs during cleanup
   */
  @Override
  public void close() throws Exception {
    c_Destroy(this.c_internal);
  }

  /** Creates a new native context instance */
  private static native long c_Create();

  /** Copies an existing native context instance */
  private static native long c_Copy(long handle);

  /** Destroys a native context instance */
  private static native void c_Destroy(long handle);

  /** Creates an undefined value in the native context */
  private static native long c_CreateUndefined(long handle);

  /** Creates a null value in the native context */
  private static native long c_CreateNull(long handle);

  /** Creates an object in the native context */
  private static native long c_CreateObject(long handle);

  /** Creates an array in the native context */
  private static native long c_CreateArray(long handle, int length);

  /** Gets the global context from the native context */
  private static native long c_GetGlobal(long handle);

  /** Creates a scope in the native context */
  private static native long c_CreateScope(long handle);

  /** Checks if an error has occurred in the native context */
  private static native boolean c_IsError(long handle);
}
