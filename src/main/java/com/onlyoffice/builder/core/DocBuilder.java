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

import com.onlyoffice.builder.loader.LibraryLoader;
import com.onlyoffice.builder.loader.NativeDocBuilderLoader;
import docbuilder.CDocBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A high-level wrapper around the native DocBuilder library that provides document creation,
 * manipulation, and conversion capabilities.
 *
 * <p>This class is designed to be used exclusively through the DocumentSession Fluent API. Direct
 * instantiation is restricted to enforce proper resource management and the Fluent API pattern.
 *
 * <p>The DocBuilder supports various document formats and provides methods for:
 *
 * <ul>
 *   <li>Opening and creating documents
 *   <li>Saving documents in different formats
 *   <li>Executing commands and scripts
 *   <li>Managing document context and properties
 * </ul>
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 * @see DocumentSession
 * @see CDocBuilder
 * @see DocBuilderContext
 * @see LibraryLoader
 */
public class DocBuilder implements AutoCloseable {
  private static final Logger logger = LoggerFactory.getLogger(DocBuilder.class);

  private static final String ERROR_NATIVE_LIBRARIES_NOT_LOADED =
      "Builder can't be initialized without the native libraries";
  private static final String LOG_BUILDER_CREATED = "DocBuilder created successfully";
  private static final String LOG_CLOSE_COMPLETED = "close() completed";

  private final CDocBuilder delegate;

  /**
   * Package-private constructor for DocBuilder.
   *
   * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
   * DocumentSession and other core classes can create DocBuilder instances.
   *
   * @param loader the library loader to use for native library validation
   * @throws IllegalArgumentException if native libraries are not loaded
   */
  DocBuilder(LibraryLoader loader) {
    validateNativeLibraries(loader);
    this.delegate = new CDocBuilder();
    logger.debug(LOG_BUILDER_CREATED);
  }

  /**
   * Executes an operation with logging for both method entry and result.
   *
   * @param <T> the return type of the operation
   * @param methodName the name of the method being executed
   * @param operation the operation to execute
   * @param format the format string for logging parameters
   * @param args the arguments for the format string
   * @return the result of the operation
   */
  private <T> T logAndExecute(
      String methodName, Operation<T> operation, String format, Object... args) {
    logger.debug("{}() called with {}", methodName, String.format(format, args));
    var result = operation.execute();
    logger.debug("{}() result: {}", methodName, result);
    return result;
  }

  /**
   * Executes an operation with logging for both method entry and result.
   *
   * @param <T> the return type of the operation
   * @param methodName the name of the method being executed
   * @param operation the operation to execute
   * @return the result of the operation
   */
  private <T> T logAndExecute(String methodName, Operation<T> operation) {
    logger.debug("{}() called", methodName);
    var result = operation.execute();
    logger.debug("{}() result: {}", methodName, result);
    return result;
  }

  /**
   * Opens a document file with the specified path and parameters.
   *
   * <p>This method opens an existing document file and loads it into the builder for further
   * manipulation. The file must exist and be accessible at the specified path.
   *
   * @param filePath the absolute or relative path to the file to open
   * @param params additional parameters for opening the file (can be null for default settings)
   * @return a positive integer representing the document handle on success, or a negative value on
   *     failure (typically -1 for file not found, -2 for unsupported format, -3 for corrupted file)
   * @throws IllegalArgumentException if filePath is null or empty
   */
  int openFile(String filePath, String params) {
    return logAndExecute(
        "openFile",
        () -> delegate.openFile(filePath, params),
        "filePath: {}, params: {}",
        filePath,
        params);
  }

  /**
   * Creates a new document of the specified type.
   *
   * <p>Creates a new empty document based on the document type identifier. The document type should
   * correspond to a supported format as defined in the Document Builder API.
   *
   * @param type the document type identifier (use constants from Files.Document, Files.Spreadsheet,
   *     etc.)
   * @return true if the document was created successfully, false otherwise
   * @throws IllegalArgumentException if type is not a valid document type identifier
   */
  boolean createFile(int type) {
    return logAndExecute("createFile", () -> delegate.createFile(type), "type: {}", type);
  }

