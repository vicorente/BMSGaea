package battleSystemApp.core;

import gov.nasa.worldwind.Configuration;
import gov.nasa.worldwind.WorldWindow;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.geom.Sector;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.ogc.kml.KMLStyle;
import gov.nasa.worldwind.render.DrawContext;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.Renderable;
import gov.nasa.worldwind.symbology.BasicTacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.TacticalSymbol;
import gov.nasa.worldwind.symbology.TacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;
import gov.nasa.worldwind.util.BasicDragger;
import gov.nasa.worldwindx.examples.ApplicationTemplate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Authenticator;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import listeners.DDSDragger;
import battleSystemApp.utils.ConfigurationManager;
import battleSystemApp.utils.ProxyAuthenticator;
import si.xlab.gaea.avlist.AvKeyExt;
import si.xlab.gaea.core.event.FeatureSelectListener;
import si.xlab.gaea.core.layers.RenderToTextureLayer;
import si.xlab.gaea.core.layers.elev.ElevationLayer;
import si.xlab.gaea.core.layers.elev.SlopeLayer;
import si.xlab.gaea.core.layers.wfs.WFSGenericLayer;
import si.xlab.gaea.core.layers.wfs.WFSService;
import si.xlab.gaea.core.ogc.kml.KMLStyleFactory;
import si.xlab.gaea.examples.WfsPanel;

/**
 * Aplicación de posicionamiento de unidades militares basada en GAEA+ 
 * @author vgonllo
 * 
 */
public class BMSAppFrame extends ApplicationTemplate {

