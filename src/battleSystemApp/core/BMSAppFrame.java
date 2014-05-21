package battleSystemApp.core;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.symbology.BasicTacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.TacticalSymbol;
import gov.nasa.worldwind.symbology.TacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;
import gov.nasa.worldwind.util.WWUtil;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Authenticator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import si.xlab.gaea.avlist.AvKeyExt;
import si.xlab.gaea.core.layers.RenderToTextureLayer;
import si.xlab.gaea.core.layers.elev.SlopeLayer;
import battleSystemApp.components.InterfaceLayer;
import battleSystemApp.components.InterfaceLayerSelectListener;
import battleSystemApp.components.RoundedPanel;
import battleSystemApp.dds.DDSCommLayer;
import battleSystemApp.dds.DDSListener;
import battleSystemApp.dds.idl.Msg;
import battleSystemApp.dds.listeners.DDSDragger;
import battleSystemApp.utils.ConfigurationManager;
import battleSystemApp.utils.ProxyAuthenticator;


/**
 * Aplicación de posicionamiento de unidades militares basada en GAEA+
 * 
 * @author vgonllo
 * 
 */
public class BMSAppFrame extends ApplicationTemplate {

	

	
	private static class MessageItem extends JMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6016671095079453916L;
		private final String message;

		public MessageItem(String message, String caption) {
			this.message = message;
			setAction(new AbstractAction(caption) {
				/**
				 * 
				 */
				private static final long serialVersionUID = -4750875727415359231L;

				@Override
				public void actionPerformed(ActionEvent e) {
					showMessage();
				}
			});
		}

		public void showMessage() {
			JOptionPane.showMessageDialog(null, message);
		}
	}

	

	public static class GaeaAppFrame extends AppFrame implements DDSListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6059677693262885004L;
		protected RenderableLayer symbolLayer;
		protected TacticalSymbolAttributes sharedAttrs;
		protected TacticalSymbolAttributes sharedHighlightAttrs;
		protected DDSDragger dragger;
		protected ConfigurationManager confManager;
		protected DDSCommLayer dds;
		private RoundedPanel configPanel;

		/**
		 * 
		 */
		public GaeaAppFrame() {

			confManager = new ConfigurationManager();
			// Autenticamos la app contra el proxy
			Authenticator.setDefault(new ProxyAuthenticator(confManager
					.getProperty(confManager.PROXY_USERNAME), confManager
					.getProperty(confManager.PROXY_PASSWORD)));
			System.getProperties().put("http.proxyHost", "10.7.180.112");
			System.getProperties().put("http.proxyPort", "80");
			System.getProperties().put("https.proxyHost", "10.7.180.112");
			System.getProperties().put("https.proxyPort", "80");
			
//			this.getWwd().getSceneController().firePropertyChange(AvKeyExt.ENABLE_SUNLIGHT, false, true);
//			this.getWwd().getSceneController().firePropertyChange(AvKeyExt.ENABLE_ATMOSPHERE, false, true);
//			this.getWwd().getSceneController().firePropertyChange(AvKeyExt.ENABLE_ATMOSPHERE_WITH_AERIAL_PERSPECTIVE, false, true);
//			this.getWwd().getSceneController().firePropertyChange(AvKeyExt.ENABLE_SHADOWS, false, true);
			this.getWwd().getSceneController().firePropertyChange(AvKeyExt.ENABLE_POS_EFFECTS, false, true);

			//insertBeforePlacenames(this.getWwd(), new SlopeLayer());
			// Capa de comunicaciones DDS
			dds = new DDSCommLayer();
			this.dds.addListener(this);
			
			this.symbolLayer = new RenderableLayer();
			this.symbolLayer.setName("Simbolos Tacticos");
			
			// OPERACIONES DE INTERFAZ
			// Create and install the view controls layer and register a controller for it with the World Window.
            InterfaceLayer interfaceLayer = new InterfaceLayer();
            insertBeforeCompass(this.getWwd(), interfaceLayer);
            this.getWwd().addSelectListener(new InterfaceLayerSelectListener(this.getWwd(), interfaceLayer));
            
			
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
			TacticalSymbol airSymbol = new MilStd2525TacticalSymbol(
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
			this.symbolLayer.addRenderable(airSymbol);

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
			TacticalSymbol groundSymbol = new MilStd2525TacticalSymbol(
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
			TacticalSymbol machineGunSymbol = new MilStd2525TacticalSymbol(
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
			machineGunSymbol.setModifier(SymbologyConstants.DATE_TIME_GROUP,
					"30140000ZSEP97");
			this.symbolLayer.addRenderable(machineGunSymbol);

			// Add a dragging controller to enable user click-and-drag control
			// over tactical symbols.
			this.dragger = new DDSDragger(this.getWwd(), true, dds);
			this.getWwd().addSelectListener(this.dragger);
			
			// Create a Swing control panel that provides user control over the
			// symbol's appearance.
			//this.addSymbolControls();

			// Add the symbol layer to the World Wind model.
			this.getWwd().getModel().getLayers().add(symbolLayer);
			
			 // Update the layer panel to display the symbol layer.
            this.getLayerPanel().update(this.getWwd());
            
            Dimension size = new Dimension(1800, 1000);
            this.setPreferredSize(size);
            this.pack();
            WWUtil.alignComponent(null, this, AVKey.CENTER);
            
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
		protected void addInMapControls(){
			
		}
		/**
		 * Implementación de la interfaz DDSListener.
		 * Llamado cuando llega un mensaje
		 */
		@Override
		public void receivedMessage(Msg message) {
			Logger.getLogger(DDSCommLayer.class.getName()).log(
					Level.INFO,
					"Recibido mensaje DDS -" + message.unitID + "- Lat: "
							+ message.lat + " Lon: " + message.lon + " Alt: "
							+ message.alt);
			for (Renderable r : symbolLayer.getRenderables()) {
				TacticalSymbol C2Symbol = (TacticalSymbol) r;
				if (C2Symbol.getIdentifier().equals(message.unitID)) {
					// Set new symbol position
					C2Symbol.setPosition(Position.fromDegrees(message.lat,
							message.lon, message.alt));
				}
			}
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
		  // Configure the initial view parameters so that this example starts looking at the symbols.
        Configuration.setValue(AVKey.INITIAL_LATITUDE, 32.49);
        Configuration.setValue(AVKey.INITIAL_LONGITUDE, 63.455);
        Configuration.setValue(AVKey.INITIAL_HEADING, 22);
        Configuration.setValue(AVKey.INITIAL_PITCH, 82);
        Configuration.setValue(AVKey.INITIAL_ALTITUDE, 20000);

//        start("World Wind Tactical Symbols", GaeaAppFrame.class);
//		Configuration
//				.insertConfigurationDocument("si/xlab/gaea/examples/gaea-example-config.xml");
		appFrame = (GaeaAppFrame) start(
				"Gaea+ Open Source Example Application", GaeaAppFrame.class);
		insertBeforeCompass(appFrame.getWwd(),
				RenderToTextureLayer.getInstance());
	
	}

}
