
package campuscare.ui;

import campuscare.dao.BuildingDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class BuildingsFrame extends JFrame {

    private final BuildingDAO dao = new BuildingDAO();

    // CampusCare Deep Teal theme
    private final Color accent = CampusCareTheme.ACCENT;
    private final Color background = CampusCareTheme.BACKGROUND;
    private final Color surface = CampusCareTheme.SURFACE;
    private final Color surfaceAlt = CampusCareTheme.SURFACE_ALT;
    private final Color textColor = CampusCareTheme.TEXT;
    private final Color mutedText = CampusCareTheme.MUTED_TEXT;
    private final Color borderColor = CampusCareTheme.BORDER;

    private final Font bodyFont =
            new Font("Segoe UI", Font.PLAIN, 13);

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Building Name", "Description"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(22);

    private final TableRowSorter<DefaultTableModel> sorter =
            new TableRowSorter<>(model);

    public BuildingsFrame() {
        setTitle("CampusCare | Buildings");
        setSize(1000, 600);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        loadBuildings();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(background);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));
        setContentPane(root);

        // Heading
        JLabel title = new JLabel("Buildings Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(textColor);

        JLabel subtitle = new JLabel(
                "Add, view, edit, search, and delete campus buildings."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(mutedText);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(
                new BoxLayout(heading, BoxLayout.Y_AXIS)
        );
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        root.add(heading, BorderLayout.NORTH);

        // Main content panel
        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(surface);
        content.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(18, 18, 18, 18)
        ));

        // Search and refresh
        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(textColor);
        searchLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        searchField.setFont(bodyFont);
        searchField.setForeground(textColor);
        searchField.setBackground(surfaceAlt);
        searchField.setCaretColor(accent);
        searchField.setSelectionColor(accent);
        searchField.setSelectedTextColor(Color.WHITE);
        searchField.setPreferredSize(new Dimension(260, 36));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(5, 9, 5, 9)
        ));

        JButton refreshButton = createButton("Refresh");
        refreshButton.addActionListener(e -> loadBuildings());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(refreshButton);

        content.add(searchPanel, BorderLayout.NORTH);

        // Buildings table
        table.setRowSorter(sorter);
        table.setRowHeight(36);
        table.setFont(bodyFont);
        table.setForeground(textColor);
        table.setBackground(surface);
        table.setSelectionBackground(accent);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(borderColor);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setOpaque(true);

        // Alternating row colors and readable selected rows
        DefaultTableCellRenderer cellRenderer =
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable source,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column) {

                        Component component =
                                super.getTableCellRendererComponent(
                                        source,
                                        value,
                                        isSelected,
                                        hasFocus,
                                        row,
                                        column
                                );

                        if (isSelected) {
                            component.setBackground(accent);
                            component.setForeground(Color.WHITE);
                        } else {
                            component.setBackground(
                                    row % 2 == 0 ? surface : surfaceAlt
                            );
                            component.setForeground(textColor);
                        }

                        setBorder(new EmptyBorder(0, 10, 0, 10));
                        return component;
                    }
                };

        for (int column = 0;
             column < table.getColumnCount();
             column++) {
            table.getColumnModel()
                    .getColumn(column)
                    .setCellRenderer(cellRenderer);
        }


JTableHeader header = table.getTableHeader();

header.setFont(new Font("Segoe UI", Font.BOLD, 13));
header.setBackground(new Color(18, 59, 66));
header.setForeground(Color.WHITE);
header.setOpaque(true);
header.setReorderingAllowed(false);
header.setPreferredSize(new Dimension(0, 40));
header.setBorder(
        BorderFactory.createMatteBorder(
                0, 0, 1, 0, borderColor
        )
);


