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
package com.onlyoffice.builder.util;

/**
 * Utility class containing file format definitions and their corresponding codes used by the
 * Document Builder system.
 *
 * <p>This class provides enums for different document types including:
 *
 * <ul>
 *   <li>Document formats (Word processing)
 *   <li>Presentation formats
 *   <li>Spreadsheet formats
 *   <li>Graphics and PDF formats
 * </ul>
 *
 * <p>Each format is associated with a unique integer code that identifies the file type in the
 * Document Builder API.
 *
 * @author ONLYOFFICE
 * @version 0.1.0
 * @since 0.1.0
 */
public class Files {

  /**
   * Enumeration of supported document (word processing) file formats. These formats represent
   * various word processing document types that can be processed by the Document Builder.
   */
  public enum Document {
    /** Generic document mask identifier */
    MASK(44),
    /** Microsoft Word Open XML Document (.docx) */
    DOCX(65),
    /** Microsoft Word Document (.doc) */
    DOC(66),
    /** OpenDocument Text (.odt) */
    ODT(67),
    /** Rich Text Format (.rtf) */
    RTF(68),
    /** Plain Text (.txt) */
    TXT(69),
    /** Microsoft Word Open XML Template (.dotx) */
    DOTX(76),
    /** OpenDocument Text Template (.ott) */
    OTT(79),
    /** HyperText Markup Language (.html) */
    HTML(82),
    /** ONLYOFFICE Form PDF (.oform) */
    OFORM_PDF(87);

    /** The unique identifier code for this document format */
    private int code;

    /**
     * Constructs a new Document format with the specified code.
     *
     * @param code the unique identifier code for this document format
     */
    Document(int code) {
      this.code = code;
    }

    /**
     * Returns the unique identifier code for this document format.
     *
     * @return the integer code representing this document format
     */
    public int getCode() {
      return code;
    }
  }

  /**
   * Enumeration of supported presentation file formats. These formats represent various
   * presentation types that can be processed by the Document Builder.
   */
  public enum Presentation {
    /** Generic presentation mask identifier */
    MASK(128),
    /** Microsoft PowerPoint Open XML Presentation (.pptx) */
    PPTX(129),
    /** Microsoft PowerPoint Presentation (.ppt) */
    PPT(130),
    /** OpenDocument Presentation (.odp) */
    ODP(131),
    /** Microsoft PowerPoint Open XML Show (.ppsx) */
    PPSX(132),
    /** Microsoft PowerPoint Open XML Template (.potx) */
    POTX(135),
    /** OpenDocument Presentation Template (.otp) */
    OTP(138);

    /** The unique identifier code for this presentation format */
    private int code;

    /**
     * Constructs a new Presentation format with the specified code.
     *
     * @param code the unique identifier code for this presentation format
     */
    Presentation(int code) {
      this.code = code;
    }

    /**
     * Returns the unique identifier code for this presentation format.
     *
     * @return the integer code representing this presentation format
     */
    public int getCode() {
      return code;
    }
  }

  /**
   * Enumeration of supported spreadsheet file formats. These formats represent various spreadsheet
   * types that can be processed by the Document Builder.
   */
  public enum Spreadsheet {
    /** Generic spreadsheet mask identifier */
    MASK(256),
    /** Microsoft Excel Open XML Workbook (.xlsx) */
    XLSX(257),
    /** Microsoft Excel Workbook (.xls) */
    XLS(258),
    /** OpenDocument Spreadsheet (.ods) */
    ODS(259),
    /** Comma-Separated Values (.csv) */
    CSV(260),
    /** Microsoft Excel Open XML Template (.xltx) */
    XLTX(262),
    /** OpenDocument Spreadsheet Template (.ots) */
    OTS(265);

    /** The unique identifier code for this spreadsheet format */
    private int code;

    /**
     * Constructs a new Spreadsheet format with the specified code.
     *
     * @param code the unique identifier code for this spreadsheet format
     */
    Spreadsheet(int code) {
      this.code = code;
    }

    /**
     * Returns the unique identifier code for this spreadsheet format.
     *
     * @return the integer code representing this spreadsheet format
     */
    public int getCode() {
      return code;
    }
  }

  /**
   * Enumeration of supported graphics and PDF file formats. These formats represent various image
   * and document types that can be processed by the Document Builder.
   */
  public enum Graphics {
    /** Generic PDF mask identifier */
    PDF_MASK(512),
    /** Portable Document Format (.pdf) */
    PDF(513),
    /** PDF/A Archive (.pdf) */
    PDFA(521),
    /** Generic image mask identifier */
    IMAGE_MASK(1024),
    /** JPEG Image (.jpg, .jpeg) */
    JPG(1025),
    /** Portable Network Graphics (.png) */
    PNG(1029),
    /** Bitmap Image (.bmp) */
    BMP(1032);

    /** The unique identifier code for this graphics format */
    private int code;

    /**
     * Constructs a new Graphics format with the specified code.
     *
     * @param code the unique identifier code for this graphics format
     */
    Graphics(int code) {
      this.code = code;
    }

    /**
     * Returns the unique identifier code for this graphics format.
     *
     * @return the integer code representing this graphics format
     */
    public int getCode() {
      return code;
    }
  }
}
