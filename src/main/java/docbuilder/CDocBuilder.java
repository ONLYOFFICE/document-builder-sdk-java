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
 * JNI wrapper class for the native document builder library.
 *
 * <p>This class provides a Java interface to the underlying C++ document builder library through
 * JNI (Java Native Interface). It handles document creation, manipulation, and conversion
 * operations.
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
 * @see CDocBuilderContext
 * @see CDocBuilderValue
 * @version 0.1.0
 * @since 0.1.0
 */
public class CDocBuilder implements AutoCloseable {
  /** Native handle to the underlying C++ document builder instance */
  final long c_internal;

  /**
   * Creates a new document builder instance.
   *
   * <p>This constructor initializes the native document builder and allocates necessary resources.
   * The native instance will be created immediately.
   *
   * @throws RuntimeException if the native document builder cannot be created
   */
  public CDocBuilder() {
    this.c_internal = c_Create();
  }

  /**
   * Opens an existing document file for editing or manipulation.
   *
   * @param filePath the path to the document file to open
   * @param params optional parameters for opening the file (can be null)
   * @return status code indicating success or failure of the operation
   * @throws IllegalArgumentException if filePath is null or empty
   */
  public int openFile(String filePath, String params) {
    return c_OpenFile(this.c_internal, filePath, params);
  }

  /**
   * Creates a new document file of the specified type.
   *
   * @param type the document type identifier (use constants defined in the native library)
   * @return true if the document was created successfully, false otherwise
   */
  public boolean createFile(int type) {
    return c_CreateFileByType(this.c_internal, type);
  }

  /**
   * Creates a new document file based on the file extension.
   *
   * @param extension the file extension (e.g., "docx", "xlsx", "pptx")
   * @return true if the document was created successfully, false otherwise
   * @throws IllegalArgumentException if extension is null or empty
   */
  public boolean createFile(String extension) {
    return c_CreateFileByExtension(this.c_internal, extension);
  }

  /**
   * Sets the temporary folder path for document processing operations.
   *
   * <p>This method configures where temporary files will be stored during document operations such
   * as conversion or processing.
   *
   * @param path the path to the temporary folder
   * @throws IllegalArgumentException if path is null or empty
   */
  public void setTmpFolder(String path) {
    c_SetTmpFolder(this.c_internal, path);
  }

  /**
   * Saves the current document to a file with the specified type.
   *
   * @param type the document type identifier for saving
   * @param path the file path where the document should be saved
   * @return status code indicating success or failure of the save operation
   * @throws IllegalArgumentException if path is null or empty
   */
  public int saveFile(int type, String path) {
    return c_SaveFileByType(this.c_internal, type, path);
  }

  /**
   * Saves the current document to a file with the specified type and additional parameters.
   *
   * @param type the document type identifier for saving
   * @param path the file path where the document should be saved
   * @param params additional parameters for the save operation (can be null)
   * @return status code indicating success or failure of the save operation
   * @throws IllegalArgumentException if path is null or empty
   */
  public int saveFile(int type, String path, String params) {
    return c_SaveFileByTypeWithParams(this.c_internal, type, path, params);
  }

  /**
   * Saves the current document to a file with the specified extension.
   *
   * @param extension the file extension for the output file (e.g., "pdf", "docx")
   * @param path the file path where the document should be saved
   * @return status code indicating success or failure of the save operation
   * @throws IllegalArgumentException if extension or path is null or empty
   */
  public int saveFile(String extension, String path) {
    return c_SaveFileByExtension(this.c_internal, extension, path);
  }

  /**
   * Saves the current document to a file with the specified extension and additional parameters.
   *
   * @param extension the file extension for the output file (e.g., "pdf", "docx")
   * @param path the file path where the document should be saved
   * @param params additional parameters for the save operation (can be null)
   * @return status code indicating success or failure of the save operation
   * @throws IllegalArgumentException if extension or path is null or empty
   */
  public int saveFile(String extension, String path, String params) {
    return c_SaveFileByExtensionWithParams(this.c_internal, extension, path, params);
  }

