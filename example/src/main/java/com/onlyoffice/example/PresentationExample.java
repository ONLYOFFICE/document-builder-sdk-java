package com.onlyoffice.example;

import com.onlyoffice.builder.core.DocBuilderValue;
import com.onlyoffice.builder.core.DocumentSession;
import com.onlyoffice.builder.loader.NativeDocBuilderLoader;
import com.onlyoffice.builder.loader.NativeLibraryLoader;
import com.onlyoffice.builder.util.SystemOSChecker;
import java.nio.file.Path;

public class PresentationExample {
  public static void main(String[] args) throws Exception {
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

      createWithFluentAPI(loader);
    } catch (Exception e) {
      System.err.println("Error running the example: " + e.getMessage());
    }
  }

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
                  run ->
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
                                          .chain("SetBold", isBold))));
          content.chain("Push", paragraph);
          paragraph.chain("SetJc", "center");
        });
  }

  protected static void createFluentTitleShape(DocBuilderValue api, DocBuilderValue slide) {
    api.with(
        "CreateNoFill",
        noFill -> {
          api.with(
              "CreateNoFill",
              strokeNoFill ->
                  api.with(
                      "CreateStroke",
                      0,
                      strokeNoFill,
                      stroke ->
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
                              })));
        });
    api.with(
        "CreateNoFill",
        noFill2 ->
            api.with(
                "CreateNoFill",
                strokeNoFill2 ->
                    api.with(
                        "CreateStroke",
                        0,
                        strokeNoFill2,
                        stroke2 ->
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
                                                api, content, "No more direct calls!", 48, false);
                                          });
                                  slide.chain("AddObject", subtitleShape);
                                }))));
  }

  protected static void createWithFluentAPI(NativeLibraryLoader loader) throws Exception {
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
        "presentation.pptx");
  }
}
