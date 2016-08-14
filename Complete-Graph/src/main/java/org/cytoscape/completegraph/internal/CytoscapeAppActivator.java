package org.cytoscape.completegraph.internal;

import java.util.Properties;

import org.osgi.framework.BundleContext;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.service.util.AbstractCyActivator;


/**
 * @author SrikanthB
 *  This class is the entry point to Cytoscape app
 */


public class CytoscapeAppActivator extends AbstractCyActivator {
    
    private BundleContext context;
    public static final String APPNAME = "Fully Connected Network ";
    
    public CytoscapeAppActivator() {
        super();
    }
    
    public void start(BundleContext context) throws Exception {
        this.context = context;
        CyApplicationManager cyApplicationManager = getService(context, CyApplicationManager.class);
        OpenPcmGuiAction oplaction = new OpenPcmGuiAction(this, cyApplicationManager);
        registerAllServices(context, oplaction, new Properties());
    }
    
    public <S> S getService(Class<S> cls) {
            return this.getService(context, cls);
    }
	
    public <S> S getService(Class<S> cls, String properties) {
            return this.getService(context, cls, properties);
    }

}