  /**
   * Closes the currently open document.
   *
   * <p>This method releases resources associated with the current document but keeps the document
   * builder instance active for further operations.
   */
  public void closeFile() {
    c_CloseFile(this.c_internal);
  }

  /**
   * Executes a command on the current document.
   *
   * <p>Commands are typically used to manipulate document content, formatting, or perform other
   * document-specific operations.
   *
   * @param command the command string to execute
   * @return true if the command was executed successfully, false otherwise
   * @throws IllegalArgumentException if command is null or empty
   */
  public boolean executeCommand(String command) {
    return c_ExecuteCommand(this.c_internal, command);
  }

  /**
   * Executes a command on the current document and returns a value.
   *
   * <p>This method executes a command and stores the return value in the provided {@link
   * CDocBuilderValue} object.
   *
   * @param command the command string to execute
   * @param returnValue the object to store the command's return value
   * @return true if the command was executed successfully, false otherwise
   * @throws IllegalArgumentException if command is null or empty, or if returnValue is null
   */
  public boolean executeCommand(String command, CDocBuilderValue returnValue) {
    return c_ExecuteCommandWithRetValue(this.c_internal, command, returnValue.c_internal);
  }

  /**
   * Runs a script file on the current document.
   *
   * <p>Scripts can contain multiple commands and provide more complex document manipulation
   * workflows.
   *
   * @param scriptPath the path to the script file to execute
   * @return true if the script was executed successfully, false otherwise
   * @throws IllegalArgumentException if scriptPath is null or empty
   */
  public boolean run(String scriptPath) {
    return c_Run(this.c_internal, scriptPath);
  }

  /**
   * Executes script content directly from a string.
   *
   * <p>This method allows executing script commands without requiring a separate script file.
   *
   * @param script the script content to execute
   * @return true if the script was executed successfully, false otherwise
   * @throws IllegalArgumentException if script is null or empty
   */
  public boolean runText(String script) {
    return c_RunText(this.c_internal, script);
  }

  /**
   * Sets a property for the document builder instance.
   *
   * <p>Properties can configure various aspects of document processing, such as formatting options,
   * conversion settings, or other behaviors.
   *
   * @param name the property name
   * @param value the property value
   * @throws IllegalArgumentException if name or value is null
   */
  public void setProperty(String name, String value) {
    c_SetProperty(this.c_internal, name, value);
  }

  /**
   * Writes data to the document builder instance.
   *
   * <p>This method can be used to provide additional data or resources that may be needed during
   * document processing.
   *
   * @param name the name identifier for the data
   * @param data the data content to write
   * @param isBinary true if the data is binary, false if it's text
   * @throws IllegalArgumentException if name or data is null
   */
  public void writeData(String name, String data, boolean isBinary) {
    c_WriteData(this.c_internal, name, data, isBinary);
  }

  /**
   * Checks if the document builder is configured to save with doctrenderer mode.
   *
   * <p>Doctrenderer mode affects how documents are processed and saved, typically used for specific
   * document rendering scenarios.
   *
   * @return true if doctrenderer mode is enabled, false otherwise
   */
  public boolean isSaveWithDoctrendererMode() {
    return c_IsSaveWithDoctrendererMode(this.c_internal);
  }

  /**
   * Gets the version of the native document builder library.
   *
   * @return the version string of the native library
   */
  public String getVersion() {
    return c_GetVersion(this.c_internal);
  }

  /**
   * Gets the document builder context without entering it.
   *
   * <p>The context provides access to the current document's structure and allows for more detailed
   * manipulation operations.
   *
   * @return a new {@link CDocBuilderContext} instance
   */
  public CDocBuilderContext getContext() {
    return new CDocBuilderContext(c_GetContext(this.c_internal));
  }

  /**
   * Gets the document builder context with the option to enter it.
   *
   * <p>Entering the context may change the current working context and affect subsequent
   * operations.
   *
   * @param enter true to enter the context, false to just retrieve it
   * @return a new {@link CDocBuilderContext} instance
   */
  public CDocBuilderContext getContext(boolean enter) {
    return new CDocBuilderContext(c_GetContextWithEnterParam(this.c_internal, enter));
  }

