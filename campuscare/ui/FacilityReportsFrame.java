
package campuscare.ui;

import campuscare.dao.ReportDAO;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import campuscare.model.User;

public class FacilityReportsFrame extends JFrame {


private final Color navy = new Color(23, 48, 83);
private final Color blue = CampusCareTheme.ACCENT;
private final Color background = CampusCareTheme.BACKGROUND;
private final Color textColor = CampusCareTheme.TEXT;

private final Color surface = CampusCareTheme.SURFACE;
private final Color surfaceAlt = CampusCareTheme.SURFACE_ALT;
private final Color mutedText = CampusCareTheme.MUTED_TEXT;
private final Color borderColor = CampusCareTheme.BORDER;


    private final ReportDAO reportDAO = new ReportDAO();
    private final User currentUser;
    private final Runnable dashboardRefresh;

    private final DefaultTableModel model = new DefaultTableModel(
        new Object[] {
            "Report ID", "Issue", "Category", "Location",
            "Priority", "Status", "Created"
        }, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private final JTextField searchField = new JTextField(25);

    private final TableRowSorter<DefaultTableModel> sorter =
        new TableRowSorter<>(model);

    private final String[] statuses = {
        "SUBMITTED", "ASSIGNED", "IN_PROGRESS",
        "RESOLVED", "CLOSED"
    };

    public FacilityReportsFrame(
            User currentUser,
            Runnable dashboardRefresh) {

        this.currentUser = currentUser;
        this.dashboardRefresh = dashboardRefresh;

        setTitle("CampusCare | Facility Reports");
        setSize(1150, 650);
        setMinimumSize(new Dimension(900, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        loadReports();
    }

    
private void buildUI() {
    JPanel root = new JPanel(new BorderLayout(0, 18));
    root.setBackground(background);
    root.setBorder(new EmptyBorder(24, 28, 24, 28));
    setContentPane(root);


    JPanel header = new JPanel(new BorderLayout(10, 8));
    header.setOpaque(false);

    JLabel title = new JLabel("Facility Reports");
    title.setFont(new Font("Segoe UI", Font.BOLD, 27));
    title.setForeground(textColor);

    String role = currentUser.getRole();
    String subtitleText;

    if ("REPORTER".equalsIgnoreCase(role)) {
        subtitleText = "View and track your submitted facility issues.";
    } else if ("MAINTENANCE".equalsIgnoreCase(role)) {
        subtitleText = "View reports and update maintenance status.";
    } else {
        subtitleText = "View, search, update, and manage facility issues.";
    }

    JLabel subtitle = new JLabel(subtitleText);
    subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    subtitle.setForeground(mutedText);

    JPanel heading = new JPanel();
    heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
    heading.setOpaque(false);
    heading.add(title);
    heading.add(Box.createVerticalStrut(6));
    heading.add(subtitle);

    header.add(heading, BorderLayout.WEST);
    root.add(header, BorderLayout.NORTH);


    JPanel content = new JPanel(new BorderLayout(0, 14));
    content.setBackground(surface);
    content.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            new EmptyBorder(18, 18, 18, 18)
    ));

    // Search and refresh
    JPanel searchPanel = new JPanel(new BorderLayout(12, 0));
    searchPanel.setOpaque(false);

    JLabel searchLabel = new JLabel("Search reports:");
    searchLabel.setFont(new Font("Arial", Font.BOLD, 13));
    searchLabel.setForeground(textColor);

    searchField.setFont(new Font("Arial", Font.PLAIN, 14));
    searchField.setToolTipText(
        "Search by ID, issue, category, location, priority, or status"
    );
    searchField.setPreferredSize(new Dimension(300, 36));

    JPanel searchLeft = new JPanel(
        new FlowLayout(FlowLayout.LEFT, 10, 0)
    );
    searchLeft.setOpaque(false);
    searchLeft.add(searchLabel);
    searchLeft.add(searchField);

    JButton refreshButton = new JButton("Refresh");
    styleButton(refreshButton);
    refreshButton.addActionListener(e -> loadReports());

    searchPanel.add(searchLeft, BorderLayout.WEST);
    searchPanel.add(refreshButton, BorderLayout.EAST);
    content.add(searchPanel, BorderLayout.NORTH);


    // Reports table
    table.setRowSorter(sorter);
    table.setRowHeight(34);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.setForeground(textColor);
    table.setBackground(surface);
    table.setSelectionBackground(new Color(39, 95, 100));
    table.setSelectionForeground(Color.WHITE);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    table.setShowVerticalLines(false);
    table.setShowHorizontalLines(true);
    table.setGridColor(borderColor);
    table.setFillsViewportHeight(true);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

    // Style every header cell so the titles remain visible.
    table.getTableHeader().setFont(
            new Font("Segoe UI", Font.BOLD, 12)
    );
    table.getTableHeader().setBackground(navy);
    table.getTableHeader().setForeground(Color.WHITE);
    table.getTableHeader().setOpaque(true);
    table.getTableHeader().setPreferredSize(
            new Dimension(0, 40)
    );

    DefaultTableCellRenderer headerRenderer =
            new DefaultTableCellRenderer();

    headerRenderer.setOpaque(true);
    headerRenderer.setBackground(navy);
    headerRenderer.setForeground(Color.WHITE);
    headerRenderer.setFont(new Font("Segoe UI", Font.BOLD, 12));
    headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
    headerRenderer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(
                    0, 0, 1, 1, borderColor
            ),
            BorderFactory.createEmptyBorder(0, 10, 0, 8)
    ));

