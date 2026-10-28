
package campuscare.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class Theme {

    private Theme() {
    }

    // Main palette
    public static final Color DEEP_TEAL =
        new Color(18, 59, 66);

    public static final Color PRIMARY =
        new Color(29, 155, 138);

    public static final Color PRIMARY_HOVER =
        new Color(39, 174, 155);

    // Dark mode surfaces
    public static final Color BACKGROUND =
        new Color(16, 26, 27);

    public static final Color SIDEBAR =
        new Color(18, 59, 66);

    public static final Color SURFACE =
        new Color(23, 38, 40);

    public static final Color SURFACE_ALT =
        new Color(29, 49, 51);

    // Text and borders
    public static final Color TEXT =
        new Color(230, 242, 240);

    public static final Color MUTED =
        new Color(159, 182, 179);

    public static final Color BORDER =
        new Color(43, 65, 66);

    // Status colors
    public static final Color SUCCESS =
        new Color(46, 160, 115);

    public static final Color WARNING =
        new Color(210, 151, 55);

    public static final Color DANGER =
        new Color(205, 83, 83);

    // Standard fonts
    public static final Font BODY_FONT =
        new Font("Segoe UI", Font.PLAIN, 13);

    public static final Font BUTTON_FONT =
        new Font("Segoe UI", Font.BOLD, 13);

    public static final Font SUBTITLE_FONT =
        new Font("Segoe UI", Font.PLAIN, 12);

    public static final Font TITLE_FONT =
        new Font("Segoe UI", Font.BOLD, 25);

    // Primary action buttons
    public static void stylePrimaryButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
            BorderFactory.createEmptyBorder(
                10, 16, 10, 16
            )
        );
    }

    // Secondary action buttons
    public static void styleSecondaryButton(
            JButton button) {

        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT);
        button.setBackground(SURFACE_ALT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(
            new Cursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
            BorderFactory.createEmptyBorder(
                10, 16, 10, 16
            )
        );
    }

    // Standard text fields
    public static void styleTextField(
            JTextField field) {

        field.setFont(BODY_FONT);
        field.setForeground(TEXT);
        field.setBackground(SURFACE_ALT);
        field.setCaretColor(TEXT);
        field.setSelectionColor(PRIMARY);
        field.setSelectedTextColor(Color.WHITE);

        Border padding =
            BorderFactory.createEmptyBorder(
                8, 10, 8, 10
            );

        Border outline =
            BorderFactory.createLineBorder(BORDER);

        field.setBorder(
            BorderFactory.createCompoundBorder(
                outline, padding
            )
        );
    }

    // Standard tables
    public static void styleTable(JTable table) {
        table.setFont(BODY_FONT);
        table.setForeground(TEXT);
        table.setBackground(SURFACE);
        table.setSelectionBackground(DEEP_TEAL);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(BORDER);
        table.setRowHeight(34);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();

        header.setFont(BUTTON_FONT);
        header.setForeground(TEXT);
        header.setBackground(SURFACE_ALT);
        header.setOpaque(true);
        header.setBorder(
            BorderFactory.createMatteBorder(
                0, 0, 1, 0, BORDER
            )
        );
    }

    // Standard panels
    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND);
    }

    // Standard labels
    public static void styleLabel(JLabel label) {
        label.setFont(BODY_FONT);
        label.setForeground(TEXT);
    }
}