  /**
   * Creates a new document with the specified file extension.
   *
   * <p>Creates a new empty document based on the file extension, which determines the document
   * format. Supported extensions include: "docx", "xlsx", "pptx", "pdf", "rtf", "txt", "html".
   *
   * @param extension the file extension indicating the document format (case-insensitive)
   * @return true if the document was created successfully, false otherwise
   * @throws IllegalArgumentException if extension is null, empty, or not supported
   */
  boolean createFile(String extension) {
    return logAndExecute(
        "createFile", () -> delegate.createFile(extension), "extension: {}", extension);
  }

  /**
   * Closes the currently open document.
   *
   * <p>This method closes the document that is currently loaded in the builder, freeing up memory
   * and resources associated with it.
   */
  void closeFile() {
    logAndExecute(
        "closeFile",
        () -> {
          delegate.closeFile();
          return null;
        });
  }

  /**
   * Saves the current document to a file with the specified type and path.
   *
   * <p>Saves the document in the specified format to the given file path. The document must be open
   * before calling this method. The target directory must exist and be writable.
   *
   * @param type the document type/format for saving (use constants from Files.Document,
   *     Files.Spreadsheet, etc.)
   * @param path the absolute or relative file path where the document should be saved
   * @return a positive integer on success, or a negative value on failure (-1 for file access
   *     error, -2 for unsupported format)
   * @throws IllegalStateException if no document is currently open
   * @throws IllegalArgumentException if path is null or empty, or type is invalid
   */
  int saveFile(int type, String path) {
    return logAndExecute(
        "saveFile", () -> delegate.saveFile(type, path), "type: {}, path: {}", type, path);
  }

  /**
   * Saves the current document to a file with the specified type, path, and parameters.
   *
   * <p>Saves the document in the specified format to the given file path with additional save
   * parameters for customization. Parameters can include encoding, compression, and other
   * format-specific options.
   *
   * @param type the document type/format for saving (use constants from Files.Document,
   *     Files.Spreadsheet, etc.)
   * @param path the absolute or relative file path where the document should be saved
   * @param params additional parameters for saving (can be null for default settings, JSON format
   *     for custom options)
   * @return a positive integer on success, or a negative value on failure (-1 for file access
   *     error, -2 for unsupported format)
   * @throws IllegalStateException if no document is currently open
   * @throws IllegalArgumentException if path is null or empty, or type is invalid
   */
  int saveFile(int type, String path, String params) {
    return logAndExecute(
        "saveFile",
        () -> delegate.saveFile(type, path, params),
        "type: {}, path: {}, params: {}",
        type,
        path,
        params);
  }

  /**
   * Saves the current document to a file with the specified extension and path.
   *
   * <p>Saves the document in the format indicated by the extension to the given file path. The
   * target directory must exist and be writable.
   *
   * @param extension the file extension indicating the save format (e.g., "docx", "pdf", "html")
   * @param path the absolute or relative file path where the document should be saved
   * @return a positive integer on success, or a negative value on failure (-1 for file access
   *     error, -2 for unsupported format)
   * @throws IllegalStateException if no document is currently open
   * @throws IllegalArgumentException if path is null or empty, or extension is not supported
   */
  int saveFile(String extension, String path) {
    return logAndExecute(
        "saveFile",
        () -> delegate.saveFile(extension, path),
        "extension: {}, path: {}",
        extension,
        path);
  }

  /**
   * Saves the current document to a file with the specified extension, path, and parameters.
   *
   * <p>Saves the document in the format indicated by the extension to the given file path with
   * additional save parameters for customization. Parameters can include encoding, compression, and
   * other format-specific options.
   *
   * @param extension the file extension indicating the save format (e.g., "docx", "pdf", "html")
   * @param path the absolute or relative file path where the document should be saved
   * @param params additional parameters for saving (can be null for default settings, JSON format
   *     for custom options)
   * @return a positive integer on success, or a negative value on failure (-1 for file access
   *     error, -2 for unsupported format)
   * @throws IllegalStateException if no document is currently open
   * @throws IllegalArgumentException if path is null or empty, or extension is not supported
   */
  int saveFile(String extension, String path, String params) {
    return logAndExecute(
        "saveFile",
        () -> delegate.saveFile(extension, path, params),
        "extension: {}, path: {}, params: {}",
        extension,
        path,
        params);
  }

