package com.fkistner.SouthQuay.UI;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;
import javax.swing.*;
import java.awt.*;

/**
 * UI builder designed super class of the Editor controller.
 */
public class EditorBase {
    protected JPanel rootPanel;
    protected JScrollPane scrollPane;
    protected RSyntaxTextArea syntaxTextArea;
    protected JTextPane outputTextPane;
    protected JButton evaluateButton;
    protected JButton abortButton;
    protected JLabel statusLabel;
    protected ErrorStrip errorStrip;

    /**
     * Creates some of the UI components using custom logic.
     */
    private void createUIComponents() {
        syntaxTextArea = new RSyntaxTextArea();
        syntaxTextArea.setCodeFoldingEnabled(true);
        Font font = syntaxTextArea.getFont();

        outputTextPane = new JTextPane();
        outputTextPane.setFont(font);

        errorStrip = new ErrorStrip(syntaxTextArea);

        Gutter gutter = new Gutter(syntaxTextArea);
        gutter.setLineNumberFont(font);
        gutter.setLineNumberColor(SyntaxColors.INSTANCE.getSecondaryColor());
        scrollPane = new JScrollPane();
        scrollPane.setRowHeaderView(gutter);
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
        createUIComponents();
        rootPanel = new JPanel();
        rootPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.setPreferredSize(new Dimension(800, 800));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        rootPanel.add(toolBar1, BorderLayout.SOUTH);
        statusLabel = new JLabel();
        toolBar1.add(statusLabel);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        toolBar1.add(panel1);
        evaluateButton = new JButton();
        evaluateButton.setText("▶");
        toolBar1.add(evaluateButton);
        abortButton = new JButton();
        abortButton.setText("◼");
        abortButton.setVisible(false);
        toolBar1.add(abortButton);
        scrollPane.setHorizontalScrollBarPolicy(31);
        scrollPane.setVerticalScrollBarPolicy(22);
        rootPanel.add(scrollPane, BorderLayout.CENTER);
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setContinuousLayout(true);
        splitPane1.setDividerLocation(400);
        splitPane1.setResizeWeight(1.0);
        scrollPane.setViewportView(splitPane1);
        splitPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setPreferredSize(new Dimension(0, 0));
        scrollPane1.setVerticalScrollBarPolicy(21);
        splitPane1.setLeftComponent(scrollPane1);
        scrollPane1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        syntaxTextArea.setCurrentLineHighlightColor(new Color(-328966));
        syntaxTextArea.setPaintMatchedBracketPair(true);
        syntaxTextArea.setPaintTabLines(true);
        syntaxTextArea.setTabsEmulated(true);
        syntaxTextArea.setWhitespaceVisible(true);
        scrollPane1.setViewportView(syntaxTextArea);
        final JScrollPane scrollPane2 = new JScrollPane();
        scrollPane2.setPreferredSize(new Dimension(0, 0));
        scrollPane2.setVerticalScrollBarPolicy(21);
        splitPane1.setRightComponent(scrollPane2);
        scrollPane2.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), null));
        outputTextPane.setBackground(new Color(-1644826));
        outputTextPane.setEditable(false);
        scrollPane2.setViewportView(outputTextPane);
        errorStrip.setShowMarkAll(true);
        errorStrip.setShowMarkedOccurrences(true);
        rootPanel.add(errorStrip, BorderLayout.EAST);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
