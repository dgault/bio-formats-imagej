//
// QTWidgets.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser and Stack Slicer. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden and Christopher Peterson.

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

package loci.plugins.config;

import ij.Prefs;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;

import loci.plugins.util.LociPrefs;

/**
 * Custom widgets for configuring Bio-Formats QuickTime support.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/config/QTWidgets.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/config/QTWidgets.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class QTWidgets implements IFormatWidgets, ItemListener {

  // -- Fields --

  private String[] labels;
  private Component[] widgets;

  // -- Constructor --

  public QTWidgets() {
    boolean qtJava = Prefs.get(LociPrefs.PREF_QT_QTJAVA, false);

    String legacyLabel = "Legacy";
    JCheckBox legacyBox = new JCheckBox(
      "Use QTJava instead of native QT support", qtJava);
    legacyBox.addItemListener(this);

    labels = new String[] {legacyLabel};
    widgets = new Component[] {legacyBox};
  }

  // -- IFormatWidgets API methods --

  public String[] getLabels() {
    return labels;
  }

  public Component[] getWidgets() {
    return widgets;
  }

  // -- ItemListener API methods --

  public void itemStateChanged(ItemEvent e) {
    JCheckBox box = (JCheckBox) e.getSource();
    Prefs.set(LociPrefs.PREF_QT_QTJAVA, box.isSelected());
  }

}
