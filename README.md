# ONLYOFFICE Document Builder - Java Wrapper

A Java wrapper for the ONLYOFFICE Document Builder that provides a **100% Fluent API** with **enforced restrictive access** to ensure proper usage patterns.

## Key Features

- **Automatic cleanup** - All resources handled internally by DocumentSession
- **Fluent API** - Intuitive method chaining for document building
- **Restrictive access** - Core classes are package-private
- **Type safety** - Compile-time safety for document operations
- **Comprehensive error handling** - Detailed error messages and logging

## Restrictive Design

This wrapper is designed with **enforced restrictive access** to ensure the Fluent API pattern is always used:

- **DocBuilder**: Package-private (cannot be instantiated from client code)
- **DocBuilderContext**: Package-private (cannot be instantiated from client code)  
- **DocBuilderValue**: Package-private (cannot be instantiated from client code)
- **DocBuilderContextScope**: Package-private (cannot be instantiated from client code)
- **DocumentSession**: **ONLY public entry point**

## Usage

### Initialize Native Library Loader
```java
var loader = NativeLibraryLoader.getInstance(new SystemOSChecker());
try (var docBuilderLoader = NativeDocBuilderLoader.getInstance(loader)){
    var nativePath = Path.of("location_to_binary");
    loader.load(nativePath);
    docBuilderLoader.load(nativePath.toString());
}
```

### Basic Document Creation

```java
// CORRECT: Use DocumentSession (Fluent API)
DocumentSession.createDocument(loader)
    .build(doc -> doc
        .withDocument((api, document) -> {
            api.with("CreateParagraph", paragraph -> {
                paragraph.chain("AddText", "Hello World");
                document.chain("Push", paragraph);
            });
        }),
        "output.docx");

// INCORRECT: Direct core class usage (will not compile)
// DocBuilder builder = new DocBuilder(loader); // Compilation error
// DocBuilderContext context = new DocBuilderContext(); // Compilation error
```

### Spreadsheet Creation

```java
DocumentSession.createSpreadsheet(loader)
    .build(doc -> doc
        .useAPI(api -> {
            api.with("GetActiveSheet", worksheet -> {
                worksheet.with("GetRange", "A1", range -> 
                    range.chain("SetValue", "Hello"));
            });
        }),
        "spreadsheet.xlsx");
```

### Presentation Creation

```java
DocumentSession.createPresentation(loader)
    .build(doc -> doc
        .withPresentation((api, presentation) -> {
            presentation.chain("SetSizes", 9144000, 6858000);
            api.with("CreateSlide", slide -> {
                presentation.chain("AddSlide", slide);
            });
        }),
        "presentation.pptx");
```

### Advanced Spreadsheet with Data

```java
Object[][] data = {
    {"Id", "Product", "Price", "Available"},
    {1001, "Item A", 12.2, true},
    {1002, "Item B", 18.8, true}
};

DocumentSession.createSpreadsheet(loader)
    .build(doc -> doc
        .useAPI(api -> {
            api.with("GetActiveSheet", worksheet -> {
                worksheet.with("GetRangeByNumber", 0, 0, startCell -> {
                    worksheet.with("GetRangeByNumber", data.length - 1, data[0].length - 1, endCell -> {
                        worksheet.with("GetRange", startCell, endCell, range -> {
                            range.chain("SetValue", data);
                        });
                    });
                });
            });
        }),
        "data.xlsx");
```

## Architecture

The wrapper follows a strict layered architecture:

```
Client Code (Main.java)
        ↓
DocumentSession (ONLY public entry point)
        ↓
DocumentBuilder (inner class, fluent operations)
        ↓
Core Classes (package-private, cannot be accessed directly)
        ↓
Native ONLYOFFICE Document Builder
```

## Resource Management

- **Automatic**: Each DocumentSession creates and manages its own DocBuilder instance
- **Isolated**: Different file types use completely isolated DocBuilder instances
- **Cleanup**: All resources are automatically cleaned up when the build method completes
- **Thread-safe**: Each session is independent and thread-safe

## DocumentSession API

### Factory Methods

```java
// Convenience methods for common formats
DocumentSession.createDocument(loader)        // Creates .docx
DocumentSession.createPresentation(loader)    // Creates .pptx  
DocumentSession.createSpreadsheet(loader)     // Creates .xlsx
DocumentSession.createPdf(loader)            // Creates .pdf

// Specific format control
DocumentSession.create(loader, Files.Document.ODT)      // Creates .odt
DocumentSession.create(loader, Files.Presentation.ODP)  // Creates .odp
DocumentSession.create(loader, Files.Spreadsheet.XLSX)  // Creates .xlsx
```

### DocumentBuilder Methods

```java
// Work with document content
.withDocument((api, document) -> { /* operations */ })

// Work with presentation content  
.withPresentation((api, presentation) -> { /* operations */ })

// Work with worksheet content
.withWorksheet((api, worksheet) -> { /* operations */ })
.withWorksheet(index, (api, worksheet) -> { /* operations */ })

// Use API directly for advanced operations
.useAPI(api -> { /* operations */ })

// Add elements
.addElement("CreateParagraph", paragraph -> { /* configure */ })
```

## Supported File Formats

### Documents
- **DOCX** - Microsoft Word Open XML Document
- **DOC** - Microsoft Word Document  
- **ODT** - OpenDocument Text
- **RTF** - Rich Text Format
- **TXT** - Plain Text
- **HTML** - HyperText Markup Language

### Presentations
- **PPTX** - Microsoft PowerPoint Open XML Presentation
- **PPT** - Microsoft PowerPoint Presentation
- **ODP** - OpenDocument Presentation
- **PPSX** - Microsoft PowerPoint Open XML Show

### Spreadsheets
- **XLSX** - Microsoft Excel Open XML Spreadsheet
- **XLS** - Microsoft Excel Spreadsheet
- **ODS** - OpenDocument Spreadsheet

### Graphics & PDF
- **PDF** - Portable Document Format
- **PNG** - Portable Network Graphics
- **JPG** - JPEG Image

## Important Notes

1. **Never try to instantiate core classes directly** - they are package-private for a reason
2. **Always use DocumentSession** - it's the only public entry point
3. **No manual resource management** - everything is handled automatically
4. **Fluent chaining is enforced** - all methods return the builder for chaining
5. **Use the correct with* methods** - `withDocument`, `withPresentation`, `withWorksheet`
6. **For spreadsheets, use `useAPI` with `GetActiveSheet`** - the `withWorksheet` method has limitations