header.setDefaultRenderer((tableHeader, value, isSelected,
                           hasFocus, row, column) -> {
    JLabel label = new JLabel(
            value == null ? "" : value.toString()
    );

    label.setFont(new Font("Segoe UI", Font.BOLD, 13));
    label.setForeground(Color.WHITE);
    label.setBackground(new Color(18, 59, 66));
    label.setOpaque(true);
    label.setBorder(new EmptyBorder(0, 10, 0, 10));
    label.setHorizontalAlignment(SwingConstants.LEFT);

    return label;
});

        table.getColumnModel().getColumn(0).setPreferredWidth(65);
        table.getColumnModel().getColumn(1).setPreferredWidth(240);
        table.getColumnModel().getColumn(2).setPreferredWidth(420);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(borderColor)
        );
        scrollPane.getViewport().setBackground(surface);
        scrollPane.getVerticalScrollBar().setBackground(surface);
        scrollPane.getHorizontalScrollBar().setBackground(surface);

        content.add(scrollPane, BorderLayout.CENTER);

        // Action buttons
        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        actions.setOpaque(false);

        JButton addButton = createButton("Add Building");
        JButton editButton = createButton("Edit Building");
        JButton deleteButton = createDangerButton("Delete Building");

        addButton.addActionListener(e -> addBuilding());
        editButton.addActionListener(e -> editBuilding());
        deleteButton.addActionListener(e -> deleteBuilding());

        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);

        content.add(actions, BorderLayout.SOUTH);
        root.add(content, BorderLayout.CENTER);

        // Search filtering
        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    private void filter() {
                        String keyword =
                                searchField.getText().trim();

                        if (keyword.isEmpty()) {
                            sorter.setRowFilter(null);
                        } else {
                            sorter.setRowFilter(
                                    RowFilter.regexFilter(
                                            "(?i)" + Pattern.quote(keyword)
                                    )
                            );
                        }
                    }

                    @Override
                    public void insertUpdate(
                            javax.swing.event.DocumentEvent e) {
                        filter();
                    }

                    @Override
                    public void removeUpdate(
                            javax.swing.event.DocumentEvent e) {
                        filter();
                    }

                    @Override
                    public void changedUpdate(
                            javax.swing.event.DocumentEvent e) {
                        filter();
                    }
                }
        );
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(accent);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        button.setBorder(
                new EmptyBorder(11, 16, 11, 16)
        );

        button.addChangeListener(e -> {
            ButtonModel buttonModel = button.getModel();

            if (buttonModel.isPressed()) {
                button.setBackground(
                        new Color(18, 59, 66)
                );
            } else if (buttonModel.isRollover()) {
                button.setBackground(accent.brighter());
            } else {
                button.setBackground(accent);
            }
        });

        return button;
    }

    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(new Color(176, 64, 70));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        button.setBorder(
                new EmptyBorder(11, 16, 11, 16)
        );

        button.addChangeListener(e -> {
            ButtonModel buttonModel = button.getModel();

            if (buttonModel.isPressed()) {
                button.setBackground(new Color(130, 42, 48));
            } else if (buttonModel.isRollover()) {
                button.setBackground(new Color(200, 78, 84));
            } else {
                button.setBackground(new Color(176, 64, 70));
            }
        });

        return button;
    }

    private void loadBuildings() {
        try {
            model.setRowCount(0);

            for (Object[] building : dao.getAllBuildings()) {
                model.addRow(building);
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private int getSelectedBuildingId() {
        int selected = table.getSelectedRow();

        if (selected == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a building first.",
                    "No Building Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return -1;
        }

        int row = table.convertRowIndexToModel(selected);
        return ((Number) model.getValueAt(row, 0)).intValue();
    }

    private void addBuilding() {
        JTextField nameField = new JTextField(25);
        JTextArea descriptionArea = new JTextArea(4, 25);
        styleDialogFields(nameField, descriptionArea);

        JPanel form = createForm(nameField, descriptionArea);

        int choice = JOptionPane.showConfirmDialog(
                this,
                form,
                "Add Building",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (!validateInput(name, description)) {
            return;
        }

        try {
            if (dao.addBuilding(name, description)) {
                loadBuildings();
                JOptionPane.showMessageDialog(
                        this,
                        "Building added successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void editBuilding() {
        int id = getSelectedBuildingId();

        if (id == -1) {
            return;
        }

        int row = table.convertRowIndexToModel(
                table.getSelectedRow()
        );

        String oldName = String.valueOf(
                model.getValueAt(row, 1)
        );

        Object oldDescription = model.getValueAt(row, 2);

        JTextField nameField = new JTextField(oldName, 25);
        JTextArea descriptionArea = new JTextArea(
                oldDescription == null
                        ? ""
                        : oldDescription.toString(),
                4,
                25
        );

        styleDialogFields(nameField, descriptionArea);

        JPanel form = createForm(nameField, descriptionArea);

        int choice = JOptionPane.showConfirmDialog(
                this,
                form,
                "Edit Building #" + id,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (!validateInput(name, description)) {
            return;
        }

        try {
            if (dao.updateBuilding(id, name, description)) {
                loadBuildings();
                JOptionPane.showMessageDialog(
                        this,
                        "Building updated successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void styleDialogFields(
            JTextField nameField,
            JTextArea descriptionArea) {

        nameField.setFont(bodyFont);
        nameField.setForeground(textColor);
        nameField.setBackground(surfaceAlt);
        nameField.setCaretColor(accent);
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(7, 9, 7, 9)
        ));

        descriptionArea.setFont(bodyFont);
        descriptionArea.setForeground(textColor);
        descriptionArea.setBackground(surfaceAlt);
        descriptionArea.setCaretColor(accent);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(new EmptyBorder(7, 9, 7, 9));
    }

    private JPanel createForm(
            JTextField nameField,
            JTextArea descriptionArea) {

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(surface);
        form.setBorder(new EmptyBorder(8, 8, 8, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(7, 7, 7, 7);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Building Name:");
        nameLabel.setForeground(textColor);
        nameLabel.setFont(bodyFont);

        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(nameField, gbc);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setForeground(textColor);
        descriptionLabel.setFont(bodyFont);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        JScrollPane descriptionScroll =
                new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(
                new Dimension(320, 100)
        );
        descriptionScroll.setBorder(
                BorderFactory.createLineBorder(borderColor)
        );
        descriptionScroll.getViewport().setBackground(surfaceAlt);

        form.add(descriptionScroll, gbc);

        return form;
    }

    private boolean validateInput(
            String name,
            String description) {

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Building name is required.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        if (name.length() > 100) {
            JOptionPane.showMessageDialog(
                    this,
                    "Building name cannot exceed 100 characters.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        if (description.length() > 255) {
            JOptionPane.showMessageDialog(
                    this,
                    "Description cannot exceed 255 characters.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        return true;
    }

    private void deleteBuilding() {
        int id = getSelectedBuildingId();

        if (id == -1) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected building?\n"
                        + "Buildings containing rooms may not be deletable.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (dao.deleteBuilding(id)) {
                loadBuildings();
                JOptionPane.showMessageDialog(
                        this,
                        "Building deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(SQLException ex) {
        ex.printStackTrace();

        String message = ex.getMessage();

        if (ex.getErrorCode() == 1062) {
            message = "That building name already exists. "
                    + "Please choose a unique name.";
        } else if (ex.getErrorCode() == 1451) {
            message = "This building cannot be deleted because "
                    + "rooms or other records still reference it. "
                    + "Remove or reassign those records first.";
        }

        JOptionPane.showMessageDialog(
                this,
                "Database operation failed:\n" + message,
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
