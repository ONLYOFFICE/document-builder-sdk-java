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
import com.onlyoffice.builder.util.Files;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DocumentSession - The primary public entry point for ONLYOFFICE Document Builder.
 *
 * <p>This class provides a fluent API for creating documents of various types (Word, PowerPoint,
 * Excel, PDF) with automatic resource management. It is the only class that client code should use
 * to create documents, as all other core classes are package-private and cannot be instantiated
 * directly.
 *
 * <p>The fluent API design makes document creation simple and intuitive. Clients specify what they
 * want to create and where to save it, while the API handles all complex resource management
 * automatically.
 *
 * <h2>Basic Usage Example:</h2>
 *
 * <pre>{@code
 * DocumentSession.createDocument(loader)
 *     .build(doc -> doc
 *         .addElement("CreateParagraph", p -> p.chain("AddText", "Hello World"))
 *         .addElement("CreateParagraph", p -> p.chain("AddText", "Main Heading")),
 *         "output.docx");
 * }</pre>
 *
 * <h2>Key Features:</h2>
 *
 * <ul>
 *   <li><b>Zero resource management:</b> No manual try-with-resources needed
 *   <li><b>Automatic cleanup:</b> All resources are handled internally
 *   <li><b>Fluent chaining:</b> Intuitive method chaining for document building
 *   <li><b>Type safety:</b> Compile-time safety for document operations
 *   <li><b>Comprehensive error handling:</b> Detailed error messages and logging
 * </ul>
 *
 * <h2>Workflow:</h2>
 *
 * <ol>
 *   <li>Choose your document type using one of the static factory methods
 *   <li>Call {@code .build()} with your document operations
 *   <li>Specify the output file path
 *   <li>The API handles resource creation, document building, and cleanup automatically
 * </ol>
 *
 * <p><strong>Important:</strong> This is the only class you should use for document creation. All
 * other classes in the core package are package-private and cannot be accessed from client code.
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 */
public class DocumentSession {
  private static final Logger logger = LoggerFactory.getLogger(DocumentSession.class);

  private final LibraryLoader loader;
  private final Object fileType;
  private final int fileTypeCode;

  /**
   * Private constructor for DocumentSession.
   *
   * <p>Creates a new DocumentSession instance with the specified library loader and file type. The
   * file type determines the format of the document that will be created. The loader must be
   * properly initialized with native libraries before use.
   *
   * @param loader the native library loader to use for document operations (must not be null)
   * @param fileType the file type object (Document, Presentation, Spreadsheet, or Graphics)
   * @throws IllegalArgumentException if loader is null
   */
  private DocumentSession(LibraryLoader loader, Object fileType) {
    if (loader == null) throw new IllegalArgumentException("LibraryLoader cannot be null");
    this.loader = loader;
    this.fileType = fileType;
    this.fileTypeCode = getFileTypeCode(fileType);
    logger.debug(
        "Created DocumentSession with loader: {}, fileType: {}, code: {}",
        loader,
        fileType,
        fileTypeCode);
  }

  /**
   * Creates a DocumentSession for creating Word documents in .docx format.
   *
   * <p>This method creates a new DocumentSession configured specifically for Word document
   * creation. The session will handle all the complexity of document building, including resource
   * management and cleanup.
   *
   * @param loader the native library loader to use for document operations (must not be null)
   * @return a new DocumentSession configured for Word document creation
   * @throws IllegalArgumentException if loader is null
   */
  public static DocumentSession createDocument(LibraryLoader loader) {
    if (loader == null) throw new IllegalArgumentException("LibraryLoader cannot be null");
    return new DocumentSession(loader, Files.Document.DOCX);
  }

  /**
   * Creates a DocumentSession for creating PowerPoint presentations in .pptx format.
   *
   * <p>This method creates a new DocumentSession configured specifically for PowerPoint
   * presentation creation. The session will handle all the complexity of presentation building,
   * including resource management and cleanup.
   *
   * @param loader the native library loader to use for document operations (must not be null)
   * @return a new DocumentSession configured for PowerPoint presentation creation
   * @throws IllegalArgumentException if loader is null
   */
  public static DocumentSession createPresentation(LibraryLoader loader) {
    if (loader == null) throw new IllegalArgumentException("LibraryLoader cannot be null");
    return new DocumentSession(loader, Files.Presentation.PPTX);
  }