    for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
        table.getColumnModel().getColumn(i)
                .setHeaderRenderer(headerRenderer);
    }

    // Add padding to each report cell.
    DefaultTableCellRenderer cellRenderer =
            new DefaultTableCellRenderer();

    cellRenderer.setBorder(
            BorderFactory.createEmptyBorder(0, 10, 0, 8)
    );

    for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
        table.getColumnModel().getColumn(i)
                .setCellRenderer(cellRenderer);
    }

    table.getColumnModel().getColumn(0).setPreferredWidth(80);
    table.getColumnModel().getColumn(1).setPreferredWidth(190);
    table.getColumnModel().getColumn(2).setPreferredWidth(140);
    table.getColumnModel().getColumn(3).setPreferredWidth(230);
    table.getColumnModel().getColumn(4).setPreferredWidth(100);
    table.getColumnModel().getColumn(5).setPreferredWidth(130);
    table.getColumnModel().getColumn(6).setPreferredWidth(160);

    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.getViewport().setBackground(surface);
    scrollPane.setBorder(
            BorderFactory.createLineBorder(borderColor)
    );
    scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
    );
    scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
    );

        BorderFactory.createLineBorder(new Color(225, 231, 239)
    );
    content.add(scrollPane, BorderLayout.CENTER);

    // Role-based action buttons
    JPanel actions = new JPanel(
        new FlowLayout(FlowLayout.RIGHT, 10, 0)
    );
    actions.setOpaque(false);

    if ("ADMIN".equalsIgnoreCase(role)
            || "MAINTENANCE".equalsIgnoreCase(role)) {
        JButton updateButton = new JButton("Update Status");
        styleButton(updateButton);
        updateButton.addActionListener(e -> updateSelectedStatus());
        actions.add(updateButton);
    }

if ("ADMIN".equalsIgnoreCase(role)) {
    JButton deleteButton = new JButton("Delete Report");
    styleButton(deleteButton);
    deleteButton.addActionListener(
        e -> deleteSelectedReport()
    );
    actions.add(deleteButton);

} else if ("REPORTER".equalsIgnoreCase(role)) {
    JButton deleteButton = new JButton("Delete My Report");
    styleButton(deleteButton);
    deleteButton.addActionListener(
        e -> deleteSelectedReport()
    );
    actions.add(deleteButton);
}

    content.add(actions, BorderLayout.SOUTH);
    root.add(content, BorderLayout.CENTER);

    // Live search
    searchField.getDocument().addDocumentListener(
        new DocumentListener() {
            private void filterReports() {
                String keyword = searchField.getText().trim();

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
            public void insertUpdate(DocumentEvent e) {
                filterReports();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterReports();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterReports();
            }
        }
    );
}



