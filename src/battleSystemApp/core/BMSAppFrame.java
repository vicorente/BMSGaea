package battleSystemApp.core;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Offset;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.symbology.AbstractTacticalSymbol;
import gov.nasa.worldwind.symbology.BasicTacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.TacticalSymbol;
import gov.nasa.worldwind.symbology.TacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwind.util.dashboard.DashboardController;
import gov.nasa.worldwind.util.layertree.LayerTree;
import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwindx.examples.util.HotSpotController;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Authenticator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import si.xlab.gaea.avlist.AvKeyExt;
import si.xlab.gaea.core.layers.RenderToTextureLayer;
import battleSystemApp.components.ContextMenuInfo;
import battleSystemApp.components.ContextMenuItemInfo;
import battleSystemApp.components.InterfaceLayer;
import battleSystemApp.components.InterfaceLayerSelectListener;
import battleSystemApp.components.RoundedPanel;
import battleSystemApp.components.TacticalSymbolContextMenu;
import battleSystemApp.dds.DDSCommLayer;
import battleSystemApp.dds.DDSListener;
import battleSystemApp.dds.idl.Msg;
import battleSystemApp.dds.listeners.DDSDragger;
import battleSystemApp.utils.ConfigurationManager;
import battleSystemApp.utils.ProxyAuthenticator;
import battleSystemApp.views.ViewController;

/**
 * Aplicación de posicionamiento de unidades militares basada en GAEA+
 * 
 * @author vgonllo
 * 
 */
public class BMSAppFrame extends ApplicationTemplate {

