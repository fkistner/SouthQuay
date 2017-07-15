package com.fkistner.SouthQuay.UI;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.*;
import javax.swing.*;
import java.awt.*;

/**
 * Created by florian on 7/12/17.
 */
public class EditorBase {
    protected JPanel panel;
    protected JScrollPane scrollPane;
    protected RSyntaxTextArea syntaxTextArea;
    protected RSyntaxTextArea outputTextArea;
    protected JButton evaluateButton;
    protected JLabel statusLabel;
    protected Gutter gutter;


    private void createUIComponents() {
        syntaxTextArea = new RSyntaxTextArea();
        syntaxTextArea.setCodeFoldingEnabled(true);
        Font defaultFont = new Font("Monospaced", Font.PLAIN, 12);
        gutter = new Gutter(syntaxTextArea);
        gutter.setLineNumberFont(defaultFont);
        gutter.setLineNumberColor(Color.GRAY);
        gutter.setBookmarkingEnabled(true);
        scrollPane = new JScrollPane(syntaxTextArea);
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
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));
        panel.setPreferredSize(new Dimension(600, 600));
        scrollPane.setVerticalScrollBarPolicy(22);
        panel.add(scrollPane, BorderLayout.CENTER);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        scrollPane.setViewportView(panel1);
        outputTextArea = new RSyntaxTextArea();
        outputTextArea.setBackground(new Color(-1644826));
        outputTextArea.setEditable(false);
        outputTextArea.setHighlightCurrentLine(false);
        outputTextArea.setPreferredSize(new Dimension(200, 15));
        panel1.add(outputTextArea, BorderLayout.EAST);
        syntaxTextArea.setPaintMatchedBracketPair(true);
        syntaxTextArea.setPaintTabLines(true);
        syntaxTextArea.setTabsEmulated(true);
        syntaxTextArea.setWhitespaceVisible(true);
        panel1.add(syntaxTextArea, BorderLayout.CENTER);
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        panel.add(toolBar1, BorderLayout.SOUTH);
        statusLabel = new JLabel();
        toolBar1.add(statusLabel);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        toolBar1.add(panel2);
        evaluateButton = new JButton();
        evaluateButton.setText("▶");
        toolBar1.add(evaluateButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}