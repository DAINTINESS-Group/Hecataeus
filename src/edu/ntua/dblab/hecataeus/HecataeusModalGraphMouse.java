/**
 * @author George Papastefanatos, National Technical University of Athens
 * @author Fotini Anagnostou, National Technical University of Athens
 */
package edu.ntua.dblab.hecataeus;

import edu.ntua.dblab.hecataeus.graph.evolution.NodeCategory;
import edu.ntua.dblab.hecataeus.graph.visual.VisualEdge;
import edu.ntua.dblab.hecataeus.graph.visual.VisualGraph;
import edu.ntua.dblab.hecataeus.graph.visual.VisualNode;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AnimatedPickingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.RotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.ShearingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.TranslatingGraphMousePlugin;

import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;


public class HecataeusModalGraphMouse extends DefaultModalGraphMouse<VisualNode,VisualEdge> 
		implements GraphMouseListener<VisualNode> {

	protected TranslatingGraphMousePlugin  translatingPluginMiddleButton;
	protected HecataeusPopupGraphMousePlugin  popupEditingPlugin;
     /**
     * create an instance with default values
     *
     */
    public HecataeusModalGraphMouse() {
    	super(); 
    }
    
    /**
     * create an instance with passed values
     * @param in override value for scale in
     * @param out override value for scale out
     */
    public HecataeusModalGraphMouse(float in, float out) {
    	super(in, out); 

    }
    
    protected void loadPlugins() { 
    	
    	pickingPlugin = new HecataeusPickingGraphMousePlugin();
    	animatedPickingPlugin = new AnimatedPickingGraphMousePlugin<VisualNode,VisualEdge>();
    	// add an additional translating plugin 
    	// for middle mouse button that is never removed by other modes 
    	translatingPluginMiddleButton = new TranslatingGraphMousePlugin(InputEvent.BUTTON2_MASK);
    	popupEditingPlugin = new HecataeusPopupGraphMousePlugin();
    	translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
    	scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
    	rotatingPlugin = new RotatingGraphMousePlugin();
    	shearingPlugin = new ShearingGraphMousePlugin();
    	add(scalingPlugin);
    	add(translatingPluginMiddleButton);
    	add(popupEditingPlugin);
    	setMode(Mode.TRANSFORMING);
    	    	
    } 
    
       
    public JMenu getModeMenu() {
    	if(modeMenu == null) {
        	modeMenu = new JMenu("Graph");
			
            final JRadioButtonMenuItem transformingButton = 
                new JRadioButtonMenuItem("Move");
            transformingButton.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {
                        setMode(Mode.TRANSFORMING);
                    }
                }});
            
            final JRadioButtonMenuItem pickingButton =
                new JRadioButtonMenuItem("Pick");
            pickingButton.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if(e.getStateChange() == ItemEvent.SELECTED) {
                        setMode(Mode.PICKING);
                    }
                }});
			
            ButtonGroup radio = new ButtonGroup();
            radio.add(transformingButton);
            radio.add(pickingButton);
            transformingButton.setSelected(true);
            modeMenu.add(transformingButton);
            modeMenu.add(pickingButton);
            modeMenu.setToolTipText("Menu for setting Mouse Mode");
            addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						if(e.getItem() == Mode.TRANSFORMING) {
							transformingButton.setSelected(true);
						} else if(e.getItem() == Mode.PICKING) {
							pickingButton.setSelected(true);
						} 
					}
				}});
        }
        return modeMenu;
    }

	@Override
	/***
	 * 	Double click on a graph vertex, 
	 *  If vertex is a top-level module, then it sets visible/invisible
	 *  the subgraph of the module    
	 */
	public void graphClicked(VisualNode node, MouseEvent me) {
		if (me.getClickCount()==2) {
			VisualizationViewer<VisualNode,VisualEdge> vv = (VisualizationViewer<VisualNode,VisualEdge>) me.getSource();
			VisualGraph g = (VisualGraph) vv.getGraphLayout().getGraph();
			if (node.getType().getCategory()==NodeCategory.MODULE) {
				List<VisualNode> module = g.getModule(node);
				for (VisualNode child : module) {
					child.setVisible(!child.getVisible());
				}
				node.setVisible(true);
			}
		}
	}
	
	@Override
	public void graphPressed(VisualNode v, MouseEvent me) {
		
	
	}

	@Override
	public void graphReleased(VisualNode v, MouseEvent me) {
	
	}
}

