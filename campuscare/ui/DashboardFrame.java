package campuscare.ui;

import campuscare.model.User;
import campuscare.dao.DashboardDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DashboardFrame extends JFrame {

    private final User currentUser;

    private JPanel contentPanel;

    private JLabel totalReportsValue;
    private JLabel pendingValue;
    private JLabel inProgressValue;
    private JLabel resolvedValue;

    private DefaultTableModel reportsModel;

    // CampusCare Deep Teal theme
    private final Color navy = CampusCareTheme.DEEP_TEAL;
    private final Color blue = CampusCareTheme.ACCENT;
    private final Color background = CampusCareTheme.BACKGROUND;
    private final Color textColor = CampusCareTheme.TEXT;

    public DashboardFrame(User user) {
        this.currentUser = user;

        String role = user.getRole();

        // Set the correct window title for the logged-in role.
        switch (role.toUpperCase()) {
            case "ADMIN":
                setTitle("CampusCare | Admin Dashboard");
                break;

            case "MAINTENANCE":
                setTitle("CampusCare | Maintenance Dashboard");
                break;

            case "REPORTER":
                setTitle("CampusCare | Reporter Dashboard");
                break;

            default:
                setTitle("CampusCare | Dashboard");
                break;
        }

        setSize(1200, 750);
        setMinimumSize(new Dimension(1000, 650));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(background);

        root.add(createSidebar(user), BorderLayout.WEST);
        root.add(createMainContent(user), BorderLayout.CENTER);

        setContentPane(root);

        loadDashboardData();
    }

    // =========================================================
    // SIDEBAR
    // =========================================================

    private JPanel createSidebar(User user) {
        JPanel sidebar = new JPanel();

        sidebar.setPreferredSize(new Dimension(235, 0));
        sidebar.setBackground(navy);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(25, 15, 20, 15));

        JLabel brand = new JLabel("  CAMPUSCARE");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Arial", Font.BOLD, 21));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel university = new JLabel("ILOILO UNIVERSITY");
        university.setForeground(new Color(190, 207, 229));
        university.setFont(new Font("Arial", Font.PLAIN, 10));
        university.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(university);
        sidebar.add(Box.createVerticalStrut(40));

        String role = user.getRole();
        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

        addNavButton(
                sidebar,
                "Dashboard",
                () -> showPage("Dashboard")
        );

        addNavButton(
                sidebar,
                "Facility Reports",
                () -> showPage("Facility Reports")
        );

        if (isAdmin) {
            addNavButton(
                    sidebar,
                    "Buildings",
                    () -> showPage("Buildings")
            );

            addNavButton(
                    sidebar,
                    "Rooms",
                    () -> showPage("Rooms")
            );

            addNavButton(
                    sidebar,
                    "Issue Categories",
                    () -> showPage("Issue Categories")
            );

            addNavButton(
                    sidebar,
                    "User Management",
                    () -> showPage("User Management")
            );
        }

        if ("REPORTER".equalsIgnoreCase(role)) {
            addNavButton(
                    sidebar,
                    "Report an Issue",
                    () -> showPage("Report an Issue")
            );
        }

        sidebar.add(Box.createVerticalGlue());

        JLabel account = new JLabel("  Signed in as " + role);
        account.setForeground(Color.WHITE);
        account.setFont(new Font("Arial", Font.PLAIN, 12));
        account.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(account);
        sidebar.add(Box.createVerticalStrut(12));

        addNavButton(
                sidebar,
                "Logout",
                () -> logout()
        );

        return sidebar;
    }

    private void addNavButton(
            JPanel sidebar,
            String label,
            Runnable action) {

        JButton button = new JButton(label);

        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(navy);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(13, 12, 13, 8));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(205, 45));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(blue);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(navy);
            }
        });

        button.addActionListener(e -> action.run());

        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    // =========================================================
    // MAIN CONTENT AND ROLE-BASED HEADER
    // =========================================================

    private JPanel createMainContent(User user) {

        JPanel main = new JPanel(new BorderLayout(0, 22));
        main.setBackground(background);
        main.setBorder(new EmptyBorder(28, 30, 25, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout(15, 0));
        header.setOpaque(false);

        String role = user.getRole();

        String dashboardTitle;
        String dashboardSubtitle;

        switch (role.toUpperCase()) {
            case "ADMIN":
                dashboardTitle = "Admin Dashboard";
                dashboardSubtitle =
                        "Monitor campus facilities and maintenance reports";
                break;

            case "MAINTENANCE":
                dashboardTitle = "Maintenance Dashboard";
                dashboardSubtitle =
                        "Manage facility issues and maintenance reports";
                break;

            case "REPORTER":
                dashboardTitle = "Reporter Dashboard";
                dashboardSubtitle =
                        "Submit and track your facility issue reports";
                break;

            default:
                dashboardTitle = "Dashboard";
                dashboardSubtitle =
                        "CampusCare facility management system";
                break;
        }

        JLabel title = new JLabel(dashboardTitle);
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(textColor);

        JLabel subtitle = new JLabel(dashboardSubtitle);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(CampusCareTheme.MUTED_TEXT);

        // Title and subtitle container
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(
                new BoxLayout(titles, BoxLayout.Y_AXIS)
        );

        titles.add(title);
        titles.add(Box.createVerticalStrut(5));
        titles.add(subtitle);

        // Logged-in user's role badge
        JLabel roleLabel = new JLabel(role.toUpperCase());
        roleLabel.setOpaque(true);
        roleLabel.setBackground(new Color(36, 86, 79));
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setBorder(new EmptyBorder(9, 14, 9, 14));
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));

        header.add(titles, BorderLayout.CENTER);
        header.add(roleLabel, BorderLayout.EAST);

        // =====================================================
        // DASHBOARD BODY
        // =====================================================

        JPanel body = new JPanel(new BorderLayout());
        body.setOpaque(false);

        JPanel dashboardContent = new JPanel();
        dashboardContent.setOpaque(false);
        dashboardContent.setLayout(
                new BoxLayout(dashboardContent, BoxLayout.Y_AXIS)
        );

        JLabel overview = new JLabel("Reports Overview");
        overview.setFont(new Font("Segoe UI", Font.BOLD, 19));
        overview.setForeground(textColor);
        overview.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboardContent.add(overview);
        dashboardContent.add(Box.createVerticalStrut(16));
        dashboardContent.add(createStatistics());
        dashboardContent.add(Box.createVerticalStrut(22));

        // =====================================================
        // RECENT FACILITY REPORTS
        // =====================================================

        JPanel recentPanel = new JPanel(
                new BorderLayout(0, 14)
        );

        recentPanel.setBackground(CampusCareTheme.SURFACE);
        recentPanel.setOpaque(true);

        recentPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                CampusCareTheme.BORDER
                        ),
                        new EmptyBorder(20, 20, 20, 20)
                )
        );

        JLabel recentTitle = new JLabel(
                "Recent Facility Reports"
        );

        recentTitle.setFont(
                new Font("Segoe UI", Font.BOLD, 17)
        );

        recentTitle.setForeground(CampusCareTheme.TEXT);

        String[] columns = {
                "Report ID",
                "Issue",
                "Location",
                "Status"
        };

        reportsModel = new DefaultTableModel(
                new Object[][] {},
                columns
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(reportsModel);

        table.setRowHeight(36);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setBackground(CampusCareTheme.SURFACE);
        table.setForeground(CampusCareTheme.TEXT);
        table.setSelectionBackground(new Color(36, 86, 79));
        table.setSelectionForeground(CampusCareTheme.TEXT);
        table.setGridColor(CampusCareTheme.BORDER);
        table.setFillsViewportHeight(true);
        table.setShowVerticalLines(false);
        table.setOpaque(true);
        table.setAutoCreateRowSorter(true);

        // Table cell styling
        table.setDefaultRenderer(
                Object.class,
                new DefaultTableCellRenderer() {
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

                        label.setOpaque(true);
                        label.setFont(
                                new Font(
                                        "Segoe UI",
                                        Font.PLAIN,
                                        13
                                )
                        );

                        label.setBackground(
                                isSelected
                                        ? new Color(36, 86, 79)
                                        : CampusCareTheme.SURFACE
                        );

                        label.setForeground(CampusCareTheme.TEXT);

                        label.setBorder(
                                BorderFactory.createEmptyBorder(
                                        0, 10, 0, 10
                                )
                        );

                        return label;
                    }
                }
        );

        // Table header styling
        JTableHeader tableHeader = table.getTableHeader();

        tableHeader.setPreferredSize(new Dimension(0, 38));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setOpaque(true);

        tableHeader.setDefaultRenderer(
                new DefaultTableCellRenderer() {
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

                        label.setOpaque(true);
                        label.setBackground(
                                CampusCareTheme.DEEP_TEAL
                        );
                        label.setForeground(CampusCareTheme.TEXT);
                        label.setFont(
                                new Font(
                                        "Segoe UI",
                                        Font.BOLD,
                                        12
                                )
                        );
                        label.setHorizontalAlignment(
                                SwingConstants.LEFT
                        );

                        label.setBorder(
                                BorderFactory.createEmptyBorder(
                                        0, 10, 0, 10
                                )
                        );

                        return label;
                    }
                }
        );

        table.getColumnModel()
                .getColumn(0).setPreferredWidth(80);

        table.getColumnModel()
                .getColumn(1).setPreferredWidth(220);

        table.getColumnModel()
                .getColumn(2).setPreferredWidth(395);

        table.getColumnModel()
                .getColumn(3).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBackground(CampusCareTheme.SURFACE);
        scrollPane.getViewport().setBackground(
                CampusCareTheme.SURFACE
        );

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        CampusCareTheme.BORDER
                )
        );

        scrollPane.setPreferredSize(
                new Dimension(600, 240)
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        recentPanel.add(recentTitle, BorderLayout.NORTH);
        recentPanel.add(scrollPane, BorderLayout.CENTER);

        recentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        recentPanel.setPreferredSize(
                new Dimension(800, 310)
        );

        recentPanel.setMinimumSize(
                new Dimension(400, 270)
        );

        recentPanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 340)
        );

        dashboardContent.add(recentPanel);

        body.add(dashboardContent, BorderLayout.NORTH);

        main.add(header, BorderLayout.NORTH);
        main.add(body, BorderLayout.CENTER);

        return main;
    }

    // =========================================================
    // STATISTICS CARDS
    // =========================================================

    private JPanel createStatistics() {

        JPanel cards = new JPanel(
                new GridLayout(1, 4, 16, 0)
        );

        cards.setOpaque(false);
        cards.setAlignmentX(Component.LEFT_ALIGNMENT);

        cards.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 145)
        );

        cards.setPreferredSize(
                new Dimension(900, 145)
        );

        cards.add(createCard("Total Reports", "total"));
        cards.add(createCard("Pending", "pending"));
        cards.add(createCard("In Progress", "progress"));
        cards.add(createCard("Resolved", "resolved"));

        return cards;
    }

    private JPanel createCard(String title, String key) {

        Color accent;

        switch (key) {
            case "pending":
                accent = new Color(230, 176, 72);
                break;

            case "progress":
                accent = new Color(76, 160, 220);
                break;

            case "resolved":
                accent = CampusCareTheme.ACCENT;
                break;

            default:
                accent = new Color(150, 135, 235);
                break;
        }

        JPanel card = new JPanel(
                new BorderLayout()
        );

        card.setBackground(CampusCareTheme.SURFACE);
        card.setOpaque(true);

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                CampusCareTheme.BORDER
                        ),
                        BorderFactory.createEmptyBorder(0, 0, 0, 0)
                )
        );

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setPreferredSize(new Dimension(0, 4));

        card.add(accentBar, BorderLayout.NORTH);

        JPanel details = new JPanel();

        details.setOpaque(false);
        details.setLayout(
                new BoxLayout(details, BoxLayout.Y_AXIS)
        );

        details.setBorder(
                new EmptyBorder(15, 16, 14, 12)
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setFont(
                new Font("Segoe UI", Font.PLAIN, 13)
        );

        titleLabel.setForeground(
                CampusCareTheme.MUTED_TEXT
        );

        titleLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        JLabel valueLabel = new JLabel("0");

        valueLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 32)
        );

        valueLabel.setForeground(accent);
        valueLabel.setAlignmentX(
                Component.LEFT_ALIGNMENT
        );

        switch (key) {
            case "total":
                totalReportsValue = valueLabel;
                break;

            case "pending":
                pendingValue = valueLabel;
                break;

            case "progress":
                inProgressValue = valueLabel;
                break;

            case "resolved":
                resolvedValue = valueLabel;
                break;
        }

        details.add(titleLabel);
        details.add(Box.createVerticalStrut(12));
        details.add(valueLabel);
        details.add(Box.createVerticalGlue());

        card.add(details, BorderLayout.CENTER);

        return card;
    }

    // =========================================================
    // NAVIGATION AND ACCESS CONTROL
    // =========================================================

    private void showPage(String page) {

        String role = currentUser.getRole();

        boolean isAdmin =
                "ADMIN".equalsIgnoreCase(role);

        boolean adminOnly =
                "Buildings".equals(page)
                || "Rooms".equals(page)
                || "Issue Categories".equals(page)
                || "User Management".equals(page);

        if (adminOnly && !isAdmin) {

            JOptionPane.showMessageDialog(
                    this,
                    "Access denied. Administrator permission required.",
                    "Permission Denied",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if ("Dashboard".equals(page)) {

            loadDashboardData();

        } else if ("Facility Reports".equals(page)) {

            new FacilityReportsFrame(
                    currentUser,
                    this::loadDashboardData
            ).setVisible(true);

        } else if ("Report an Issue".equals(page)) {

            if (!"REPORTER".equalsIgnoreCase(
                    currentUser.getRole())) {

                JOptionPane.showMessageDialog(
                        this,
                        "Only reporters can submit new issues.",
                        "Access Denied",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            new ReportIssueFrame(
                    currentUser.getUserId(),
                    this::loadDashboardData
            ).setVisible(true);

        } else if ("Buildings".equals(page)) {

            new BuildingsFrame().setVisible(true);

        } else if ("Rooms".equals(page)) {

            new RoomsFrame().setVisible(true);

        } else if ("Issue Categories".equals(page)) {

            new IssueCategoriesFrame().setVisible(true);

        } else if ("User Management".equals(page)) {

            new UserManagementFrame(
                    currentUser.getUserId()
            ).setVisible(true);

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Unknown module: " + page,
                    "CampusCare",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }

    // =========================================================
    // LOGOUT
    // =========================================================

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }

    // =========================================================
    // LOAD DASHBOARD DATA
    // =========================================================

    private void loadDashboardData() {

        try {
            DashboardDAO dao = new DashboardDAO();

            int[] counts = dao.getReportCounts();

            totalReportsValue.setText(
                    String.valueOf(counts[0])
            );

            pendingValue.setText(
                    String.valueOf(counts[1])
            );

            inProgressValue.setText(
                    String.valueOf(counts[2])
            );

            resolvedValue.setText(
                    String.valueOf(counts[3])
            );

            reportsModel.setRowCount(0);

            List<Object[]> recentReports =
                    dao.getRecentReports();

            for (Object[] report : recentReports) {
                reportsModel.addRow(new Object[]{
                        report[0],
                        report[1],
                        report[3],
                        report[4]
                });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load dashboard data:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }
}