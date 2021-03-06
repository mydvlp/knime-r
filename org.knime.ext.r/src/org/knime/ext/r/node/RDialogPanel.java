/*
 * ------------------------------------------------------------------------
 *  Copyright by KNIME AG, Zurich, Switzerland
 *  Website: http://www.knime.com; Email: contact@knime.com
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME AG herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ------------------------------------------------------------------------
 *
 */
package org.knime.ext.r.node;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

import org.knime.base.util.flowvariable.FlowVariableResolver;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DoubleValue;
import org.knime.core.data.IntValue;
import org.knime.core.data.StringValue;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.DataColumnSpecListCellRenderer;
import org.knime.core.node.util.FlowVariableListCellRenderer;
import org.knime.core.node.workflow.FlowVariable;

/**
 * Panel to enter R expressions.
 *
 * @author Thomas Gabriel, University of Konstanz
 */
@SuppressWarnings("serial")
public class RDialogPanel extends JPanel {

    /** Key for the R expression command. */
    private static final String CFG_EXPRESSION = "EXPRESSION";

    /** The default R command. */
    public static final String DEFAULT_R_COMMAND = "R<-R";

    private final JEditorPane m_textExpression;

    private final JList<DataColumnSpec> m_list;

    private final DefaultListModel<DataColumnSpec> m_listModel;

    private final JList<FlowVariable> m_listVars;

    private final DefaultListModel<FlowVariable> m_listModelVars;

    private String m_defaultCommand = DEFAULT_R_COMMAND;

    /**
     * Creates a new dialog to enter R expressions with a default mouse listener.
     */
    public RDialogPanel() {
        this("R");
    }

