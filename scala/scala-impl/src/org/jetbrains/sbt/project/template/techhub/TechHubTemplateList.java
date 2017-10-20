package org.jetbrains.sbt.project.template.techhub;

import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.speedSearch.ListWithFilter;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * User: Dmitry.Naydanov
 * Date: 29.01.15.
 */
public class TechHubTemplateList {
  private JPanel mainPanel;
  private JBList<IndexEntry> list1;
  //  private JTextArea textArea1;
  private JPanel listPanel;
  private JTextPane descriptionPane;

  public TechHubTemplateList(IndexEntry[] items) {
    list1 = new JBList<>();
    TemplateListModel templateListModel = new TemplateListModel(items);
    $$$setupUI$$$();
    list1.setModel(templateListModel);

    JComponent filterList = ListWithFilter.wrap(
        list1,
        ScrollPaneFactory.createScrollPane(list1),
        o -> o == null ? "" : o.displayName());

    listPanel.add(filterList,
        new GridConstraints(0, 0, 1, 1,
            GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW,
            GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

    descriptionPane.setAutoscrolls(false);

    list1.addListSelectionListener(listSelectionEvent -> {
      IndexEntry template = getSelectedTemplate();
      if (template != null) {
        descriptionPane.setText(assemblyText(template));
        descriptionPane.setCaretPosition(0);
      }
    });
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public IndexEntry getSelectedTemplate() {
    return list1.getSelectedValue();
  }

  private String assemblyText(IndexEntry template) {
    StringBuilder builder = new StringBuilder();
    builder.append("<html>");

    builder.append("<p>");
    if (template.description() != null)
      builder.append(template.description());
    else if (template.summary() != null)
      builder.append(template.summary());
    builder.append("</p>");

    String[] tags = template.keywords();
    if (tags != null && tags.length > 0) {
      builder.append("<p>");
      builder.append("Tags: ");
      builder.append(Arrays.toString(tags));
      builder.append("</p>");
    }

    builder.append("</html>");

    return builder.toString();
  }

  private void createUIComponents() {
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));
    final JSeparator separator1 = new JSeparator();
    mainPanel.add(separator1, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JLabel label1 = new JLabel();
    label1.setText("Description");
    mainPanel.add(label1, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JScrollPane scrollPane1 = new JScrollPane();
    mainPanel.add(scrollPane1, new GridConstraints(1, 1, 2, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    descriptionPane = new JTextPane();
    descriptionPane.setContentType("text/html");
    scrollPane1.setViewportView(descriptionPane);
    listPanel = new JPanel();
    listPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    mainPanel.add(listPanel, new GridConstraints(0, 0, 3, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return mainPanel;
  }
}
