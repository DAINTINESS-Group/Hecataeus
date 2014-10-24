package edu.ntua.dblab.hecataeus;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import net.miginfocom.swing.MigLayout;
import edu.ntua.dblab.hecataeus.graph.evolution.EdgeType;
import edu.ntua.dblab.hecataeus.graph.evolution.EvolutionEvent;
import edu.ntua.dblab.hecataeus.graph.evolution.EvolutionPolicy;
import edu.ntua.dblab.hecataeus.graph.evolution.NodeCategory;
import edu.ntua.dblab.hecataeus.graph.evolution.NodeType;
import edu.ntua.dblab.hecataeus.graph.evolution.PolicyType;
import edu.ntua.dblab.hecataeus.graph.evolution.StatusType;
import edu.ntua.dblab.hecataeus.graph.visual.VisualAggregateLayout;
import edu.ntua.dblab.hecataeus.graph.visual.VisualCluster;
import edu.ntua.dblab.hecataeus.graph.visual.VisualEdge;
import edu.ntua.dblab.hecataeus.graph.visual.VisualEdgeBetweennessClustering;
import edu.ntua.dblab.hecataeus.graph.visual.VisualFileColor;
import edu.ntua.dblab.hecataeus.graph.visual.VisualGraph;
import edu.ntua.dblab.hecataeus.graph.visual.VisualGraphEdgeCrossings;
import edu.ntua.dblab.hecataeus.graph.visual.VisualLayoutType;
import edu.ntua.dblab.hecataeus.graph.visual.VisualNode;
import edu.ntua.dblab.hecataeus.graph.visual.VisualNodeIcon;
import edu.ntua.dblab.hecataeus.graph.visual.VisualNodeLabel;
import edu.ntua.dblab.hecataeus.graph.visual.VisualNodeStroke;
import edu.ntua.dblab.hecataeus.graph.visual.VisualNodeVisible;
import edu.ntua.dblab.hecataeus.graph.visual.VisualNodeVisible.VisibleLayer;
import edu.ntua.dblab.hecataeus.graph.visual.VisualTotalClusters;
import edu.ntua.dblab.hecataeus.metrics.HecataeusMetricManager;
import edu.ntua.dblab.hecataeus.parser.HecataeusSQLParser;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;

public class HecataeusViewer {
	
	// a dummy counter for tabs  
	public static int countOpenTabs = 0;
	// the visual graph object
/**@author pmanousi Needed for topologicalTravel so became public. */
	public static VisualGraph graph;
	public Viewers viewer;
	
	protected Container content;
	protected HecataeusProjectConfiguration projectConf;
	protected HecataeusPolicyManagerGUI policyManagerGui;
	protected HecataeusEventManagerGUI eventManagerGui;
	protected HecataeusFileStructureGUI filesTreeGui;
	public static HecataeusClusterMap hecMap;
	protected VisualNode epilegmenosKombos;
	protected JTabbedPane managerTabbedPane;
	
	protected JTabbedPane summaryGraphTabbedPane;
	protected static JTabbedPane summaryGraphSourceTabbedPane;
	protected static int summaryGraphSourceTabbedPaneIndex;
	
	protected JTabbedPane tabbedPane;
	protected static JTabbedPane sourceTabbedPane;
	protected static int sourceTabbedPaneIndex;
	public static JFrame frame;
	public static boolean nodeSize;
	public static HecataeusViewer myViewer;
/**@author pmanousi Needed for informing user. */
	private JTextArea informationArea;
	private JLabel informationAreaLabel;
	public JPanel informationPanel;

	// the scale object for zoom capabilities 
	private final ScalingControl scaler = new CrossoverScalingControl();
	
	public static VisualAggregateLayout layout ;
	protected VisualAggregateLayout containerLayout;
	protected VisualAggregateLayout subLayout ;
	
	private static final String frameTitle = "HECATAEUS";
	// the visual component
	public static VisualizationViewer<VisualNode, VisualEdge> vv;
	protected VisualizationViewer<VisualNode, VisualEdge> vv1;
	protected static List<VisualizationViewer<VisualNode, VisualEdge>> viewers;
	private static final String frameIconUrl = "resources/hecataeusIcon.png";
	protected Viewers VisualizationViewer;
	protected MouseListener ml;
	protected VisualNodeStroke<Integer,Number> vsh;
	public static  List<VisualGraph> graphs;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HecataeusViewer window = new HecataeusViewer(new VisualGraph());
					myViewer = window;
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public int getHeight()
	{
		return (this.tabbedPane.getHeight());
	}

	
	/**
	 * Create the application.
	 */
	public HecataeusViewer(VisualGraph inGraph) {
		projectConf=new HecataeusProjectConfiguration();
		this.graph = inGraph;
		graphs = new ArrayList<VisualGraph>();
		Dimension prefferedSize = Toolkit.getDefaultToolkit().getScreenSize(); 
		viewers = new ArrayList<VisualizationViewer<VisualNode, VisualEdge>>() ;
		// the layout
		layout = new VisualAggregateLayout(graph, VisualLayoutType.StaticLayout, VisualLayoutType.StaticLayout);
		layout.setSize(prefferedSize);
		VisualizationViewer = new Viewers();
		vv = VisualizationViewer.SetViewers(layout, this);
		vv.setName("Summary Graph");
		viewers.add(vv);
		
		//TODO: FIX THIS
		vv = VisualizationViewer.SetViewers(layout, this);
		vv.setName("Zoom");
		viewers.add(vv);
		/**
		 * @author pmanousi
		 * Inform mouse plugins for the viewer (and have access to epilegmenosKombos in order to update managers. 
		 */
		HecataeusModalGraphMouse gm = new HecataeusModalGraphMouse();
		vv.setGraphMouse(gm);		
		gm.HecataeusViewerPM(this);
		vsh = new VisualNodeStroke<Integer,Number>(vv.getPickedVertexState(), graph, vv);
		vv.getRenderContext().setVertexStrokeTransformer(vsh);
		initialize();
	}

	public void updateManagers()
	{
		if(managerTabbedPane.getSelectedComponent()==policyManagerGui)
		{
			policyManagerGui.UPDATE();
		}
		else if(managerTabbedPane.getSelectedComponent()==eventManagerGui)
		{
			eventManagerGui.UPDATE();
		}
		else
		{	// When map or file system structure are displayed.
			policyManagerGui.UPDATE();
			eventManagerGui.UPDATE();
		}
	}
	
	/**
	 * @author pmanousi
	 * Creates a new project.
	 */
	private void newProject()
	{
		FileDialog fd=new FileDialog((Dialog)null, "Select the name and possition of new project", FileDialog.SAVE);
		fd.setVisible(true);
		if(projectConf==null)
		{
			projectConf=new HecataeusProjectConfiguration();
		}
		else
		{
			projectConf.clearProject();
		}
		if(fd.getFile()!=null||fd.getFile().trim().isEmpty()==false)
		{
			projectConf.curPath=fd.getDirectory()+"/"+fd.getFile();
			projectConf.projectName=fd.getFile().trim();
			projectConf.clearArrayLists();
			File projectDirectory=new File(projectConf.curPath);
			if(projectDirectory.mkdir()==false)
			{
				JOptionPane.showMessageDialog(null, "Could not create project directory,\nplease check your write permitions\nor check if the project already exists.");
				return;
			}			
			frame.setTitle( frameTitle + " - "+projectConf.projectName);
			projectDirectory=new File(projectConf.curPath+"/XML");
			projectDirectory.mkdir();
			projectDirectory=new File(projectConf.curPath+"/SQLS");
			projectDirectory.mkdir();
			projectDirectory=new File(projectConf.curPath+"/POLICIES");
			projectDirectory.mkdir();
			projectDirectory=new File(projectConf.curPath+"/OTHER");
			projectDirectory.mkdir();
			projectConf.setDefaultPolicies();
			policyManagerGui.UPDATE();
			eventManagerGui.UPDATE();
			projectConf.writeConfig();
		}
	}
	
	public void setTextToInformationArea(String text)
	{
		informationArea.setText(text);
	}
	
