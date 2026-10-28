
package campuscare.ui;

import campuscare.dao.IssueCategoryDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.table.JTableHeader;

public class IssueCategoriesFrame extends JFrame {

    private final IssueCategoryDAO dao = new IssueCategoryDAO();

    // CampusCare Deep Teal theme
    private final Color background = CampusCareTheme.BACKGROUND;
    private final Color surface = CampusCareTheme.SURFACE;
    private final Color surfaceAlt = CampusCareTheme.SURFACE_ALT;
    private final Color accent = CampusCareTheme.ACCENT;
    private final Color textColor = CampusCareTheme.TEXT;
    private final Color mutedText = CampusCareTheme.MUTED_TEXT;
    private final Color borderColor = CampusCareTheme.BORDER;
    private final Color danger = new Color(190, 65, 72);
    private final Color fieldBackground = new Color(21, 36, 41);

    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField searchField;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private int selectedCategoryId = -1;

    public IssueCategoriesFrame() {
        setTitle("CampusCare | Issue Categories");
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        loadCategories();
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(background);
        mainPanel.setBorder(new EmptyBorder(24, 28, 24, 28));
        setContentPane(mainPanel);

        // Heading
        JLabel heading = new JLabel("Issue Categories Management");
        heading.setFont(new Font("Segoe UI", Font.BOLD, 26));
        heading.setForeground(textColor);

        JLabel subtitle = new JLabel(
                "Create and manage facility issue categories."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(mutedText);

        JPanel headingPanel = new JPanel();
        headingPanel.setOpaque(false);
        headingPanel.setLayout(
                new BoxLayout(headingPanel, BoxLayout.Y_AXIS)
        );
        headingPanel.add(heading);
        headingPanel.add(Box.createVerticalStrut(6));
        headingPanel.add(subtitle);

        mainPanel.add(headingPanel, BorderLayout.NORTH);

        // Main content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setOpaque(false);

        // Category form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(surface);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(12, 16, 12, 16)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;

        nameField = createTextField();
        descriptionField = createTextField();

        addFormRow(formPanel, gbc, 0, "Category Name", nameField);
        addFormRow(formPanel, gbc, 1, "Description", descriptionField);

        // Action buttons
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0)
        );
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 2, 0));

        JButton addButton = createButton("Add Category", accent);
        JButton updateButton = createButton(
                "Update", new Color(35, 112, 105)
        );
        JButton deleteButton = createButton("Delete", danger);
        JButton clearButton = createButton(
                "Clear", new Color(65, 83, 89)
        );
        JButton refreshButton = createButton(
                "Refresh", new Color(47, 83, 91)
        );

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        JPanel formSection = new JPanel(new BorderLayout(0, 10));
        formSection.setOpaque(false);
        formSection.add(formPanel, BorderLayout.CENTER);
        formSection.add(buttonPanel, BorderLayout.SOUTH);

        centerPanel.add(formSection, BorderLayout.NORTH);

        // Search field
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(2, 0, 2, 0));

        JLabel searchLabel = new JLabel("Search categories:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchLabel.setForeground(textColor);

        searchField = createTextField();
        searchField.setToolTipText("Search by category name or description");
        searchField.setPreferredSize(new Dimension(200, 36));

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Table
        tableModel = new DefaultTableModel(
                new String[]{"ID", "Category Name", "Description"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        categoryTable = new JTable(tableModel);
        categoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        categoryTable.setForeground(textColor);
        categoryTable.setBackground(surface);
        categoryTable.setRowHeight(34);
        categoryTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );
        categoryTable.setSelectionBackground(
                new Color(35, 100, 94)
        );
        categoryTable.setSelectionForeground(Color.WHITE);
        categoryTable.setGridColor(borderColor);
        categoryTable.setShowGrid(true);
        categoryTable.setFillsViewportHeight(true);
        categoryTable.setIntercellSpacing(new Dimension(1, 1));


JTableHeader header = categoryTable.getTableHeader();

header.setFont(new Font("Segoe UI", Font.BOLD, 12));
header.setBackground(new Color(18, 59, 66));
header.setForeground(Color.WHITE);
header.setOpaque(true);
header.setReorderingAllowed(false);
header.setPreferredSize(new Dimension(0, 38));

header.setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column
        );

        label.setOpaque(true);
        label.setBackground(new Color(18, 59, 66));
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 1, borderColor
                ),
                new EmptyBorder(0, 8, 0, 8)
        ));

        return label;
    }
});


        categoryTable.getColumnModel().getColumn(0)
                .setPreferredWidth(65);
        categoryTable.getColumnModel().getColumn(1)
                .setPreferredWidth(220);
        categoryTable.getColumnModel().getColumn(2)
                .setPreferredWidth(550);

        sorter = new TableRowSorter<>(tableModel);
        categoryTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(categoryTable);
        scrollPane.setBackground(surface);
        scrollPane.getViewport().setBackground(surface);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(borderColor)
        );

        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setOpaque(false);
        tablePanel.add(searchPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Search filtering
        searchField.getDocument().addDocumentListener(
                new javax.swing.event.DocumentListener() {
                    private void filter() {
                        String text = searchField.getText().trim();

                        if (text.isEmpty()) {
                            sorter.setRowFilter(null);
                        } else {
                            sorter.setRowFilter(
                                    RowFilter.regexFilter(
                                            "(?i)" + Pattern.quote(text)
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

        // Select a row to load its values into the form
        categoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int viewRow = categoryTable.getSelectedRow();

            if (viewRow < 0) {
                return;
            }

            int modelRow = categoryTable.convertRowIndexToModel(viewRow);

            selectedCategoryId = Integer.parseInt(
                    tableModel.getValueAt(modelRow, 0).toString()
            );

            nameField.setText(
                    String.valueOf(tableModel.getValueAt(modelRow, 1))
            );

            Object description = tableModel.getValueAt(modelRow, 2);
            descriptionField.setText(
                    description == null ? "" : description.toString()
            );
        });

        // Button actions
        addButton.addActionListener(e -> addCategory());
        updateButton.addActionListener(e -> updateCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        clearButton.addActionListener(e -> clearForm());
        refreshButton.addActionListener(e -> {
            loadCategories();
            clearForm();
        });
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setForeground(textColor);
        field.setBackground(fieldBackground);
        field.setCaretColor(accent);
        field.setSelectionColor(accent);
        field.setSelectedTextColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(7, 9, 7, 9)
        ));
        field.setPreferredSize(new Dimension(200, 36));
        return field;
    }

    private void addFormRow(
            JPanel panel,
            GridBagConstraints gbc,
            int row,
            String label,
            JComponent field) {

        JLabel formLabel = new JLabel(label + ":");
        formLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formLabel.setForeground(textColor);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(formLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 14, 10, 14));
        button.setMinimumSize(new Dimension(80, 38));
        return button;
    }

    private void loadCategories() {
        try {
            List<Object[]> categories = dao.getAllCategories();
            tableModel.setRowCount(0);

            for (Object[] category : categories) {
                tableModel.addRow(category);
            }
        } catch (SQLException ex) {
            showError("Could not load categories.", ex);
        }
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a category name.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            nameField.requestFocusInWindow();
            return false;
        }

        if (name.length() > 60) {
            JOptionPane.showMessageDialog(
                    this,
                    "Category name must not exceed 60 characters.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        if (description.length() > 255) {
            JOptionPane.showMessageDialog(
                    this,
                    "Description must not exceed 255 characters.",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }

        return true;
    }

    private void addCategory() {
        if (!validateForm()) {
            return;
        }

        try {
            dao.addCategory(
                    nameField.getText().trim(),
                    descriptionField.getText().trim()
            );

            JOptionPane.showMessageDialog(
                    this, "Category added successfully!"
            );
            clearForm();
            loadCategories();

        } catch (SQLException ex) {
            showError(
                    "Could not add category. The category name may already exist.",
                    ex
            );
        }
    }

    private void updateCategory() {
        if (selectedCategoryId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a category from the table first.",
                    "No Category Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!validateForm()) {
            return;
        }

        try {
            dao.updateCategory(
                    selectedCategoryId,
                    nameField.getText().trim(),
                    descriptionField.getText().trim()
            );

            JOptionPane.showMessageDialog(
                    this, "Category updated successfully!"
            );
            clearForm();
            loadCategories();

        } catch (SQLException ex) {
            showError(
                    "Could not update category. The category name may already exist.",
                    ex
            );
        }
    }

    private void deleteCategory() {
        if (selectedCategoryId == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a category to delete.",
                    "No Category Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete this category? Categories used by reports may be protected.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.deleteCategory(selectedCategoryId);

            JOptionPane.showMessageDialog(
                    this, "Category deleted successfully!"
            );
            clearForm();
            loadCategories();

        } catch (SQLException ex) {
            showError(
                    "This category may be linked to existing reports and cannot be deleted.",
                    ex
            );
        }
    }

    private void clearForm() {
        selectedCategoryId = -1;
        nameField.setText("");
        descriptionField.setText("");
        categoryTable.clearSelection();
        nameField.requestFocusInWindow();
    }

    private void showError(String message, SQLException ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
                this,
                message + "\n\nDatabase details: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}