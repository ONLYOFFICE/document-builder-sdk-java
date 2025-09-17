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

import static org.openjdk.jmh.annotations.Scope.Benchmark;

import com.onlyoffice.builder.core.DocBuilderValue;
import com.onlyoffice.builder.core.DocumentSession;
import com.onlyoffice.builder.loader.NativeDocBuilderLoader;
import com.onlyoffice.builder.loader.NativeLibraryLoader;
import com.onlyoffice.builder.util.SystemOSChecker;
import java.nio.file.Files;
import java.nio.file.Path;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for JMH (Java Microbenchmark Harness) memory tests.
 *
 * <p>This abstract class provides common setup, teardown, and utility methods for benchmarking
 * memory usage patterns in the Document Builder library. It handles native library loading,
 * document creation utilities, and cleanup operations.
 *
 * <p>The class is designed to work with JMH benchmarking framework and provides a foundation for
 * various memory leak detection and performance measurement tests.
 *
 * <p>Key features:
 *
 * <ul>
 *   <li>Automatic native library loading and initialization
 *   <li>Document creation utilities using the fluent API
 *   <li>Proper resource cleanup and teardown
 *   <li>Error logging and handling for benchmark operations
 * </ul>
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 */
@State(Benchmark)
public abstract class BaseJMHMemoryTest {
  /** Logger instance for this class */
  protected static final Logger logger = LoggerFactory.getLogger(BaseJMHMemoryTest.class);

  /** Native library loader instance for loading platform-specific libraries */
  protected NativeLibraryLoader loader;

  /** Document builder loader instance for creating document sessions */
  protected NativeDocBuilderLoader docBuilderLoader;

  /**
   * Sets up the test environment by loading native libraries and initializing the document builder
   * components.
   *
   * <p>This method:
   *
   * <ul>
   *   <li>Creates and configures the native library loader
   *   <li>Loads native libraries from the specified path or default location
   *   <li>Initializes the document builder loader
   *   <li>Configures JNA debug properties
   * </ul>
   *
   * <p>The native library path is determined by the NATIVE_PATH environment variable, with a
   * fallback to the default macOS ARM64 path.
   */
  @Setup
  public void setup() {
    loader = NativeLibraryLoader.getInstance(new SystemOSChecker());

    var nativeLocation = System.getenv("NATIVE_PATH");
    if (nativeLocation == null || nativeLocation.trim().isEmpty())
      nativeLocation = "/onlyoffice-documentbuilder-macos-arm64";
    var nativePath = Path.of(nativeLocation);

    loader.load(nativePath);

    docBuilderLoader = NativeDocBuilderLoader.getInstance(loader);
    docBuilderLoader.load(nativePath.toString());

    System.setProperty("jna.debug_load", "false");
    System.setProperty("jna.debug_load.jna", "false");
  }

  /**
   * Cleans up resources after test execution.
   *
   * <p>This method ensures proper cleanup of the document builder loader and logs any errors that
   * occur during cleanup.
   */
  @TearDown
  public void tearDown() {
    if (docBuilderLoader != null) {
      try {
        docBuilderLoader.close();
      } catch (Exception e) {
        logger.error("Error closing docBuilderLoader: {}", e.getMessage());
      }
    }
  }

  /**
   * Logs benchmark operation errors with consistent formatting.
   *
   * @param operation the name of the benchmark operation that failed
   * @param e the exception that occurred during the operation
   */
  protected void logBenchmarkError(String operation, Exception e) {
    logger.error("Benchmark operation '{}' failed: {}", operation, e.getMessage());
  }

  /**
   * Creates formatted text content using the fluent API.
   *
   * <p>This utility method demonstrates the fluent API pattern for creating styled text content
   * with specific formatting attributes.
   *
   * @param api the DocBuilderValue API instance for document operations
   * @param content the content container where the text will be added
   * @param text the text content to add
   * @param fontSize the font size in points
   * @param isBold whether the text should be bold
   */
  protected static void addFluentText(
      DocBuilderValue api, DocBuilderValue content, String text, int fontSize, boolean isBold) {
    api.with(
        "CreateParagraph",
        paragraph -> {
          paragraph
              .chain("SetSpacingBefore", 0)
              .chain("SetSpacingAfter", 0)
              .with(
                  "AddText",
                  text,
                  run -> {
                    api.with(
                        "CreateRGBColor",
                        255,
                        255,
                        255,
                        rgb ->
                            api.with(
                                "CreateSolidFill",
                                rgb,
                                fill ->
                                    run.chain("SetFill", fill)
                                        .chain("SetFontSize", fontSize)
                                        .chain("SetFontFamily", "Georgia")
                                        .chain("SetBold", isBold)));
                  });
          content.chain("Push", paragraph);
          paragraph.chain("SetJc", "center");
        });
  }