  /**
   * Executes a command on the current document.
   *
   * <p>Executes a document manipulation command using the builder's scripting engine. The command
   * should be a valid JavaScript-like script command that can be applied to the document. Commands
   * follow the ONLYOFFICE Document Builder API syntax.
   *
   * @param command the command string to execute (e.g., "oDocument.InsertText('Hello')")
   * @return true if the command was executed successfully, false otherwise
   * @throws IllegalStateException if no document is currently open
   * @throws IllegalArgumentException if command is null or empty
   */
  boolean executeCommand(String command) {
    return logAndExecute(
        "executeCommand", () -> delegate.executeCommand(command), "command: {}", command);
  }

  /**
   * Executes a command on the current document and stores the return value.
   *
   * <p>Executes a document manipulation command and stores the result in the provided return value
   * object for further processing. This is useful when you need to capture the result of a command
   * for subsequent operations.
   *
   * @param command the command string to execute (e.g., "oDocument.GetContent()")
   * @param returnValue the object to store the command's return value (must not be null)
   * @return true if the command was executed successfully, false otherwise
   * @throws IllegalStateException if no document is currently open
   * @throws IllegalArgumentException if command is null or empty, or returnValue is null
   */
  boolean executeCommand(String command, DocBuilderValue returnValue) {
    return logAndExecute(
        "executeCommand",
        () -> delegate.executeCommand(command, returnValue.getDelegate()),
        "command: {}, returnValue: {}",
        command,
        returnValue);
  }

  /**
   * Executes a script from a file.
   *
   * <p>Loads and executes a script file containing document manipulation commands. The script file
   * should contain valid JavaScript-like commands that follow the ONLYOFFICE Document Builder API
   * syntax. The file must exist and be readable.
   *
   * @param scriptPath the absolute or relative path to the script file to execute
   * @return true if the script was executed successfully, false otherwise
   * @throws IllegalStateException if no document is currently open
   * @throws IllegalArgumentException if scriptPath is null or empty
   */
  boolean runScript(String scriptPath) {
    return logAndExecute("runScript", () -> delegate.run(scriptPath), "scriptPath: {}", scriptPath);
  }

  /**
   * Executes a script from a string.
   *
   * <p>Executes a script string containing document manipulation commands. The script should
   * contain valid JavaScript-like commands that follow the ONLYOFFICE Document Builder API syntax.
   * This method is useful for executing dynamic scripts or scripts generated at runtime.
   *
   * @param script the script string to execute (e.g., "oDocument.InsertText('Hello');
   *     oDocument.SetBold(true);")
   * @return true if the script was executed successfully, false otherwise
   * @throws IllegalStateException if no document is currently open
   * @throws IllegalArgumentException if script is null or empty
   */
  boolean runScriptText(String script) {
    return logAndExecute("runScriptText", () -> delegate.runText(script), "script: {}", script);
  }

  /**
   * Sets the temporary folder path for the builder.
   *
   * <p>Specifies the directory where temporary files will be stored during document processing
   * operations. This directory must exist and be writable. Temporary files are automatically
   * cleaned up when the builder is closed.
   *
   * @param path the absolute or relative path to the temporary folder
   * @throws IllegalArgumentException if path is null or empty, or the directory doesn't exist
   */
  void setTmpFolder(String path) {
    logAndExecute(
        "setTmpFolder",
        () -> {
          delegate.setTmpFolder(path);
          return null;
        },
        "path: {}",
        path);
  }

  /**
   * Sets a property for the builder.
   *
   * <p>Sets a configuration property that affects the behavior of the builder and its operations.
   * Properties can control various aspects such as encoding, compression, compatibility modes, and
   * other builder-specific settings.
   *
   * @param name the name of the property to set (case-sensitive)
   * @param value the value to assign to the property (can be null to reset to default)
   * @throws IllegalArgumentException if name is null or empty
   */
  void setProperty(String name, String value) {
    logAndExecute(
        "setProperty",
        () -> {
          delegate.setProperty(name, value);
          return null;
        },
        "name: {}, value: {}",
        name,
        value);
  }