    /**
     * Creates a new dialog to enter R expressions with a default mouse listener.
     *
     * @param varName variable prefix holding the input data, default is <code>R</code>.
     * @since 2.8
     */
    public RDialogPanel(final String varName) {
        super(new BorderLayout());

        // init editor pane
        m_textExpression = new JEditorPane();
        m_textExpression.setPreferredSize(new Dimension(350, 200));
        final Font font = m_textExpression.getFont();
        final Font newFont = new Font(Font.MONOSPACED, Font.PLAIN, (font == null ? 12 : font.getSize()));
        m_textExpression.setFont(newFont);
        m_textExpression.setBorder(BorderFactory.createTitledBorder(" R Snippet "));
        final JScrollPane scroll = new JScrollPane(m_textExpression, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        final JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.add(scroll, BorderLayout.CENTER);
        textPanel.setMinimumSize(new Dimension(0, 0));
        textPanel.setMaximumSize(new Dimension(0, 0));

        // init column list
        m_listModel = new DefaultListModel<>();
        m_list = new JList<>(m_listModel);
        m_list.setBorder(BorderFactory.createTitledBorder(" Column List"));
        m_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_list.setCellRenderer(new DataColumnSpecListCellRenderer());
        m_list.addMouseListener(new MouseAdapter() {
            /** {@inheritDoc} */
            @Override
            public final void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    final Object o = m_list.getSelectedValue();
                    if (o != null) {
                        final DataColumnSpec cspec = (DataColumnSpec)o;
                        m_textExpression.replaceSelection(formatColumnName(varName, cspec.getName()));
                        m_list.clearSelection();
                        m_textExpression.requestFocus();
                    }
                }
            }
        });
        final JScrollPane leftComp = new JScrollPane(m_list, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // init variable list
        m_listModelVars = new DefaultListModel<>();
        m_listVars = new JList<>(m_listModelVars);

        final JSplitPane allSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        allSplit.setResizeWeight(0.25);
        allSplit.setRightComponent(textPanel);

        m_listVars.setBorder(BorderFactory.createTitledBorder(" Flow Variable List "));
        m_listVars.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_listVars.setCellRenderer(new FlowVariableListCellRenderer());
        m_listVars.addMouseListener(new MouseAdapter() {
            /** {@inheritDoc} */
            @Override
            public final void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2) {
                    final Object o = m_listVars.getSelectedValue();
                    if (o != null) {
                        final FlowVariable var = (FlowVariable)o;
                        m_textExpression.replaceSelection(FlowVariableResolver.getPlaceHolderForVariable(var));
                        m_listVars.clearSelection();
                        m_textExpression.requestFocus();
                    }
                }
            }
        });
        final JScrollPane scrollVars = new JScrollPane(m_listVars, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        final JSplitPane leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftComp, scrollVars);
        leftPane.setResizeWeight(0.5);

        allSplit.setLeftComponent(leftPane);
        super.add(allSplit, BorderLayout.CENTER);

    }

    /**
     * Updates the list of columns based on the given table spec.
     *
     * @param spec to get columns compatible with String, Double, Integer
     */
    private final void update(final DataTableSpec spec, final Map<String, FlowVariable> map)
        throws NotConfigurableException {
        // update column list
        m_listModel.removeAllElements();

        // check of null spec in case of optional input data port
        if (spec != null) {
            final DataTableSpec newSpec = RConnectionRemote.createRenamedDataTableSpec(spec);
            for (int i = 0; i < newSpec.getNumColumns(); i++) {
                final DataColumnSpec cspec = newSpec.getColumnSpec(i);
                final DataType type = cspec.getType();
                if (type.isCompatible(IntValue.class)) {
                    m_listModel.addElement(cspec);
                } else if (type.isCompatible(DoubleValue.class)) {
                    m_listModel.addElement(cspec);
                } else if (type.isCompatible(StringValue.class)) {
                    m_listModel.addElement(cspec);
                }
            }
            if (m_listModel.size() <= 0) {
                throw new NotConfigurableException("No valid columns (Integer, Double, String) are available!");
            }
        }

        // update list of flow/workflow variables
        m_listModelVars.removeAllElements();
        for (final Map.Entry<String, FlowVariable> e : map.entrySet()) {
            m_listModelVars.addElement(e.getValue());
        }
        repaint();
    }

    /**
     * @return complete text as string.
     */
    public final String getText() {
        return m_textExpression.getText();
    }

    /**
     * @param str sets the given string as text.
     */
    public final void setText(final String str) {
        m_textExpression.setText(str);
        m_textExpression.setCaretPosition(str.length());
    }

    /**
     * Saves internal R command string to given settings instance.
     *
     * @param settings settings instance to write R command string to.
     */
    public void saveSettingsTo(final NodeSettingsWO settings) {
        setExpressionTo(settings, getText());
    }

    /**
     * Loads R command string out of given settings instance.
     *
     * @param settings settings instance to load R command string from.
     * @param specs input specs, can be null or its elements or not of type DTS
     * @param map holding flow variables together with its identifier
     * @throws NotConfigurableException if no columns are available.
     */
    public void loadSettingsFrom(final NodeSettingsRO settings, final PortObjectSpec[] specs,
        final Map<String, FlowVariable> map) throws NotConfigurableException {
        if ((specs != null) && (specs.length > 0) && (specs[0] != null) && (specs[0] instanceof DataTableSpec)) {
            update((DataTableSpec)specs[0], map);
        } else {
            update(null, map);
        }
        setText(getExpressionFrom(settings));
    }

    /**
     * Loads expression from given settings instance and returns it as string.
     *
     * @param settings settings instance to load expression from.
     * @return The expression loaded from settings instance.
     */
    public static final String getExpressionFrom(final NodeSettingsRO settings) {
        return settings.getString(CFG_EXPRESSION, DEFAULT_R_COMMAND);
    }

    /**
     * Loads expression from given settings instance and returns it as string. If no settings can be loaded, the given
     * string is returned as default expression.
     *
     * @param settings settings instance to load expression from.
     * @param defaultExpr the default expression if no other can be loaded from settings.
     * @return The expression loaded from settings instance.
     */
    public static final String getExpressionFrom(final NodeSettingsRO settings, final String defaultExpr) {
        return settings.getString(CFG_EXPRESSION, defaultExpr);
    }

    /**
     * Saves given expression to given settings instance.
     *
     * @param settings settings instance to save expression to.
     * @param expr expression to save.
     */
    public static final void setExpressionTo(final NodeSettingsWO settings, final String expr) {
        settings.addString(CFG_EXPRESSION, expr);
    }

    /**
     * Loads expression from given settings instance and returns it as string array.
     *
     * @param settings settings instance to load expression from.
     * @return The expression loaded from settings instance.
     */
    public static final String[] getExpressionsFrom(final NodeSettingsRO settings) {
        final String expr = settings.getString(CFG_EXPRESSION, DEFAULT_R_COMMAND);
        return expr.split("\n");
    }

    /**
     * Saves given array of expressions to given settings instance.
     *
     * @param settings settings instance to save expression to.
     * @param exprs array of expressions to save.
     */
    public static final void setExpressionsTo(final NodeSettingsWO settings, final String[] exprs) {
        final StringBuilder expr = new StringBuilder();
        if (exprs != null) {
            for (int i = 0; i < exprs.length; i++) {
                if (i > 0) {
                    expr.append("\n");
                }
                expr.append(exprs[i]);
            }
        } else {
            expr.append(DEFAULT_R_COMMAND);
        }
        settings.addString(CFG_EXPRESSION, expr.toString());
    }

    /**
     * Formats the given string by attaching the String "R$".
     *
     * @param name The name of the column to format.
     * @return The formatted column name.
     */
    private static String formatColumnName(final String varName, final String name) {
        return varName + "$\"" + name + "\"";
    }

    /**
     * @return the defaultCommand
     */
    public String getDefaultCommand() {
        return m_defaultCommand;
    }

    /**
     * @param defaultCommand the defaultCommand to set
     */
    public void setDefaultCommand(final String defaultCommand) {
        m_defaultCommand = defaultCommand;
    }
}
