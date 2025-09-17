package com.onlyoffice.example;

import com.onlyoffice.builder.core.DocumentSession;
import com.onlyoffice.builder.loader.NativeDocBuilderLoader;
import com.onlyoffice.builder.loader.NativeLibraryLoader;
import com.onlyoffice.builder.util.SystemOSChecker;
import java.nio.file.Path;

public class DocumentExample {
  private static final String[] plans =
      new String[] {
        "Mobile App Development - Q2 2024", "Cloud Migration - Q3 2024", "AI Integration - Q4 2024"
      };
  private static final String[] achievements =
      new String[] {
        "Increased market share by 15%",
        "Launched 3 new product lines",
        "Improved customer satisfaction to 95%",
        "Reduced operational costs by 12%"
      };

  public static void main(String[] args) {
    var resultPath = "result.docx";
    createSimpleReport(resultPath);
  }

  public static void createSimpleReport(String resultPath) {
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

      DocumentSession.createDocument(loader)
          .build(
              doc -> {
                doc.withDocument(
                    (api, document) -> {
                      api.with(
                          "CreateParagraph",
                          paragraph -> {
                            paragraph
                                .chain("AddText", "Annual Report for 2024")
                                .chain("SetFontSize", 44);
                            document.chain("Push", paragraph);
                          });
                      api.with(
                          "CreateParagraph",
                          paragraph -> {
                            paragraph
                                .chain("AddText", "Financial Performance")
                                .chain("SetFontSize", 32)
                                .chain("SetBold", true);
                            document.chain("Push", paragraph);
                          });
                      api.with(
                          "CreateParagraph",
                          paragraph -> {
                            paragraph
                                .chain("AddText", "Achievements")
                                .chain("SetFontSize", 32)
                                .chain("SetBold", true);
                            document.chain("Push", paragraph);
                          });
                      api.with(
                          "CreateParagraph",
                          paragraph -> {
                            paragraph
                                .chain("AddText", "Future Plans")
                                .chain("SetFontSize", 32)
                                .chain("SetBold", true);
                            document.chain("Push", paragraph);
                          });
                      api.with(
                          "CreateParagraph",
                          paragraph -> {
                            paragraph
                                .chain("AddText", "Total Revenue: $1,250,000")
                                .chain("SetFontSize", 22);
                            document.chain("Push", paragraph);
                          });
                      api.with(
                          "CreateParagraph",
                          paragraph -> {
                            paragraph
                                .chain("AddText", "Total Expenses: $850,000")
                                .chain("SetFontSize", 22);
                            document.chain("Push", paragraph);
                          });
                      api.with(
                          "CreateParagraph",
                          paragraph -> {
                            paragraph
                                .chain("AddText", "Net Profit: $400,000")
                                .chain("SetFontSize", 22);
                            document.chain("Push", paragraph);
                          });

                      for (var achievement : achievements)
                        api.with(
                            "CreateParagraph",
                            paragraph -> {
                              paragraph
                                  .chain("AddText", "• " + achievement)
                                  .chain("SetFontSize", 22);
                              document.chain("Push", paragraph);
                            });

                      for (var plan : plans)
                        api.with(
                            "CreateParagraph",
                            paragraph -> {
                              paragraph.chain("AddText", "• " + plan).chain("SetFontSize", 22);
                              document.chain("Push", paragraph);
                            });
                    });
              },
              resultPath);
    } catch (Exception e) {
      System.err.println("Error running the example: " + e.getMessage());
    } finally {
      System.gc();
    }
  }
}
