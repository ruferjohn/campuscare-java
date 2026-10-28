
package campuscare.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class CampusCareTheme {

    private CampusCareTheme() {
    }

    public static final Color DEEP_TEAL =
            new Color(18, 59, 66);       // #123B42

    public static final Color ACCENT =
            new Color(29, 155, 138);     // #1D9B8A

    public static final Color BACKGROUND =
            new Color(16, 27, 32);       // #101B20

    public static final Color SURFACE =
            new Color(26, 41, 47);       // #1A292F

    public static final Color SURFACE_ALT =
            new Color(32, 52, 58);

    public static final Color TEXT =
            new Color(241, 248, 247);    // #F1F8F7

    public static final Color MUTED_TEXT =
            new Color(169, 194, 193);

    public static final Color BORDER =
            new Color(41, 64, 71);

    public static final Font TITLE_FONT =
            new Font("Segoe UI", Font.BOLD, 26);

    public static final Font HEADING_FONT =
            new Font("Segoe UI", Font.BOLD, 16);

    public static final Font BODY_FONT =
            new Font("Segoe UI", Font.PLAIN, 13);

    public static final Font BUTTON_FONT =
            new Font("Segoe UI", Font.BOLD, 12);

    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(ACCENT);
        button.setForeground(TEXT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(
                new EmptyBorder(10, 16, 10, 16));
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(SURFACE_ALT);
        button.setForeground(TEXT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(
                new EmptyBorder(10, 16, 10, 16));
        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR));
    }

    public static void styleTable(JTable table) {
        table.setBackground(SURFACE);
        table.setForeground(TEXT);
        table.setSelectionBackground(
                new Color(36, 86, 79));
        table.setSelectionForeground(TEXT);
        table.setGridColor(BORDER);
        table.setRowHeight(36);
        table.setFont(BODY_FONT);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);

        JTableHeader header = table.getTableHeader();
        header.setBackground(DEEP_TEAL);
        header.setForeground(TEXT);
        header.setFont(
                new Font("Segoe UI", Font.BOLD, 12));
        header.setOpaque(true);
        header.setPreferredSize(
                new Dimension(0, 40));
    }

    public static void styleTextField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setBackground(SURFACE_ALT);
        field.setForeground(TEXT);
        field.setCaretColor(TEXT);
        field.setSelectionColor(ACCENT);
        field.setSelectedTextColor(TEXT);
        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(8, 10, 8, 10)
                )
        );
    }
}