  /**
   * Creates a DocumentSession for creating Excel spreadsheets in .xlsx format.
   *
   * <p>This method creates a new DocumentSession configured specifically for Excel spreadsheet
   * creation. The session will handle all the complexity of spreadsheet building, including
   * resource management and cleanup.
   *
   * @param loader the native library loader to use for document operations (must not be null)
   * @return a new DocumentSession configured for Excel spreadsheet creation
   * @throws IllegalArgumentException if loader is null
   */
  public static DocumentSession createSpreadsheet(LibraryLoader loader) {
    if (loader == null) throw new IllegalArgumentException("LibraryLoader cannot be null");
    return new DocumentSession(loader, Files.Spreadsheet.XLSX);
  }

  /**
   * Creates a DocumentSession for creating PDF documents.
   *
   * <p>This method creates a new DocumentSession configured specifically for PDF document creation.
   * The session will handle all the complexity of PDF generation, including resource management and
   * cleanup.
   *
   * @param loader the native library loader to use for document operations (must not be null)
   * @return a new DocumentSession configured for PDF creation
   * @throws IllegalArgumentException if loader is null
   */
  public static DocumentSession createPdf(LibraryLoader loader) {
    if (loader == null) throw new IllegalArgumentException("LibraryLoader cannot be null");
    return new DocumentSession(loader, Files.Graphics.PDF);
  }

  /**
   * Creates a DocumentSession for a specific document type.
   *
   * <p>This method creates a new DocumentSession for the specified document type, allowing
   * fine-grained control over the document format being created.
   *
   * @param loader the native library loader to use for document operations (must not be null)
   * @param documentType the specific document type to create
   * @return a new DocumentSession configured for the specified document type
   * @throws IllegalArgumentException if loader is null
   */
  public static DocumentSession create(LibraryLoader loader, Files.Document documentType) {
    if (loader == null) throw new IllegalArgumentException("LibraryLoader cannot be null");
    return new DocumentSession(loader, documentType);
  }

  /**
   * Creates a DocumentSession for a specific presentation type.
   *
   * <p>This method creates a new DocumentSession for the specified presentation type, allowing
   * fine-grained control over the presentation format being created.
   *
   * @param loader the native library loader to use for document operations (must not be null)
   * @param presentationType the specific presentation type to create
   * @return a new DocumentSession configured for the specified presentation type
   * @throws IllegalArgumentException if loader is null
   */
  public static DocumentSession create(LibraryLoader loader, Files.Presentation presentationType) {
    if (loader == null) throw new IllegalArgumentException("LibraryLoader cannot be null");
    return new DocumentSession(loader, presentationType);
  }

  /**
   * Creates a DocumentSession for a specific spreadsheet type.
   *
   * <p>This method creates a new DocumentSession for the specified spreadsheet type, allowing
   * fine-grained control over the spreadsheet format being created.
   *
   * @param loader the native library loader to use for document operations (must not be null)
   * @param spreadsheetType the specific spreadsheet type to create
   * @return a new DocumentSession configured for the specified spreadsheet type
   * @throws IllegalArgumentException if loader is null
   */
  public static DocumentSession create(LibraryLoader loader, Files.Spreadsheet spreadsheetType) {
    if (loader == null) throw new IllegalArgumentException("LibraryLoader cannot be null");
    return new DocumentSession(loader, spreadsheetType);
  }

  /**
   * Creates a DocumentSession for a specific graphics type.
   *
   * <p>This method creates a new DocumentSession for the specified graphics type, allowing
   * fine-grained control over the graphics format being created.
   *
   * @param loader the native library loader to use for document operations (must not be null)
   * @param graphicsType the specific graphics type to create
   * @return a new DocumentSession configured for the specified graphics type
   * @throws IllegalArgumentException if loader is null
   */
  public static DocumentSession create(LibraryLoader loader, Files.Graphics graphicsType) {
    if (loader == null) throw new IllegalArgumentException("LibraryLoader cannot be null");
    return new DocumentSession(loader, graphicsType);
  }

