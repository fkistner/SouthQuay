package com.fkistner.SouthQuay.UI;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import javax.swing.*;
import java.awt.*;

/**
 * Created by florian on 7/12/17.
 */
public class Editor {
    public JPanel panel;
    public RSyntaxTextArea syntaxTextArea;
    private RTextScrollPane scrollPane;

    private void createUIComponents() {
        syntaxTextArea = new RSyntaxTextArea();
        syntaxTextArea.setCodeFoldingEnabled(true);
        syntaxTextArea.setPaintMatchedBracketPair(true);
        syntaxTextArea.setTabsEmulated(true);
        scrollPane = new RTextScrollPane(syntaxTextArea);
        scrollPane.setFoldIndicatorEnabled(true);
        scrollPane.setIconRowHeaderEnabled(true);
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
        panel.setPreferredSize(new Dimension(480, 480));
        scrollPane.setLineNumbersEnabled(true);
        panel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setViewportView(syntaxTextArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel;
    }
}
