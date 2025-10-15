package com.dabomstew.pkrandom.gui;

import com.dabomstew.pkromio.gamedata.Effectiveness;
import com.dabomstew.pkromio.gamedata.TypeTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.ResourceBundle;

class TypeEffectivenessEditorDialog extends JDialog {
    private static final Effectiveness[] ALLOWED_EFFECTIVENESSES = new Effectiveness[] {
            Effectiveness.ZERO, Effectiveness.HALF, Effectiveness.NEUTRAL, Effectiveness.DOUBLE };

    private final ResourceBundle bundle;
    private final TypeTable originalTable;
    private final TypeTable workingTable;
    private final List<com.dabomstew.pkromio.gamedata.Type> types;

    private boolean saved;
    private TypeTable resultTable;

    TypeEffectivenessEditorDialog(java.awt.Window owner, ResourceBundle bundle, TypeTable sourceTable) {
        super(owner, bundle.getString("GUI.teManualEditorDialog.title"), ModalityType.APPLICATION_MODAL);
        this.bundle = bundle;
        this.originalTable = new TypeTable(sourceTable);
        this.workingTable = new TypeTable(sourceTable);
        this.types = workingTable.getTypes();
        initUI();
        setMinimumSize(new Dimension(720, 480));
        pack();
        setLocationRelativeTo(owner);
    }

    boolean wasSaved() {
        return saved;
    }

