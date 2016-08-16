package org.cytoscape.completegraph.internal;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import org.cytoscape.app.CyAppAdapter;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;

import static org.cytoscape.completegraph.internal.CytoscapeAppActivator.APPNAME;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;

/**
 * @author SrikanthB
 *  This class creates the network and registers with Network Manager
 */

public class CompleteGraphAction
    extends AbstractCyAction
{
    private CytoscapeAppActivator activator;
    private CyApplicationManager applicationManager;
    
    public CompleteGraphAction(CytoscapeAppActivator activator, CyApplicationManager applicationManager)
    {
        super(APPNAME , applicationManager, null, null);
        setPreferredMenu("File.New.Network");
        this.activator = activator;
        this.applicationManager = applicationManager;
    }


    @Override
    public void actionPerformed(ActionEvent arg0)
    {
        int size;
        String numberString = JOptionPane.showInputDialog("Enter the number of nodes (i.e) size of complete graph ");
        
        try{
            size = Integer.parseInt(numberString);
        }   catch(NullPointerException e){
            JOptionPane.showMessageDialog(null, "Enter a valid number ", "Null value detected, Not a valid number", JOptionPane.WARNING_MESSAGE);
            return;
        }   catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Enter a valid number ", "Not a valid number", JOptionPane.WARNING_MESSAGE);
            System.out.println("Number format exception");
            return;
        }
        
        CyNetworkFactory networkFactory = activator.getService(CyNetworkFactory.class);
        CyNetwork fcnetwork = networkFactory.createNetwork();
        
        fcnetwork.getRow(fcnetwork).set(CyNetwork.NAME, "Fully Connected Network -"+ size);
        
        List<CyNode> nodeList = new ArrayList<CyNode>();
        for(int i=1; i<=size; i++) {
            CyNode node = fcnetwork.addNode();
            fcnetwork.getRow(node).set(CyNetwork.NAME, "Node "+ i);
            nodeList.add(node);
        }
        
        for(CyNode n1 : nodeList) {
            for(CyNode n2 : nodeList) {
                if(n1.equals(n2))
                    continue;
                
                if(fcnetwork.containsEdge(n1, n2) || fcnetwork.containsEdge(n2, n1))
                    continue;
                
                fcnetwork.addEdge(n1, n2, true);
            }
        }
        
        CyNetworkManager networkManager = activator.getService(CyNetworkManager.class);
        networkManager.addNetwork(fcnetwork);
        
        CyNetworkViewFactory networkViewFactory = activator.getService(CyNetworkViewFactory.class);
        CyNetworkView fcnView = networkViewFactory.createNetworkView(fcnetwork);
        
        updateView(fcnView);
    }
    
    public void updateView(CyNetworkView view){
        CyAppAdapter appAdapter = activator.getService(CyAppAdapter.class);
        final CyLayoutAlgorithmManager alMan = appAdapter.getCyLayoutAlgorithmManager();
        CyLayoutAlgorithm algor = alMan.getDefaultLayout();;
        TaskIterator itr = algor.createTaskIterator(view,algor.createLayoutContext(),CyLayoutAlgorithm.ALL_NODE_VIEWS,null);
        appAdapter.getTaskManager().execute(itr);
        SynchronousTaskManager<?> synTaskMan = appAdapter.getCyServiceRegistrar().getService(SynchronousTaskManager.class);           
        synTaskMan.execute(itr); 
        
        view.updateView();
        appAdapter.getVisualMappingManager().getDefaultVisualStyle().apply(view);
        
        CyNetworkViewManager networkViewManager = activator.getService(CyNetworkViewManager.class);
        networkViewManager.addNetworkView(view);
    }

}
