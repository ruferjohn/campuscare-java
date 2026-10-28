
package campuscare.ui;

import campuscare.dao.RoomDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class RoomsFrame extends JFrame {

    private final RoomDAO dao = new RoomDAO();

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

    private DefaultTableModel model;
    private JTable table;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public RoomsFrame() {
        setTitle("CampusCare | Rooms Management");
        setSize(1050, 650);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        loadRooms();
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(background);
        root.setBorder(new EmptyBorder(25, 28, 25, 28));
        setContentPane(root);

        // Heading
        JLabel title = new JLabel("Rooms Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 27));
        title.setForeground(textColor);

        JLabel subtitle = new JLabel(
                "Manage campus classrooms and laboratories."
        );
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(mutedText);

        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.add(title);
        heading.add(Box.createVerticalStrut(6));
        heading.add(subtitle);

        JPanel top = new JPanel(new BorderLayout(0, 18));
        top.setOpaque(false);
        top.add(heading, BorderLayout.NORTH);

        // Search toolbar
        JPanel toolbar = new JPanel(
                new BorderLayout(12, 0)
        );
        toolbar.setOpaque(false);

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 10, 0)
        );
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setForeground(textColor);
        searchLabel.setFont(
                new Font("Segoe UI", Font.BOLD, 13)
        );

        searchField = new JTextField();
        searchField.setFont(bodyFont);
        searchField.setForeground(textColor);
        searchField.setBackground(surfaceAlt);
        searchField.setCaretColor(accent);
        searchField.setSelectionColor(accent);
        searchField.setSelectedTextColor(Color.WHITE);
        searchField.setPreferredSize(new Dimension(250, 38));
        searchField.setToolTipText(
                "Search by room, building, floor, or description"
        );
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(6, 9, 6, 9)
        ));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);

        JButton addButton = createButton("Add Room");
        JButton editButton = createButton("Edit Room");
        JButton deleteButton = createDangerButton("Delete Room");
        JButton refreshButton = createButton("Refresh");

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );
        actions.setOpaque(false);
        actions.add(addButton);
        actions.add(editButton);
        actions.add(deleteButton);
        actions.add(refreshButton);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.CENTER);

        top.add(toolbar, BorderLayout.SOUTH);

        // Table model
        String[] columns = {
                "Room ID",
                "Room Name",
                "Building",
                "Floor",
                "Description"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
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

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Explicit header renderer prevents invisible column titles.
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(18, 59, 66));
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(
                            JTable source,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column) {

                        JLabel label = (JLabel)
                                super.getTableCellRendererComponent(
                                        source,
                                        value,
                                        isSelected,
                                        hasFocus,
                                        row,
                                        column
                                );

                        label.setFont(
                                new Font("Segoe UI", Font.BOLD, 13)
                        );
                        label.setForeground(Color.WHITE);
                        label.setBackground(new Color(18, 59, 66));
                        label.setOpaque(true);
                        label.setHorizontalAlignment(
                                SwingConstants.LEFT
                        );
                        label.setBorder(new EmptyBorder(0, 10, 0, 8));

                        return label;
                    }
                }
        );

        // Alternating row colors
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

                        setBorder(new EmptyBorder(0, 9, 0, 9));
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

        table.getColumnModel().getColumn(0).setPreferredWidth(75);
        table.getColumnModel().getColumn(1).setPreferredWidth(190);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(75);
        table.getColumnModel().getColumn(4).setPreferredWidth(280);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createLineBorder(borderColor)
        );
        scrollPane.getViewport().setBackground(surface);
        scrollPane.getVerticalScrollBar().setBackground(surface);
        scrollPane.getHorizontalScrollBar().setBackground(surface);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(surface);
        tablePanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);
        root.add(tablePanel, BorderLayout.CENTER);

        // Search filter
        searchField.getDocument().addDocumentListener(
                new DocumentListener() {
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
                    public void insertUpdate(DocumentEvent e) {
                        filter();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filter();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filter();
                    }
                }
        );

        // Actions
        addButton.addActionListener(e -> addRoom());
        editButton.addActionListener(e -> editRoom());
        deleteButton.addActionListener(e -> deleteRoom());
        refreshButton.addActionListener(e -> loadRooms());
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(accent);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        button.setBorder(new EmptyBorder(11, 14, 11, 14));

        button.addChangeListener(e -> {
            ButtonModel buttonModel = button.getModel();

            if (buttonModel.isPressed()) {
                button.setBackground(new Color(18, 59, 66));
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
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(176, 64, 70));
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
        button.setBorder(new EmptyBorder(11, 14, 11, 14));

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

    private void loadRooms() {
        try {
            model.setRowCount(0);

            for (Object[] room : dao.getAllRooms()) {
                model.addRow(room);
            }
        } catch (SQLException ex) {
            showError("Unable to load rooms", ex);
        }
    }

    private int getSelectedRoomId() {
        int selected = table.getSelectedRow();

        if (selected < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a room first.",
                    "No Room Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return -1;
        }

        int modelRow = table.convertRowIndexToModel(selected);

        return Integer.parseInt(
                model.getValueAt(modelRow, 0).toString()
        );
    }

    private void addRoom() {
        try {
            List<Object[]> buildings = dao.getBuildings();

            if (buildings.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Add a building before creating a room.",
                        "No Buildings",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            RoomForm form = createForm(buildings, null);

            if (JOptionPane.showConfirmDialog(
                    this,
                    form.panel,
                    "Add Room",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            ) != JOptionPane.OK_OPTION) {
                return;
            }

            String name = form.name.getText().trim();
            String description = form.description.getText().trim();
            String floorText = form.floor.getText().trim();

            if (name.isEmpty()) {
                showValidation("Room name is required.");
                return;
            }

            if (name.length() > 100 || description.length() > 255) {
                showValidation(
                        "Room name must be at most 100 characters "
                                + "and description at most 255 characters."
                );
                return;
            }

            int floor;
            try {
                floor = Integer.parseInt(floorText);
            } catch (NumberFormatException ex) {
                showValidation("Floor must be a whole number.");
                return;
            }

            if (form.buildingBox.getSelectedItem() == null) {
                showValidation("Please select a building.");
                return;
            }

            int buildingId = (Integer) form.buildingBox.getSelectedItem();

            if (dao.addRoom(buildingId, name, floor, description)) {
                JOptionPane.showMessageDialog(
                        this, "Room added successfully."
                );
                loadRooms();
            }

        } catch (SQLException ex) {
            showError("Unable to add room", ex);
        }
    }

    private void editRoom() {
        int id = getSelectedRoomId();
        if (id < 0) {
            return;
        }

        int row = table.convertRowIndexToModel(table.getSelectedRow());

        try {
            List<Object[]> buildings = dao.getBuildings();

            String oldName = model.getValueAt(row, 1).toString();
            String oldBuilding = model.getValueAt(row, 2).toString();
            String oldFloor = model.getValueAt(row, 3).toString();

            Object descValue = model.getValueAt(row, 4);
            String oldDescription =
                    descValue == null ? "" : descValue.toString();

            RoomForm form = createForm(buildings, oldBuilding);
            form.name.setText(oldName);
            form.floor.setText(oldFloor);
            form.description.setText(oldDescription);

            if (JOptionPane.showConfirmDialog(
                    this,
                    form.panel,
                    "Edit Room",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
            ) != JOptionPane.OK_OPTION) {
                return;
            }

            String name = form.name.getText().trim();
            String description = form.description.getText().trim();

            if (name.isEmpty()) {
                showValidation("Room name is required.");
                return;
            }

            if (name.length() > 100 || description.length() > 255) {
                showValidation(
                        "Room name must be at most 100 characters "
                                + "and description at most 255 characters."
                );
                return;
            }

            int floor;
            try {
                floor = Integer.parseInt(form.floor.getText().trim());
            } catch (NumberFormatException ex) {
                showValidation("Floor must be a whole number.");
                return;
            }

            if (form.buildingBox.getSelectedItem() == null) {
                showValidation("Please select a building.");
                return;
            }

            int buildingId = (Integer) form.buildingBox.getSelectedItem();

            if (dao.updateRoom(
                    id, buildingId, name, floor, description)) {
                JOptionPane.showMessageDialog(
                        this, "Room updated successfully."
                );
                loadRooms();
            }

        } catch (SQLException ex) {
            showError("Unable to update room", ex);
        }
    }

    private void deleteRoom() {
        int id = getSelectedRoomId();
        if (id < 0) {
            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete the selected room? This cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            if (dao.deleteRoom(id)) {
                JOptionPane.showMessageDialog(
                        this, "Room deleted successfully."
                );
                loadRooms();
            }
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1451) {
                JOptionPane.showMessageDialog(
                        this,
                        "This room is linked to existing reports "
                                + "and cannot be deleted.",
                        "Room In Use",
                        JOptionPane.WARNING_MESSAGE
                );
            } else {
                showError("Unable to delete room", ex);
            }
        }
    }

    private RoomForm createForm(
            List<Object[]> buildings,
            String selectedBuilding) {

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBackground(surface);
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JTextField name = createDialogField();
        JTextField floor = createDialogField();
        floor.setText("1");
        JTextField description = createDialogField();

        JComboBox<Integer> buildingBox = new JComboBox<>();

        for (Object[] building : buildings) {
            buildingBox.addItem((Integer) building[0]);
        }

        buildingBox.setFont(bodyFont);
        buildingBox.setForeground(textColor);
        buildingBox.setBackground(surfaceAlt);

        buildingBox.setRenderer(new DefaultListCellRenderer() {
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
                                isSelected, cellHasFocus
                        );

                label.setOpaque(true);
                label.setForeground(
                        isSelected ? Color.WHITE : textColor
                );
                label.setBackground(
                        isSelected ? accent : surfaceAlt
                );

                if (value != null) {
                    for (Object[] building : buildings) {
                        if (building[0].equals(value)) {
                            label.setText(building[1].toString());
                            break;
                        }
                    }
                }

                return label;
            }
        });

        if (selectedBuilding != null) {
            for (Object[] building : buildings) {
                if (building[1].toString().equals(selectedBuilding)) {
                    buildingBox.setSelectedItem(building[0]);
                    break;
                }
            }
        }

        addFormLabel(panel, "Room Name:");
        panel.add(name);

        addFormLabel(panel, "Building:");
        panel.add(buildingBox);

        addFormLabel(panel, "Floor Number:");
        panel.add(floor);

        addFormLabel(panel, "Description:");
        panel.add(description);

        return new RoomForm(
                panel, name, buildingBox, floor, description
        );
    }

    private JTextField createDialogField() {
        JTextField field = new JTextField();
        field.setFont(bodyFont);
        field.setForeground(textColor);
        field.setBackground(surfaceAlt);
        field.setCaretColor(accent);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(6, 8, 6, 8)
        ));
        return field;
    }

    private void addFormLabel(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(bodyFont);
        label.setForeground(textColor);
        panel.add(label);
    }

    private void showValidation(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void showError(String message, SQLException ex) {
        JOptionPane.showMessageDialog(
                this,
                message + ":\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );
        ex.printStackTrace();
    }

    private static class RoomForm {
        JPanel panel;
        JTextField name;
        JComboBox<Integer> buildingBox;
        JTextField floor;
        JTextField description;

        RoomForm(
                JPanel panel,
                JTextField name,
                JComboBox<Integer> buildingBox,
                JTextField floor,
                JTextField description) {

            this.panel = panel;
            this.name = name;
            this.buildingBox = buildingBox;
            this.floor = floor;
            this.description = description;
        }
    }
}
