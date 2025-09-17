package com.onlyoffice.example;

import com.onlyoffice.builder.core.DocBuilderValue;
import com.onlyoffice.builder.core.DocumentSession;
import com.onlyoffice.builder.loader.NativeDocBuilderLoader;
import com.onlyoffice.builder.loader.NativeLibraryLoader;
import com.onlyoffice.builder.util.SystemOSChecker;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SpreadsheetExample {
  public static void main(String[] args) throws Exception {
    var resultLocation = "feedback_report.xlsx";
    var resourcesLocation = "src/main/resources";
    var jsonResourceLocation = resourcesLocation + "/data/feedback_data.json";

    var data = (JSONArray) new JSONParser().parse(new FileReader(jsonResourceLocation));
    var loader = NativeLibraryLoader.getInstance(new SystemOSChecker());
    try (var docBuilderLoader = NativeDocBuilderLoader.getInstance(loader)) {
      var nativeLocation = System.getenv("NATIVE_PATH");
      if (nativeLocation == null || nativeLocation.trim().isEmpty())
        nativeLocation = "/Users/stzmn/Downloads/onlyoffice-documentbuilder-macos-arm64";
      var nativePath = Path.of(nativeLocation);

      loader.load(nativePath);
      docBuilderLoader.load(nativePath.toString());

      System.setProperty("jna.debug_load", "false");
      System.setProperty("jna.debug_load.jna", "false");

      Locale.setDefault(Locale.US);

      createUserFeedbackReport(loader, resultLocation, data);
    } catch (Exception e) {
      System.err.println("Error running the example: " + e.getMessage());
    } finally {
      System.gc();
    }
  }

  protected static void createUserFeedbackReport(
      NativeLibraryLoader loader, String resultLocation, JSONArray data) throws Exception {
    var session = DocumentSession.createSpreadsheet(loader);
    session.build(
        doc ->
            doc.useAPI(
                api ->
                    api.with(
                        "GetActiveSheet",
                        worksheet1 -> {
                          worksheet1.chain("SetName", "Average");
                          fillAverageSheetSimple(api, worksheet1, data);
                          api.with("AddSheet", "Comments", temp -> {});
                          api.with(
                              "GetActiveSheet",
                              worksheet2 ->
                                  fillPersonalRatingsAndCommentsSimple(api, worksheet2, data));
                        })),
        resultLocation);
  }

  private static void fillAverageSheetSimple(
      DocBuilderValue api, DocBuilderValue worksheet, JSONArray feedbackData) {
    api.with("GetRange", "A1", cell -> cell.chain("SetValue", "Question"));
    api.with("GetRange", "B1", cell -> cell.chain("SetValue", "Average Rating"));

    var questionRatings = new LinkedHashMap<String, List<Integer>>();

    for (var data : feedbackData) {
      var record = (JSONObject) data;
      var feedback = (JSONArray) record.get("feedback");

      for (var entry : feedback) {
        var item = (JSONObject) entry;
        var question = item.get("question").toString();
        var rating = ((Long) ((JSONObject) item.get("answer")).get("rating")).intValue();

        questionRatings.putIfAbsent(question, new ArrayList<>());
        questionRatings.get(question).add(rating);
      }
    }

    var row = 2;
    for (var entry : questionRatings.entrySet()) {
      var question = entry.getKey();
      var ratings = entry.getValue();

      var average = ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);

      api.with("GetRange", "A" + row, cell -> cell.chain("SetValue", question));
      api.with(
          "GetRange", "B" + row, cell -> cell.chain("SetValue", String.format("%.2f", average)));

      row++;
    }
  }

  private static void fillPersonalRatingsAndCommentsSimple(
      DocBuilderValue api, DocBuilderValue worksheet, JSONArray feedbackData) {
    api.with("GetRange", "A1", cell -> cell.chain("SetValue", "Date"));
    api.with("GetRange", "B1", cell -> cell.chain("SetValue", "Question"));
    api.with("GetRange", "C1", cell -> cell.chain("SetValue", "Comment"));
    api.with("GetRange", "D1", cell -> cell.chain("SetValue", "Rating"));

    var row = 2;
    for (var data : feedbackData) {
      var record = (JSONObject) data;
      var date = record.get("date").toString();
      var feedback = (JSONArray) record.get("feedback");

      for (var entry : feedback) {
        var item = (JSONObject) entry;
        var question = item.get("question").toString();
        var answer = (JSONObject) item.get("answer");
        var comment = answer.get("comment").toString();
        var rating = answer.get("rating").toString();

        api.with("GetRange", "A" + row, cell -> cell.chain("SetValue", date));
        api.with("GetRange", "B" + row, cell -> cell.chain("SetValue", question));
        api.with("GetRange", "C" + row, cell -> cell.chain("SetValue", comment));
        api.with("GetRange", "D" + row, cell -> cell.chain("SetValue", rating));

        row++;
      }
    }
  }

  private static int getSum(ArrayList<Integer> values) {
    var sum = 0;
    for (var value : values) sum += value;
    return sum;
  }

  private static void setTableStyle(DocBuilderValue range) {
    var lineStyle = "Thin";
    range.chain("SetRowHeight", 24).chain("SetAlignVertical", "center");
    range
        .chain("SetBorders", "Top", lineStyle, 0)
        .chain("SetBorders", "Left", lineStyle, 0)
        .chain("SetBorders", "Right", lineStyle, 0)
        .chain("SetBorders", "Bottom", lineStyle, 0)
        .chain("SetBorders", "InsideHorizontal", lineStyle, 0)
        .chain("SetBorders", "InsideVertical", lineStyle, 0);
  }

  private static int fillAverageSheet(
      DocBuilderValue api, DocBuilderValue worksheet, JSONArray feedbackData) {
    var result = new LinkedHashMap<String, ArrayList<Integer>>();
    for (var data : feedbackData) {
      var userFeedback = (JSONObject) data;
      var feedback = (JSONArray) userFeedback.get("feedback");
      for (var entry : feedback) {
        var feedbackItem = (JSONObject) entry;
        var question = feedbackItem.get("question").toString();
        var rating = ((Long) ((JSONObject) feedbackItem.get("answer")).get("rating")).intValue();

        result.putIfAbsent(question, new ArrayList<>());
        result.get(question).add(rating);
      }
    }

    var tableHeaders = new String[] {"Question", "Average Rating", "Number of Responses"};
    var averageValues = new String[result.size() + 1][tableHeaders.length];
    averageValues[0] = tableHeaders;

    var index = 1;
    for (var entry : result.entrySet()) {
      var values = entry.getValue();
      var sum = getSum(values);
      averageValues[index] =
          new String[] {
            entry.getKey(),
            String.format("%.1f", (double) sum / values.size()),
            String.valueOf(values.size())
          };
      index++;
    }

    var colsCount = tableHeaders.length - 1;
    api.with(
        "GetRangeByNumber",
        0,
        0,
        startCell ->
            api.with(
                "GetRangeByNumber",
                averageValues.length - 1,
                colsCount,
                endCell ->
                    api.with(
                        "GetRange",
                        startCell,
                        endCell,
                        averageRange -> {
                          setTableStyle(averageRange);
                          api.with(
                              "GetRange",
                              api.with("GetRangeByNumber", 1, 1, temp -> {}),
                              endCell,
                              centerRange -> centerRange.chain("SetAlignHorizontal", "center"));
                          api.with(
                              "GetRange",
                              startCell,
                              api.with("GetRangeByNumber", 0, colsCount, temp -> {}),
                              headerRow -> headerRow.chain("SetBold", true));
                          averageRange
                              .chain("SetValue", averageValues)
                              .chain("AutoFit", false, true);
                        })));

    return averageValues.length;
  }

  private static int fillPersonalRatingsAndComments(
      DocBuilderValue api, DocBuilderValue worksheet, JSONArray feedbackData) {
    var tableHeaders =
        new String[][] {{"Date", "Question", "Comment", "Rating", "Average User Rating"}};
    var colsCount = tableHeaders[0].length - 1;

    api.with(
        "GetRangeByNumber",
        0,
        0,
        startCell ->
            api.with(
                "GetRangeByNumber",
                0,
                colsCount,
                endCell ->
                    api.with(
                        "GetRange",
                        startCell,
                        endCell,
                        headerRow -> {
                          headerRow.chain("SetValue", tableHeaders).chain("SetBold", true);
                        })));

    var rowsCount = 1;
    var totalRows = 1;

    for (var data : feedbackData) {
      JSONObject record = (JSONObject) data;
      JSONArray feedback = (JSONArray) record.get("feedback");
      totalRows += feedback.size();
    }

    for (var data : feedbackData) {
      JSONObject record = (JSONObject) data;
      JSONArray feedback = (JSONArray) record.get("feedback");

      var ratings = new int[2];
      var userFeedback = new String[feedback.size()][tableHeaders[0].length];
      for (var j = 0; j < feedback.size(); j++) {
        var item = (JSONObject) feedback.get(j);
        var answer = (JSONObject) item.get("answer");
        userFeedback[j] =
            new String[] {
              item.get("question").toString(),
              answer.get("comment").toString(),
              answer.get("rating").toString()
            };
        ratings[0] += ((Long) answer.get("rating")).intValue();
        ratings[1]++;
      }

      var userRowsCount = userFeedback.length - 1;
      var totalRating = (double) ratings[0] / ratings[1];
      final int currentRowsCount = rowsCount;

      api.with(
          "GetRange",
          api.with("GetRangeByNumber", currentRowsCount, 0, temp -> {}),
          api.with("GetRangeByNumber", currentRowsCount + userRowsCount, 0, temp -> {}),
          dateCell -> {
            dateCell.chain("Merge", false).chain("SetValue", record.get("date").toString());
          });
      api.with(
          "GetRange",
          api.with("GetRangeByNumber", currentRowsCount, 1, temp -> {}),
          api.with("GetRangeByNumber", currentRowsCount + userRowsCount, colsCount - 1, temp -> {}),
          userRange -> userRange.chain("SetValue", userFeedback));
      api.with(
          "GetRange",
          api.with("GetRangeByNumber", currentRowsCount, colsCount, temp -> {}),
          api.with("GetRangeByNumber", currentRowsCount + userRowsCount, colsCount, temp -> {}),
          ratingCell -> {
            ratingCell.chain("Merge", false).chain("SetValue", String.format("%.1f", totalRating));
          });
      if (totalRating <= 2) {
        api.with(
            "GetRange",
            api.with("GetRangeByNumber", currentRowsCount, 0, temp -> {}),
            api.with("GetRangeByNumber", currentRowsCount + userRowsCount, colsCount, temp -> {}),
            highlightRange -> highlightRange.chain("SetFillColor", 237, 125, 49));
      }

      rowsCount += userFeedback.length;
    }

    final int finalRowsCount = totalRows - 1;
    api.with(
        "GetRange",
        api.with("GetRangeByNumber", 0, 0, temp -> {}),
        api.with("GetRangeByNumber", finalRowsCount, colsCount, temp -> {}),
        resultRange -> {
          setTableStyle(resultRange);
          api.with(
              "GetRange",
              api.with("GetRangeByNumber", 1, colsCount - 1, temp -> {}),
              api.with("GetRangeByNumber", finalRowsCount, colsCount, temp -> {}),
              centerRange -> centerRange.chain("SetAlignHorizontal", "center"));
          resultRange.chain("AutoFit", false, true);
        });

    return totalRows;
  }
}
