package campuscare.ui;

import campuscare.dao.UserDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.swing.plaf.basic.BasicComboBoxUI;

public class UserManagementFrame extends JFrame {

    // CampusCare Deep Teal theme
    private final Color navy = new Color(18, 59, 66);
    private final Color blue = new Color(29, 155, 138);
    private final Color background = new Color(16, 27, 32);
    private final Color surface = new Color(26, 41, 47);
    private final Color surfaceAlt = new Color(32, 52, 58);
    private final Color textColor = new Color(241, 248, 247);
    private final Color mutedText = new Color(169, 194, 193);
    private final Color borderColor = new Color(41, 64, 71);
    private final Color danger = new Color(190, 65, 75);
    private final Color purple = new Color(120, 91, 160);

    private final UserDAO userDAO = new UserDAO();
    private final int currentUserId;

    private JTable userTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private JTextField fullNameField;
    private JTextField usernameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JTextField searchField;

    public UserManagementFrame(int currentUserId) {
        this.currentUserId = currentUserId;

        setTitle("CampusCare | User Management");
        setSize(1100, 700);
        setMinimumSize(new Dimension(900, 580));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        loadUsers();
        setVisible(true);
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(0, 16));
        main.setBackground(background);
        main.setBorder(new EmptyBorder(22, 25, 22, 25));
        setContentPane(main);

        // Heading
        JLabel title = new JLabel("User Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(textColor);

        JLabel subtitle = new JLabel(
                "Manage CampusCare accounts, roles, and passwords.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(mutedText);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        main.add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        main.add(center, BorderLayout.CENTER);

        // User form
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(surface);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(15, 18, 15, 18)
        ));

        fullNameField = new JTextField(18);
        usernameField = new JTextField(15);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(15);

        styleTextField(fullNameField);
        styleTextField(usernameField);
        styleTextField(emailField);
        styleTextField(passwordField);

// Role combo box — forced Deep Teal styling
roleCombo = new JComboBox<>(
        new String[]{"REPORTER", "MAINTENANCE", "ADMIN"}
);

roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
roleCombo.setForeground(textColor);
roleCombo.setBackground(surfaceAlt);
roleCombo.setOpaque(true);
roleCombo.setEnabled(true);
roleCombo.setFocusable(true);
roleCombo.setEditable(false);
roleCombo.setBorder(
        BorderFactory.createLineBorder(borderColor)
);

// Force Swing to use a UI delegate that respects our colors.
roleCombo.setUI(new BasicComboBoxUI() {
    @Override
    protected JButton createArrowButton() {
        JButton arrow = new JButton("▼");

        arrow.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        arrow.setForeground(textColor);
        arrow.setBackground(surfaceAlt);
        arrow.setBorderPainted(false);
        arrow.setFocusPainted(false);
        arrow.setOpaque(true);

        return arrow;
    }
});

// Style the selected value AND the dropdown items.
roleCombo.setRenderer(new DefaultListCellRenderer() {
    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        JLabel label = (JLabel)
                super.getListCellRendererComponent(
                        list, value, index,
                        isSelected, cellHasFocus);

        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setOpaque(true);
        label.setBorder(
                new EmptyBorder(5, 8, 5, 8)
        );

        if (isSelected && index >= 0) {
            label.setBackground(blue);
            label.setForeground(Color.WHITE);
        } else {
            label.setBackground(surfaceAlt);
            label.setForeground(textColor);
        }

        return label;
    }
});