	public static class GaeaAppFrame extends AppFrame implements DDSListener,
			SymbolListener {
		/**
		 * 
		 */
		static
	    {
	        // The following is required to use Swing menus with the heavyweight canvas used by World Wind.
	        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
	        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
	    }
		private static final long serialVersionUID = 6059677693262885004L;
		protected RenderableLayer symbolLayer;
		protected TacticalSymbolAttributes sharedAttrs;
		protected TacticalSymbolAttributes sharedHighlightAttrs;
		protected DDSDragger dragger;
		protected ConfigurationManager confManager;
		protected DDSCommLayer dds;
		protected RoundedPanel configPanel;
		protected ArrayList<AbstractTacticalSymbol> objectsToTrack;
		protected ViewController viewController;
		protected LayerTree layerTree;
		protected RenderableLayer hiddenLayer;
		protected HotSpotController hotSpotController;
		/**
		 * 
		 */
		public GaeaAppFrame() {

			super(true, false, false);

			
			this.layerTree = new LayerTree(new Offset(20d, 160d, AVKey.PIXELS,
					AVKey.INSET_PIXELS));
			this.layerTree.getModel().refresh(
					this.getWwd().getModel().getLayers());

			
			// Set up a layer to display the on-screen layer tree in the
			// WorldWindow. This layer is not displayed in
			// the layer tree's model. Doing so would enable the user to hide
			// the layer tree display with no way of
			// bringing it back.
			this.hiddenLayer = new RenderableLayer();
			this.hiddenLayer.addRenderable(this.layerTree);
			this.getWwd().getModel().getLayers().add(this.hiddenLayer);

			// Add a controller to handle input events on the layer selector and
			// on browser balloons.
			this.hotSpotController = new HotSpotController(this.getWwd());

			// elementos a seguir
			objectsToTrack = new ArrayList<AbstractTacticalSymbol>();

			confManager = new ConfigurationManager();
			// Autenticamos la app contra el proxy
			Authenticator.setDefault(new ProxyAuthenticator(confManager
					.getProperty(confManager.PROXY_USERNAME), confManager
					.getProperty(confManager.PROXY_PASSWORD)));
			System.getProperties().put("http.proxyHost", "10.7.180.112");
			System.getProperties().put("http.proxyPort", "80");
			System.getProperties().put("https.proxyHost", "10.7.180.112");
			System.getProperties().put("https.proxyPort", "80");

			// this.getWwd().getSceneController().firePropertyChange(AvKeyExt.ENABLE_SUNLIGHT,
			// false, true);
			// this.getWwd().getSceneController().firePropertyChange(AvKeyExt.ENABLE_ATMOSPHERE,
			// false, true);
			// this.getWwd().getSceneController().firePropertyChange(AvKeyExt.ENABLE_ATMOSPHERE_WITH_AERIAL_PERSPECTIVE,
			// false, true);
			// this.getWwd().getSceneController().firePropertyChange(AvKeyExt.ENABLE_SHADOWS,
			// false, true);
			this.getWwd()
					.getSceneController()
					.firePropertyChange(AvKeyExt.ENABLE_POS_EFFECTS, false,
							true);

			// insertBeforePlacenames(this.getWwd(), new SlopeLayer());
			// Capa de comunicaciones DDS
			dds = new DDSCommLayer();
			this.dds.addListener(this);

			// Add the bulk download control panel.
			// this.getLayerPanel().add(new BulkDownloadPanel(this.getWwd()),
			// BorderLayout.SOUTH);

			this.symbolLayer = new RenderableLayer();
			this.symbolLayer.setName("Simbolos Tacticos");

			// OPERACIONES DE INTERFAZ
			// Create and install the view controls layer and register a
			// controller for it with the World Window.
			InterfaceLayer interfaceLayer = new InterfaceLayer();
			insertBeforeCompass(this.getWwd(), interfaceLayer);
			this.getWwd().addSelectListener(
					new InterfaceLayerSelectListener(this.getWwd(),
							interfaceLayer));

			// Create normal and highlight attribute bundles that are shared by
			// all tactical symbols. Changes to these
			// attribute bundles are reflected in all symbols. We specify both
			// attribute bundle types in this example in
			// order to keep a symbol's scale constant when it's highlighted,
			// and change only its opacity.
			this.sharedAttrs = new BasicTacticalSymbolAttributes();
			this.sharedHighlightAttrs = new BasicTacticalSymbolAttributes();
			this.sharedHighlightAttrs.setInteriorMaterial(Material.WHITE);
			this.sharedHighlightAttrs.setOpacity(1.0);

			// Create an air tactical symbol for the MIL-STD-2525 symbology set.
			// This symbol identifier specifies a
			// MIL-STD-2525 friendly Special Operations Forces Drone Aircraft.
			// MilStd2525TacticalSymbol automatically
			// sets the altitude mode to WorldWind.ABSOLUTE. We've configured
			// this symbol's modifiers to display the
			// represented object's Echelon, Task Force Indicator, Feint/Dummy
			// Indicator, and Direction of Movement.
			// The Echelon, Task Force Indicator, and Feint/Dummy Indicator are
			// specified in characters 11-12 of the
			// symbol identifier ("GI"). The Direction of Movement is specified
			// by calling TacticalSymbol.setModifier
			// with the appropriate key and value.
			AbstractTacticalSymbol airSymbol = new MilStd2525TacticalSymbol(
					"SHAPMFQM--GIUSA", Position.fromDegrees(32.4520, 63.44553,
							3000));
			airSymbol.setValue(AVKey.DISPLAY_NAME,
					"MIL-STD-2525 Friendly SOF Drone Aircraft"); // Tool tip
																	// text.
			airSymbol.setAttributes(this.sharedAttrs);
			airSymbol.setHighlightAttributes(this.sharedHighlightAttrs);
			airSymbol.setModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT,
					Angle.fromDegrees(235));
			airSymbol.setShowLocation(false);
			ContextMenuItemInfo[] itemActionNames = new ContextMenuItemInfo[] {
					new ContextMenuItemInfo("Do This"),
					new ContextMenuItemInfo("Do That"),
					new ContextMenuItemInfo("Do the Other Thing"), };
			airSymbol.setValue(TacticalSymbolContextMenu.CONTEXT_MENU_INFO,
					new ContextMenuInfo("Placemark A", itemActionNames));
			this.symbolLayer.addRenderable(airSymbol);
			objectsToTrack.add(airSymbol);

			// Create a ground tactical symbol for the MIL-STD-2525 symbology
			// set. This symbol identifier specifies
			// multiple hostile Self-Propelled Rocket Launchers with a destroyed
			// state. MilStd2525TacticalSymbol
			// automatically sets the altitude mode to
			// WorldWind.CLAMP_TO_GROUND. We've configured this symbol's
			// modifiers to display the represented object's Operational
			// Condition, Direction of Movement and Speed
			// Leader. The Operational Condition is specified in character 4 of
			// the symbol identifier ("X"). The
			// Direction of Movement and Speed Leader are specified by calling
			// TacticalSymbol.setModifier with the
			// appropriate key and value.The Speed Leader modifier has the
			// effect of scaling the Direction of Movement's
			// line segment. In this example, we've scaled the line to 50% of
			// its original length.
			AbstractTacticalSymbol groundSymbol = new MilStd2525TacticalSymbol(
					"SHGXUCFRMS----G",
					Position.fromDegrees(32.4014, 63.3894, 0));
			groundSymbol.setValue(AVKey.DISPLAY_NAME,
					"MIL-STD-2525 Hostile Self-Propelled Rocket Launchers");
			groundSymbol.setAttributes(this.sharedAttrs);
			groundSymbol.setHighlightAttributes(this.sharedHighlightAttrs);
			groundSymbol.setModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT,
					Angle.fromDegrees(90));
			groundSymbol
					.setModifier(SymbologyConstants.SPEED_LEADER_SCALE, 0.5);
			groundSymbol.setShowLocation(false);
			this.symbolLayer.addRenderable(groundSymbol);
			objectsToTrack.add(groundSymbol);

