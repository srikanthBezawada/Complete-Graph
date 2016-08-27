package org.cytoscape.completegraph.internal;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.app.CyAppAdapter;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;

/**
 * @author SrikanthB
 *  This task creates the fully connected network and creates the view for it
 */

class KGraphTask extends AbstractTask{
    
    CyNetworkFactory networkFactory;
    CyNetworkManager networkManager;
    CyNetworkViewFactory networkViewFactory; 
    CyNetworkViewManager networkViewManager;
    CyAppAdapter appAdapter;
    CyEventHelper eventHelper;
    int size;
    
    public KGraphTask(CyNetworkFactory networkFactory, CyNetworkManager networkManager, CyNetworkViewFactory networkViewFactory, CyNetworkViewManager networkViewManager, CyEventHelper eventHelper, CyAppAdapter appAdapter, int size) {
        this.networkFactory = networkFactory;
        this.networkManager = networkManager;
        this.networkViewFactory = networkViewFactory;
        this.networkViewManager = networkViewManager;
        this.appAdapter = appAdapter;
        this.eventHelper = eventHelper;
        this.size = size;
    }

    @Override
    public void run(TaskMonitor tm) throws Exception {
        CyNetwork fcnetwork = networkFactory.createNetwork();
        fcnetwork.getRow(fcnetwork).set(CyNetwork.NAME, "Fully Connected Network -"+ size);
        List<CyNode> nodeList = new ArrayList<CyNode>();
        for(int i=1; i<=size; i++) {
            CyNode node = fcnetwork.addNode();
            fcnetwork.getRow(node).set(CyNetwork.NAME, "Node "+ i);
            nodeList.add(node);
        }
        tm.setProgress(0.5);
        String interaction = "interaction type";
        for(CyNode n1 : nodeList) {
            for(CyNode n2 : nodeList) {
                if(n1.equals(n2))
                    continue;
                
                if(fcnetwork.containsEdge(n1, n2) || fcnetwork.containsEdge(n2, n1))
                    continue;
                CyEdge edge = fcnetwork.addEdge(n1, n2, true);
                fcnetwork.getRow(edge).set(CyNetwork.NAME, fcnetwork.getRow(n1).get(CyNetwork.NAME, String.class) + " (" + interaction + ") " + fcnetwork.getRow(n2).get(CyNetwork.NAME, String.class));
                fcnetwork.getRow(edge).set(CyEdge.INTERACTION, interaction);
            }
        }
        tm.setProgress(0.9);
        networkManager.addNetwork(fcnetwork);
        CyNetworkView fcnView = networkViewFactory.createNetworkView(fcnetwork);
        
        updateView(fcnView);
        tm.setProgress(1.0);
    }
    
    public void updateView(CyNetworkView view){
        final CyLayoutAlgorithmManager alMan = appAdapter.getCyLayoutAlgorithmManager();
        CyLayoutAlgorithm algor = alMan.getDefaultLayout();;
        TaskIterator itr = algor.createTaskIterator(view,algor.createLayoutContext(),CyLayoutAlgorithm.ALL_NODE_VIEWS,null);
        appAdapter.getTaskManager().execute(itr);
        SynchronousTaskManager<?> synTaskMan = appAdapter.getCyServiceRegistrar().getService(SynchronousTaskManager.class);           
        synTaskMan.execute(itr); 
        
        view.updateView();
        appAdapter.getVisualMappingManager().getDefaultVisualStyle().apply(view);
        //appAdapter.getVisualMappingManager().setCurrentVisualStyle(appAdapter.getVisualMappingManager().getDefaultVisualStyle());
        networkViewManager.addNetworkView(view);
    }
    
}
