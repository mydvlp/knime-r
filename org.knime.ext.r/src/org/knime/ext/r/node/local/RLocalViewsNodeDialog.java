/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2008
 * University of Konstanz, Germany
 * Chair for Bioinformatics and Information Mining (Prof. M. Berthold)
 * and KNIME GmbH, Konstanz, Germany
 *
 * You may not modify, publish, transmit, transfer or sell, reproduce,
 * create derivative works from, distribute, perform, display, or in
 * any way exploit any of the content, in whole or in part, except as
 * otherwise expressly permitted in writing by the copyright owner or
 * as specified in the license file distributed with this product.
 *
 * If you have any questions please contact the copyright holder:
 * website: www.knime.org
 * email: contact@knime.org
 * ---------------------------------------------------------------------
 * 
 * History
 *   18.09.2007 (thiel): created
 */
package org.knime.ext.r.node.local;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;

/**
 * The dialog of the <code>RLocalViewsNodeDialog</code> which provides a
 * drop down menu containing a set of names of R plots and a multi line text 
 * field to specify R code for the usage of plots. When a certain R plot
 * is specified using the drop down menu, a dummy R code template is
 * shown up in the multi line text field. Additionally a column selection panel 
 * is provided to specify columns to use and import into R.
 * 
 * @author Kilian Thiel, University of Konstanz
 */
public class RLocalViewsNodeDialog extends RLocalNodeDialogPane {
    
    private static final String TAB_TITLE = "View";
    private static final String TAB_R_BINARY = "R Binary";
    
    private final RViewsDialogPanel m_viewScriptPanel;
    private final RViewsPngDialogPanel m_viewPngPanel;
    
    /**
     * Creates new instance of <code>RLocalViewsNodeDialog</code>.
     */
    public RLocalViewsNodeDialog() {
        super();
        m_viewScriptPanel = new RViewsDialogPanel();
        addTab(TAB_TITLE, m_viewScriptPanel);
        
        m_viewPngPanel = new RViewsPngDialogPanel();
        addTab(RViewsPngDialogPanel.TAB_PNG_TITLE, m_viewPngPanel);
        
        setDefaultTabTitle(TAB_R_BINARY);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void loadAdditionalSettingsFrom(final NodeSettingsRO settings,
            final DataTableSpec[] specs) throws NotConfigurableException {
        super.loadAdditionalSettingsFrom(settings, specs);
        m_viewScriptPanel.loadSettings(settings, specs);
        m_viewPngPanel.loadSettings(settings, specs);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAdditionalSettingsTo(final NodeSettingsWO settings)
        throws InvalidSettingsException {
        super.saveAdditionalSettingsTo(settings);
        m_viewScriptPanel.saveSettings(settings);
        m_viewPngPanel.saveSettings(settings);
    }
}
