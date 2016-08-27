package org.cytoscape.completegraph.internal;

import javax.swing.JOptionPane;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

/**
 * @author SrikanthB
 * Gets the input from user and creates the task of creating network
 */

class KGraphTaskFactory implements TaskFactory{
    
    CyServiceRegistrar serviceRegistrar;
    
    public KGraphTaskFactory(CyServiceRegistrar serviceRegistrar) {
        this.serviceRegistrar = serviceRegistrar;
    }
    
    @Override
    public TaskIterator createTaskIterator() {
        CyNetworkFactory networkFactory = serviceRegistrar.getService(CyNetworkFactory.class);
        CyNetworkManager networkManager = serviceRegistrar.getService(CyNetworkManager.class);
        CyNetworkViewFactory networkViewFactory = serviceRegistrar.getService(CyNetworkViewFactory.class);
        CyNetworkViewManager networkViewManager = serviceRegistrar.getService(CyNetworkViewManager.class);
        CyEventHelper eventHelper = serviceRegistrar.getService(CyEventHelper.class);
        CyAppAdapter appAdapter = serviceRegistrar.getService(CyAppAdapter.class);
        int size;
        String numberString = JOptionPane.showInputDialog("Enter the number of nodes (i.e) size of complete graph ");
        
        try{
            size = Integer.parseInt(numberString);
        }  
        catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a valid number ", "Not a valid number", JOptionPane.WARNING_MESSAGE);
            System.out.println("Number format exception");
            return null;
        }        

        TaskIterator ti = new TaskIterator(new KGraphTask(networkFactory, networkManager, networkViewFactory, networkViewManager, eventHelper, appAdapter, size));
        return ti;
    }

    @Override
    public boolean isReady() {
        return true;
    }
    
}
