package battleSystemApp.features;

import java.text.SimpleDateFormat;
import java.util.Date;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.Layer;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.symbology.AbstractTacticalSymbol;
import gov.nasa.worldwind.symbology.BasicTacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.SymbologyConstants;
import gov.nasa.worldwind.symbology.TacticalSymbolAttributes;
import gov.nasa.worldwind.symbology.milstd2525.MilStd2525TacticalSymbol;
import gov.nasa.worldwind.util.BasicDragger;
import battleSystemApp.components.ContextMenuInfo;
import battleSystemApp.components.ContextMenuItemInfo;
import battleSystemApp.components.TacticalSymbolContextMenu;
import battleSystemApp.core.Constants;
import battleSystemApp.core.Controller;
import battleSystemApp.core.Registry;
import battleSystemApp.core.layermanager.LayerPath;

public class MilSymbolFeatureLayer extends AbstractOnDemandLayerFeature {

	public MilSymbolFeatureLayer() {
		this(null);
	}

	public MilSymbolFeatureLayer(Registry registry) {
		super("MIL-STD SYMBOL LAYER", Constants.FEATURE_MIL_STD,"images/warning64.png", null, registry);
	}
    @Override
    public void initialize(Controller controller)
    {
        super.initialize(controller);

        this.addToToolBar();
    }

    @Override
    protected Layer createLayer()
    {
        Layer layer = this.doCreateLayer();

        layer.setPickEnabled(true);

        return layer;
    }

    @Override
    protected void addLayer(LayerPath path)
    {
        controller.addInternalActiveLayer(this.layer);
    }

    @Override
    protected void removeLayer()
    {
        this.controller.getWWPanel().removeLayer(this.layer);
    }
    
	
	protected Layer doCreateLayer() {

		RenderableLayer layer = new RenderableLayer();
		layer.setName("MILSTD");

		TacticalSymbolAttributes sharedAttrs = new BasicTacticalSymbolAttributes();
		TacticalSymbolAttributes sharedHighlightAttrs = new BasicTacticalSymbolAttributes();
		sharedHighlightAttrs.setInteriorMaterial(Material.WHITE);
		sharedHighlightAttrs.setOpacity(1.0);

		// AIR SYMBOL
		AbstractTacticalSymbol airSymbol = new MilStd2525TacticalSymbol(
				"SHAPMFQM--GIUSA",
				Position.fromDegrees(32.4520, 63.44553, 3000));
		airSymbol.setValue(AVKey.DISPLAY_NAME,
				"MIL-STD-2525 Friendly SOF Drone Aircraft"); // Tool tip
																// text.
		airSymbol.setAttributes(sharedAttrs);
		airSymbol.setHighlightAttributes(sharedHighlightAttrs);
		airSymbol.setModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT,
				Angle.fromDegrees(235));
		airSymbol.setShowLocation(false);
		ContextMenuItemInfo[] itemActionNames = new ContextMenuItemInfo[] {
				new ContextMenuItemInfo("Do This"),
				new ContextMenuItemInfo("Do That"),
				new ContextMenuItemInfo("Do the Other Thing") };
		airSymbol.setValue(TacticalSymbolContextMenu.CONTEXT_MENU_INFO,
				new ContextMenuInfo("Placemark A", itemActionNames));
		layer.addRenderable(airSymbol);

		// GROUND SYMBOL
		AbstractTacticalSymbol groundSymbol = new MilStd2525TacticalSymbol(
				"SHGXUCFRMS----G", Position.fromDegrees(32.4014, 63.3894, 0));
		groundSymbol.setValue(AVKey.DISPLAY_NAME,
				"MIL-STD-2525 Hostile Self-Propelled Rocket Launchers");
		groundSymbol.setAttributes(sharedAttrs);
		groundSymbol.setHighlightAttributes(sharedHighlightAttrs);
		groundSymbol.setModifier(SymbologyConstants.DIRECTION_OF_MOVEMENT,
				Angle.fromDegrees(90));
		groundSymbol.setModifier(SymbologyConstants.SPEED_LEADER_SCALE, 0.5);
		groundSymbol.setShowLocation(false);
		layer.addRenderable(groundSymbol);

		// GROUND SYMBOL
		AbstractTacticalSymbol machineGunSymbol = new MilStd2525TacticalSymbol(
				"SFGPEWRH--MTUSG", Position.fromDegrees(32.3902, 63.4161, 0));
		machineGunSymbol.setValue(AVKey.DISPLAY_NAME,
				"MIL-STD-2525 Friendly Heavy Machine Gun");
		machineGunSymbol.setAttributes(sharedAttrs);
		machineGunSymbol.setHighlightAttributes(sharedHighlightAttrs);
		machineGunSymbol.setModifier(SymbologyConstants.QUANTITY, 200);
		machineGunSymbol.setModifier(SymbologyConstants.STAFF_COMMENTS,
				"FOR REINFORCEMENTS");
		machineGunSymbol.setModifier(SymbologyConstants.ADDITIONAL_INFORMATION,
				"ADDED SUPPORT FOR JJ");
		machineGunSymbol.setModifier(SymbologyConstants.TYPE, "MACHINE GUN");
		// formato de hora del STANAG
		String newString = new SimpleDateFormat("ddHHmmss'Z'MMMYYYY").format(
				new Date()).toUpperCase(); // 9:00
		machineGunSymbol.setModifier(SymbologyConstants.DATE_TIME_GROUP,
				newString);

		layer.addRenderable(machineGunSymbol);
		BasicDragger dragger = new BasicDragger(controller.getWWd());
        controller.getWWd().addSelectListener(dragger);
		layer.setValue(Constants.SCREEN_LAYER, true);
		

		return layer;
	}


}