  /**
   * Creates a title shape with formatted text using the fluent API.
   *
   * <p>This method creates a rectangular shape containing formatted text, demonstrating complex
   * document element creation with the fluent API.
   *
   * @param api the DocBuilderValue API instance for document operations
   * @param slide the slide where the title shape will be added
   */
  protected static void createFluentTitleShape(DocBuilderValue api, DocBuilderValue slide) {
    api.with(
        "CreateNoFill",
        noFill -> {
          api.with(
              "CreateNoFill",
              strokeNoFill -> {
                api.with(
                    "CreateStroke",
                    0,
                    strokeNoFill,
                    stroke -> {
                      api.with(
                          "CreateShape",
                          "rect",
                          8000000,
                          1500000,
                          noFill,
                          stroke,
                          titleShape -> {
                            titleShape
                                .chain("SetPosition", 572000, 1200000)
                                .with(
                                    "GetDocContent",
                                    content -> {
                                      content.chain("RemoveAllElements");
                                      addFluentText(api, content, "Fluent API Demo", 120, true);
                                    });
                            slide.chain("AddObject", titleShape);
                          });
                    });
              });
        });
    api.with(
        "CreateNoFill",
        noFill2 -> {
          api.with(
              "CreateNoFill",
              strokeNoFill2 -> {
                api.with(
                    "CreateStroke",
                    0,
                    strokeNoFill2,
                    stroke2 -> {
                      api.with(
                          "CreateShape",
                          "rect",
                          8000000,
                          1000000,
                          noFill2,
                          stroke2,
                          subtitleShape -> {
                            subtitleShape
                                .chain("SetPosition", 572000, 3000000)
                                .with(
                                    "GetDocContent",
                                    content -> {
                                      content.chain("RemoveAllElements");
                                      addFluentText(
                                          api, content, "No More Try-With-Resources!", 48, false);
                                    });
                            slide.chain("AddObject", subtitleShape);
                          });
                    });
              });
        });
  }

  /**
   * Creates a presentation document using the fluent API.
   *
   * <p>This method demonstrates the complete workflow of creating a presentation with multiple
   * slides, background images, and formatted content using the fluent API pattern.
   *
   * @param loader the native library loader instance
   * @param resultPath the file path where the generated presentation will be saved
   * @throws Exception if an error occurs during document creation
   */
  protected static void createWithFluentAPI(NativeLibraryLoader loader, String resultPath)
      throws Exception {
    var session = DocumentSession.createPresentation(loader);
    session.build(
        doc ->
            doc.withPresentation(
                (api, presentation) -> {
                  presentation
                      .chain("SetSizes", 9144000, 6858000)
                      .with("GetSlideByIndex", 0, slide -> slide.chain("Delete"));
                  api.with(
                      "CreateSlide",
                      slide -> {
                        presentation.chain("AddSlide", slide);
                        api.with(
                            "CreateBlipFill",
                            "https://static.onlyoffice.com/assets/docs/samples/img/presentation_sky.png",
                            "stretch",
                            fill -> slide.chain("SetBackground", fill));
                        slide.chain("RemoveAllObjects");
                        createFluentTitleShape(api, slide);
                      });
                  api.with(
                      "CreateSlide",
                      slide -> {
                        presentation.chain("AddSlide", slide);
                        api.with(
                            "CreateBlipFill",
                            "https://static.onlyoffice.com/assets/docs/samples/img/presentation_gun.png",
                            "stretch",
                            fill -> slide.chain("SetBackground", fill));
                        slide.chain("RemoveAllObjects");
                        createFluentTitleShape(api, slide);
                      });
                }),
        resultPath);
  }

  /**
   * Safely deletes a file if it exists.
   *
   * <p>This utility method provides safe file cleanup by checking if the file exists before
   * attempting deletion and silently ignoring any exceptions that occur during the cleanup process.
   *
   * @param filePath the path to the file to be deleted
   */
  protected void cleanupFile(String filePath) {
    try {
      Files.deleteIfExists(Path.of(filePath));
    } catch (Exception ignored) {
    }
  }
}
