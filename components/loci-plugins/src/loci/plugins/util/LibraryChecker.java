//
// LibraryChecker.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins.util;

import ij.IJ;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Utility methods for verifying that classes
 * are present and versions are correct.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/util/LibraryChecker.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/util/LibraryChecker.java">SVN</a></dd></dl>
 */
public final class LibraryChecker {

  // -- Constants --

  /** List of possible libraries for which to check. */
  public enum Library {
    BIO_FORMATS,  // Bio-Formats
    OME_JAVA_XML, // OME-XML Java
    OME_JAVA_DS,  // OME-Java
    FORMS         // JGoodies Forms
  }

  /** Minimum version of ImageJ necessary for CompositeImage. */
  public static final String IMAGEJ_VERSION = "1.34";

  /** Message to be displayed if ImageJ is too old for LOCI plugins. */
  public static final String IMAGEJ_MSG =
    "Sorry, the LOCI plugins require ImageJ v" + IMAGEJ_VERSION + " or later.";

  /** Minimum version of ImageJ necessary for LOCI plugins. */
  public static final String COMPOSITE_VERSION = "1.39l";

  /** Message to be displayed if ImageJ is too old for CompositeImage. */
  public static final String COMPOSITE_MSG = "ImageJ " + COMPOSITE_VERSION +
    " or later is required to merge >8 bit or >3 channel data";

  // -- Constructor --

  private LibraryChecker() { }

  // -- Utility methods --

  /** Checks whether the given class is available. */
  public static boolean checkClass(String className) {
    try { Class.forName(className); }
    catch (Throwable t) { return false; }
    return true;
  }

  /** Checks for a required library. */
  public static void checkLibrary(Library library, HashSet missing) {
    switch (library) {
      case BIO_FORMATS:
        checkLibrary("loci.common.RandomAccessInputStream",
          "loci-common.jar", missing);
        checkLibrary("loci.formats.FormatHandler", "bio-formats.jar", missing);
        checkLibrary("loci.poi.poifs.filesystem.POIFSDocument",
          "poi-loci.jar", missing);
        checkLibrary("mdbtools.libmdb.MdbFile", "mdbtools-java.jar", missing);
        break;
      case OME_JAVA_XML:
        checkLibrary("ome.xml.OMEXMLNode", "ome-xml.jar", missing);
        break;
      case OME_JAVA_DS:
        checkLibrary("org.openmicroscopy.ds.DataServer",
          "ome-java.jar", missing);
        checkLibrary("org.apache.xmlrpc.XmlRpcClient",
          "xmlrpc-1.2-b1.jar", missing);
        checkLibrary("org.apache.commons.httpclient.HttpClient",
          "commons-httpclient-2.0-rc2.jar", missing);
        checkLibrary("org.apache.commons.logging.Log",
          "commons-logging.jar", missing);
        break;
      case FORMS:
        checkLibrary("com.jgoodies.forms.layout.FormLayout",
          "forms-1.0.4.jar", missing);
        break;
    }
  }

  /**
   * Checks whether the given class is available; if not,
   * adds the specified JAR file name to the hash set
   * (presumably to report it missing to the user).
   */
  public static void checkLibrary(String className,
    String jarFile, HashSet missing)
  {
    if (!checkClass(className)) missing.add(jarFile);
  }

  /** Checks for a new enough version of the Java Runtime Environment. */
  public static boolean checkJava() {
    String version = System.getProperty("java.version");
    double ver = Double.parseDouble(version.substring(0, 3));
    if (ver < 1.4) {
      IJ.error("LOCI Plugins",
        "Sorry, the LOCI plugins require Java 1.4 or later." +
        "\nYou can download ImageJ with JRE 5.0 from the ImageJ web site.");
      return false;
    }
    return true;
  }

  /** Checks whether ImageJ is new enough for the LOCI plugins. */
  public static boolean checkImageJ() {
    return checkImageJ(IMAGEJ_VERSION, IMAGEJ_MSG);
  }

  /** Checks whether ImageJ is new enough for CompositeImages. */
  public static boolean checkComposite() {
    return checkImageJ(COMPOSITE_VERSION, COMPOSITE_MSG);
  }

  /**
   * Returns true the current ImageJ version is greater than or equal to the
   * specified version. Displays the given warning message if the current
   * version is too old.
   */
  public static boolean checkImageJ(String target, String msg) {
    return checkImageJ(target, msg, "LOCI Plugins");
  }

  /**
   * Returns true the current ImageJ version is greater than or equal to the
   * specified version. Displays the given warning message with the specified
   * title if the current version is too old.
   */
  public static boolean checkImageJ(String target, String msg, String title) {
    boolean success;
    try {
      String current = IJ.getVersion();
      success = current != null && current.compareTo(target) >= 0;
    }
    catch (NoSuchMethodError err) {
      success = false;
    }
    if (!success) IJ.error(title, msg);
    return success;
  }

  /**
   * Reports missing libraries in the given hash set to the user.
   * @return true iff no libraries are missing (the hash set is empty).
   */
  public static boolean checkMissing(HashSet missing) {
    int num = missing.size();
    if (num == 0) return true;
    StringBuffer sb = new StringBuffer();
    sb.append("The following librar");
    sb.append(num == 1 ? "y was" : "ies were");
    sb.append(" not found:");
    Iterator iter = missing.iterator();
    for (int i=0; i<num; i++) sb.append("\n    " + iter.next());
    String them = num == 1 ? "it" : "them";
    sb.append("\nPlease download ");
    sb.append(them);
    sb.append(" from the LOCI website at");
    sb.append("\n    http://www.loci.wisc.edu/software/");
    sb.append("\nand place ");
    sb.append(them);
    sb.append(" in the ImageJ plugins folder.");
    IJ.error("LOCI Plugins", sb.toString());
    return false;
  }

}
