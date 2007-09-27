/* ------------------------------------------------------------------
 * This source code, its documentation and all appendant files
 * are protected by copyright law. All rights reserved.
 *
 * Copyright, 2003 - 2007
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
 *   17.09.2007 (thiel): created
 */
package org.knime.ext.r.node.local;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

/**
 * The <code>RLocalScriptingNodeModel</code> provides functionality to create
 * a R script with user defined R code and run it.
 * 
 * @author Kilian Thiel, University of Konstanz
 */
public class RLocalScriptingNodeModel extends RLocalNodeModel {
    
    private String m_rCommand = new String();
    
    /**
     * Creates new instance of <code>RLocalScriptingNodeModel</code> with one
     * data in and data one out port.
     */
    public RLocalScriptingNodeModel() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getCommand() {
        return m_rCommand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        return new DataTableSpec[1];
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.loadValidatedSettingsFrom(settings);
        m_rCommand = settings.getString("EXPRESSION");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        super.saveSettingsTo(settings);
        settings.addString("EXPRESSION", m_rCommand);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        super.validateSettings(settings);
        testExpressions(settings.getString("EXPRESSION"));
    }
    
    
    private void testExpressions(final String str)
            throws InvalidSettingsException {
        if (str.contains("R<-")) {
            // ok, we have an result in R
            return;
        }
        throw new InvalidSettingsException("The result has to be provided"
                + " inside the variable R");
    }
}
