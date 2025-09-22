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
package com.onlyoffice.example;

import com.onlyoffice.builder.core.DocumentSession;
import com.onlyoffice.builder.loader.NativeDocBuilderLoader;
import com.onlyoffice.builder.loader.NativeLibraryLoader;
import com.onlyoffice.builder.util.SystemOSChecker;
import java.io.FileReader;
import java.nio.file.Path;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class InvoiceExample {
  public static void main(String[] args) throws Exception {
    var resultLocation = "invoice.pdf";
    var resourcesLocation = "src/main/resources";
    var jsonResourceLocation = resourcesLocation + "/data/invoice_data.json";

    var data = (JSONObject) new JSONParser().parse(new FileReader(jsonResourceLocation));
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

      createWithFluentAPI(loader, resultLocation, data);
    } catch (Exception e) {
      System.err.println("Error running the example: " + e.getMessage());
    } finally {
      System.gc();
    }
  }

  protected static void createWithFluentAPI(
      NativeLibraryLoader loader, String resultLocation, JSONObject data) throws Exception {
    var session = DocumentSession.createDocument(loader);
    session.build(
        doc ->
            doc.withDocument(
                (api, document) -> {
                  var invoice = (JSONObject) data.get("invoice");
                  api.with(
                      "CreateParagraph",
                      header -> {
                        header
                            .chain("AddText", "INVOICE")
                            .chain("SetBold", true)
                            .chain("SetFontSize", 28);
                        document.chain("Push", header);
                      });
                  api.with(
                      "CreateParagraph",
                      invNum -> {
                        invNum.chain("AddText", "Invoice No.: " + invoice.get("number").toString());
                        document.chain("Push", invNum);
                      });
                  api.with(
                      "CreateParagraph",
                      invDate -> {
                        invDate.chain("AddText", "Date: " + invoice.get("date").toString());
                        document.chain("Push", invDate);
                      });

                  var seller = (JSONObject) data.get("seller");
                  api.with(
                      "CreateParagraph",
                      sellerHeader -> {
                        sellerHeader.chain("AddText", "SELLER INFORMATION").chain("SetBold", true);
                        document.chain("Push", sellerHeader);
                      });
                  api.with(
                      "CreateParagraph",
                      companyName -> {
                        companyName.chain(
                            "AddText", "Company: " + seller.get("company_name").toString());
                        document.chain("Push", companyName);
                      });
                  api.with(
                      "CreateParagraph",
                      address -> {
                        address.chain("AddText", "Address: " + seller.get("address").toString());
                        document.chain("Push", address);
                      });

                  var buyer = (JSONObject) data.get("buyer");
                  api.with(
                      "CreateParagraph",
                      buyerHeader -> {
                        buyerHeader.chain("AddText", "BUYER INFORMATION").chain("SetBold", true);
                        document.chain("Push", buyerHeader);
                      });
                  api.with(
                      "CreateParagraph",
                      buyerCompany -> {
                        buyerCompany.chain(
                            "AddText", "Company: " + buyer.get("company_name").toString());
                        document.chain("Push", buyerCompany);
                      });
                  api.with(
                      "CreateParagraph",
                      buyerAddress -> {
                        buyerAddress.chain(
                            "AddText", "Address: " + buyer.get("address").toString());
                        document.chain("Push", buyerAddress);
                      });

                  var items = (JSONArray) data.get("items");
                  api.with(
                      "CreateParagraph",
                      itemsHeader -> {
                        itemsHeader.chain("AddText", "ITEMS").chain("SetBold", true);
                        document.chain("Push", itemsHeader);
                      });

                  for (var objItem : items) {
                    var item = (JSONObject) objItem;
                    api.with(
                        "CreateParagraph",
                        itemPara -> {
                          itemPara.chain(
                              "AddText",
                              String.format(
                                  "%s - Qty: %s - Price: $%s - Total: $%s",
                                  item.get("description").toString(),
                                  item.get("quantity").toString(),
                                  item.get("unit_price").toString(),
                                  item.get("total").toString()));
                          document.chain("Push", itemPara);
                        });
                  }

                  var totals = (JSONObject) data.get("totals");
                  api.with(
                      "CreateParagraph",
                      totalsHeader -> {
                        totalsHeader.chain("AddText", "TOTALS").chain("SetBold", true);
                        document.chain("Push", totalsHeader);
                      });
                  api.with(
                      "CreateParagraph",
                      subtotal -> {
                        subtotal.chain(
                            "AddText", "Subtotal: $" + totals.get("subtotal").toString());
                        document.chain("Push", subtotal);
                      });
                  api.with(
                      "CreateParagraph",
                      tax -> {
                        tax.chain("AddText", "Tax: $" + totals.get("tax").toString());
                        document.chain("Push", tax);
                      });
                  api.with(
                      "CreateParagraph",
                      total -> {
                        total
                            .chain("AddText", "Total: $" + totals.get("total_due").toString())
                            .chain("SetBold", true);
                        document.chain("Push", total);
                      });
                }),
        resultLocation);
  }
}