	/**
	 * @author pmanousi
	 * Opens an existing project.
	 */
	private void openProject(){
		FileDialog fd=new FileDialog((Dialog)null, "Select the project you want to open.", FileDialog.LOAD);
		fd.setDirectory(projectConf.curPath);
		fd.setVisible(true);
		if(fd.getFile()!=null)
		{
			graph.clear();
			projectConf.clearProject();
			frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			projectConf.readConfig(fd.getDirectory(), fd.getFile());	/** Reading configuration */
			boolean selection=false;
			if(JOptionPane.showConfirmDialog(null, "Do you want to open an already saved xml file (YES),\nor start with a clean project configuration(NO)")==0)
			{
				JFileChooser chooser2 = new JFileChooser(projectConf.curPath+"/XML/");
				FileFilterImpl filter = new FileFilterImpl("xml");
				chooser2.addChoosableFileFilter(filter);
				int option = chooser2.showOpenDialog(content);
				if (option == JFileChooser.APPROVE_OPTION)
				{
					File file = chooser2.getSelectedFile();
					graph = graph.importFromXML(file);
					this.graphs.add(graph);
					HecataeusViewer.this.setLayout(VisualLayoutType.ConcentricCircleLayout, VisualLayoutType.ConcentricCircleLayout);
					//in the XML file case, the locations are also defined and they must pass to the graph.
					HecataeusViewer.this.setLayoutPositions();
					selection=true;
				}
				else
				{
					selection=false;
					JOptionPane.showMessageDialog(null, "You will start with a clean project configuration.");
				}
			}
			if(selection==false)
			{	/** @author pmanousi parse files of configuration. */
				HecataeusSQLParser reader = new HecataeusSQLParser(graph);
				frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				for(int i=0;i<projectConf.sqls.size();i++)
				{
					try
					{
						reader.processFile(new File(projectConf.curPath+projectConf.sqls.get(i)));
						graph = reader.getParsedGraph();
						this.graphs.add(graph);
						HecataeusViewer.this.setLayout(VisualLayoutType.ConcentricCircleLayout, VisualLayoutType.ConcentricCircleLayout);
					}
					catch (IOException e1) {}
					catch (SQLException e1) {}
					catch (Exception e1) {}
				}
			}
			VisualFileColor vfc = new VisualFileColor();
			List<String> fileNames = new ArrayList<String>();
			for(VisualNode v : graph.getVertices()){
				if(v.getVisible()){
					if(v.getType().getCategory() == NodeCategory.MODULE ){
						List<VisualEdge> edges = new ArrayList<VisualEdge>(v._inEdges);
						for(VisualEdge e : edges){
							if(e.getType() == EdgeType.EDGE_TYPE_CONTAINS){
								if(e.getFromNode().getType() == NodeType.NODE_TYPE_FILE){
									if(!fileNames.contains(e.getFromNode().getFileName())){
										fileNames.add(e.getFromNode().getFileName());
									}
									v.setFile(e.getFromNode().getFile());
								}
							}
						}
					}
				}
			}
			vfc.setFileNames(fileNames);
			frame.setTitle(frameTitle + " - "+projectConf.projectName);
			filesTreeGui.createPanel(projectConf.curPath+"SQLS/");
			filesTreeGui.setVisible(true);
			policyManagerGui.UPDATE();
			eventManagerGui.UPDATE();
			frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			
			/** Not good but it is the only way to make it work. */
			frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			getLayout(getActiveViewer()).setTopLayoutType(VisualLayoutType.ClustersonaCircleLayoutForInit);
			frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));	
			getActiveViewer().getRenderContext().setVertexIconTransformer(null);
			getActiveViewer().repaint();
			//TODO: FIX THIS
			getActiveViewerZOOM().getRenderContext().setVertexIconTransformer(null);
			getActiveViewerZOOM().repaint();
		}
	}
	
	public void showImpact()
	{
		this.tabbedPane.setSelectedIndex(0);
		showAffected();
		String info=new String();
		String eol=System.getProperty("line.separator");
		for(VisualNode v: graph.getVertices())
		{
			if(v.getStatus() != StatusType.NO_STATUS)
			{
				if(v.getType().getCategory()==NodeCategory.MODULE)
				{
					info+=eol+v.getName();
				}
				else
				{
					info+=eol+v.getParentNode().getName()+"."+v.getName();
				}
			}
		}
		informationArea.setText("Nodes that were affected by the change:"+info);
	}
	
	
	/**
	 * @author pmanousi
	 * Adds a file to the project.
	 */
	private void addFile()
	{
		if(projectConf.projectName.isEmpty())
		{	/** @author pmanousi No active project yet */
			return;
		}
		JFileChooser chooser = new JFileChooser(projectConf.curPath);
		chooser.setMultiSelectionEnabled(true);			//added by sgerag

		FileFilterImpl filter = new FileFilterImpl("sql","SQL File");
		chooser.addChoosableFileFilter(filter);
		int option = chooser.showOpenDialog(content);
		if (option == JFileChooser.APPROVE_OPTION) {
			//modification gy sgerag
			File[] files = chooser.getSelectedFiles();
			//let the append file option to use multiple files
			HecataeusSQLParser reader = new HecataeusSQLParser(graph);
			try
			{																										
				for (int i=0;i<files.length;i++)
				{
					frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));													
					reader.processFile(files[i]);
					projectConf.copySQLFile(files[i]);	/** @author pmanousi Copying files to SQLS directory of project and insert them to configuration file. */
					policyManagerGui.UPDATE();
					eventManagerGui.UPDATE();
				}
				projectConf.writeConfig();
				graph = reader.getParsedGraph();
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			} catch (IOException e1) {
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				final HecataeusMessageDialog p = new HecataeusMessageDialog(frame, "Error description: ", e1.getMessage());
			} catch (SQLException e1) {
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				final HecataeusMessageDialog p = new HecataeusMessageDialog(frame, "Error description: ", e1.getMessage());
			} catch (Exception e1) {											//added by sgerag
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				final HecataeusMessageDialog p = new HecataeusMessageDialog(frame, "Error description: ", e1.getMessage());
			}
		}
	}
	
	public void saveXmlForWhatIf(String event, String node){
		String fileDescription = this.projectConf.curPath+"XML/"+event+"_of_"+node+".xml";
		File file = new File(fileDescription);
		try {
			frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			HecataeusViewer.this.getLayoutPositions();
			layout.getGraph().exportToXML(file);
			frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
//			JOptionPane.showMessageDialog(null,"Previous graph saved under: "+fileDescription,"Information",JOptionPane.INFORMATION_MESSAGE);
		} catch (RuntimeException e1) {}
	}
	
	
	
	/**
	 * @author pmanousi
	 * Exports the graph to a jpg image.
	 */
	private void exportToJpg()
	{
		JFileChooser chooser = new JFileChooser(projectConf.curPath+"/OTHER");
		FileFilterImpl filter = new FileFilterImpl("jpg","Image File");
		chooser.addChoosableFileFilter(filter);
		if ((chooser.showSaveDialog(frame)) == JFileChooser.APPROVE_OPTION) {
			String fileDescription = chooser.getSelectedFile().getAbsolutePath();
			if (!fileDescription.endsWith(".jpg"))
				fileDescription += ".jpg";				
			HecataeusViewer.this.writeJPEGImage(new File(fileDescription));
		}
	}
	
	/**
	 * @author pmanousi
	 * Closes the project.
	 */
	private void closeProject()
	{
		layout.getGraph().clear();
		graph.clear();
		vv.repaint();
		projectConf.clearProject();
		policyManagerGui.UPDATE();
		eventManagerGui.UPDATE();
	}
	
	/**
	 * @author pmanousi
	 * Saves xml.
	 */
	private void saveProject()
	{
		JFileChooser chooser = new JFileChooser(projectConf.curPath+"/XML/");
		FileFilterImpl filter = new FileFilterImpl("xml");
		chooser.addChoosableFileFilter(filter);
		int option = chooser.showSaveDialog(content);
		if (option == JFileChooser.APPROVE_OPTION) {
			String fileDescription = chooser.getSelectedFile().getAbsolutePath();
			if (!fileDescription.endsWith("xml"))
				fileDescription += ".xml";		
			File file = new File(fileDescription);
			if (file.exists()) {
				int response = JOptionPane.showConfirmDialog(null,"The file will be overriden! Do you agree? Answer with y or n","Warning!", JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
				if (response == 0)
					try {
						frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
						//get the location of the vertices from the layout of the graph
						//TODO: this only saves the logical graph, Fix for physical graph as well
						layout.getGraph().exportToXML(file);
						frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
						JOptionPane.showMessageDialog(null,"The file was created successfully","Information",JOptionPane.INFORMATION_MESSAGE);
					} catch (RuntimeException e1) {}
				else
					;
			} else {
				try {
					frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					//get the location of the vertices from the layout of the graph
					//TODO: this only saves the logical graph, Fix for physical graph as well
					layout.getGraph().exportToXML(file);
					frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					JOptionPane.showMessageDialog(null,"The file was created successfully","Information",JOptionPane.INFORMATION_MESSAGE);
				} catch (RuntimeException e1) {}
			}
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("HECATAEUS");
		Dimension prefferedSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setSize(prefferedSize);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage(new ImageIcon(frameIconUrl).getImage());
		frame.getContentPane().setLayout(new MigLayout("", "[grow,fill]", "[984.00]15[]"));

		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		content = frame.getContentPane();

		JMenu mnNewMenu = new JMenu("Project");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewProject = new JMenuItem("New Project");
		mntmNewProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newProject();
			}
		});
		mnNewMenu.add(mntmNewProject);
		
		JMenuItem mntmOpenProject = new JMenuItem("Open Project");
		mntmOpenProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openProject();
				frame.setVisible(true);
			    frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
				frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
				try {
					Thread.sleep(10);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
				//TODO: FIX THIS
				zoomToWindow(activeViewer,null);
				frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		});
		mnNewMenu.add(mntmOpenProject);
		
		JMenuItem mntmAddFile = new JMenuItem("Add File");
		mntmAddFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addFile();
			}
		});
		mnNewMenu.add(mntmAddFile);
		
		JMenuItem mntmExportToJpg = new JMenuItem("Export to jpg");
		mntmExportToJpg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {				
				exportToJpg();
			}
		});
		mnNewMenu.add(mntmExportToJpg);
		
		JMenuItem mntmCloseProject = new JMenuItem("Close Project");
		mntmCloseProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeProject();
			}
		});
		mnNewMenu.add(mntmCloseProject);

		JMenuItem mntmExportClusterData = new JMenuItem("Export Cluster Data");
		mntmExportClusterData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VisualTotalClusters vtc = new VisualTotalClusters(0);
				String message = "<html><head><title>Hecataeus</title></head><body>";
				message+="<h3 style=\"background-color:red;\">Cluster Data</h3>";
				for(VisualCluster vc : vtc.getClusters()){
					message+="<tr><h3>Cluster Name: " + vc.getClusterLabel(vc) + "</h3></tr>";
					message+="<h3>Relations in cluster: </h3>";
					for(VisualNode v : vc.getRelationsInCluster()){
						message+="<b>"+v.getName()+"</b>"+"<br>";
					}
					message+="<h3>Views in cluster: </h3>";
					for(VisualNode v : vc.getViewsInCluster()){
						message+="<b>"+v.getName()+"</b>"+ " from file: " + v.getFileName()+"<br>";
					}
					message+="<h3>Queries in cluster: </h3>";
					for(VisualNode v : vc.getQueriesInCluster()){
						message+="<b>"+v.getName()+"</b>" + " from file: " + v.getFileName()+"<br>";
					}
					message+="<hr>";
				}
				message+="</body></html>";
				
				JFileChooser chooser = new JFileChooser(projectConf.curPath+"/OTHER");
				FileFilterImpl filter = new FileFilterImpl("txt","Text File");
				chooser.addChoosableFileFilter(filter);
				if ((chooser.showSaveDialog(frame)) == JFileChooser.APPROVE_OPTION) {
					String fileDescription = chooser.getSelectedFile().getAbsolutePath();
					if (!fileDescription.endsWith(".html"))
						fileDescription += ".html";				
					writeHtmlFile(new File(fileDescription),message);
				}
			}
		});
		mnNewMenu.add(mntmExportClusterData);

		JMenuItem mntmSaveProject = new JMenuItem("Save Project");
		mntmSaveProject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveProject();
			}
		});
		mnNewMenu.add(mntmSaveProject);

		JMenu mnVisualize = new JMenu("Visualize");
		menuBar.add(mnVisualize);
		
		JMenuItem mntmZoomIn = new JMenuItem("Zoom in");
		mntmZoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//TODO
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
				scaler.scale(activeViewer, 1.1f, activeViewer.getCenter());
			}
		});
		mnVisualize.add(mntmZoomIn);
		
		JMenuItem mntmZoomOut = new JMenuItem("Zoom out");
		mntmZoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
				scaler.scale(activeViewer, 1 / 1.1f, activeViewer.getCenter());
				//TODO: FIX THIS
				final VisualizationViewer<VisualNode, VisualEdge> activeViewerZOOM = HecataeusViewer.getActiveViewerZOOM();
				scaler.scale(activeViewerZOOM, 1 / 1.1f, activeViewerZOOM.getCenter());
			}
		});
		mnVisualize.add(mntmZoomOut);
		
		JMenuItem mntmZoomInWindow = new JMenuItem("Zoom in window");
		mntmZoomInWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
				centerAt(((VisualGraph)activeViewer.getGraphLayout().getGraph()).getCenter());
				//TODO: FIX THIS
				final VisualizationViewer<VisualNode, VisualEdge> activeViewerZOOM = HecataeusViewer.getActiveViewerZOOM();
				centerAt(((VisualGraph)activeViewerZOOM.getGraphLayout().getGraph()).getCenter());
				zoomToWindow(activeViewer,null);
			}
		});
		mnVisualize.add(mntmZoomInWindow);
		
		mnVisualize.addSeparator();
		
		JMenu mnShow = new JMenu("Show");
		mnVisualize.add(mnShow);
		
		JMenuItem mntmToplevelNodes = new JMenuItem("Top-Level Nodes");
		mntmToplevelNodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// reset to default Hecataeus Layout
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = getActiveViewer();
				VisualNodeVisible showAll = (VisualNodeVisible) activeViewer.getRenderContext().getVertexIncludePredicate();
				showAll.setVisibleLevel(graph.getVertices(),VisibleLayer.MODULE);
				System.out.println("active viewer Top-Level Nodes  " + activeViewer.getName());
				activeViewer.repaint();
				//vvContainer.repaint();
				//TODO: FIX THIS
				final VisualizationViewer<VisualNode, VisualEdge> activeViewerZOOM = getActiveViewerZOOM();
				VisualNodeVisible showAllZOOM = (VisualNodeVisible) activeViewerZOOM.getRenderContext().getVertexIncludePredicate();
				showAllZOOM.setVisibleLevel(graph.getVertices(),VisibleLayer.MODULE);
				activeViewerZOOM.repaint();
			}
		});
		mnShow.add(mntmToplevelNodes);
		
		JMenuItem mntmMidlevelNodes = new JMenuItem("Mid-Level Nodes");
		mntmMidlevelNodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = getActiveViewer();
				VisualNodeVisible showAll = (VisualNodeVisible) activeViewer.getRenderContext().getVertexIncludePredicate();
				showAll.setVisibleLevel(graph.getVertices(),VisibleLayer.SCHEMA);
				System.out.println("active viewer Mid-Level Nodes  " + activeViewer.getName());
				activeViewer.repaint();
			}
		});
		mnShow.add(mntmMidlevelNodes);
		
		JMenuItem mntmLowlevelNodes = new JMenuItem("Low-Level Nodes");
		mntmLowlevelNodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = getActiveViewer();
				VisualNodeVisible showAll = (VisualNodeVisible)  activeViewer.getRenderContext().getVertexIncludePredicate();
				showAll.setVisibleLevel(graph.getVertices(),VisibleLayer.SEMANTICS);
				System.out.println("active viewer Low-Level Nodes  " + activeViewer.getName());
				activeViewer.repaint();
				//TODO: FIX THIS
				final VisualizationViewer<VisualNode, VisualEdge> activeViewerZOOM = getActiveViewerZOOM();
				VisualNodeVisible showAllZOOM = (VisualNodeVisible)  activeViewerZOOM.getRenderContext().getVertexIncludePredicate();
				showAllZOOM.setVisibleLevel(graph.getVertices(),VisibleLayer.SEMANTICS);
				activeViewerZOOM.repaint();
			}
		});
		mnShow.add(mntmLowlevelNodes);
		
		mnShow.addSeparator();
		
		JMenuItem mntmNodesWithStatus = new JMenuItem("Nodes with Status");
		mntmNodesWithStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// reset to default Hecataeus Layout
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = getActiveViewer();
				VisualNodeVisible showAll = (VisualNodeVisible) activeViewer.getRenderContext().getVertexIncludePredicate();
				showAll.setVisibleLevel(graph.getVertices(),VisibleLayer.STATUS);
				activeViewer.repaint();
				//TODO: FIX THIS
				final VisualizationViewer<VisualNode, VisualEdge> activeViewerZOOM = getActiveViewerZOOM();
				VisualNodeVisible showAllZOOM = (VisualNodeVisible) activeViewerZOOM.getRenderContext().getVertexIncludePredicate();
				showAllZOOM.setVisibleLevel(graph.getVertices(),VisibleLayer.STATUS);
				activeViewerZOOM.repaint();
			}
		});
		mnShow.add(mntmNodesWithStatus);
		
		JMenuItem mntmNodesWithPolicy = new JMenuItem("Nodes with Policy");
		mntmNodesWithPolicy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = getActiveViewer();
				VisualNodeVisible showAll = (VisualNodeVisible) activeViewer.getRenderContext().getVertexIncludePredicate();
				showAll.setVisibleLevel(graph.getVertices(),VisibleLayer.POLICIES);
				activeViewer.repaint();
			}
		});
		mnShow.add(mntmNodesWithPolicy);
		
		JMenuItem mntmNodesWithEvent = new JMenuItem("Nodes with Event");
		mntmNodesWithEvent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = getActiveViewer();
				VisualNodeVisible showAll = (VisualNodeVisible) activeViewer.getRenderContext().getVertexIncludePredicate();
				showAll.setVisibleLevel(graph.getVertices(),VisibleLayer.EVENTS);
				activeViewer.repaint();
			}
		});
		mnShow.add(mntmNodesWithEvent);
		
		
		final JMenu mnAlgorithms = new JMenu("Algorithms");
		
		for (final VisualLayoutType layoutType : VisualLayoutType.values()) {
			if(layoutType.equals(VisualLayoutType.ZoomedLayoutForModules))
			{
				mnAlgorithms.addSeparator();
			}
			if(layoutType.equals(VisualLayoutType.ZoomedLayoutForModules)||layoutType.equals(VisualLayoutType.ClustersonaCircleLayoutForInit))
			{
				continue;
			}
			mnAlgorithms.add(new AbstractAction(layoutType.toString()) {
				public void actionPerformed(ActionEvent e) {
						vv.setGraphLayout(layout);
						if(VisualEdgeBetweennessClustering.south!=null){
							frame.remove(VisualEdgeBetweennessClustering.south);
						}
						frame.repaint();
						
						final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
						getLayout(activeViewer).setTopLayoutType(layoutType);  
						HecataeusViewer.this.getLayoutPositions();
						vv.repaint();
						centerAt(((VisualGraph)activeViewer.getGraphLayout().getGraph()).getCenter());
						// TODO: FIX THIS
						zoomToWindow(activeViewer,null);
				}
			});
		}
		
		mnVisualize.add(mnAlgorithms);
		mnAlgorithms.addSeparator();
		
		JMenuItem mntmRevert = new JMenuItem("Revert");
		mntmRevert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
				//update the new layout's positions
				// FIXME: restore original layout
				HecataeusViewer.this.getLayoutPositions();
				HecataeusViewer.this.centerAt(graph.getCenter());
				// TODO: FIX THIS
				HecataeusViewer.this.zoomToWindow(activeViewer,null);
			}
		});
		mnAlgorithms.add(mntmRevert);
		
		/*
		 * eixe ginei gia na zwgrafizei me D3 douleuei alla telika to paratisame
		 * 
		 */
		
		
