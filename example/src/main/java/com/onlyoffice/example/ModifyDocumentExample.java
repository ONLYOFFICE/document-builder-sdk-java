package com.onlyoffice.example;

import com.onlyoffice.builder.core.DocumentSession;
import com.onlyoffice.builder.loader.NativeDocBuilderLoader;
import com.onlyoffice.builder.loader.NativeLibraryLoader;
import com.onlyoffice.builder.util.SystemOSChecker;
import java.nio.file.Path;

public class ModifyDocumentExample {
  public static void main(String[] args) {
    var resultLocation = "modified.docx";
    var resourcesLocation = "src/main/resources";
    var initialLocation = resourcesLocation + "/sample.docx";

    var loader = NativeLibraryLoader.getInstance(new SystemOSChecker());
    try (var docBuilderLoader = NativeDocBuilderLoader.getInstance(loader)) {
      var nativeLocation = System.getenv("NATIVE_PATH");
      if (nativeLocation == null || nativeLocation.trim().isEmpty())
        nativeLocation = "/onlyoffice-documentbuilder-macos-arm64";
      var nativePath = Path.of(nativeLocation);

      loader.load(nativePath);
      docBuilderLoader.load(nativePath.toString());

      System.setProperty("jna.debug_load", "false");
      System.setProperty("jna.debug_load.jna", "false");

      modifyDocument(loader, initialLocation, resultLocation);

    } catch (Exception e) {
      System.err.println("Error running the example: " + e.getMessage());
    } finally {
      System.gc();
    }
  }

  private static void modifyDocument(
      NativeLibraryLoader loader, String initialLocation, String resultLocation) throws Exception {
    var session = DocumentSession.createDocument(loader);
    session.openFile(
        initialLocation,
        "",
        builder -> {
          builder.withDocument(
              (api, doc) -> {
                api.with(
                    "CreateParagraph",
                    paragraph -> {
                      paragraph.chain("AddText", "New Title").chain("SetFontSize", 44);
                      doc.chain("Push", paragraph);
                    });
              });
        },
        resultLocation);
  }
}