  /**
   * Initializes the native document builder library.
   *
   * <p>This static method must be called before creating any {@link CDocBuilder} instances. It sets
   * up the native library and prepares it for use.
   *
   * <p><strong>Note:</strong> This method should typically be called once during application
   * startup.
   */
  public static void initialize() {
    c_Initialize();
  }

  /**
   * Initializes the native document builder library with a specific directory.
   *
   * <p>This method allows specifying a custom directory for library initialization, which may be
   * useful for custom library paths or configurations.
   *
   * @param directory the directory path for library initialization
   * @throws IllegalArgumentException if directory is null or empty
   */
  public static void initialize(String directory) {
    c_InitializeWithDirectory(directory);
  }

  /**
   * Disposes of the native document builder library.
   *
   * <p>This static method should be called during application shutdown to clean up native resources
   * and properly terminate the library.
   *
   * <p><strong>Note:</strong> This method should typically be called once during application
   * shutdown.
   */
  public static void dispose() {
    c_Dispose();
  }

  /**
   * Closes this document builder instance and releases native resources.
   *
   * <p>This method is called automatically when using try-with-resources. It destroys the native
   * document builder instance and frees associated memory.
   *
   * <p><strong>Important:</strong> Always call this method or use try-with-resources to prevent
   * memory leaks from native resources.
   */
  @Override
  public void close() {
    c_Destroy(this.c_internal);
  }

  /** Creates a new native document builder instance */
  private static native long c_Create();

  /** Destroys a native document builder instance */
  private static native void c_Destroy(long value);

  /** Opens a file in the native document builder */
  private static native int c_OpenFile(long handle, String filePath, String params);

  /** Creates a file by type in the native document builder */
  private static native boolean c_CreateFileByType(long handle, int type);

  /** Creates a file by extension in the native document builder */
  private static native boolean c_CreateFileByExtension(long handle, String extension);

  /** Sets the temporary folder in the native document builder */
  private static native void c_SetTmpFolder(long handle, String path);

  /** Saves a file by type in the native document builder */
  private static native int c_SaveFileByType(long handle, int type, String path);

  /** Saves a file by type with parameters in the native document builder */
  private static native int c_SaveFileByTypeWithParams(
      long handle, int type, String path, String params);

  /** Saves a file by extension in the native document builder */
  private static native int c_SaveFileByExtension(long handle, String extension, String path);

  /** Saves a file by extension with parameters in the native document builder */
  private static native int c_SaveFileByExtensionWithParams(
      long handle, String extension, String path, String params);

  /** Closes the current file in the native document builder */
  private static native void c_CloseFile(long handle);

  /** Executes a command in the native document builder */
  private static native boolean c_ExecuteCommand(long handle, String command);

  /** Executes a command with return value in the native document builder */
  private static native boolean c_ExecuteCommandWithRetValue(
      long handle, String command, long returnValueHandle);

  /** Runs a script file in the native document builder */
  private static native boolean c_Run(long handle, String scriptPath);

  /** Runs script text in the native document builder */
  private static native boolean c_RunText(long handle, String script);

  /** Sets a property in the native document builder */
  private static native void c_SetProperty(long handle, String name, String value);

  /** Writes data to the native document builder */
  private static native void c_WriteData(long handle, String name, String data, boolean isBinary);

  /** Checks if save with doctrenderer mode is enabled in the native document builder */
  private static native boolean c_IsSaveWithDoctrendererMode(long handle);

  /** Gets the version from the native document builder */
  private static native String c_GetVersion(long handle);

  /** Gets the context from the native document builder */
  private static native long c_GetContext(long handle);

  /** Gets the context with enter parameter from the native document builder */
  private static native long c_GetContextWithEnterParam(long handle, boolean enter);

  /** Initializes the native document builder library */
  private static native void c_Initialize();

  /** Initializes the native document builder library with directory */
  private static native void c_InitializeWithDirectory(String directory);

  /** Disposes of the native document builder library */
  private static native void c_Dispose();
}
