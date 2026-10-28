
package campuscare.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import campuscare.dao.ReportDAO;

public class ReportIssueFrame extends JFrame {

    private final int reporterId;
    private final Runnable onReportSubmitted;
    private final ReportDAO reportDAO = new ReportDAO();

    // Shared CampusCare theme
    private final Color navy = new Color(23, 48, 83);
    private final Color accent = CampusCareTheme.ACCENT;
    private final Color background = CampusCareTheme.BACKGROUND;
    private final Color surface = CampusCareTheme.SURFACE;
    private final Color surfaceAlt = CampusCareTheme.SURFACE_ALT;
    private final Color textColor = CampusCareTheme.TEXT;
    private final Color mutedText = CampusCareTheme.MUTED_TEXT;
    private final Color borderColor = CampusCareTheme.BORDER;

    private final Font bodyFont =
            new Font("Segoe UI", Font.PLAIN, 13);

    private JComboBox<ChoiceItem> buildingBox;
    private JComboBox<ChoiceItem> roomBox;
    private JComboBox<ChoiceItem> categoryBox;
    private JComboBox<String> priorityBox;

    private JTextField titleField;
    private JTextArea descriptionArea;

    public ReportIssueFrame(
            int reporterId,
            Runnable onReportSubmitted) {

        this.reporterId = reporterId;
        this.onReportSubmitted = onReportSubmitted;

        setTitle("CampusCare | Report an Issue");
        setSize(700, 640);
        setMinimumSize(new Dimension(620, 600));
        setLocationRelativeTo(null);

        buildUI();
        loadBuildings();
        loadCategories();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(background);
        root.setBorder(new EmptyBorder(24, 30, 24, 30));
        setContentPane(root);

        // Page heading
        JLabel heading = new JLabel("Report a Facility Issue");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setForeground(textColor);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel(
                "Describe the problem so maintenance can address it."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(mutedText);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headingPanel = new JPanel();
        headingPanel.setOpaque(false);
        headingPanel.setLayout(
                new BoxLayout(headingPanel, BoxLayout.Y_AXIS)
        );

        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(7));
        headingPanel.add(subtitle);

        root.add(headingPanel, BorderLayout.NORTH);

        // Form surface
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(surface);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(18, 22, 18, 22)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Dropdowns
        buildingBox = new JComboBox<>();
        roomBox = new JComboBox<>();
        categoryBox = new JComboBox<>();

        priorityBox = new JComboBox<>(
                new String[]{"LOW", "MEDIUM", "HIGH", "URGENT"}
        );
        priorityBox.setSelectedItem("MEDIUM");

        styleComboBox(buildingBox);
        styleComboBox(roomBox);
        styleComboBox(categoryBox);
        styleComboBox(priorityBox);

        // Report title
        titleField = new JTextField();
        styleTextField(titleField);

        // Description
        descriptionArea = new JTextArea(5, 25);
        descriptionArea.setFont(bodyFont);
        descriptionArea.setForeground(textColor);
        descriptionArea.setBackground(surfaceAlt);
        descriptionArea.setCaretColor(accent);
        descriptionArea.setSelectionColor(accent);
        descriptionArea.setSelectedTextColor(Color.WHITE);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(
                new EmptyBorder(9, 10, 9, 10)
        );

        JScrollPane descriptionScroll =
                new JScrollPane(descriptionArea);

        descriptionScroll.setPreferredSize(
                new Dimension(400, 75)
        );
        descriptionScroll.setBorder(
                BorderFactory.createLineBorder(borderColor)
        );
        descriptionScroll.getViewport().setBackground(surfaceAlt);
        descriptionScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        // Form fields
        addField(form, gbc, 0, "Building", buildingBox);
        addField(form, gbc, 1, "Room", roomBox);
        addField(form, gbc, 2, "Issue Category", categoryBox);
        addField(form, gbc, 3, "Report Title", titleField);
        addField(form, gbc, 4, "Priority", priorityBox);
        addField(form, gbc, 5, "Description", descriptionScroll);

        // Update available rooms when the building changes.
        buildingBox.addActionListener(e -> loadRooms());

        // Buttons
        JButton clearButton = new JButton("Clear Form");
        styleSecondaryButton(clearButton);
        clearButton.addActionListener(e -> clearForm());

        JButton submitButton = new JButton("Submit Report");
        stylePrimaryButton(submitButton);
        submitButton.addActionListener(e -> submitReport());

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        buttons.setOpaque(false);
        buttons.add(clearButton);
        buttons.add(submitButton);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(form, BorderLayout.CENTER);
        center.add(buttons, BorderLayout.SOUTH);

        root.add(center, BorderLayout.CENTER);

        // Enter in the title field submits the form.
        getRootPane().setDefaultButton(submitButton);
    }

    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JComponent input) {

        gbc.gridx = 0;
        gbc.weightx = 1;

        // Label
        gbc.gridy = row * 2;
        gbc.insets = new Insets(
                row == 0 ? 0 : 7, 0, 4, 0
        );

        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        fieldLabel.setForeground(textColor);

        panel.add(fieldLabel, gbc);

        // Input control
        gbc.gridy = row * 2 + 1;
        gbc.insets = new Insets(0, 0, 5, 0);

        input.setFont(bodyFont);
        input.setPreferredSize(
                new Dimension(400, 36)
        );
        input.setMinimumSize(
                new Dimension(100, 36)
        );

        panel.add(input, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(textColor);
        field.setBackground(surfaceAlt);
        field.setCaretColor(accent);
        field.setSelectionColor(accent);
        field.setSelectedTextColor(Color.WHITE);
        field.setOpaque(true);

        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(7, 10, 7, 10)
        ));

        field.setPreferredSize(new Dimension(400, 36));
    }

    private void styleComboBox(JComboBox<?> comboBox) {
        Color fieldText = new Color(28, 39, 43);
        Color fieldBackground = new Color(242, 247, 248);

        comboBox.setFont(bodyFont);
        comboBox.setForeground(fieldText);
        comboBox.setBackground(fieldBackground);
        comboBox.setOpaque(true);
        comboBox.setFocusable(true);

        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                super.getListCellRendererComponent(
                        list, value, index,
                        isSelected, cellHasFocus
                );

                setFont(bodyFont);
                setOpaque(true);
                setForeground(fieldText);
                setBackground(fieldBackground);
                setBorder(new EmptyBorder(5, 8, 5, 8));

                if (isSelected && index >= 0) {
                    setForeground(Color.WHITE);
                    setBackground(accent);
                }

                return this;
            }
        });
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );
        button.setForeground(Color.WHITE);
        button.setBackground(accent);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(
                new EmptyBorder(11, 18, 11, 18)
        );
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        button.addChangeListener(e -> {
            ButtonModel model = button.getModel();

            if (model.isPressed()) {
                button.setBackground(navy);
            } else if (model.isRollover()) {
                button.setBackground(accent.brighter());
            } else {
                button.setBackground(accent);
            }
        });
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(new Color(28, 39, 43));
        button.setBackground(new Color(242, 247, 248));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFocusPainted(false);

        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(10, 16, 10, 16)
        ));

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    private void loadBuildings() {
        try {
            buildingBox.removeAllItems();

            List<Object[]> buildings = reportDAO.getBuildings();

            for (Object[] row : buildings) {
                buildingBox.addItem(
                        new ChoiceItem(
                                ((Number) row[0]).intValue(),
                                String.valueOf(row[1])
                        )
                );
            }

            loadRooms();

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadRooms() {
        if (roomBox == null || buildingBox == null) {
            return;
        }

        roomBox.removeAllItems();

        ChoiceItem building =
                (ChoiceItem) buildingBox.getSelectedItem();

        if (building == null) {
            return;
        }

        try {
            List<Object[]> rooms =
                    reportDAO.getRoomsByBuilding(building.id);

            for (Object[] row : rooms) {
                roomBox.addItem(
                        new ChoiceItem(
                                ((Number) row[0]).intValue(),
                                String.valueOf(row[1])
                        )
                );
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadCategories() {
        try {
            categoryBox.removeAllItems();

            List<Object[]> categories = reportDAO.getCategories();

            for (Object[] row : categories) {
                categoryBox.addItem(
                        new ChoiceItem(
                                ((Number) row[0]).intValue(),
                                String.valueOf(row[1])
                        )
                );
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void submitReport() {
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();

        ChoiceItem room =
                (ChoiceItem) roomBox.getSelectedItem();

        ChoiceItem category =
                (ChoiceItem) categoryBox.getSelectedItem();

        String priority =
                (String) priorityBox.getSelectedItem();

        if (title.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both a title and description.",
                    "Missing Information",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (title.length() > 150) {
            JOptionPane.showMessageDialog(
                    this,
                    "The title must not exceed 150 characters.",
                    "Invalid Title",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (room == null || category == null || priority == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a building with a room and an issue category.",
                    "Missing Selection",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Submit this facility issue?",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean saved = reportDAO.createReport(
                    reporterId,
                    room.id,
                    category.id,
                    title,
                    description,
                    priority
            );

            if (saved) {
                JOptionPane.showMessageDialog(
                        this,
                        "Report submitted successfully!\n"
                                + "Status: SUBMITTED",
                        "Report Submitted",
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (onReportSubmitted != null) {
                    onReportSubmitted.run();
                }

                dispose();
            }

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void clearForm() {
        titleField.setText("");
        descriptionArea.setText("");
        priorityBox.setSelectedItem("MEDIUM");

        if (buildingBox.getItemCount() > 0) {
            buildingBox.setSelectedIndex(0);
        }

        if (categoryBox.getItemCount() > 0) {
            categoryBox.setSelectedIndex(0);
        }

        titleField.requestFocusInWindow();
    }

    private void showError(SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                "Database operation failed:\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private static class ChoiceItem {
        private final int id;
        private final String name;

        private ChoiceItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