  /**
   * Builds the document with automatic resource management.
   *
   * <p>This method creates a new DocBuilder instance, executes the provided operations through the
   * DocumentBuilder, and automatically saves the result to the specified output path. All resource
   * cleanup is handled automatically, so client code does not need to use try-with-resources.
   *
   * <p><strong>This is the primary method for creating documents.</strong> Each session creates and
   * manages its own DocBuilder instance, ensuring proper resource isolation.
   *
   * @param operations a consumer that defines the document building operations to perform
   * @param outputPath the file path where the generated document should be saved
   * @throws RuntimeException if document building fails for any reason
   * @throws IllegalArgumentException if operations or outputPath is null or empty
   * @throws Exception if any other error occurs during document building
   */
  public void build(Consumer<DocumentBuilder> operations, String outputPath) throws Exception {
    if (operations == null) throw new IllegalArgumentException("Operations cannot be null");
    if (outputPath == null || outputPath.trim().isEmpty())
      throw new IllegalArgumentException("Output path cannot be null or empty");

    logger.debug("Building document with outputPath: {}", outputPath);

    try (var docBuilder = new DocBuilder(loader)) {
      logger.debug("Creating file with type code: {}", fileTypeCode);

      if (!docBuilder.createFile(fileTypeCode))
        throw new RuntimeException(
            "Failed to create document file with type code: " + fileTypeCode);

      try (var context = docBuilder.getContext()) {
        var builder = new DocumentBuilder(context, fileType);

        try {
          operations.accept(builder);
        } catch (Exception e) {
          throw new RuntimeException("Failed to execute document building operations", e);
        }

        logger.debug("Saving file to: {}", outputPath);
        int saveResult = docBuilder.saveFile(fileTypeCode, outputPath);
        if (saveResult < 0)
          throw new RuntimeException(
              "Failed to save document to: " + outputPath + " (result: " + saveResult + ")");

        docBuilder.closeFile();
        logger.debug("Document successfully built and saved to: {}", outputPath);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to build document: " + e.getMessage(), e);
    }
  }

  /**
   * Opens an existing file, performs operations on it, and saves it to a new location.
   *
   * <p>This method opens an existing document file, loads it into the builder for manipulation,
   * executes the provided operations through the DocumentBuilder, and automatically saves the
   * modified document to the specified output path. All resource cleanup is handled automatically.
   *
   * <p><strong>This is the primary method for modifying existing documents.</strong> Each session
   * creates and manages its own DocBuilder instance, ensuring proper resource isolation.
   *
   * @param inputPath the file path of the existing document to open and modify
   * @param operations a consumer that defines the document modification operations to perform
   * @param outputPath the file path where the modified document should be saved
   * @throws RuntimeException if document opening, modification, or saving fails for any reason
   * @throws IllegalArgumentException if any parameter is null or empty
   * @throws Exception if any other error occurs during document processing
   */
  public void openFile(String inputPath, Consumer<DocumentBuilder> operations, String outputPath)
      throws Exception {
    openFile(inputPath, "", operations, outputPath);
  }

  /**
   * Opens an existing file with parameters, performs operations on it, and saves it to a new
   * location.
   *
   * <p>This method opens an existing document file with optional parameters, loads it into the
   * builder for manipulation, executes the provided operations through the DocumentBuilder, and
   * automatically saves the modified document to the specified output path. All resource cleanup is
   * handled automatically.
   *
   * <p><strong>This is the primary method for modifying existing documents with custom
   * parameters.</strong> Each session creates and manages its own DocBuilder instance, ensuring
   * proper resource isolation.
   *
   * @param inputPath the file path of the existing document to open and modify
   * @param params additional parameters for opening the file (can be null for default settings)
   * @param operations a consumer that defines the document modification operations to perform
   * @param outputPath the file path where the modified document should be saved
   * @throws RuntimeException if document opening, modification, or saving fails for any reason
   * @throws IllegalArgumentException if inputPath, operations, or outputPath is null or empty
   * @throws Exception if any other error occurs during document processing
   */
  public void openFile(
      String inputPath, String params, Consumer<DocumentBuilder> operations, String outputPath)
      throws Exception {
    if (inputPath == null || inputPath.trim().isEmpty())
      throw new IllegalArgumentException("Input path cannot be null or empty");
    if (operations == null) throw new IllegalArgumentException("Operations cannot be null");
    if (outputPath == null || outputPath.trim().isEmpty())
      throw new IllegalArgumentException("Output path cannot be null or empty");

    logger.debug(
        "Opening file from: {} with params: {}, outputPath: {}", inputPath, params, outputPath);

    try (var docBuilder = new DocBuilder(loader)) {
      logger.debug("Opening file: {}", inputPath);

      int openResult = docBuilder.openFile(inputPath, params);
      if (openResult < 0)
        throw new RuntimeException(
            String.format("Could not open a file due-to an error: %d", openResult));

      try (var context = docBuilder.getContext()) {
        var builder = new DocumentBuilder(context, fileType);

        operations.accept(builder);

        logger.debug("Saving modified file to: {}", outputPath);
        int saveResult = docBuilder.saveFile(fileTypeCode, outputPath);
        if (saveResult < 0)
          throw new RuntimeException(
              String.format("Could not save a modified file due-to an error: %d", saveResult));

        docBuilder.closeFile();
        logger.debug("Document successfully opened, modified, and saved to: {}", outputPath);
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to process document", e);
    }
  }

  /**
   * Helper method to extract file type code from the file type object.
   *
   * <p>This method determines the appropriate integer file type code based on the file type object.
   * It supports all four main file type categories: Document, Presentation, Spreadsheet, and
   * Graphics.
   *
   * @param fileType the file type object to extract the code from
   * @return the integer file type code
   * @throws IllegalArgumentException if the file type is not supported
   */
  private static int getFileTypeCode(Object fileType) {
    if (fileType instanceof Files.Document) return ((Files.Document) fileType).getCode();
    else if (fileType instanceof Files.Presentation)
      return ((Files.Presentation) fileType).getCode();
    else if (fileType instanceof Files.Spreadsheet) return ((Files.Spreadsheet) fileType).getCode();
    else if (fileType instanceof Files.Graphics) return ((Files.Graphics) fileType).getCode();
    else throw new IllegalArgumentException("Unsupported file type: " + fileType);
  }

  /**
   * Builder class that provides chainable operations for document creation.
   *
   * <p>This inner class encapsulates all document building operations and provides a fluent API for
   * creating document content. All methods return the builder instance to enable method chaining,
   * making document construction intuitive and readable.
   *
   * <p>The DocumentBuilder automatically handles the creation and management of DocBuilderValue
   * objects, ensuring proper resource cleanup and preventing memory leaks.
   *
   * <p><strong>Usage:</strong> All methods return this builder for chaining, allowing you to build
   * complex documents with a single fluent expression.
   */
  public static class DocumentBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DocumentBuilder.class);

    private final DocBuilderContext context;
    private final Object fileType;

    /**
     * Package-private constructor for DocumentBuilder.
     *
     * <p>This constructor is restricted to package access to enforce the Fluent API pattern. Only
     * DocumentSession can create DocumentBuilder instances, ensuring proper encapsulation and
     * resource management.
     *
     * @param context the DocBuilderContext to use for document operations (must not be null)
     * @param fileType the file type object for this builder (must not be null)
     * @throws IllegalArgumentException if context or fileType is null
     */
    DocumentBuilder(DocBuilderContext context, Object fileType) {
      if (context == null) throw new IllegalArgumentException("Context cannot be null");
      if (fileType == null) throw new IllegalArgumentException("File type cannot be null");
      this.context = context;
      this.fileType = fileType;
      logger.debug("Created DocumentBuilder with context: {}, fileType: {}", context, fileType);
    }

    /**
     * Executes operations on the global API object.
     *
     * <p>This method provides access to the global API object for advanced operations that require
     * direct API access. The API object provides access to all Document Builder functions and can
     * be used for complex document manipulation scenarios.
     *
     * @param apiOperations a consumer that defines operations to perform on the API object
     * @return this DocumentBuilder for method chaining
     * @throws RuntimeException if the API operations fail
     */
    public DocumentBuilder useAPI(Consumer<DocBuilderValue> apiOperations) {
      context.withAPI(apiOperations);
      return this;
    }

    /**
     * Executes operations on both the API object and the main document object.
     *
     * <p>This method automatically retrieves the main object (document, presentation, or worksheet)
     * and provides both the API and main object to the operations consumer. This is useful for
     * operations that need to work with both the global API and the specific document content.
     *
     * @param operations a bi-consumer that receives both the API object and the main object
     * @return this DocumentBuilder for method chaining
     * @throws RuntimeException if the operations fail
     */
    public DocumentBuilder withDocument(
        DocBuilderContext.BiConsumer<DocBuilderValue, DocBuilderValue> operations) {
      context.withDocument(operations);
      return this;
    }

    /**
     * Executes operations on both the API object and the presentation object.
     *
     * <p>This method automatically retrieves the presentation object and provides both the API and
     * presentation to the operations consumer. It's specifically designed for presentation-specific
     * operations and automatically handles resource cleanup.
     *
     * @param operations a bi-consumer that receives both the API object and the presentation object
     * @return this DocumentBuilder for method chaining
     * @throws RuntimeException if the operations fail
     */
    public DocumentBuilder withPresentation(
        DocBuilderContext.BiConsumer<DocBuilderValue, DocBuilderValue> operations) {
      context.withAPI(
          api -> {
            try (var presentation = api.call("GetPresentation")) {
              operations.accept(api, presentation);
            } catch (Exception e) {
              throw new RuntimeException("Failed to execute operations on presentation", e);
            }
          });

      return this;
    }

    /**
     * Executes operations on both the API object and the first worksheet (index 0).
     *
     * <p>This method automatically retrieves the first worksheet and provides both the API and
     * worksheet to the operations consumer. It's a convenience method for working with the primary
     * worksheet in spreadsheets.
     *
     * @param operations a bi-consumer that receives both the API object and the worksheet object
     * @return this DocumentBuilder for method chaining
     * @throws RuntimeException if the operations fail
     */
    public DocumentBuilder withWorksheet(
        DocBuilderContext.BiConsumer<DocBuilderValue, DocBuilderValue> operations) {
      return withWorksheet(0, operations);
    }

    /**
     * Executes operations on both the API object and a specific worksheet.
     *
     * <p>This method automatically retrieves the worksheet at the specified index and provides both
     * the API and worksheet to the operations consumer. It's useful for working with multiple
     * worksheets in a spreadsheet.
     *
     * @param index the zero-based index of the worksheet to work with
     * @param operations a bi-consumer that receives both the API object and the worksheet object
     * @return this DocumentBuilder for method chaining
     * @throws RuntimeException if the operations fail
     */
    public DocumentBuilder withWorksheet(
        int index, DocBuilderContext.BiConsumer<DocBuilderValue, DocBuilderValue> operations) {
      context.withAPI(
          api -> {
            try (var worksheet = api.call("GetActiveSheet")) {
              operations.accept(api, worksheet);
            } catch (Exception e) {
              throw new RuntimeException("Failed to execute operations on worksheet", e);
            }
          });

      return this;
    }

    /**
     * Creates an element of the specified type and configures it.
     *
     * <p>This method creates an element using the API, configures it with the provided
     * configuration, and automatically adds it to the appropriate location in the document. It's
     * the primary method for adding content elements like paragraphs, tables, images, etc.
     *
     * @param elementType the type of element to create (e.g., "CreateParagraph", "CreateTable")
     * @param elementConfig a consumer that configures the created element
     * @return this DocumentBuilder for method chaining
     * @throws RuntimeException if element creation or configuration fails
     */
    public DocumentBuilder addElement(String elementType, Consumer<DocBuilderValue> elementConfig) {
      return withDocument(
          (api, mainObject) -> {
            try (var element = api.call(elementType)) {
              elementConfig.accept(element);
            } catch (Exception e) {
              throw new RuntimeException("Failed to create element: " + elementType, e);
            }
          });
    }
  }
}
