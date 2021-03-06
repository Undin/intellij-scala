package org.jetbrains.plugins.scala.debugger.filters;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.jetbrains.annotations.Nls;
import org.jetbrains.plugins.scala.ScalaBundle;
import org.jetbrains.plugins.scala.icons.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

/**
 * @author ilyas
 */
public class ScalaDebuggerSettingsConfigurable implements Configurable {
    private JPanel myPanel;
    private JLabel startIndexLabel;
    private JSpinner myStartIndexSpinner;
    private JSpinner myEndIndexSpinner;
    private JLabel endIndexLabel;
    private JCheckBox friendlyDisplayOfScalaCheckBox;
    private JCheckBox dontShowRuntimeRefs;
    private JCheckBox doNotExpandStreamsCheckBox;
    private JCheckBox showOuterVariables;
    private JCheckBox forceClassPrepareRequestsForNestedTypes;
    private JCheckBox forcePositionLookupInNestedTypes;
    private boolean isModified = false;
    private final ScalaDebuggerSettings mySettings;

    public ScalaDebuggerSettingsConfigurable(final ScalaDebuggerSettings settings) {
        mySettings = settings;
        friendlyDisplayOfScalaCheckBox.setSelected(settings.FRIENDLY_COLLECTION_DISPLAY_ENABLED);
        doNotExpandStreamsCheckBox.setSelected(settings.DO_NOT_DISPLAY_STREAMS);
        dontShowRuntimeRefs.setSelected(settings.DONT_SHOW_RUNTIME_REFS);
        friendlyDisplayOfScalaCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final boolean collectionsSettingsEnabled = friendlyDisplayOfScalaCheckBox.isSelected();
                myStartIndexSpinner.setEnabled(collectionsSettingsEnabled);
                myEndIndexSpinner.setEnabled(collectionsSettingsEnabled);
                doNotExpandStreamsCheckBox.setEnabled(collectionsSettingsEnabled);
            }
        });
        showOuterVariables.setSelected(settings.SHOW_VARIABLES_FROM_OUTER_SCOPES);
        forceClassPrepareRequestsForNestedTypes.setSelected(settings.FORCE_CLASS_PREPARE_REQUESTS_FOR_NESTED_TYPES);
        forcePositionLookupInNestedTypes.setSelected(settings.FORCE_POSITION_LOOKUP_IN_NESTED_TYPES);
    }

    @Nls
    public String getDisplayName() {
        return ScalaBundle.message("scala.debug.caption");
    }

    public Icon getIcon() {
        return Icons.SCALA_SMALL_LOGO;
    }

    public String getHelpTopic() {
        return null;
    }

    public JComponent createComponent() {
        myStartIndexSpinner.setModel(new SpinnerNumberModel(1, 1, null, 1));
        myEndIndexSpinner.setModel(new SpinnerNumberModel(1, 1, null, 1));

        myStartIndexSpinner.setValue(mySettings.COLLECTION_START_INDEX);
        myEndIndexSpinner.setValue(mySettings.COLLECTION_END_INDEX);

        return myPanel;
    }

    public boolean isModified() {
        return mySettings.COLLECTION_START_INDEX != myStartIndexSpinner.getValue() ||
                mySettings.COLLECTION_END_INDEX != myEndIndexSpinner.getValue() ||
                mySettings.FRIENDLY_COLLECTION_DISPLAY_ENABLED != friendlyDisplayOfScalaCheckBox.isSelected() ||
                mySettings.DONT_SHOW_RUNTIME_REFS != dontShowRuntimeRefs.isSelected() ||
                mySettings.DO_NOT_DISPLAY_STREAMS != doNotExpandStreamsCheckBox.isSelected() ||
                mySettings.SHOW_VARIABLES_FROM_OUTER_SCOPES != showOuterVariables.isSelected() ||
                mySettings.FORCE_CLASS_PREPARE_REQUESTS_FOR_NESTED_TYPES != forceClassPrepareRequestsForNestedTypes.isSelected() ||
                mySettings.FORCE_POSITION_LOOKUP_IN_NESTED_TYPES != forcePositionLookupInNestedTypes.isSelected();
    }

    public void apply() throws ConfigurationException {
        mySettings.FRIENDLY_COLLECTION_DISPLAY_ENABLED = friendlyDisplayOfScalaCheckBox.isSelected();
        mySettings.DONT_SHOW_RUNTIME_REFS = dontShowRuntimeRefs.isSelected();
        mySettings.COLLECTION_START_INDEX = (Integer) myStartIndexSpinner.getValue();
        mySettings.COLLECTION_END_INDEX = (Integer) myEndIndexSpinner.getValue();
        mySettings.DO_NOT_DISPLAY_STREAMS = doNotExpandStreamsCheckBox.isSelected();
        mySettings.SHOW_VARIABLES_FROM_OUTER_SCOPES = showOuterVariables.isSelected();
        mySettings.FORCE_CLASS_PREPARE_REQUESTS_FOR_NESTED_TYPES = forceClassPrepareRequestsForNestedTypes.isSelected();
        mySettings.FORCE_POSITION_LOOKUP_IN_NESTED_TYPES = forcePositionLookupInNestedTypes.isSelected();
    }

    public void reset() {
        friendlyDisplayOfScalaCheckBox.setSelected(mySettings.FRIENDLY_COLLECTION_DISPLAY_ENABLED);
        dontShowRuntimeRefs.setSelected(mySettings.DONT_SHOW_RUNTIME_REFS);
        myStartIndexSpinner.setValue(mySettings.COLLECTION_START_INDEX);
        myEndIndexSpinner.setValue(mySettings.COLLECTION_END_INDEX);
        doNotExpandStreamsCheckBox.setSelected(mySettings.DO_NOT_DISPLAY_STREAMS);
        showOuterVariables.setSelected(mySettings.SHOW_VARIABLES_FROM_OUTER_SCOPES);
        forceClassPrepareRequestsForNestedTypes.setSelected(mySettings.FORCE_CLASS_PREPARE_REQUESTS_FOR_NESTED_TYPES);
        forcePositionLookupInNestedTypes.setSelected(mySettings.FORCE_POSITION_LOOKUP_IN_NESTED_TYPES);
    }

    public void disposeUIResources() {
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        myPanel = new JPanel();
        myPanel.setLayout(new GridLayoutManager(8, 3, new Insets(0, 0, 0, 0), -1, -1));
        final Spacer spacer1 = new Spacer();
        myPanel.add(spacer1, new GridConstraints(7, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        friendlyDisplayOfScalaCheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(friendlyDisplayOfScalaCheckBox, this.$$$getMessageFromBundle$$$("org/jetbrains/plugins/scala/ScalaBundle", "friendly.collection.display.enabled"));
        myPanel.add(friendlyDisplayOfScalaCheckBox, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        myPanel.add(panel1, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        startIndexLabel = new JLabel();
        this.$$$loadLabelText$$$(startIndexLabel, this.$$$getMessageFromBundle$$$("org/jetbrains/plugins/scala/ScalaBundle", "friendly.collection.debug.start.index"));
        panel1.add(startIndexLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        myStartIndexSpinner = new JSpinner();
        panel1.add(myStartIndexSpinner, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        endIndexLabel = new JLabel();
        this.$$$loadLabelText$$$(endIndexLabel, this.$$$getMessageFromBundle$$$("org/jetbrains/plugins/scala/ScalaBundle", "friendly.collection.debug.end.index"));
        panel1.add(endIndexLabel, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        myEndIndexSpinner = new JSpinner();
        panel1.add(myEndIndexSpinner, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        doNotExpandStreamsCheckBox = new JCheckBox();
        this.$$$loadButtonText$$$(doNotExpandStreamsCheckBox, this.$$$getMessageFromBundle$$$("org/jetbrains/plugins/scala/ScalaBundle", "friendly.collection.do.not.display.streams"));
        myPanel.add(doNotExpandStreamsCheckBox, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        myPanel.add(spacer2, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        myPanel.add(spacer3, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("");
        myPanel.add(label1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("   ");
        myPanel.add(label2, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        dontShowRuntimeRefs = new JCheckBox();
        this.$$$loadButtonText$$$(dontShowRuntimeRefs, this.$$$getMessageFromBundle$$$("org/jetbrains/plugins/scala/ScalaBundle", "dont.show.runtime.refs"));
        myPanel.add(dontShowRuntimeRefs, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        showOuterVariables = new JCheckBox();
        this.$$$loadButtonText$$$(showOuterVariables, this.$$$getMessageFromBundle$$$("org/jetbrains/plugins/scala/ScalaBundle", "show.variables.from.outer.scopes.in.variables.view"));
        myPanel.add(showOuterVariables, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forceClassPrepareRequestsForNestedTypes = new JCheckBox();
        this.$$$loadButtonText$$$(forceClassPrepareRequestsForNestedTypes, this.$$$getMessageFromBundle$$$("org/jetbrains/plugins/scala/ScalaBundle", "force.class.prepare.request.for.nested.types"));
        myPanel.add(forceClassPrepareRequestsForNestedTypes, new GridConstraints(5, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        forcePositionLookupInNestedTypes = new JCheckBox();
        this.$$$loadButtonText$$$(forcePositionLookupInNestedTypes, this.$$$getMessageFromBundle$$$("org/jetbrains/plugins/scala/ScalaBundle", "force.position.lookup.in.nested.types"));
        myPanel.add(forcePositionLookupInNestedTypes, new GridConstraints(6, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    private static Method $$$cachedGetBundleMethod$$$ = null;

    private String $$$getMessageFromBundle$$$(String path, String key) {
        ResourceBundle bundle;
        try {
            Class<?> thisClass = this.getClass();
            if ($$$cachedGetBundleMethod$$$ == null) {
                Class<?> dynamicBundleClass = thisClass.getClassLoader().loadClass("com.intellij.DynamicBundle");
                $$$cachedGetBundleMethod$$$ = dynamicBundleClass.getMethod("getBundle", String.class, Class.class);
            }
            bundle = (ResourceBundle) $$$cachedGetBundleMethod$$$.invoke(null, path, thisClass);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle(path);
        }
        return bundle.getString(key);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadLabelText$$$(JLabel component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setDisplayedMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return myPanel;
    }

}