//		JMenuItem mntmWeb = new JMenuItem("web");
//		mntmWeb.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				VisualCircleLayout vcl = new VisualCircleLayout(graph);
//				List<VisualNode> relations = new ArrayList<VisualNode>(vcl.getRelations());
//				System.out.println(relations);
//				List<VisualNode> queries = new ArrayList<VisualNode>(vcl.getQueries());
//				List<VisualNode> views = new ArrayList<VisualNode>(vcl.getViews());
//
//				try {
//					File file = new File("/home/eva/newTest1/data1.json");
//					FileWriter fw = new FileWriter(file.getAbsoluteFile());
//					BufferedWriter bw = new BufferedWriter(fw);
//					
//					String content = "[";
//					
//					for(VisualNode r : relations){
//						content += "{" + "\"name\":\"" + r.getName() + "\"" + ",\"imports\":\n";
//						List<VisualEdge> inEdges = new ArrayList<VisualEdge>(r._inEdges);
//						
//						for(VisualEdge edge : inEdges){
//							if(edge.getFromNode().getType() == NodeType.NODE_TYPE_QUERY || edge.getFromNode().getType() == NodeType.NODE_TYPE_VIEW){
//								content+="[";
//								break;
//							}
//						}
//						for(VisualEdge edge : inEdges){
//							if(edge.getFromNode().getType() == NodeType.NODE_TYPE_QUERY || edge.getFromNode().getType() == NodeType.NODE_TYPE_VIEW){
//								//content+=edge.getFromNode().getName()+"," ;
//								
//								content+="\" "+edge.getFromNode().getName() +"\""+"," ;
//							}
//						}
//						content=content.substring(0,content.length()-1);
//						content+="]},";
//					}
//					content=content.substring(0,content.length()-1);
//					content+="]";
//					
////					String content = "{\n";
////					content+="\"name\":\"eva\""+",\n";
////					content+="\"children\": [\n \t{\n";
////					FileWriter fw = new FileWriter(file.getAbsoluteFile());
////					BufferedWriter bw = new BufferedWriter(fw);
////					
////					for(VisualNode r : relations){
////						content+="\"name\":\""+ r.getName()+"\""+",\n";
////						List<VisualEdge> inEdges = new ArrayList<VisualEdge>(r._inEdges);
////						
////						for(VisualEdge edge : inEdges){
////							if(edge.getFromNode().getType() == NodeType.NODE_TYPE_QUERY || edge.getFromNode().getType() == NodeType.NODE_TYPE_VIEW){
////								content+="\"children\": [\n";
////								break;
////							}
////						}
////						
////						for(VisualEdge edge : inEdges){
////							if(edge.getFromNode().getType() == NodeType.NODE_TYPE_QUERY || edge.getFromNode().getType() == NodeType.NODE_TYPE_VIEW){
////							//	content+="\"children\": [\n {";
////								content+="{"+"\"name\":\""+ edge.getFromNode().getName()+"\""+"}," ;
//////								if(edge.getFromNode().getType() == NodeType.NODE_TYPE_VIEW){
//////									List<VisualEdge> inViewEdges = new ArrayList<VisualEdge>(edge.getFromNode()._inEdges);
//////									for(VisualEdge vEdge :  inViewEdges){
//////										if(vEdge.getFromNode().getType() == NodeType.NODE_TYPE_QUERY || edge.getFromNode().getType() == NodeType.NODE_TYPE_VIEW){
//////											content+="\"children\": [\n {";
//////											content+="\"name\":\""+ vEdge.getFromNode().getName()+"\""+",\n" ;
//////										}
//////									}
//////									
//////								}
////							}
////						}
////						content=content.substring(0, content.length()-1);
////						content+= "\n]\n},{";
////						
////					}
////					content=content.substring(0, content.length()-2);
////					
////					content+= "\n]\n}";
//					bw.write(content);
//					bw.close();
//				} catch (IOException fileE) {
//					fileE.printStackTrace();
//				}
//			}
//		});
//		mnAlgorithms.add(mntmWeb);
		
		mnVisualize.addSeparator();
		
		JCheckBoxMenuItem chckbxmntmNewCheckItem = new JCheckBoxMenuItem("Icons On");
		chckbxmntmNewCheckItem.setSelected(false);
		chckbxmntmNewCheckItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				AbstractButton aButton = (AbstractButton) event.getSource();
				boolean selected = aButton.getModel().isSelected();
				if (selected) {
					getActiveViewer().getRenderContext().setVertexIconTransformer(new VisualNodeIcon());
					getActiveViewer().repaint();
					//TODO: FIX THIS
					getActiveViewerZOOM().getRenderContext().setVertexIconTransformer(new VisualNodeIcon());
					getActiveViewerZOOM().repaint();
				} else {
					getActiveViewer().getRenderContext().setVertexIconTransformer(null);
					getActiveViewer().repaint();
					//TODO: FIX THIS
					getActiveViewerZOOM().getRenderContext().setVertexIconTransformer(null);
					getActiveViewerZOOM().repaint();
				}
			}
		});
		mnVisualize.add(chckbxmntmNewCheckItem);
		
		mnVisualize.addSeparator();
		JCheckBoxMenuItem mntmRelationLabels = new JCheckBoxMenuItem("Relation Labels On");
		mntmRelationLabels.setSelected(false);
		mntmRelationLabels.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent event) {
				AbstractButton aButton = (AbstractButton) event.getSource();
				boolean selected = aButton.getModel().isSelected();
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = getActiveViewer();
				VisualNodeLabel vnl=new VisualNodeLabel();
				if (selected)
					vnl.setVisibility(true);
				else
					vnl.setVisibility(false);
				activeViewer.getRenderContext().setVertexLabelTransformer(vnl);
				activeViewer.repaint();
				//TODO: FIX THIS
				final VisualizationViewer<VisualNode, VisualEdge> activeViewerZOOM = getActiveViewerZOOM();
				activeViewerZOOM.getRenderContext().setVertexLabelTransformer(vnl);
				activeViewerZOOM.repaint();
			}
		});
		mnVisualize.add(mntmRelationLabels);
		
		vv.getRenderContext().setVertexIconTransformer(null);
		vv.repaint();
		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);
		
		JMenuItem mntmFindNodes = new JMenuItem("Find Node");
		mntmFindNodes.setAccelerator(KeyStroke.getKeyStroke("control F"));
		mntmFindNodes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO: FIX THIS
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
				final VisualizationViewer<VisualNode, VisualEdge> activeViewerZOOM = HecataeusViewer.getActiveViewerZOOM();
				// obtain user input from JOptionPane input dialogs
				String nodeNames = JOptionPane.showInputDialog( "The name of nodes to find (separated with ,): ");
				StringTokenizer token = new StringTokenizer("");
				if (nodeNames != null) {
					token = new StringTokenizer(nodeNames);
				}
				activeViewer.getPickedVertexState().clear();
				activeViewerZOOM.getPickedVertexState().clear();
				VisualNode v = null;

				while (token.hasMoreTokens()) {
					String name = token.nextToken(",");
						for (VisualNode u : activeViewer.getGraphLayout().getGraph().getVertices()) {
							if (u.getName().equals(name.trim().toUpperCase())
									&& u.getVisible()) {
								activeViewer.getPickedVertexState().pick(u,
										true);
								v = u;
							}
					}
					//TODO: FIX THIS	
						for (VisualNode u : activeViewerZOOM.getGraphLayout().getGraph().getVertices()) {
							if (u.getName().equals(name.trim().toUpperCase()) && u.getVisible()) {
								activeViewerZOOM.getPickedVertexState().pick(u, true);
								v = u;
							}
					}
				}
				if (v != null)
				{
//					centerAt(v.getLocation());
					centerAt(activeViewer.getGraphLayout().transform(v));
					centerAt(activeViewerZOOM.getGraphLayout().transform(v));
					epilegmenosKombos=v;
					updateManagers();
				}
			}
		});
		mnTools.add(mntmFindNodes);
		
		JMenuItem mntmFindEdge = new JMenuItem("Find Edge");
		mntmFindEdge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
				// obtain user input from JOptionPane input dialogs
				String name = JOptionPane
						.showInputDialog("The name of the edge to find: ");
				activeViewer.getPickedEdgeState().clear();
				for (VisualEdge edge: graph.getEdges()) {
					if (edge.getName().equals(name)) {
						activeViewer.getPickedEdgeState().pick(edge, true);
					}
				}
			}
		});
		mnTools.add(mntmFindEdge);
		
		JMenuItem mntmSellectAll = new JMenuItem("Select All");
		mntmSellectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
				for (VisualNode u : graph.getVertices()) {
					if (u.getVisible()) {
						activeViewer.getPickedVertexState().pick(u, true);
					}
				}
			}
		});
		mnTools.add(mntmSellectAll);
		
		mnTools.addSeparator();
		
		JMenuItem mntmModuleSynopsys = new JMenuItem("Module Synopsis");
		mnTools.add(mntmModuleSynopsys);
		
		JMenuItem mntmOutputModuleStructure = new JMenuItem("Output Module Structure");
		mntmOutputModuleStructure.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				
				List<VisualNode> modules = activeGraph.getVertices(NodeType.NODE_TYPE_RELATION);
				modules.addAll(activeGraph.getVertices(NodeType.NODE_TYPE_VIEW));
				modules.addAll(activeGraph.getVertices(NodeType.NODE_TYPE_QUERY));
				String message = "\n";
				message = "\t";
				for (VisualNode aNode: modules) {
					message += aNode + "\t";
				}
				for (VisualNode aNode: modules) {
					message +="\n" + aNode; 
					for (VisualNode bNode: modules) {
						message +="\t" + activeGraph.getConnections(activeGraph.getModule(aNode),activeGraph.getModule(bNode));
					}	
				}
				
				
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Degree Total", message);
			}
		});
		mnTools.add(mntmOutputModuleStructure);
		
		JMenu mnManage = new JMenu("Manage");
		menuBar.add(mnManage);
		JMenuItem mntmClearAllPolicies = new JMenuItem("Clear All Policies");
		mntmClearAllPolicies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane
						.showConfirmDialog(
								frame,
								"All policies on the graph will be deleted. Are you sure?",
								"Warning!", JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);
				if (response == 0) {
					for (VisualNode v : graph.getVertices()) {
						v.getPolicies().clear();
					}
					//empty default policies definitions
					graph.getDefaultPolicyDecsriptions().clear();
					vv.repaint();
//					vvContainer.repaint();
				}
			}
		});
		mnManage.add(mntmClearAllPolicies);
		
		JMenuItem mntmClearAllEvents = new JMenuItem("Clear All Events");
		mntmClearAllEvents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int response = JOptionPane
						.showConfirmDialog(
								frame,
								"All events on the graph will be deleted. Are you sure?",
								"Warning!", JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE);
				if (response == 0) {
					if (response == 0) {
						for (VisualNode v : graph.getVertices()) {
							v.getEvents().clear();
						}
						vv.repaint();
//						vvContainer.repaint();
					}
				}
			}
		});
		mnManage.add(mntmClearAllEvents);
		
		JMenuItem mntmClearAllStatuces = new JMenuItem("Clear All Statuses");
		mntmClearAllStatuces.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (VisualNode v : graph.getVertices()) {
					v.setStatus(StatusType.NO_STATUS, true);
				}
				for (VisualEdge edge : graph.getEdges()) {
					edge.setStatus(StatusType.NO_STATUS, true);
				}
				vv.repaint();
