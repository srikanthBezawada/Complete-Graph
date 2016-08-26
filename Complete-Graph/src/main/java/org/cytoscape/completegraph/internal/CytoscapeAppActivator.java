package org.cytoscape.completegraph.internal;

import java.util.Properties;

import org.osgi.framework.BundleContext;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import static org.cytoscape.work.ServiceProperties.PREFERRED_MENU;
import static org.cytoscape.work.ServiceProperties.TITLE;
import org.cytoscape.work.TaskFactory;


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
        final CyServiceRegistrar serviceRegistrar = getService(context, CyServiceRegistrar.class);
        final KGraphTaskFactory kGraphFactory = new KGraphTaskFactory(serviceRegistrar);
        Properties kGraphProps = new Properties();
        kGraphProps.setProperty(PREFERRED_MENU, "File.New.Network");
        kGraphProps.setProperty(TITLE, "Complete Network");
        registerService(context, kGraphFactory, TaskFactory.class, kGraphProps);
    }
    
    public <S> S getService(Class<S> cls) {
            return this.getService(context, cls);
    }
	
    public <S> S getService(Class<S> cls, String properties) {
            return this.getService(context, cls, properties);
    }

}