  /**
   * Writes data to the builder.
   *
   * <p>Writes named data to the builder, which can be used by scripts or commands during document
   * processing. This data persists for the lifetime of the builder instance and can be accessed by
   * scripts using the builder's data access methods.
   *
   * @param name the name identifier for the data (must be unique within the builder instance)
   * @param data the data content to write (can be null for empty data)
   * @param isBinary whether the data is binary content (affects how the data is stored and
   *     processed)
   * @throws IllegalArgumentException if name is null or empty
   */
  void writeData(String name, String data, boolean isBinary) {
    logAndExecute(
        "writeData",
        () -> {
          delegate.writeData(name, data, isBinary);
          return null;
        },
        "name: {}, data: {}, isBinary: {}",
        name,
        data,
        isBinary);
  }

  /**
   * Checks if the builder is in save with doctrenderer mode.
   *
   * <p>Returns whether the builder is configured to save documents using the doctrenderer mode,
   * which affects how documents are processed during saving.
   *
   * @return true if doctrenderer mode is enabled for saving, false otherwise
   */
  boolean isSaveWithDoctrendererMode() {
    return logAndExecute("isSaveWithDoctrendererMode", delegate::isSaveWithDoctrendererMode);
  }

  /**
   * Gets the version of the DocBuilder library.
   *
   * <p>Returns a string representation of the current version of the underlying DocBuilder library.
   *
   * @return the version string of the DocBuilder library
   */
  String getVersion() {
    return logAndExecute("getVersion", delegate::getVersion);
  }

  /**
   * Gets the current document context.
   *
   * <p>Returns a wrapper around the current document context, which provides access to the
   * document's structure and content. The context is automatically entered when retrieved. This
   * method is equivalent to calling {@code getContext(true)}.
   *
   * @return a DocBuilderContext wrapper for the current document context
   * @throws IllegalStateException if no document is currently open
   */
  DocBuilderContext getContext() {
    return logAndExecute("getContext", () -> new DocBuilderContext(delegate.getContext()));
  }

  /**
   * Gets the document context with optional entry control.
   *
   * <p>Returns a wrapper around the document context, with the option to control whether entering
   * the context is required. When {@code enter} is true, the context is activated for operations.
   * When false, the context is retrieved without activation, which can be useful for inspection
   * without modifying the current execution state.
   *
   * @param enter whether to enter the context when getting it (true for active operations, false
   *     for inspection only)
   * @return a DocBuilderContext wrapper for the document context
   * @throws IllegalStateException if no document is currently open
   */
  DocBuilderContext getContext(boolean enter) {
    return logAndExecute(
        "getContext", () -> new DocBuilderContext(delegate.getContext(enter)), "enter: {}", enter);
  }

  /**
   * Closes the DocBuilder and releases associated resources.
   *
   * <p>This method implements the AutoCloseable interface and ensures proper cleanup of resources.
   * It closes any open document but does not dispose of the native resources to prevent
   * segmentation faults when multiple DocBuilder instances are created.
   *
   * <p>This method is automatically called when using try-with-resources statements.
   */
  @Override
  public void close() {
    logger.debug("close() called");
    try {
      closeFile();
    } finally {
      // Don't call delegate.close() here as it disposes native resources
      // The native resources should only be disposed once at the end
      // This prevents segfaults when multiple DocBuilder instances are created
      logger.debug(LOG_CLOSE_COMPLETED);
    }
  }

  /**
   * Validates that native libraries are properly loaded.
   *
   * <p>Checks if the native DocBuilder libraries are available and loaded before allowing the
   * builder to be initialized.
   *
   * @param loader the library loader to check
   * @throws IllegalArgumentException if native libraries are not loaded
   */
  private void validateNativeLibraries(LibraryLoader loader) {
    if (!NativeDocBuilderLoader.getInstance(loader).isLoaded())
      throw new IllegalArgumentException(ERROR_NATIVE_LIBRARIES_NOT_LOADED);
  }

  /**
   * Functional interface for operations that return a value.
   *
   * @param <T> the return type of the operation
   */
  @FunctionalInterface
  private interface Operation<T> {
    /**
     * Executes the operation.
     *
     * @return the result of the operation
     */
    T execute();
  }

  /**
   * Functional interface for operations that return void.
   *
   * <p>Note: This interface is currently unused but kept for potential future use.
   */
  @FunctionalInterface
  private interface VoidOperation {
    /** Executes the operation. */
    void execute();
  }
}
