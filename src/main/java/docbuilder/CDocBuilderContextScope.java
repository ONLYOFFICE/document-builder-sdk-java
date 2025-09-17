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
 * JNI wrapper class for the native document builder context scope.
 *
 * <p>This class provides a Java interface to the underlying C++ document builder context scope
 * through JNI (Java Native Interface). Context scopes provide isolated execution environments
 * within a document builder context, allowing for better control over variable visibility,
 * lifetime, and execution isolation.
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
 * <p><strong>Usage:</strong> Scope instances are typically created from a {@link
 * CDocBuilderContext} instance and provide isolated execution environments for document
 * manipulation operations. Scopes are useful for managing variable scope and ensuring proper
 * cleanup of temporary resources.
 *
 * @see CDocBuilderContext
 * @see CDocBuilder
 * @version 0.1.0
 * @since 0.1.0
 */
public class CDocBuilderContextScope implements AutoCloseable {
  /** Native handle to the underlying C++ document builder context scope instance */
  final long c_internal;

  /**
   * Package-private constructor for creating scopes from native handles.
   *
   * <p>This constructor is used internally by the library to create scope instances from native
   * handles returned by other operations, such as {@link CDocBuilderContext#createScope()}.
   *
   * @param value the native handle to the scope
   */
  CDocBuilderContextScope(long value) {
    this.c_internal = value;
  }

  /**
   * Creates a new document builder context scope instance.
   *
   * <p>This constructor creates a new native scope instance. The scope provides an isolated
   * execution environment within the document builder context.
   *
   * @throws RuntimeException if the native scope cannot be created
   */
  public CDocBuilderContextScope() {
    this.c_internal = c_Create();
  }

  /**
   * Creates a copy of an existing document builder context scope.
   *
   * <p>This constructor creates a new scope that is a copy of the provided scope. This can be
   * useful for creating independent working copies of scopes or for scope inheritance scenarios.
   *
   * @param other the scope to copy
   * @throws IllegalArgumentException if other is null
   */
  public CDocBuilderContextScope(CDocBuilderContextScope other) {
    this.c_internal = c_Copy(other.c_internal);
  }

  /**
   * Closes this scope instance and releases native resources.
   *
   * <p>This method is called automatically when using try-with-resources. It destroys the native
   * scope instance and frees associated memory. Closing a scope typically also cleans up any
   * variables or resources that were created within that scope.
   *
   * <p><strong>Important:</strong> Always call this method or use try-with-resources to prevent
   * memory leaks from native resources. Scopes should be closed in the reverse order they were
   * created to maintain proper resource cleanup.
   */
  @Override
  public void close() {
    c_Destroy(this.c_internal);
  }

  /** Creates a new native context scope instance */
  private static native long c_Create();

  /** Copies an existing native context scope instance */
  private static native long c_Copy(long handle);

  /** Destroys a native context scope instance */
  private static native void c_Destroy(long handle);

  /** Closes a native context scope instance (alternative to destroy) */
  private static native void c_Close(long handle);
}