// Apply colors after setting the custom UI.
roleCombo.setForeground(textColor);
roleCombo.setBackground(surfaceAlt);
roleCombo.setSelectedIndex(0);

        // Apply styling after the renderer has been installed.
        roleCombo.setForeground(textColor);
        roleCombo.setBackground(surfaceAlt);

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 10);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.WEST;

        addField(form, g, 0, 0, "Full Name", fullNameField);
        addField(form, g, 0, 2, "Username", usernameField);
        addField(form, g, 1, 0, "Email", emailField);
        addField(form, g, 1, 2, "Role", roleCombo);
        addField(form, g, 2, 0, "Password (new users)", passwordField);

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 3));
        buttons.setOpaque(false);

        JButton addButton = makeButton("Add User", blue, Color.WHITE);
        JButton updateButton = makeButton(
                "Update Selected", new Color(32, 100, 105), Color.WHITE);
        JButton resetButton = makeButton(
                "Reset Password", purple, Color.WHITE);
        JButton deleteButton = makeButton(
                "Delete Selected", danger, Color.WHITE);
        JButton clearButton = makeButton(
                "Clear", surfaceAlt, textColor);

        addButton.addActionListener(e -> addUser());
        updateButton.addActionListener(e -> updateUser());
        resetButton.addActionListener(e -> resetPassword());
        deleteButton.addActionListener(e -> deleteUser());
        clearButton.addActionListener(e -> clearForm());

        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(resetButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 4;
        g.weightx = 1;
        form.add(buttons, g);

        center.add(form, BorderLayout.NORTH);

        // Registered users table
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));
        tablePanel.setBackground(surface);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(15, 18, 15, 18)
        ));

        JPanel tableHeading = new JPanel(new BorderLayout(10, 0));
        tableHeading.setOpaque(false);

        JLabel tableTitle = new JLabel("Registered Users");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setForeground(textColor);

        searchField = new JTextField(20);
        searchField.setToolTipText(
                "Search by name, username, email, or role");
        styleTextField(searchField);

        JPanel searchPanel = new JPanel(new BorderLayout(7, 0));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(textColor);
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        tableHeading.add(tableTitle, BorderLayout.WEST);
        tableHeading.add(searchPanel, BorderLayout.EAST);
        tablePanel.add(tableHeading, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new Object[]{
                    "ID", "Full Name", "Username",
                    "Email", "Role", "Created At"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setRowHeight(31);
        userTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.setForeground(textColor);
        userTable.setBackground(surface);
        userTable.setGridColor(borderColor);
        userTable.setSelectionBackground(blue);
        userTable.setSelectionForeground(Color.WHITE);
        userTable.setFillsViewportHeight(true);
        userTable.setAutoResizeMode(
                JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

        // Table header
        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(navy);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(0, 36));

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {

                JLabel label = (JLabel)
                        super.getTableCellRendererComponent(
                                table,
                                value,
                                isSelected,
                                hasFocus,
                                row,
                                column
                        );

                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(Color.WHITE);
                label.setBackground(navy);
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 1, borderColor),
                        new EmptyBorder(0, 8, 0, 8)
                ));

                return label;
            }
        });

        sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);

        JScrollPane scroll = new JScrollPane(userTable);
        scroll.setBorder(BorderFactory.createLineBorder(borderColor));
        scroll.getViewport().setBackground(surface);
        scroll.getViewport().setOpaque(true);

        tablePanel.add(scroll, BorderLayout.CENTER);

        JLabel note = new JLabel(
                "Select a user row before updating, resetting a password, or deleting.");
        note.setForeground(mutedText);
        note.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        tablePanel.add(note, BorderLayout.SOUTH);

        center.add(tablePanel, BorderLayout.CENTER);

        // Populate form when a table row is selected.
        userTable.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }

            int viewRow = userTable.getSelectedRow();
            if (viewRow < 0) {
                return;
            }

            int row = userTable.convertRowIndexToModel(viewRow);

            fullNameField.setText(valueAt(row, 1));
            usernameField.setText(valueAt(row, 2));
            emailField.setText(valueAt(row, 3));
            roleCombo.setSelectedItem(valueAt(row, 4));
            passwordField.setText("");
        });

        // Live search
        searchField.getDocument().addDocumentListener(
                new DocumentListener() {

            private void applyFilter() {
                String text = searchField.getText().trim();

                sorter.setRowFilter(text.isEmpty()
                        ? null
                        : RowFilter.regexFilter(
                                "(?i)" + Pattern.quote(text),
                                1, 2, 3, 4));
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilter();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilter();
            }
        });
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        field.setForeground(textColor);
        field.setBackground(surfaceAlt);
        field.setCaretColor(textColor);
        field.setSelectionColor(blue);
        field.setSelectedTextColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(5, 7, 5, 7)
        ));
    }

    private void addField(
            JPanel panel,
            GridBagConstraints g,
            int row,
            int pair,
            String label,
            JComponent field) {

        g.gridy = row;
        g.gridx = pair;
        g.gridwidth = 1;
        g.weightx = 0;

        JLabel fieldLabel = new JLabel(label + ":");
        fieldLabel.setForeground(textColor);
        fieldLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(fieldLabel, g);

        g.gridx = pair + 1;
        g.weightx = 1;
        panel.add(field, g);
    }

    private JButton makeButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFont(new Font("Segoe UI", Font.BOLD, 11));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(9, 11, 9, 11));
        return button;
    }

    private String valueAt(int row, int column) {
        Object value = tableModel.getValueAt(row, column);
        return value == null ? "" : value.toString();
    }

    private int selectedUserId() {
        int viewRow = userTable.getSelectedRow();

        if (viewRow < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a user from the table.",
                    "No User Selected",
                    JOptionPane.WARNING_MESSAGE);
            return -1;
        }

        int row = userTable.convertRowIndexToModel(viewRow);
        return Integer.parseInt(valueAt(row, 0));
    }

    private void loadUsers() {
        try {
            tableModel.setRowCount(0);

            for (Object[] user : userDAO.getAllUsers()) {
                tableModel.addRow(user);
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private boolean validateDetails() {
        String name = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please complete the name, username, and email fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (name.length() > 100 || username.length() > 50
                || email.length() > 120) {
            JOptionPane.showMessageDialog(
                    this,
                    "One or more fields exceed the database length limit.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid email address.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void addUser() {
        if (!validateDetails()) {
            return;
        }

        char[] password = passwordField.getPassword();

        if (password.length < 8) {
            Arrays.fill(password, '\0');
            JOptionPane.showMessageDialog(
                    this,
                    "A new user's password must contain at least 8 characters.",
                    "Invalid Password",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            userDAO.addUser(
                    fullNameField.getText().trim(),
                    usernameField.getText().trim(),
                    emailField.getText().trim(),
                    password,
                    (String) roleCombo.getSelectedItem());

            loadUsers();
            clearForm();

            JOptionPane.showMessageDialog(
                    this, "User account created successfully.");

        } catch (SQLException | GeneralSecurityException ex) {
            showError(ex);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void updateUser() {
        int id = selectedUserId();
        if (id < 0 || !validateDetails()) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Save changes to this user's details and role?",
                "Confirm Update",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            userDAO.updateUser(
                    id,
                    fullNameField.getText().trim(),
                    usernameField.getText().trim(),
                    emailField.getText().trim(),
                    (String) roleCombo.getSelectedItem());

            loadUsers();
            clearForm();

            JOptionPane.showMessageDialog(
                    this, "User details updated successfully.");

        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void resetPassword() {
        int id = selectedUserId();
        if (id < 0) {
            return;
        }

        JPasswordField newPasswordField = new JPasswordField();
        styleTextField(newPasswordField);

        int choice = JOptionPane.showConfirmDialog(
                this,
                new Object[]{
                    "Enter a new password (minimum 8 characters):",
                    newPasswordField
                },
                "Reset User Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        char[] password = newPasswordField.getPassword();

        if (password.length < 8) {
            Arrays.fill(password, '\0');
            JOptionPane.showMessageDialog(
                    this,
                    "The password must contain at least 8 characters.",
                    "Invalid Password",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            userDAO.resetPassword(id, password);
            JOptionPane.showMessageDialog(
                    this, "Password reset successfully.");

        } catch (SQLException | GeneralSecurityException ex) {
            showError(ex);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void deleteUser() {
        int id = selectedUserId();
        if (id < 0) {
            return;
        }

        if (id == currentUserId) {
            JOptionPane.showMessageDialog(
                    this,
                    "You cannot delete your own logged-in account.",
                    "Action Not Allowed",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Permanently delete this user account?\n"
                        + "This action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            userDAO.deleteUser(id);
            loadUsers();
            clearForm();

            JOptionPane.showMessageDialog(
                    this, "User account deleted successfully.");

        } catch (SQLException ex) {
            String message = ex.getMessage() == null
                    ? "" : ex.getMessage().toLowerCase();

            if (message.contains("foreign key")
                    || message.contains("constraint")) {
                JOptionPane.showMessageDialog(
                        this,
                        "This account is referenced by other records "
                                + "and cannot be deleted.",
                        "Account In Use",
                        JOptionPane.WARNING_MESSAGE);
            } else {
                showError(ex);
            }
        }
    }

    private void clearForm() {
        userTable.clearSelection();
        fullNameField.setText("");
        usernameField.setText("");
        emailField.setText("");
        passwordField.setText("");
        roleCombo.setSelectedItem("REPORTER");
        fullNameField.requestFocus();
    }

    private void showError(Exception ex) {
        String message = ex.getMessage() == null
                ? ex.toString() : ex.getMessage();

        if (ex instanceof SQLException
                && "23000".equals(
                        ((SQLException) ex).getSQLState())) {
            message = "The username or email may already exist.\n"
                    + message;
        }

        JOptionPane.showMessageDialog(
                this,
                "Operation failed:\n" + message,
                "CampusCare Error",
                JOptionPane.ERROR_MESSAGE);

        ex.printStackTrace();
    }
}