			// Create a ground tactical symbol for the MIL-STD-2525 symbology
			// set. This symbol identifier specifies a
			// MIL-STD-2525 friendly Heavy Machine Gun that's currently
			// mobilized via rail. This symbol is taken from
			// the MIL-STD-2525C specification, section 5.9.3 (page 49). We've
			// configured this symbol's modifiers to
			// display the Mobility Indicator, and six text modifiers. The
			// Mobility Indicator is specified in characters
			// 11-12 of the symbol identifier ("MT"). The text modifiers are
			// specified by calling
			// TacticalSymbol.setModifier with the appropriate keys and values.
			AbstractTacticalSymbol machineGunSymbol = new MilStd2525TacticalSymbol(
					"SFGPEWRH--MTUSG",
					Position.fromDegrees(32.3902, 63.4161, 0));
			machineGunSymbol.setValue(AVKey.DISPLAY_NAME,
					"MIL-STD-2525 Friendly Heavy Machine Gun");
			machineGunSymbol.setAttributes(this.sharedAttrs);
			machineGunSymbol.setHighlightAttributes(this.sharedHighlightAttrs);
			machineGunSymbol.setModifier(SymbologyConstants.QUANTITY, 200);
			machineGunSymbol.setModifier(SymbologyConstants.STAFF_COMMENTS,
					"FOR REINFORCEMENTS");
			machineGunSymbol.setModifier(
					SymbologyConstants.ADDITIONAL_INFORMATION,
					"ADDED SUPPORT FOR JJ");
			machineGunSymbol
					.setModifier(SymbologyConstants.TYPE, "MACHINE GUN");
			// formato de hora del STANAG
			String newString = new SimpleDateFormat("ddHHmmss'Z'MMMYYYY")
					.format(new Date()).toUpperCase(); // 9:00
			machineGunSymbol.setModifier(SymbologyConstants.DATE_TIME_GROUP,
					newString);

			this.symbolLayer.addRenderable(machineGunSymbol);
			objectsToTrack.add(machineGunSymbol);

			this.viewController = new ViewController(this.getWwd());
			this.viewController.setObjectsToTrack(this.objectsToTrack);
			for (AbstractTacticalSymbol abs : this.objectsToTrack) {
				abs.addSymbolListener(this);
			}

			// Add a dragging controller to enable user click-and-drag control
			// over tactical symbols.
			this.dragger = new DDSDragger(this.getWwd(), true, dds);
			this.getWwd().addSelectListener(this.dragger);

			// Create a Swing control panel that provides user control over the
			// symbol's appearance.
			// this.addSymbolControls();

			// Add the symbol layer to the World Wind model.
			this.getWwd().getModel().getLayers().add(symbolLayer);

			// Update the layer panel to display the symbol layer.
			if (this.getLayerPanel() != null) {
				this.getLayerPanel().update(this.getWwd());
			}

			Dimension size = new Dimension(1800, 1000);
			this.setPreferredSize(size);
			this.pack();
			WWUtil.alignComponent(null, this, AVKey.CENTER);