	protected static void makeMenu(final AppFrame appFrame) {
		JMenuBar menuBar = new JMenuBar();
		appFrame.setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem openWfsItem = new JMenuItem(new AbstractAction(
				"Add WFS layer...") {
			/**
			 *
			 */
			private static final long serialVersionUID = 304812140664922952L;

			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				JDialog dialog = new JDialog(appFrame, "Import WFS layer", true);
				WfsPanel wfsPanel = new WfsPanel();
				wfsPanel.setDialog(dialog);
				Dimension dimension = wfsPanel.getPreferredSize();
				dimension.setSize(dimension.getWidth() + 10,
						dimension.getHeight() + 25);
				dialog.getContentPane().add(wfsPanel);
				dialog.setSize(dimension);
				dialog.setModal(true);
				dialog.setVisible(true);

				if (wfsPanel.isConfirmed()) {
					String url = wfsPanel.getUrl();
					String name = wfsPanel.getFeatureName();
					Sector sector = wfsPanel.getSector();
					double dist = wfsPanel.getVisibleDistance();
					Angle tile = wfsPanel.getTileDelta();
					Color color = wfsPanel.getColor();
					String lineLabelTag = wfsPanel.getFeatureLableTypeName();

					try {
						addWfsLayer(url, name, sector, tile, dist * 1000,
								color, lineLabelTag);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null,
								"Error: " + e.getMessage());
					}
				}

				dialog.dispose();
			}
		});

		fileMenu.add(openWfsItem);

		JMenuItem quitItem = new JMenuItem(new AbstractAction("Quit") {
			/**
			 * 
			 */
			private static final long serialVersionUID = -813940270808953243L;

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		fileMenu.add(quitItem);

		JMenu optionsMenu = new JMenu("Shading");
		menuBar.add(optionsMenu);
		ButtonGroup optionsGroup = new ButtonGroup();

		JMenuItem wwShading = new ShadingItem(new boolean[] { true, false,
				false, false, false }, "Default World Wind");
		optionsMenu.add(wwShading);
		optionsGroup.add(wwShading);
		JMenuItem gaeaShading = new ShadingItem(new boolean[] { true, true,
				true, false, false }, "Advanced Gaea+ shading");
		optionsMenu.add(gaeaShading);
		optionsGroup.add(gaeaShading);
		JMenuItem gaeaShadingPosEffects = new ShadingItem(new boolean[] { true,
				true, true, true, false },
				"Advanced Gaea+ shading with HDR, bloom and depth of field");
		optionsMenu.add(gaeaShadingPosEffects);
		optionsGroup.add(gaeaShadingPosEffects);

		JMenuItem shadows = new ShadingItem(new boolean[] { true, true, true,
				true, true }, "Advanced Gaea+ shading with shadows");
		optionsMenu.add(shadows);
		optionsGroup.add(shadows);

		// depending on support for advanced shading, enable/disable menu items
		// and select appropriate shading model
		boolean gaeaShadingSupported = isGaeaShadingSupported(appFrame.getWwd());

		gaeaShading.setEnabled(gaeaShadingSupported);
		gaeaShadingPosEffects.setEnabled(gaeaShadingSupported);
		shadows.setEnabled(gaeaShadingSupported);

		if (gaeaShadingSupported)
			gaeaShading.doClick();
		else {
			wwShading.doClick();
			// if gaea shading is not supported by GPU/driver, we also have to
			// remove the run-time calculated layers
			ArrayList<Layer> unsupportedLayers = new ArrayList<Layer>();
			for (Layer layer : appFrame.getWwd().getModel().getLayers()) {
				if (layer instanceof SlopeLayer
						|| layer instanceof ElevationLayer)
					unsupportedLayers.add(layer);
			}
			appFrame.getWwd().getModel().getLayers()
					.removeAll(unsupportedLayers);
			if (appFrame instanceof GaeaAppFrame)
				((GaeaAppFrame) appFrame).updateLayerPanel();
		}

		JMenu helpMenu = new JMenu("Help");
		menuBar.add(helpMenu);
		String licenseMsg = "This application, together with the NASA World Wind Java SDK and the modifications to the SDK done by XLAB d.o.o.,"
				+ "are distributed under the terms of NASA Open Source Agreement license.\n"
				+ "You should have received this license together with this application. If not, please contact info@geaplus.si or visit http://www.gaeaplus.eu.\n\n"
				+ "The data layers included in this application are either licensed for use in World Wind, owned by XLAB d.o.o., or available for free from servers intended for public use.";
		helpMenu.add(new MessageItem(licenseMsg, "Terms of use"));
		String aboutMsg = "This is a demonstration of features that Gaea+ Open Source adds to NASA World Wind Java SDK.\n"
				+ "For more information, visit http://www.gaeaplus.eu/en/, https://github.com/gaeaplus/gaeaplus, and http://worldwind.arc.nasa.gov/java/.";
		helpMenu.add(new MessageItem(aboutMsg, "About..."));
	}

	private static boolean isGaeaShadingSupported(WorldWindow wwd) {
		DrawContext dc = null;
		// shading is suppored if a deferredRenderer appears in dc relatively
		// quickly AND this deferredRenderer says it is supported
		for (int wait = 0; wait < 10; wait++) {
			dc = wwd.getSceneController().getDrawContext();
			if (dc != null && dc.getDeferredRenderer() != null) {
				return dc.getDeferredRenderer().isSupported(dc);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		return false;
	}

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

	private static class ShadingItem extends JRadioButtonMenuItem {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3469131637144215638L;
		private final boolean[] states;

		public ShadingItem(boolean[] _states, String caption) {
			super(caption);
			this.states = _states;
			setAction(new AbstractAction(caption) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 5439068726551850798L;

				@Override
				public void actionPerformed(ActionEvent e) {
					appFrame.getWwd()
							.getSceneController()
							.firePropertyChange(AvKeyExt.ENABLE_SUNLIGHT,
									!states[0], states[0]);
					appFrame.getWwd()
							.getSceneController()
							.firePropertyChange(AvKeyExt.ENABLE_ATMOSPHERE,
									!states[1], states[1]);
					appFrame.getWwd()
							.getSceneController()
							.firePropertyChange(
									AvKeyExt.ENABLE_ATMOSPHERE_WITH_AERIAL_PERSPECTIVE,
									!states[2], states[2]);
					appFrame.getWwd()
							.getSceneController()
							.firePropertyChange(AvKeyExt.ENABLE_POS_EFFECTS,
									!states[3], states[3]);
					appFrame.getWwd()
							.getSceneController()
							.firePropertyChange(AvKeyExt.ENABLE_SHADOWS,
									!states[4], states[4]);
				}
			});
		}
	}

	protected static void addWfsLayer(String url, String featureTypeName,
			Sector sector, Angle tileDelta, double maxVisibleDistance,
			Color color, String lineLabelTag) {
		WFSService service = new WFSService(url, featureTypeName, sector,
				tileDelta);
		WFSGenericLayer layer = new WFSGenericLayer(service, "WFS: "
				+ featureTypeName + " (from "
				+ url.replaceAll("^.+://", "").replaceAll("/.*$", "") + ")");
		layer.setMaxActiveAltitude(maxVisibleDistance);
		if (lineLabelTag != null && !lineLabelTag.isEmpty()) {
			layer.setLineLabelTag(lineLabelTag);
		}

		KMLStyle style = layer.getDefaultStyle();
		style.getLineStyle().setField("color",
				KMLStyleFactory.encodeColorToHex(color));
		style.getPolyStyle().setField(
				"color",
				KMLStyleFactory.encodeColorToHex(color).replaceFirst("^ff",
						"80")); // semi-transparent fill
		layer.setDefaultStyle(style);
		insertBeforePlacenames(appFrame.getWwd(), layer);
		layer.setEnabled(true);
		appFrame.updateLayerPanel();
	}

	public static class GaeaAppFrame extends AppFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6059677693262885004L;
		protected RenderableLayer symbolLayer;
		protected TacticalSymbolAttributes sharedAttrs;
		protected TacticalSymbolAttributes sharedHighlightAttrs;
		protected DDSDragger dragger;
		protected ConfigurationManager confManager;

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

			this.symbolLayer = new RenderableLayer();
			this.symbolLayer.setName("Simbolos Tacticos");

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
			this.dragger = new DDSDragger(this.getWwd(), true);
			this.getWwd().addSelectListener(this.dragger);

			// Create a Swing control panel that provides user control over the
			// symbol's appearance.
			this.addSymbolControls();

			// Add the symbol layer to the World Wind model.
			this.getWwd().getModel().getLayers().add(symbolLayer);
			
		}

		protected void updateLayerPanel() {
			// remove RTT layer, update layer panel, re-insert RTT; otherwise it
			// will appear in the layer list
			int rttIndex = getWwd().getModel().getLayers()
					.indexOf(RenderToTextureLayer.getInstance());
			if (rttIndex != -1)
				getWwd().getModel().getLayers().remove(rttIndex);
			this.layerPanel.update(getWwd());
			getWwd().getModel().getLayers()
					.add(rttIndex, RenderToTextureLayer.getInstance());
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
	}

	private static GaeaAppFrame appFrame = null;

	public static void main(String[] args) {
		// MeasureRenderTime.enable(true);
		// MeasureRenderTime.setMesureGpu(true);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			Logger.getLogger(BMSAppFrame.class.getName()).log(Level.SEVERE,
					"ERROR al fijar el look and feel de la aplicación", e);
			e.printStackTrace();
		}
		Configuration
				.insertConfigurationDocument("si/xlab/gaea/examples/gaea-example-config.xml");
		appFrame = (GaeaAppFrame) ApplicationTemplate.start(
				"Gaea+ Open Source Example Application", GaeaAppFrame.class);
		insertBeforeCompass(appFrame.getWwd(),
				RenderToTextureLayer.getInstance());
		appFrame.getWwd().addSelectListener(
				new FeatureSelectListener(appFrame.getWwd()));
		makeMenu(appFrame);
	}
}