    TypeTable getResultTable() {
        return resultTable;
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        TypeEffectivenessTableModel model = new TypeEffectivenessTableModel();
        JTable table = new JTable(model) {
            @Override
            public String getToolTipText(java.awt.event.MouseEvent event) {
                int row = rowAtPoint(event.getPoint());
                int column = columnAtPoint(event.getPoint());
                if (row >= 0 && column > 0) {
                    Object value = getValueAt(row, column);
                    if (value instanceof Effectiveness) {
                        return effectDescription((Effectiveness) value);
                    }
                }
                return super.getToolTipText(event);
            }
        };
        table.setRowSelectionAllowed(false);
        table.setCellSelectionEnabled(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowHeight(26);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        table.setDefaultRenderer(Effectiveness.class, new EffectivenessCellRenderer());
        table.getTableHeader().setReorderingAllowed(false);

        JComboBox<Effectiveness> effectivenessCombo = new JComboBox<>(ALLOWED_EFFECTIVENESSES);
        effectivenessCombo.setRenderer(new EffectivenessComboRenderer());
        table.setDefaultEditor(Effectiveness.class, new DefaultCellEditor(effectivenessCombo));

        TableColumn defenderColumn = table.getColumnModel().getColumn(0);
        defenderColumn.setPreferredWidth(140);
        defenderColumn.setMinWidth(140);
        defenderColumn.setCellRenderer(new TypeNameCellRenderer(false));
        TableCellRenderer defaultHeaderRenderer = table.getTableHeader().getDefaultRenderer();
        for (int i = 1; i < table.getColumnCount(); i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(110);
            column.setMinWidth(110);
            column.setHeaderRenderer(new TypeNameCellRenderer(true, defaultHeaderRenderer));
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(960, 520));
        add(scrollPane, BorderLayout.CENTER);

        JLabel legendLabel = new JLabel(bundle.getString("GUI.teManualEditorDialog.legend"));
        legendLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(legendLabel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton resetButton = new JButton(bundle.getString("GUI.teManualEditorDialog.reset"));
        JButton cancelButton = new JButton(bundle.getString("GUI.teManualEditorDialog.cancel"));
        JButton applyButton = new JButton(bundle.getString("GUI.teManualEditorDialog.apply"));
        buttonsPanel.add(resetButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(applyButton);
        add(buttonsPanel, BorderLayout.SOUTH);

        resetButton.addActionListener(e -> {
            resetWorkingTable();
            model.fireTableDataChanged();
        });
        cancelButton.addActionListener(e -> dispose());
        applyButton.addActionListener(e -> {
            resultTable = new TypeTable(workingTable);
            saved = true;
            dispose();
        });
        getRootPane().setDefaultButton(applyButton);
    }

    private void resetWorkingTable() {
        for (com.dabomstew.pkromio.gamedata.Type attacker : types) {
            for (com.dabomstew.pkromio.gamedata.Type defender : types) {
                workingTable.setEffectiveness(attacker, defender,
                        originalTable.getEffectiveness(attacker, defender));
            }
        }
    }

    private String effectDisplay(Effectiveness effectiveness) {
        if (effectiveness == null) {
            return "";
        }
        switch (effectiveness) {
            case ZERO:
                return bundle.getString("GUI.teManualEditorDialog.effect.zero");
            case HALF:
                return bundle.getString("GUI.teManualEditorDialog.effect.half");
            case NEUTRAL:
                return bundle.getString("GUI.teManualEditorDialog.effect.neutral");
            case DOUBLE:
                return bundle.getString("GUI.teManualEditorDialog.effect.double");
            default:
                return effectiveness.name();
        }
    }

    private String effectDescription(Effectiveness effectiveness) {
        if (effectiveness == null) {
            return "";
        }
        switch (effectiveness) {
            case ZERO:
                return bundle.getString("GUI.teManualEditorDialog.effect.description.zero");
            case HALF:
                return bundle.getString("GUI.teManualEditorDialog.effect.description.half");
            case NEUTRAL:
                return bundle.getString("GUI.teManualEditorDialog.effect.description.neutral");
            case DOUBLE:
                return bundle.getString("GUI.teManualEditorDialog.effect.description.double");
            default:
                return effectiveness.name();
        }
    }

    private Color effectColor(Effectiveness effectiveness) {
        if (effectiveness == null) {
            return Color.WHITE;
        }
        switch (effectiveness) {
            case ZERO:
                return new Color(224, 224, 224);
            case HALF:
                return new Color(255, 246, 207);
            case DOUBLE:
                return new Color(210, 235, 208);
            default:
                return Color.WHITE;
        }
    }

    private Color typeColor(com.dabomstew.pkromio.gamedata.Type type, boolean header) {
        Color base;
        switch (type) {
            case BUG:
                base = new Color(95, 165, 63);
                break;
            case DARK:
                base = new Color(55, 51, 58);
                break;
            case DRAGON:
                base = new Color(0, 169, 200);
                break;
            case ELECTRIC:
                base = new Color(247, 212, 62);
                break;
            case FAIRY:
                base = new Color(208, 60, 132);
                break;
            case FIGHTING:
                base = new Color(213, 95, 30);
                break;
            case FIRE:
                base = new Color(208, 71, 50);
                break;
            case FLYING:
                base = new Color(126, 162, 189);
                break;
            case GHOST:
                base = new Color(98, 64, 160);
                break;
            case GRASS:
                base = new Color(87, 160, 58);
                break;
            case GROUND:
                base = new Color(176, 123, 44);
                break;
            case ICE:
                base = new Color(147, 215, 235);
                break;
            case NORMAL:
                base = new Color(184, 163, 150);
                break;
            case POISON:
                base = new Color(126, 74, 170);
                break;
            case PSYCHIC:
                base = new Color(224, 68, 159);
                break;
            case ROCK:
                base = new Color(139, 98, 50);
                break;
            case STEEL:
                base = new Color(111, 142, 132);
                break;
            case WATER:
                base = new Color(42, 116, 218);
                break;
            default:
                base = new Color(200, 200, 200);
                break;
        }
        return header ? mixWithWhite(base, 0.35f) : base;
    }

    private Color mixWithWhite(Color base, float ratio) {
        ratio = Math.max(0f, Math.min(1f, ratio));
        int r = (int) (base.getRed() + (255 - base.getRed()) * ratio);
        int g = (int) (base.getGreen() + (255 - base.getGreen()) * ratio);
        int b = (int) (base.getBlue() + (255 - base.getBlue()) * ratio);
        return new Color(r, g, b);
    }

    private boolean shouldUseLightText(Color color) {
        double luminance = 0.2126 * color.getRed() + 0.7152 * color.getGreen() + 0.0722 * color.getBlue();
        return luminance < 140;
    }

    private final class TypeEffectivenessTableModel extends AbstractTableModel {
        @Override
        public int getRowCount() {
            return types.size();
        }

        @Override
        public int getColumnCount() {
            return types.size() + 1;
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) {
                return bundle.getString("GUI.teManualEditorDialog.defendingHeader");
            }
            return types.get(column - 1).name();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnIndex == 0 ? String.class : Effectiveness.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > 0;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            com.dabomstew.pkromio.gamedata.Type defender = types.get(rowIndex);
            if (columnIndex == 0) {
                return defender.name();
            }
            com.dabomstew.pkromio.gamedata.Type attacker = types.get(columnIndex - 1);
            return workingTable.getEffectiveness(attacker, defender);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (!(aValue instanceof Effectiveness) || columnIndex == 0) {
                return;
            }
            com.dabomstew.pkromio.gamedata.Type defender = types.get(rowIndex);
            com.dabomstew.pkromio.gamedata.Type attacker = types.get(columnIndex - 1);
            workingTable.setEffectiveness(attacker, defender, (Effectiveness) aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    private final class TypeNameCellRenderer extends DefaultTableCellRenderer {
        private final boolean header;
        private final TableCellRenderer fallbackRenderer;

        TypeNameCellRenderer(boolean header) {
            this(header, null);
        }

        TypeNameCellRenderer(boolean header, TableCellRenderer fallbackRenderer) {
            this.header = header;
            this.fallbackRenderer = fallbackRenderer;
            setHorizontalAlignment(header ? SwingConstants.CENTER : SwingConstants.LEFT);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component component;
            if (header && fallbackRenderer != null) {
                component = fallbackRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                        column);
            } else {
                component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
            String text = value == null ? "" : value.toString();
            com.dabomstew.pkromio.gamedata.Type parsedType = parseType(text);
            if (parsedType != null) {
                Color background = typeColor(parsedType, header);
                component.setBackground(background);
                Color foreground = shouldUseLightText(background) ? Color.WHITE : Color.BLACK;
                component.setForeground(foreground);
            } else {
                component.setBackground(header ? mixWithWhite(Color.DARK_GRAY, 0.75f) : Color.WHITE);
                component.setForeground(Color.BLACK);
            }
            if (!header && component instanceof JComponent) {
                ((JComponent) component).setBorder(BorderFactory.createMatteBorder(header ? 0 : 1, 0, 1, 0,
                        new Color(230, 230, 230)));
            }
            return component;
        }

        private com.dabomstew.pkromio.gamedata.Type parseType(String text) {
            if (text == null) {
                return null;
            }
            try {
                return com.dabomstew.pkromio.gamedata.Type.valueOf(text);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    }

    private final class EffectivenessCellRenderer extends DefaultTableCellRenderer {
        EffectivenessCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Effectiveness effectiveness = value instanceof Effectiveness ? (Effectiveness) value : null;
            setText(effectDisplay(effectiveness));
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(effectColor(effectiveness));
                setForeground(table.getForeground());
            }
            setToolTipText(effectDescription(effectiveness));
            return this;
        }
    }

    private final class EffectivenessComboRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            Effectiveness effectiveness = value instanceof Effectiveness ? (Effectiveness) value : null;
            setText(effectDisplay(effectiveness));
            return this;
        }
    }
}