			// Set up a one-shot timer to zoom to the objects once the app
			// launches.
			Timer timer = new Timer(1000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					viewController.gotoScene();
				}
			});
			timer.setRepeats(false);
			timer.start();

			// Delete resources before exit
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					dds.removeAllListeners();
					dds.closeDDSEntities();
					System.exit(0);
				}
			});

		}

		public DDSCommLayer getDDSCommLayer() {
			return this.dds;
		}

		protected void addSymbolControls() {
			Box box = Box.createVerticalBox();
			box.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

			// Create a slider that controls the scale factor of all symbols.
			JLabel label = new JLabel("Scale");
			JSlider slider = new JSlider(0, 100, 100);
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent changeEvent) {
					// Scale both the normal and the highlight attributes for
					// each symbol. This prevents the symbol
					// from suddenly appearing larger when highlighted. Changes
					// in these attributes are reflected in all
					// symbols that use them.
					JSlider slider = (JSlider) changeEvent.getSource();
					double scale = (double) slider.getValue() / 100d;
					sharedAttrs.setScale(scale);
					sharedHighlightAttrs.setScale(scale);
					getWwd().redraw(); // Cause the World Window to refresh in
										// order to make these changes visible.
				}
			});
			label.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			slider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			box.add(label);
			box.add(slider);

			// Create a slider that controls the opacity of all symbols.
			label = new JLabel("Opacity");
			slider = new JSlider(0, 100, 100);
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent changeEvent) {
					// Set the opacity for only the normal attributes. This
					// causes symbols to return to 100% opacity
					// when highlighted. Changes in these attributes are
					// reflected in all symbols that use them.
					JSlider slider = (JSlider) changeEvent.getSource();
					double opacity = (double) slider.getValue() / 100d;
					sharedAttrs.setOpacity(opacity);
					getWwd().redraw(); // Cause the World Window to refresh in
										// order to make these changes visible.
				}
			});
			box.add(Box.createVerticalStrut(10));
			label.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			slider.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			box.add(label);
			box.add(slider);

			// Create a check box that toggles the visibility of graphic
			// modifiers for all symbols.
			JCheckBox cb = new JCheckBox("Graphic Modifiers", true);
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					boolean tf = ((JCheckBox) actionEvent.getSource())
							.isSelected();

					for (Renderable r : symbolLayer.getRenderables()) {
						if (r instanceof TacticalSymbol)
							((TacticalSymbol) r).setShowGraphicModifiers(tf);
						getWwd().redraw(); // Cause the World Window to refresh
											// in order to make these changes
											// visible.
					}
				}
			});
			cb.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			box.add(Box.createVerticalStrut(10));
			box.add(cb);

			// Create a check box that toggles the visibility of text modifiers
			// for all symbols.
			cb = new JCheckBox("Text Modifiers", true);
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					boolean tf = ((JCheckBox) actionEvent.getSource())
							.isSelected();

					for (Renderable r : symbolLayer.getRenderables()) {
						if (r instanceof TacticalSymbol)
							((TacticalSymbol) r).setShowTextModifiers(tf);
						getWwd().redraw(); // Cause the World Window to refresh
											// in order to make these changes
											// visible.
					}
				}
			});
			cb.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			box.add(Box.createVerticalStrut(10));
			box.add(cb);

			// Create a check box that toggles the frame visibility for all
			// symbols.
			cb = new JCheckBox("Show Frame", true);
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					boolean tf = ((JCheckBox) actionEvent.getSource())
							.isSelected();

					for (Renderable r : symbolLayer.getRenderables()) {
						if (r instanceof TacticalSymbol)
							((MilStd2525TacticalSymbol) r).setShowFrame(tf);
						getWwd().redraw(); // Cause the World Window to refresh
											// in order to make these changes
											// visible.
					}
				}
			});
			cb.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			box.add(Box.createVerticalStrut(10));
			box.add(cb);

			// Create a check box that toggles the fill visibility for all
			// symbols.
			cb = new JCheckBox("Show Fill", true);
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					boolean tf = ((JCheckBox) actionEvent.getSource())
							.isSelected();

					for (Renderable r : symbolLayer.getRenderables()) {
						if (r instanceof TacticalSymbol)
							((MilStd2525TacticalSymbol) r).setShowFill(tf);
						getWwd().redraw(); // Cause the World Window to refresh
											// in order to make these changes
											// visible.
					}
				}
			});
			cb.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			box.add(Box.createVerticalStrut(10));
			box.add(cb);

			// Create a check box that toggles the icon visibility for all
			// symbols.
			cb = new JCheckBox("Show Icon", true);
			cb.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent actionEvent) {
					boolean tf = ((JCheckBox) actionEvent.getSource())
							.isSelected();

					for (Renderable r : symbolLayer.getRenderables()) {
						if (r instanceof TacticalSymbol)
							((MilStd2525TacticalSymbol) r).setShowIcon(tf);
						getWwd().redraw(); // Cause the World Window to refresh
											// in order to make these changes
											// visible.
					}
				}
			});
			cb.setAlignmentX(JComponent.LEFT_ALIGNMENT);
			box.add(Box.createVerticalStrut(10));
			box.add(cb);

			this.getLayerPanel().add(box, BorderLayout.SOUTH);

		}

		/**
		 * Añade los controles sobre el mapa
		 */
		protected void addInMapControls() {

		}

		/**
		 * Implementación de la interfaz DDSListener. Llamado cuando llega un
		 * mensaje
		 */
		@Override
		public void receivedMessage(Msg message) {
			Logger.getLogger(DDSCommLayer.class.getName()).log(
					Level.INFO,
					"Recibido mensaje DDS -" + message.unitID + "- Lat: "
							+ message.lat + " Lon: " + message.lon + " Alt: "
							+ message.alt);

			for (Renderable r : symbolLayer.getRenderables()) {
				AbstractTacticalSymbol C2Symbol = (AbstractTacticalSymbol) r;
				if (C2Symbol.getIdentifier().equals(message.unitID)) {
					// Set new symbol position
					C2Symbol.moveTo(Position.fromDegrees(message.lat,
							message.lon, message.alt));
					String newString = new SimpleDateFormat(
							"ddHHmmss'Z'MMMYYYY").format(new Date())
							.toUpperCase(); // 9:00
					C2Symbol.setModifier(SymbologyConstants.DATE_TIME_GROUP,
							newString);
				}
			}

		}

		/**
		 * Llamado cuando somos SymbolListener y se mueve un símbolo
		 */
		@Override
		public void moved() {
			// Unicamente realizamos el segumiento de los objetos marcados como
			// tal
			viewController.sceneChanged();
		}
	}

	private static GaeaAppFrame appFrame = null;

	public static void main(String[] args) {
		// MeasureRenderTime.enable(true);
		// MeasureRenderTime.setMesureGpu(true);
		try {
			// Fijamos el aspecto de la aplicacion para que lo obtenga igual
			// al sistema donde se ejecuta
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			Logger.getLogger(BMSAppFrame.class.getName()).log(Level.SEVERE,
					"ERROR al fijar el look and feel de la aplicación", e);
			e.printStackTrace();
		}
		// Configure the initial view parameters so that this example starts
		// looking at the symbols.
		Configuration.setValue(AVKey.INITIAL_LATITUDE, 32.49);
		Configuration.setValue(AVKey.INITIAL_LONGITUDE, 63.455);
		Configuration.setValue(AVKey.INITIAL_HEADING, 22);
		Configuration.setValue(AVKey.INITIAL_PITCH, 82);
		Configuration.setValue(AVKey.INITIAL_ALTITUDE, 20000);

		// start("World Wind Tactical Symbols", GaeaAppFrame.class);
		// Configuration
		// .insertConfigurationDocument("si/xlab/gaea/examples/gaea-example-config.xml");
		// appFrame = (GaeaAppFrame) start(
		// "BMS", GaeaAppFrame.class);

		if (Configuration.isMacOS()) {
			System.setProperty(
					"com.apple.mrj.application.apple.menu.about.name", "BMS");
		}

		try {
			appFrame = new GaeaAppFrame();
			appFrame.setTitle("BMS");
			appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					appFrame.setVisible(true);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

		insertBeforeCompass(appFrame.getWwd(),
				RenderToTextureLayer.getInstance());

	}

}