private void styleButton(JButton button) {
    button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    button.setBackground(CampusCareTheme.ACCENT);
    button.setForeground(Color.WHITE);
    button.setOpaque(true);
    button.setContentAreaFilled(true);
    button.setBorderPainted(false);
    button.setFocusPainted(false);
    button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    button.getModel().addChangeListener(e -> {
        ButtonModel model = button.getModel();

        if (model.isPressed()) {
            button.setBackground(navy);
        } else if (model.isRollover()) {
            button.setBackground(CampusCareTheme.ACCENT.brighter());
        } else {
            button.setBackground(CampusCareTheme.ACCENT);
        }
    });
}

    private void loadReports() {
        try {
            List<Object[]> reports;

            if ("REPORTER".equalsIgnoreCase(
                    currentUser.getRole())) {
                reports = reportDAO.getReportsByReporterId(
                        currentUser.getUserId());
            } else {
                reports = reportDAO.getAllReports();
            }

            model.setRowCount(0);

            for (Object[] report : reports) {
                model.addRow(report);
            }

        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private int getSelectedReportId() {
        int selectedViewRow = table.getSelectedRow();

        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a report from the table first.",
                "No Report Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return -1;
        }

        int modelRow = table.convertRowIndexToModel(selectedViewRow);
        return ((Number) model.getValueAt(modelRow, 0)).intValue();
    }



private void updateSelectedStatus() {
    int reportId = getSelectedReportId();

    if (reportId == -1) {
        return;
    }

    String currentStatus = null;
    int selectedViewRow = table.getSelectedRow();

    if (selectedViewRow != -1) {
        int modelRow = table.convertRowIndexToModel(selectedViewRow);
        currentStatus = String.valueOf(
            model.getValueAt(modelRow, 5)
        );
    }

    JComboBox<String> statusBox = new JComboBox<>(statuses);
    statusBox.setSelectedItem(currentStatus);

    int choice = JOptionPane.showConfirmDialog(
        this,
        statusBox,
        "Update Report #" + reportId + " Status",
        JOptionPane.OK_CANCEL_OPTION,
        JOptionPane.PLAIN_MESSAGE
    );

    if (choice != JOptionPane.OK_OPTION) {
        return;
    }

    String newStatus = (String) statusBox.getSelectedItem();

    if (newStatus == null) {
        return;
    }

    try {
        boolean updated = reportDAO.updateStatus(
            reportId, newStatus, currentUser
        );

        if (updated) {
            JOptionPane.showMessageDialog(
                this,
                "Report status updated successfully."
            );

            loadReports();

            if (dashboardRefresh != null) {
                dashboardRefresh.run();
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "The report was not found.",
                "Update Failed",
                JOptionPane.WARNING_MESSAGE
            );
        }

    } catch (SecurityException | IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
        );
    } catch (SQLException ex) {
        showDatabaseError(ex);
    }
}





private void deleteSelectedReport() {
    int reportId = getSelectedReportId();

    if (reportId == -1) {
        return;
    }

    int choice = JOptionPane.showConfirmDialog(
        this,
        "Delete report #" + reportId
            + " permanently?\nThis action cannot be undone.",
        "Confirm Delete",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE
    );

    if (choice != JOptionPane.YES_OPTION) {
        return;
    }

    try {
        boolean deleted = reportDAO.deleteReport(
            reportId, currentUser
        );

        if (deleted) {
            JOptionPane.showMessageDialog(
                this,
                "Report deleted successfully."
            );

            loadReports();

            if (dashboardRefresh != null) {
                dashboardRefresh.run();
            }
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Report not found or you do not have permission to delete it.",
                "Delete Failed",
                JOptionPane.WARNING_MESSAGE
            );
        }

    } catch (SecurityException | IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(
            this,
            ex.getMessage(),
            "Access Denied",
            JOptionPane.WARNING_MESSAGE
        );
    } catch (SQLException ex) {
        showDatabaseError(ex);
    }
}


    private void showDatabaseError(SQLException ex) {
        ex.printStackTrace();

        JOptionPane.showMessageDialog(
            this,
            "Unable to complete the database operation:\n"
                + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