//				vvContainer.repaint();
			}
		});
		mnManage.add(mntmClearAllStatuces);
		
		JMenuItem mntmInversePolicies = new JMenuItem("Inverse Policies");
		mntmInversePolicies.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (VisualNode v : graph.getVertices()) {
					if (v.getHasPolicies()) {
						for (EvolutionPolicy<VisualNode> p : v.getPolicies()) {
							switch (p.getPolicyType()) {
							case PROPAGATE:
								p.setPolicyType(PolicyType.BLOCK);
								break;
							case BLOCK:
								p.setPolicyType(PolicyType.PROPAGATE);
								break;
							default:
								p.setPolicyType(PolicyType.PROMPT);
								break;
							}
						}
					}
				}
				vv.repaint();
//				vvContainer.repaint();
				policyManagerGui.revertPolicies();
			}
		});
		mnManage.add(mntmInversePolicies);
		
		JMenu mnMetsics = new JMenu("Metrics");
		menuBar.add(mnMetsics);
		
		JMenu mnCount = new JMenu("Count");
		mnMetsics.add(mnCount);
		
		JMenu mnNodes = new JMenu("Nodes");
		
		JMenuItem mntmAll = new JMenuItem("All");
		mntmAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph(); 
				int countR = 0, countV = 0, countQ = 0;
				for (VisualNode v : activeGraph.getVertices()) {
					if (v.getVisible() && v.getType() == NodeType.NODE_TYPE_RELATION)
						countR++;
					if (v.getVisible() && v.getType() == NodeType.NODE_TYPE_VIEW)
						countV++;
					if (v.getVisible() && v.getType() == NodeType.NODE_TYPE_QUERY)
						countQ++;
				}
				int sum = countR + countV + countQ;
				String message = "Relations: " +countR +"\nViews: " + countV +"\nQueries: "+ countQ +"\nSum: " + sum;
				JOptionPane.showMessageDialog(content, message);
			}
		});
		
		mnNodes.add(mntmAll);

		JMenuItem mntmRelations = new JMenuItem("Relations");
		mntmRelations.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph(); 
				int countNodes = 0;
				for (VisualNode v : activeGraph.getVertices()) {
					if (v.getVisible() && v.getType() == NodeType.NODE_TYPE_RELATION)
						countNodes++;
				}
				JOptionPane.showMessageDialog(content, countNodes);
			}
		});
		
		mnNodes.add(mntmRelations);
		
		JMenuItem mntmViews = new JMenuItem("Views");
		
		mntmViews.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph(); 
				int countNodes = 0;
				for (VisualNode v : activeGraph.getVertices()) {
					if (v.getVisible() && v.getType() == NodeType.NODE_TYPE_VIEW)
						countNodes++;
				}
				JOptionPane.showMessageDialog(content, countNodes);
			}
		});
		mnNodes.add(mntmViews);
		
		JMenuItem mntmQueries = new JMenuItem("Queries");
		mntmQueries.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph(); 
				int countNodes = 0;
				for (VisualNode v : activeGraph.getVertices()) {
					if (v.getVisible() && v.getType() == NodeType.NODE_TYPE_QUERY)
						countNodes++;
				}
				JOptionPane.showMessageDialog(content, countNodes);
			}
		});
		mnNodes.add(mntmQueries);

		mnCount.add(mnNodes);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Policies");
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int propagatePolicies = HecataeusMetricManager.countPolicies(activeGraph.getVertices(), PolicyType.PROPAGATE);
				int blockPolicies = HecataeusMetricManager.countPolicies(activeGraph.getVertices(), PolicyType.BLOCK);
				int promptPolicies = HecataeusMetricManager.countPolicies(activeGraph.getVertices(),PolicyType.PROMPT);

				String message = "Propagate: " + propagatePolicies;
				message += "\nBlock: " + blockPolicies;
				message += "\nPrompt: " + promptPolicies;
				JOptionPane.showMessageDialog(content, message);
			}
		});
		mnCount.add(mntmNewMenuItem);
		
		JMenuItem mntmEvents_1 = new JMenuItem("Events");
		mntmEvents_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int events = HecataeusMetricManager.countEvents(activeGraph.getVertices());
				JOptionPane.showMessageDialog(content, events);
			}
		});
		mnCount.add(mntmEvents_1);
		
		JMenuItem mntmGraphEdgeCrossings = new JMenuItem("Total Graph Crossings");
		mntmGraphEdgeCrossings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "Total edge crossings : ";
				VisualGraphEdgeCrossings vgec = new VisualGraphEdgeCrossings(graph);
				int crossings = vgec.getGraphEdgeCrossings();
				message += crossings;
				
				int edgeNum = vgec.getNumberOfEdges();
				message += " \nTotal number of edges ";
				message += edgeNum;
				
				message += "\n----------------------\n";

				JOptionPane.showMessageDialog(frame, message, "Edge Crossings", JOptionPane.INFORMATION_MESSAGE );
			}
		});
		mnMetsics.add(mntmGraphEdgeCrossings);
		
		JMenuItem mntmGraphEdgeData = new JMenuItem("Graph Cluster Data");
		mntmGraphEdgeData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "Cluster ID: \t Cluster Rad: \t Cluster edge crossings \t Cluster Line Length\n";
				
				VisualTotalClusters vtc = new VisualTotalClusters();
				message += vtc.getClustersData();
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Cluster Metrics", message);
				
			}
		});
		mnMetsics.add(mntmGraphEdgeData);
		
		JMenuItem mntmGraphSpace = new JMenuItem("Graph Space");
		mntmGraphSpace.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message ="";
				System.out.println("layout S   " +layout.getSize());
				Dimension viwerDim = layout.getSize();
				double w = viwerDim.getWidth(), h = viwerDim.getHeight();
				double maxX = 0, minX = w, maxY = 0, minY = h;
				for(VisualNode node: graph.getVertices()){
					if(node.getVisible()){
						if(node.getLocation().getX()>maxX){
							maxX=node.getLocation().getX();
						}
						if(node.getLocation().getX()<minX){
							minX = node.getLocation().getX();
						}
						if(node.getLocation().getY()>maxY){
							maxY = node.getLocation().getY();
						}
						if(node.getLocation().getY()<minY){
							minY = node.getLocation().getY();
						}
					}
				}
				Point2D myMaxX= new Point2D.Double(maxX,0);
				Point2D myMinX= new Point2D.Double(minX,0);
				Point2D myMaxY= new Point2D.Double(0,maxY);
				Point2D myMinY= new Point2D.Double(0,minY);
				double distX = myMaxX.distance(myMinX);
				double distY = myMaxY.distance(myMinY);
				double area = distX*distY;
				area = (double) Math.round(area * 100) / 100;
				VisualTotalClusters vtc = new VisualTotalClusters(0);
				message += "Area used by graph: "  +area;
				message += "\nTotal area occupied by clusters: "+vtc.getTotalArea();
				message += "\nPersent% "+ Math.round(((100*vtc.getTotalArea())/area)* 100) / 100;
				//JOptionPane.showMessageDialog(frame, message, "Total used space", JOptionPane.INFORMATION_MESSAGE );
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Total used space", message);
			}
		});
		mnMetsics.add(mntmGraphSpace);
		
		JMenuItem mntmOutpoutForModule = new JMenuItem("Output For Module Nodes");
		mntmOutpoutForModule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				String message = "NodeKey\tModule\tNode Name\tNode Type\tDEGREE IN\tDEGREE OUT\tDEGREE TOTAL\tSTRENGTH IN\tSTRENGTH OUT\tSTRENGTH TOTAL\tTRAN DEGREE IN\tTRAN DEGREE OUT\tTRAN MODULE OUT\tTRAN STRENGTH OUT\tENTROPY OUT\n";
				for (VisualNode topNode: activeGraph.getVertices(NodeCategory.MODULE)) {
							int metric = HecataeusMetricManager.degree(topNode);
							message += activeGraph.getKey(topNode)
									+ "\t"
									+ topNode.getName()
									+ "\t" + topNode.getName() 
									+ "\t" + topNode.getType()
									+ "\t" + HecataeusMetricManager.inDegree(topNode)
									+ "\t" + HecataeusMetricManager.outDegree(topNode)
									+ "\t" + HecataeusMetricManager.degree(topNode)
									+ "\t" + HecataeusMetricManager.inStrength(topNode,activeGraph)
									+ "\t" + HecataeusMetricManager.outStrength(topNode,activeGraph)
									+ "\t" + HecataeusMetricManager.strength(topNode,activeGraph)
									+ "\t" + HecataeusMetricManager.inTransitiveDegree(topNode)
									+ "\t" + HecataeusMetricManager.outTransitiveDegree(topNode)
									+ "\t" + HecataeusMetricManager.outTransitiveModuleDegree(topNode)
									+ "\t" + HecataeusMetricManager.outTransitiveStrength(topNode, graph)
									+ "\t'" + HecataeusMetricManager.entropyOutPerNode(topNode,activeGraph)
									+ "\n";
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output All metrics for top level Nodes", message);
			}
		});
		mnMetsics.add(mntmOutpoutForModule);
		
		JMenu mnOutputForAll = new JMenu("Output For All Nodes");
		mnMetsics.add(mnOutputForAll);
		
		mnMetsics.addSeparator();
		
		JMenuItem mntmDegreeTotal = new JMenuItem("Degree Total");
		mntmDegreeTotal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				String message = "NodeKey\tModule\tNode Name\tNode Type\tDegree\n";
				for (VisualNode topNode: activeGraph.getVertices(NodeCategory.MODULE)) {
						for (VisualNode evNode : activeGraph.getModule(topNode)) {
							int metric = HecataeusMetricManager.degree(evNode);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ topNode.getName()
									+ "\t" + evNode.getName() 
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
						}
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Degree Total", message);
			}
		});
		mnOutputForAll.add(mntmDegreeTotal);
		
		JMenuItem mntmDegreeIn = new JMenuItem("Degree In");
		mntmDegreeIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				String message = "NodeKey\tModule\tNode Name\tNode Type\tDegree\n";
				for (VisualNode topNode: activeGraph.getVertices(NodeCategory.MODULE)) {
						for (VisualNode evNode : activeGraph.getModule(topNode)) {
							int metric = HecataeusMetricManager.inDegree(evNode);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ topNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
						}
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Degree In", message);
			}
		});
		mnOutputForAll.add(mntmDegreeIn);
		
		JMenuItem mntmDegreeOut = new JMenuItem("Degree Out");
		mntmDegreeOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				String message = "NodeKey\tModule\tNode Name\tNode Type\tDegree\n";
				for (VisualNode topNode: activeGraph.getVertices(NodeCategory.MODULE)) {
						for (VisualNode evNode : activeGraph.getModule(topNode)) {
							int metric = HecataeusMetricManager.outDegree(evNode);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ topNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
						}
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Degree Out", message);
			}
		});
		mnOutputForAll.add(mntmDegreeOut);
		
		JMenuItem mntmTransitiveDegree = new JMenuItem("Transitive Degree");
		mntmTransitiveDegree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				String message = "NodeKey\tModule\tNode Name\tNode Type\tDegree\n";
				for (VisualNode topNode: activeGraph.getVertices(NodeCategory.MODULE)) {
						for (VisualNode evNode : activeGraph.getModule(topNode)) {
							int metric = HecataeusMetricManager.transitiveDegree(evNode);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ topNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
						}
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Transitive Degree In", message);
			}
		});
		mnOutputForAll.add(mntmTransitiveDegree);
		
		JMenuItem mntmTransitiveDegreeIn = new JMenuItem("Transitive Degree In");
		mntmTransitiveDegreeIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				String message = "NodeKey\tModule\tNode Name\tNode Type\tDegree\n";
				for (VisualNode topNode: activeGraph.getVertices(NodeCategory.MODULE)) {
						for (VisualNode evNode : activeGraph.getModule(topNode)) {
							int metric = HecataeusMetricManager.inTransitiveDegree(evNode);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ topNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
						}
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Transitive Degree In", message);
			}
		});
		mnOutputForAll.add(mntmTransitiveDegreeIn);
		
		JMenuItem mntmTransitiveDegreeOut = new JMenuItem("Transitive Degree Out");
		mntmTransitiveDegreeOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "NodeKey\tModule\tNode Name\tNode Type\tTranDegreeOut\n";
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				for (VisualNode topNode: activeGraph.getVertices(NodeCategory.MODULE)) {
						for (VisualNode evNode : activeGraph.getModule(topNode)) {
							int metric = HecataeusMetricManager.outTransitiveDegree(evNode);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ topNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
						}
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Transitive Degree Out" , message);
			}
		});
		mnOutputForAll.add(mntmTransitiveDegreeOut);
		
		JMenuItem mntmStrengthTotal = new JMenuItem("Strength Total");
		mntmStrengthTotal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "NodeKey\tModule\tNode Name\tNode Type\tStrength\n";
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				for (VisualNode evNode: activeGraph.getVertices(NodeCategory.MODULE)) {
							int metric = HecataeusMetricManager.strength(evNode, activeGraph);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ evNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Strength Total", message);
			}
		});
		mnOutputForAll.add(mntmStrengthTotal);
		
		JMenuItem mntmStrengthIn = new JMenuItem("Strength In ");
		mntmStrengthIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "NodeKey\tModule\tNode Name\tNode Type\tStrength\n";
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				for (VisualNode evNode: activeGraph.getVertices(NodeCategory.MODULE)) {
							int metric = HecataeusMetricManager.inStrength(evNode, activeGraph);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ evNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Strength In", message);
			}
		});
		mnOutputForAll.add(mntmStrengthIn);
		
		JMenuItem mntmStrengthOut = new JMenuItem("Strength Out");
		mntmStrengthOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "NodeKey\tModule\tNode Name\tNode Type\tStrength\n";
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				for (VisualNode evNode: activeGraph.getVertices(NodeCategory.MODULE)) {
							int metric = HecataeusMetricManager.outStrength(evNode, activeGraph);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ evNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Strength Out", message);
			}
		});
		mnOutputForAll.add(mntmStrengthOut);
		
		JMenuItem mntmWeightedDegreeIn = new JMenuItem("Weighted Degree In");
		mntmWeightedDegreeIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "NodeKey\tModule\tNode Name\tNode Type\tWeighted Degree In\n";
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				for (VisualNode topNode: activeGraph.getVertices(NodeCategory.MODULE)) {
						for (VisualNode evNode : activeGraph.getModule(topNode)) {
							int metric = HecataeusMetricManager.inWeightedDegree(evNode);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ topNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
						}
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Weighted Degree In", message);
			}
		});
		mnOutputForAll.add(mntmWeightedDegreeIn);
		
		JMenuItem mntmWeightedDegreeOut = new JMenuItem("Weighted Strenght In");
		mntmWeightedDegreeOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "NodeKey\tModule\tNode Name\tNode Type\tWeighted Strength In\n";
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				for (VisualNode evNode: activeGraph.getVertices(NodeCategory.MODULE)) {
							int metric = HecataeusMetricManager.inWeightedStrength(evNode, activeGraph);
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ evNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Weighted Strength In", message);
			}
		});
		mnOutputForAll.add(mntmWeightedDegreeOut);
		
		JMenuItem mntmPolicieDegreeIn = new JMenuItem("Policy Degree In");
		mntmPolicieDegreeIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "NodeKey\tModule\tNode Name\tNode Type\tPDegree\n";
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				for (VisualNode topNode: activeGraph.getVertices(NodeCategory.MODULE)) {
					int metric = 0; 
					for (VisualNode evNode : activeGraph.getModule(topNode)) {
							for (EvolutionEvent<VisualNode> event : evNode.getEvents()) {
								 metric += HecataeusMetricManager.inPolicyTransitiveDegree(event, activeGraph);
							}
					}
					message += activeGraph.getKey(topNode)
					+ "\t"
					+ topNode.getName()
					+ "\t" + topNode.getName()
					+ "\t" + topNode.getType()
					+ "\t" + metric
					+ "\n";
				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Policy Degree In", message);
			}
		});
		mnOutputForAll.add(mntmPolicieDegreeIn);
		
		JMenuItem mntmPolicyDegreeOut = new JMenuItem("Policy Degree Out");
		mntmPolicyDegreeOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "NodeKey\tModule\tNode Name\tNode Type\tPDegreeOut\n";
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				for (VisualNode topNode: activeGraph.getVertices(NodeCategory.MODULE)) {
						List<VisualNode> nodes = activeGraph.getModule(topNode);
						for (VisualNode evNode : nodes) {
							int metric = 0;
							for (VisualNode aNode : nodes) {
								for (EvolutionEvent<VisualNode> anEvent: aNode.getEvents()) {
									metric += HecataeusMetricManager.outPolicyTransitiveDegree(evNode, anEvent);
								}
							}
							message += activeGraph.getKey(evNode)
									+ "\t"
									+ topNode.getName()
									+ "\t" + evNode.getName()
									+ "\t" + evNode.getType()
									+ "\t" + metric
									+ "\n";
						}

				}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Policy Degree Out", message);
			}
		});
		mnOutputForAll.add(mntmPolicyDegreeOut);
		
		JMenuItem mntmEntropyIn = new JMenuItem("Entropy In");
		mntmEntropyIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "NodeKey\tModule\tNode Name\tNode Type\tEntropy In\n";
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				for (VisualNode evNode: activeGraph.getVertices(NodeCategory.MODULE)) {
						double metric = HecataeusMetricManager.entropyInPerNode(evNode, activeGraph);
						message += activeGraph.getKey(evNode)
								+ "\t"
								+ evNode.getName() 
								+ "\t" + evNode.getName()
								+ "\t" + evNode.getType()
								+ "\t'" + metric+ "\n";
					}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Entropy In", message);
			}
		});
		mnOutputForAll.add(mntmEntropyIn);
		
		JMenuItem mntmEntropyOuy = new JMenuItem("Entropy Out");
		mntmEntropyOuy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String message = "NodeKey\tModule\tNode Name\tNode Type\tEntropy Out\n";
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				for (VisualNode evNode: activeGraph.getVertices(NodeCategory.MODULE)) {
						double metric = HecataeusMetricManager.entropyOutPerNode(evNode, activeGraph);
						message += activeGraph.getKey(evNode)
								+ "\t"
								+ evNode.getName() 
								+ "\t" + evNode.getName()
								+ "\t" + evNode.getType()
								+ "\t'" + metric+ "\n";
					}
				final HecataeusMessageDialog m = new HecataeusMessageDialog(frame, "Graph Metrics - Output for: Entropy Out", message);
			}
		});
		mnOutputForAll.add(mntmEntropyOuy);
		
		JMenuItem mntmEntropyOfGraph = new JMenuItem("Entropy Of Graph");
		mntmEntropyOfGraph.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				float entropy = HecataeusMetricManager.entropyGraph(activeGraph);
				String message = "Entropy Of the Graph: " + entropy;
				JOptionPane.showMessageDialog(content, message);
			}
		});
		mnMetsics.add(mntmEntropyOfGraph);
		
		JMenuItem mntmMaximunEntropy = new JMenuItem("Maximun Entropy");
		mntmMaximunEntropy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				float entropy = 0;
				VisualNode maxEntropyNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					float nodeEntropy = HecataeusMetricManager.entropyOutPerNode(node, graph);
					if (nodeEntropy >= entropy) {
						maxEntropyNode = node;
						entropy = nodeEntropy;
					}
				}
				if (maxEntropyNode != null) {
					String message = "Node: " + maxEntropyNode.getName();
					message += "\nEntropy: " + entropy;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxEntropyNode));
				}
			}
		});
		mnMetsics.add(mntmMaximunEntropy);
		
		JMenu mnMaximumDegree = new JMenu("Maximum Degree");
		mnMetsics.add(mnMaximumDegree);
		
		JMenuItem mntmTotal_1 = new JMenuItem("Total");
		mntmTotal_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int degree = 0;
				VisualNode maxDegreeNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					int nodeDegree = HecataeusMetricManager.degree(node);
					if (nodeDegree >= degree) {
						maxDegreeNode = node;
						degree = nodeDegree;
					}
				}
				if (maxDegreeNode != null) {
					String message = "Node: " + maxDegreeNode.getName();
					message += "\nDegree: " + degree;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxDegreeNode));
				}
			}
		});
		mnMaximumDegree.add(mntmTotal_1);
		
		JMenuItem mntmIn = new JMenuItem("In");
		mntmIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int degree = 0;
				VisualNode maxDegreeNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					int nodeDegree = HecataeusMetricManager.inDegree(node);
					if (nodeDegree >= degree) {
						maxDegreeNode = node;
						degree = nodeDegree;
					}
				}
				if (maxDegreeNode != null) {
					String message = "Node: " + maxDegreeNode.getName();
					message += "\nIn Degree: " + degree;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxDegreeNode));
				}
			}
		});
		mnMaximumDegree.add(mntmIn);
		
		JMenuItem mntmOut = new JMenuItem("Out");
		mntmOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int degree = 0;
				VisualNode maxDegreeNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					int nodeDegree = HecataeusMetricManager.outDegree(node);
					if (nodeDegree >= degree) {
						maxDegreeNode = node;
						degree = nodeDegree;
					}
				}
				if (maxDegreeNode != null) {
					String message = "Node: " + maxDegreeNode.getName();
					message += "\nOut Degree: " + degree;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxDegreeNode));
				}
			}
		});
		mnMaximumDegree.add(mntmOut);
		
		JMenu mnMaximumStrength = new JMenu("Maximum Strength");
		mnMetsics.add(mnMaximumStrength);
		
		JMenuItem mntmTotal_2 = new JMenuItem("Total");
		mntmTotal_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int strength = 0;
				VisualNode maxStrengthNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					int nodeStrength = HecataeusMetricManager.strength(node,graph);
					if (nodeStrength >= strength) {
						maxStrengthNode = node;
						strength = nodeStrength;
					}
				}

				if (maxStrengthNode != null) {
					String message = "Node: " + maxStrengthNode.getName();
					message += "\nDegree: " + strength;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxStrengthNode));
				}
			}
		});
		mnMaximumStrength.add(mntmTotal_2);
		
		JMenuItem mntmIn_1 = new JMenuItem("In");
		mntmIn_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int strength = 0;
				VisualNode maxStrengthNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					int nodeStrength = HecataeusMetricManager.inStrength(node, graph);
					if (nodeStrength >= strength) {
						maxStrengthNode = node;
						strength = nodeStrength;
					}
				}

				if (maxStrengthNode != null) {
					String message = "Node: " + maxStrengthNode.getName();
					message += "\nDegree: " + strength;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxStrengthNode));
				}
			}
		});
		mnMaximumStrength.add(mntmIn_1);
		
		JMenuItem mntmOut_1 = new JMenuItem("Out");
		mntmOut_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int strength = 0;
				VisualNode maxStrengthNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					int nodeStrength = HecataeusMetricManager.outStrength(node, graph);
					if (nodeStrength >= strength) {
						maxStrengthNode = node;
						strength = nodeStrength;
					}
				}
				if (maxStrengthNode != null) {
					String message = "Node: " + maxStrengthNode.getName();
					message += "\nDegree: " + strength;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxStrengthNode));
				}
			}
		});
		mnMaximumStrength.add(mntmOut_1);
		
		JMenu mnMaximumWeightedDegree = new JMenu("Maximum Weighted Degree");
		mnMetsics.add(mnMaximumWeightedDegree);
		
		JMenuItem mntmIn_2 = new JMenuItem("In");
		mntmIn_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int maxwDegree = 0;
				VisualNode maxWDegreeNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					if (node.getFrequency() >= maxwDegree) {
						maxWDegreeNode = node;
						maxwDegree = node.getFrequency();
					}
				}
				if (maxWDegreeNode != null) {
					String message = "Node: " + maxWDegreeNode.getName();
					message += "\nWeighted Degree: " + maxwDegree;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxWDegreeNode));
				}
			}
		});
		mnMaximumWeightedDegree.add(mntmIn_2);
		
		JMenu mnMaximumTrasitiveDegree = new JMenu("Maximum Trasitive Degree");
		mnMetsics.add(mnMaximumTrasitiveDegree);
		
		JMenuItem mntmTotal_3 = new JMenuItem("Total");
		mntmTotal_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int maxTDegree = 0;
				VisualNode maxTDegreeNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					int nodeDegree = HecataeusMetricManager.inTransitiveDegree(node)
							+ HecataeusMetricManager.outTransitiveDegree(node);
					if (nodeDegree >= maxTDegree) {
						maxTDegreeNode = node;
						maxTDegree = nodeDegree;
					}
				}
				if (maxTDegreeNode != null) {
					String message = "Node: " + maxTDegreeNode.getName();
					message += "\nDegree: " + maxTDegree;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxTDegreeNode));
				}
			}
		});
		mnMaximumTrasitiveDegree.add(mntmTotal_3);
		
		JMenuItem mntmIn_3 = new JMenuItem("In");
		mntmIn_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int maxTDegree = 0;
				VisualNode maxTDegreeNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					int nodeDegree = HecataeusMetricManager.inTransitiveDegree(node);
					if (nodeDegree >= maxTDegree) {
						maxTDegreeNode = node;
						maxTDegree = nodeDegree;
					}
				}

				if (maxTDegreeNode != null) {
					String message = "Node: " + maxTDegreeNode.getName();
					message += "\nDegree: " + maxTDegree;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxTDegreeNode));
				}
			}
		});
		mnMaximumTrasitiveDegree.add(mntmIn_3);
		
		JMenuItem mntmOut_2 = new JMenuItem("Out");
		mntmOut_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final VisualGraph activeGraph=(VisualGraph) HecataeusViewer.getActiveViewer().getGraphLayout().getGraph();
				int maxTDegree = 0;
				VisualNode maxTDegreeNode = null;
				for (VisualNode node: activeGraph.getVertices()) {
					int nodeDegree = HecataeusMetricManager.outTransitiveDegree(node);
					if (nodeDegree >= maxTDegree) {
						maxTDegreeNode = node;
						maxTDegree = nodeDegree;
					}
				}

				if (maxTDegreeNode != null) {
					String message = "Node: " + maxTDegreeNode.getName();
					message += "\nDegree: " + maxTDegree;
					JOptionPane.showMessageDialog(content, message);
					final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
					centerAt(activeViewer.getGraphLayout().transform(maxTDegreeNode));
				}
			}
		});
		mnMaximumTrasitiveDegree.add(mntmOut_2);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmContents = new JMenuItem("Contents");
		mntmContents.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/** instructions which appear when ask for Help */
				final HecataeusMessageDialog p = new HecataeusMessageDialog(frame, "Contents", "resources/briefhelp.html", HecataeusMessageDialog.HTML_FILE);
			}
		});
		mnHelp.add(mntmContents);
		
		JMenuItem mntmColorIndex = new JMenuItem("Color Index");
		mntmColorIndex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/** instructions which appear when ask for Color Inedx */
				final HecataeusMessageDialog p = new HecataeusMessageDialog(frame, "Color Index", "resources/colorindex.html", HecataeusMessageDialog.HTML_FILE);
			}
		});
		mnHelp.add(mntmColorIndex);
		
		JMenuItem mntmImportExternalPolicy = new JMenuItem("Import External Policy File");
		mntmImportExternalPolicy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/** instructions which appear when ask for Import external policy file */
				final HecataeusMessageDialog p = new HecataeusMessageDialog(frame, "Instructions for importing policies from file", "resources/ImportPolicyFile.html", HecataeusMessageDialog.HTML_FILE);
			}
		});
		mnHelp.add(mntmImportExternalPolicy);
		
		mnHelp.addSeparator();
		
		JMenuItem mntmAboutHecataeus = new JMenuItem("About Hecataeus");
		mntmAboutHecataeus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/** instructions which appear when ask for Credits */
				final HecataeusMessageDialog p = new HecataeusMessageDialog(frame, "Credits", "resources/credits.html", HecataeusMessageDialog.HTML_FILE);
			}
		});
		mnHelp.add(mntmAboutHecataeus);

		managerTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(BorderFactory.createTitledBorder("Visual"));
		tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				sourceTabbedPane = (JTabbedPane) arg0.getSource();
				sourceTabbedPaneIndex = sourceTabbedPane.getSelectedIndex();
			}
		});
		JPanel panel_1 = new JPanel();
		panel_1.add(new JLabel("In this area you may zoom to modules to see their structure, and see the impact of a change you want to perform."));
		tabbedPane.addTab("Zoom", null, panel_1, null);
		
		summaryGraphTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		summaryGraphTabbedPane.setBorder(BorderFactory.createTitledBorder("Static"));
		summaryGraphTabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		summaryGraphTabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				summaryGraphSourceTabbedPane = (JTabbedPane) arg0.getSource();
				summaryGraphSourceTabbedPaneIndex = summaryGraphSourceTabbedPane.getSelectedIndex();
			}
		});
		JPanel summaryGraphPanel = new JPanel();
		summaryGraphTabbedPane.addTab("Summary Graph", null, summaryGraphPanel, null);
		
		policyManagerGui = new HecataeusPolicyManagerGUI(projectConf,this);
		eventManagerGui = new HecataeusEventManagerGUI(this);
		
		// Left "summary & use case" part
		JSplitPane leftSplitPane = new JSplitPane();
		leftSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		managerTabbedPane.addTab("Policy", null, policyManagerGui, null);
		managerTabbedPane.addTab("Event", null, eventManagerGui, null);
		leftSplitPane.setResizeWeight(1.);
		leftSplitPane.setTopComponent(summaryGraphTabbedPane);
		leftSplitPane.setBottomComponent(managerTabbedPane);
		leftSplitPane.setMinimumSize(new Dimension(480, this.getHeight()));
		leftSplitPane.setPreferredSize(new Dimension(480, this.getHeight()));
		leftSplitPane.setMaximumSize(new Dimension(480, this.getHeight()));

		// Right "static & information" part
		informationPanel=new JPanel();
		informationPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        informationAreaLabel = new JLabel("Information Area"); 
        informationPanel.add(informationAreaLabel, gbc);
		informationArea = new JTextArea();
		JScrollPane informationScrollArea = new JScrollPane (informationArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		informationArea.setSize(informationArea.getMaximumSize());
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx=1.;
		gbc.weighty=2.;
		gbc.fill=GridBagConstraints.BOTH;
		informationPanel.add(informationScrollArea, gbc);
		JPanel fileSystemPanel = new JPanel();
		fileSystemPanel.setBorder(BorderFactory.createTitledBorder("File system"));
		filesTreeGui = new HecataeusFileStructureGUI(this);
		fileSystemPanel.add(filesTreeGui);
		filesTreeGui.setSize(fileSystemPanel.getSize());
		gbc.gridx = 0;
        gbc.gridy = 0;
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new GridBagLayout());
		rightPanel.add(fileSystemPanel, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx=1.;
		gbc.weighty=1.;
		gbc.fill=GridBagConstraints.BOTH;
		rightPanel.add(informationPanel, gbc);
		rightPanel.setMinimumSize(new Dimension(350, this.getHeight()));
		rightPanel.setPreferredSize(new Dimension(350, this.getHeight()));
		rightPanel.setMaximumSize(new Dimension(350, this.getHeight()));
		
		// Center "zoom" part
		JSplitPane centerSplitPane;
		centerSplitPane = new JSplitPane();
		centerSplitPane.setLeftComponent(leftSplitPane);
		centerSplitPane.setRightComponent(tabbedPane);
		centerSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		centerSplitPane.setResizeWeight(0);
		
		// All together
		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane, "cell 0 0,growy");
		splitPane.setOneTouchExpandable(false);
		splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
		hecMap = new HecataeusClusterMap(this);
		splitPane.setLeftComponent(centerSplitPane);
		splitPane.setRightComponent(rightPanel);
		splitPane.setResizeWeight(1);
		}
	
	/**
	 * Class for filtering files sql and DDL files in File chooser 
	 * @author gpapas
	 *
	 */
	private class FileFilterImpl extends FileFilter {

		private Hashtable<String, FileFilterImpl> filters = null;
		private String description = null;
		private String fullDescription = null;
		private boolean useExtensionsInDescription = true;

		/**
		 * Creates a file filter. If no filters are added, then all files are
		 * accepted.
		 * 
		 * @see #addExtension
		 */
		public FileFilterImpl() {
			this.filters = new Hashtable<String, FileFilterImpl>();
		}

		/**
		 * Creates a file filter that accepts files with the given extension.
		 * Example: new ExampleFileFilter("jpg");
		 * 
		 * @see #addExtension
		 */
		public FileFilterImpl(String extension) {
			this(extension, null);
		}

		/**
		 * Creates a file filter that accepts the given file type. Example: new
		 * ExampleFileFilter("jpg", "JPEG Image Images");
		 * 
		 * Note that the "." before the extension is not needed. If provided, it
		 * will be ignored.
		 * 
		 * @see #addExtension
		 */
		public FileFilterImpl(String extension, String description) {
			this();
			if (extension != null)
				addExtension(extension);
			if (description != null)
				setDescription(description);
		}

		/**
		 * Creates a file filter from the given string array. Example: new
		 * ExampleFileFilter(String {"gif", "jpg"});
		 * 
		 * Note that the "." before the extension is not needed adn will be
		 * ignored.
		 * 
		 * @see #addExtension
		 */
		public FileFilterImpl(String[] filters) {
			this(filters, null);
		}

		/**
		 * Creates a file filter from the given string array and description.
		 * Example: new ExampleFileFilter(String {"gif", "jpg"}, "Gif and JPG
		 * Images");
		 * 
		 * Note that the "." before the extension is not needed and will be
		 * ignored.
		 * 
		 * @see #addExtension
		 */
		public FileFilterImpl(String[] filters, String description) {
			this();
			for (int i = 0; i < filters.length; i++) {
				// add filters one by one
				addExtension(filters[i]);
			}
			if (description != null)
				setDescription(description);
		}

		/**
		 * Return true if this file should be shown in the directory pane, false
		 * if it shouldn't.
		 * 
		 * Files that begin with "." are ignored.
		 * 
		 * @see #getExtension
		 * @see FileFilter#accepts
		 */
		public boolean accept(File f) {
			if (f != null) {
				if (f.isDirectory()) {
					return true;
				}
				String extension = getExtension(f);
				if (extension != null && filters.get(getExtension(f)) != null) {
					return true;
				}
				;
			}
			return false;
		}

		/**
		 * Return the extension portion of the file's name .
		 * 
		 * @see #getExtension
		 * @see FileFilter#accept
		 */
		public String getExtension(File f) {
			if (f != null) {
				String filename = f.getName();
				int i = filename.lastIndexOf('.');
				if (i > 0 && i < filename.length() - 1) {
					return filename.substring(i + 1).toLowerCase();
				}
				;
			}
			return null;
		}

		/**
		 * Adds a filetype "dot" extension to filter against.
		 * 
		 * For example: the following code will create a filter that filters out
		 * all files except those that end in ".jpg" and ".tif":
		 * 
		 * ExampleFileFilter filter = new ExampleFileFilter();
		 * filter.addExtension("jpg"); filter.addExtension("tif");
		 * 
		 * Note that the "." before the extension is not needed and will be
		 * ignored.
		 */
		public void addExtension(String extension) {
			if (filters == null) {
				filters = new Hashtable<String, FileFilterImpl>(5);
			}
			filters.put(extension.toLowerCase(), this);
			fullDescription = null;
		}

		/**
		 * Returns the human readable description of this filter. For example:
		 * "JPEG and GIF Image Files (*.jpg, *.gif)"
		 * 
		 */
		public String getDescription() {
			if (fullDescription == null) {
				if (description == null || isExtensionListInDescription()) {
					fullDescription = description == null ? "(" : description
							+ " (";
					// build the description from the extension list
					Enumeration<String> extensions = filters.keys();
					if (extensions != null) {
						fullDescription += "." + extensions.nextElement();
						while (extensions.hasMoreElements()) {
							fullDescription += ", ." + extensions.nextElement();
						}
					}
					fullDescription += ")";
				} else {
					fullDescription = description;
				}
			}
			return fullDescription;
		}

		/**
		 * Sets the human readable description of this filter. For example:
		 * filter.setDescription("Gif and JPG Images");
		 * 
		 */
		public void setDescription(String description) {
			this.description = description;
			fullDescription = null;
		}

		/**
		 * Determines whether the extension list (.jpg, .gif, etc) should show
		 * up in the human readable description.
		 * 
		 * Only relevent if a description was provided in the constructor or
		 * using setDescription();
		 * 
		 */
		public void setExtensionListInDescription(boolean b) {
			useExtensionsInDescription = b;
			fullDescription = null;
		}

		/**
		 * Returns whether the extension list (.jpg, .gif, etc) should show up
		 * in the human readable description.
		 * 
		 * Only relevent if a description was provided in the constructor or
		 * using setDescription();
		 */
		public boolean isExtensionListInDescription() {
			return useExtensionsInDescription;
		}
	}
	
	/**
	 * copy the visible part of the graph to a file as a jpeg image
	 * 
	 * @param file
	 */
	private void writeJPEGImage(File file) {
		//TODO: FIX THIS
		final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewerZOOM();
		int width = activeViewer.getWidth();
		int height = activeViewer.getHeight();

		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bi.createGraphics();
		activeViewer.paint(graphics);
		graphics.dispose();

		try {
			ImageIO.write(bi, "jpeg", file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private void writeHtmlFile(File file, String message){
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(message);
			bw.close();
			System.out.println("Done writing");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void zoomToWindow(VisualizationViewer<VisualNode, VisualEdge> currentViewer, JTabbedPane jtp){
		final VisualizationViewer<VisualNode, VisualEdge> activeViewer;
		activeViewer = currentViewer;
		Shape r = activeViewer.getBounds();
		Point2D vvcenter = activeViewer.getCenter();
		for (VisualNode jungNode: activeViewer.getGraphLayout().getGraph().getVertices()) {
			if (jungNode.getVisible()) {
				Point2D p = activeViewer.getRenderContext().getMultiLayerTransformer().transform(activeViewer.getGraphLayout().transform(jungNode));
				while (!r.contains(p)) 
					{
					scaler.scale(activeViewer, 1 / 1.1f, vvcenter);
					p = activeViewer.getRenderContext().getMultiLayerTransformer().transform(activeViewer.getGraphLayout().transform(jungNode));
					try {
						Thread.sleep(20);
					} catch (InterruptedException ex) {
					}
				}
			}
		}
		if(jtp==null)
		{
			summaryGraphSourceTabbedPane.setComponentAt(summaryGraphSourceTabbedPaneIndex, new GraphZoomScrollPane(this.getActiveViewer()));
			sourceTabbedPane.setComponentAt(sourceTabbedPaneIndex, new GraphZoomScrollPane(this.getActiveViewerZOOM()));
		}
		else
		{
			jtp.setComponentAt(getActiveTab(jtp),new GraphZoomScrollPane(activeViewer));
		}
	}
	
	/**
	 * sets the layout of the graph 
	 */
	protected void setLayout(VisualLayoutType topLayoutType, VisualLayoutType subLayoutType) {
		final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.this.getActiveViewer();
		final VisualLayoutType layoutType = VisualLayoutType.ConcentricCircleLayout;
		//create the logical graph with only module, schema and semantics nodes
		List<VisualNode> logicalNodes= graph.getVertices(NodeCategory.MODULE);
		logicalNodes.addAll(graph.getVertices(NodeCategory.SCHEMA));
		logicalNodes.addAll(graph.getVertices(NodeCategory.SEMANTICS));	
/**
 * @author pmanousi	
 */
		logicalNodes.addAll(graph.getVertices(NodeCategory.INOUTSCHEMA));
		VisualGraph logicalGraph = graph.toGraph(logicalNodes);
		// pass the graph to the layout 
		layout.setGraph(logicalGraph);
		//create the top layout graph
		/**
		 * @author evakont	
		 * the intial layout is ConcentricCircleLayout
		 */		
		getLayout(activeViewer).setTopLayoutType(layoutType);
		HecataeusViewer.this.getLayoutPositions();
		centerAt(((VisualGraph)activeViewer.getGraphLayout().getGraph()).getCenter());
		//TODO: FIX THIS
		zoomToWindow(activeViewer,null);
		VisualNodeVisible showAll = (VisualNodeVisible)  activeViewer.getRenderContext().getVertexIncludePredicate();
		showAll.setVisibleLevel(graph.getVertices(),VisibleLayer.MODULE);
	}
	
	/**
	 * It passes the locations of the layout to the graph vertices
	 */
	protected void getLayoutPositions() {
		VisualGraph gr = (VisualGraph) layout.getGraph();
		for (VisualNode node:gr.getVertices()) {
			gr.setLocation(node, layout.transform(node));
		}
	}

	/**
	 * It passes the locations of the vertices to the layout of the graph 
	 */
	protected void setLayoutPositions() {
		// get layout graph 
		VisualGraph gr = (VisualGraph) layout.getGraph();
		// first update location of top - level node
		for (VisualNode node: layout.getDelegate().getGraph().getVertices()) {
			layout.setLocation(node, gr.getLocation(node));
		}
		// then update locations of all other nodes
		for (VisualNode node: gr.getVertices()) {
			layout.setLocation(node, gr.getLocation(node));
		}
		 
	}
	
	/**
	 * Moves and centers the graph on the given point
	 * 
	 * @param Point to center at
	 */
	protected void centerAt(Point2D layoutPoint) {
		final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.getActiveViewer();
		activeViewer.getRenderContext().getMultiLayerTransformer().setToIdentity();
		Point2D vvCenter = activeViewer.getRenderContext().getMultiLayerTransformer().inverseTransform(activeViewer.getCenter());
		final double dx = (vvCenter.getX() - layoutPoint.getX()) / 5;
		final double dy = (vvCenter.getY() - layoutPoint.getY()) / 5;

		Runnable animator = new Runnable() {
			public void run() {
				for (int i = 0; i < 5; i++) {
					activeViewer.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dx, dy);
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
					}
				}
			}
		};
		Thread thread = new Thread(animator);
		thread.start();
		//TODO: FIX THIS
		final VisualizationViewer<VisualNode, VisualEdge> activeViewerZOOM = HecataeusViewer.getActiveViewerZOOM();
		activeViewerZOOM.getRenderContext().getMultiLayerTransformer().setToIdentity();
		Point2D vvCenterZOOM = activeViewerZOOM.getRenderContext().getMultiLayerTransformer().inverseTransform(activeViewerZOOM.getCenter());
		final double dxZOOM = (vvCenterZOOM.getX() - layoutPoint.getX()) / 5;
		final double dyZOOM = (vvCenterZOOM.getY() - layoutPoint.getY()) / 5;

		Runnable animatorZOOM = new Runnable() {
			public void run() {
				for (int i = 0; i < 5; i++) {
					activeViewerZOOM.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).translate(dxZOOM, dyZOOM);
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
					}
				}
			}
		};
		Thread threadZOOM = new Thread(animatorZOOM);
		threadZOOM.start();
	}
	
	public static VisualizationViewer<VisualNode, VisualEdge> getActiveViewerZOOM(){
		String tabName = sourceTabbedPane.getTitleAt(0);
		if(viewers.size()>0){
			for(VisualizationViewer<VisualNode, VisualEdge> vr : viewers){
				if(vr.getName().equals(tabName)){
					return vr;
				}
			}
		}
		else{
			return vv;
		}
		return null;
	}
	
	public static VisualizationViewer<VisualNode, VisualEdge> getActiveViewer(){
		String tabName = summaryGraphSourceTabbedPane.getTitleAt(0);
		if(viewers.size()>0){
			for(VisualizationViewer<VisualNode, VisualEdge> vr : viewers){
				if(vr.getName().equals(tabName)){
					return vr;
				}
			}
		}
		else{
			return vv;
		}
		return null;
	}
	
	public static VisualizationViewer<VisualNode, VisualEdge> getArchitectureGraphActiveViewer(){
		if(viewers.size()>0){
			for(VisualizationViewer<VisualNode, VisualEdge> vr : viewers){
				if(vr.getName().equals("Summary Graph")){ 
					return vr;
				}
			}
		}
		else{
			return null;
		}
		return null;
	}
	
	public static int getActiveTab(JTabbedPane jtp){
		if(jtp==sourceTabbedPane)
			return sourceTabbedPaneIndex;
		else
			return summaryGraphSourceTabbedPaneIndex;
	}
	
	public VisualAggregateLayout getLayout(VisualizationViewer<VisualNode, VisualEdge> activeViewer){
		if(activeViewer.getName().compareTo("Summary Graph") == 0){
			return layout;
		}else{
			return subLayout;
		}
	}
	
	private void showAffected()
	{
		List<VisualNode> modulesWithStatus=new ArrayList<VisualNode>();
		for(VisualNode v : graph.getVertices(NodeCategory.MODULE))
		{
			if(v.getStatus()!=StatusType.NO_STATUS)
			{
				modulesWithStatus.addAll(graph.getModule(v));
			}
		}
		List<VisualNode> toRM=new ArrayList<VisualNode>();
		for(VisualNode v:modulesWithStatus)
		{
			if(v.getStatus()==StatusType.NO_STATUS)
			{
				toRM.add(v);
			}
		}
		for(VisualNode v : toRM)
		{
			modulesWithStatus.remove(v);
		}
		VisualGraph GV = new VisualGraph(graph.toGraph(modulesWithStatus));
		HecataeusViewer.myViewer.zoomToModuleTab(modulesWithStatus, GV, "Impact analysis");
	}

	protected void zoomToModuleTab(List<VisualNode> subNodes, VisualGraph sub, String name){	
		this.graphs.add(sub);
		subLayout = new VisualAggregateLayout(sub, VisualLayoutType.ZoomedLayoutForModules, VisualLayoutType.ZoomedLayoutForModules);
		vv1 = VisualizationViewer.SetViewers(subLayout, this);
		GraphZoomScrollPane myPane = new GraphZoomScrollPane(vv1);
		vv1.setGraphLayout(subLayout);
		vv1.setName(name);
		viewers.add(vv1);
		tabbedPane.addTab(name, null, myPane, name);
		countOpenTabs++;		
		tabbedPane.setSelectedIndex(countOpenTabs);
		tabbedPane.setTabComponentAt(countOpenTabs,new HecataeusButtonTabComponent(tabbedPane));
		vv1.repaint();
		this.zoomToWindow(vv1,tabbedPane);
	}
	
	public static JFrame getHecFrame(){
		return frame;
	}
	
	
	public List<VisualGraph> getGraphs(){
		return this.graphs;
	}

	/**
	 * Class for choosing a node of the graph
	 * @author gpapas
	 *
	 */
	private final class NodeChooser extends JDialog {
		protected JComboBox comboBoxNode;
		protected JComboBox comboBoxChildNode;
		protected JComboBox comboBoxEvent;
		protected JButton okButton;
		final int mode = NodeChooser.FOR_EVENT;

		static final int FOR_EVENT = 1;
		static final int FOR_POLICY = 2;

		public NodeChooser(final int mode) {
			super(frame, "Select a node", true);
			setSize(440, 160);
			setLocationRelativeTo(frame);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			GridBagLayout gridbag = new GridBagLayout();
			HecataeusGridBagConstraints constraints = new HecataeusGridBagConstraints();
			JPanel pane = new JPanel();
			pane.setLayout(gridbag);

			// label for top level node
			constraints.reset(0, 0, 1, 1, 0, 40);
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.EAST;
			JLabel labelNode = new JLabel("Choose a top level node: ",
					JLabel.LEFT);
			gridbag.setConstraints(labelNode, constraints);
			pane.add(labelNode);

			// combo box for top level node
			comboBoxNode = new JComboBox();
			constraints.reset(1, 0, 2, 1, 0, 0);
			constraints.fill = GridBagConstraints.HORIZONTAL;
			gridbag.setConstraints(comboBoxNode, constraints);
			comboBoxNode.addItem(null);
			for (VisualNode node: graph.getVertices()) {
				 if (node.getType().getCategory() == NodeCategory.MODULE){
					comboBoxNode.addItem(node);
				}
			}
			// add action listener to fill childcombo
			comboBoxNode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// get parent node
					VisualNode parentNode= (VisualNode) comboBoxNode.getSelectedItem();
					// reset child combo
					comboBoxChildNode.removeAllItems();
					if (parentNode != null) {
						// get descendant nodes
						for (VisualNode node: graph.getModule(parentNode)) {
							comboBoxChildNode.addItem(node);
						}
					}
				}
			});

			pane.add(comboBoxNode);

			// label for low level node
			constraints.reset(0, 1, 1, 1, 0, 40);
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.EAST;
			labelNode = new JLabel("Choose a child node: ", JLabel.LEFT);
			gridbag.setConstraints(labelNode, constraints);
			pane.add(labelNode);

			// combo box for low level node
			constraints.reset(1, 1, 2, 1, 0, 0);
			constraints.fill = GridBagConstraints.HORIZONTAL;
			comboBoxChildNode = new JComboBox();
			gridbag.setConstraints(comboBoxChildNode, constraints);
			pane.add(comboBoxChildNode);

			// OK button
			constraints.reset(1, 3, 1, 1, 100, 20);
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.CENTER;
			okButton = new JButton("OK");
			gridbag.setConstraints(okButton, constraints);
			// create the selected event on the selected node
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// get key for node
					VisualNode node = (VisualNode) comboBoxChildNode.getSelectedItem();
					if (node!= null) {
						final VisualizationViewer<VisualNode, VisualEdge> activeViewer = HecataeusViewer.this.getActiveViewer();
						if (mode == NodeChooser.FOR_EVENT) {
							HecataeusNodeEvents nde = new HecataeusNodeEvents(
									activeViewer, node);
						}
						if (mode == NodeChooser.FOR_POLICY) {
							HecataeusNodePolicies ndp = new HecataeusNodePolicies(
									activeViewer, node);
						}
						//TODO: FIX THIS
						final VisualizationViewer<VisualNode, VisualEdge> activeViewerZOOM = HecataeusViewer.this.getActiveViewerZOOM();
						if (mode == NodeChooser.FOR_EVENT) {
							HecataeusNodeEvents nde = new HecataeusNodeEvents(activeViewerZOOM, node);
						}
						if (mode == NodeChooser.FOR_POLICY) {
							HecataeusNodePolicies ndp = new HecataeusNodePolicies(activeViewerZOOM, node);
						}
						dispose();
					}
				}
			});// add actionListener for okButton
			pane.add(okButton);

			// Cancel button
			constraints.reset(2, 3, 1, 1, 100, 20);
			constraints.fill = GridBagConstraints.NONE;
			constraints.anchor = GridBagConstraints.WEST;
			JButton cancelButton = new JButton("Cancel");
			gridbag.setConstraints(cancelButton, constraints);
			cancelButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			pane.add(cancelButton);

			setContentPane(pane);
			setVisible(true);
		}

	}
}
