/*
 * Copyright (C) 2012 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration.
 * All Rights Reserved.
 */

package battleSystemApp.core;

/**
 * @author tag
 * @version $Id: Constants.java 1171 2013-02-11 21:45:02Z dcollins $
 */
public interface Constants
{
    // Names and titles
    static final String APPLICATION_DISPLAY_NAME
        = "battleSystemApp.BMS";

    // Services
    public static final String IMAGE_SERVICE = "battleSystemApp.ImageService";

    // Core object IDs
    static final String APP_PANEL = "battleSystemApp.core.AppPanel";
    static final String APP_FRAME = "battleSystemApp.core.AppFrame";
    static final String APPLET_PANEL = "battleSystemApp.core.AppletPanel";
    static final String CONTROLS_PANEL = "battleSystemApp.features.swinglayermanager.ControlsPanel";
    static final String MENU_BAR = "battleSystemApp.core.MenuBar";
    static final String NETWORK_STATUS_SIGNAL = "battleSystemApp.features.NetworkActivitySignal";
    static final String TOOL_BAR = "battleSystemApp.core.ToolBar";
    static final String STATUS_PANEL = "battleSystemApp.core.StatusPanel";
    static final String WW_PANEL = "battleSystemApp.core.WWPanel";

    // Miscellaneous
    static final String ACCELERATOR_SUFFIX = ".Accelerator";
    static final String ACTION_COMMAND = "battleSystemApp.ActionCommand";
    static final String CONTEXT_MENU_INFO = "battleSystemApp.ContextMenuString";
    static final String FILE_MENU = "battleSystemApp.feature.FileMenu";
    static final String INFO_PANEL_TEXT = "battleSystemApp.InfoPanelText";
    static final String ON_STATE = "battleSystemApp.OnState";
    static final String RADIO_GROUP = "battleSystemApp.StatusBarMessage";
    static final String STATUS_BAR_MESSAGE = "battleSystemApp.StatusBarMessage";

    // Layer types
    static final String INTERNAL_LAYER = "battleSystemApp.InternalLayer";
        // application controls, etc.
    static final String ACTIVE_LAYER = "battleSystemApp.ActiveLayer";
        // force display in active layers
    static final String USER_LAYER = "battleSystemApp.UserLayer"; // User-generated layers
    static final String SCREEN_LAYER = "battleSystemApp.ScreenLayer";

    // in-screen application controls, etc.

    // Feature IDs
    static final String FEATURE = "battleSystemApp.feature";
    static final String FEATURE_ID = "battleSystemApp.FeatureID";
    static final String FEATURE_ACTIVE_LAYERS_PANEL
        = "battleSystemApp.feature.ActiveLayersPanel";
    static final String FEATURE_COMPASS = "battleSystemApp.feature.Compass";
    static final String FEATURE_MIL_STD = "battleSystemApp.feature.MilSymbolFeatureLayer";
    static final String FEATURE_SCREEN_LAYER_TREE = "battleSystemApp.feature.ScreenLayerTree";
    static final String FEATURE_CROSSHAIR = "battleSystemApp.feature.Crosshair";
    static final String FEATURE_COORDINATES_DISPLAY
        = "battleSystemApp.feature.CoordinatesDisplay";
    static final String FEATURE_EXTERNAL_LINK_CONTROLLER
        = "battleSystemApp.feature.ExternalLinkController";
    static final String FEATURE_GAZETTEER = "battleSystemApp.feature.Gazetteer";
    static final String FEATURE_GAZETTEER_PANEL = "battleSystemApp.feature.GazetteerPanel";
    static final String FEATURE_GRATICULE = "battleSystemApp.feature.Graticule";
    static final String FEATURE_ICON_CONTROLLER = "battleSystemApp.feature.IconController";
    static final String FEATURE_HOTSPOT_CONTROLLER = "battleSystemApp.feature.BMSHotSpotController";
    static final String FEATURE_MESSAGE_WINDOW = "battleSystemApp.feature.MessageWindow";
    
    static final String FEATURE_IMPORT_IMAGERY = "battleSystemApp.feature.ImportImagery";
    static final String FEATURE_INFO_PANEL_CONTROLLER
        = "battleSystemApp.feature.InfoPanelController";
    static final String FEATURE_LAYER_MANAGER_DIALOG
        = "battleSystemApp.feature.LayerManagerDialog";
    static final String FEATURE_LAYER_MANAGER = "battleSystemApp.feature.LayerManager";
    static final String FEATURE_LAYER_MANAGER_PANEL
        = "battleSystemApp.feature.LayerManagerPanel";
    static final String FEATURE_LATLON_GRATICULE
        = "battleSystemApp.feature.LatLonGraticule";
    static final String FEATURE_MEASUREMENT = "battleSystemApp.feature.Measurement";
    static final String FEATURE_MEASUREMENT_DIALOG
        = "battleSystemApp.feature.MeasurementDialog";
    static final String FEATURE_MEASUREMENT_PANEL
        = "battleSystemApp.feature.MeasurementPanel";
    static final String FEATURE_NAVIGATION = "battleSystemApp.feature.Navigation";
    static final String FEATURE_OPEN_FILE = "battleSystemApp.feature.OpenFile";
    static final String FEATURE_OPEN_URL = "battleSystemApp.feature.OpenURL";
    static final String FEATURE_SCALE_BAR = "battleSystemApp.feature.ScaleBar";
    static final String FEATURE_TOOLTIP_CONTROLLER
        = "battleSystemApp.feature.ToolTipController";
    static final String FEATURE_UTM_GRATICULE = "battleSystemApp.feature.UTMGraticule";
    static final String FEATURE_WMS_PANEL = "battleSystemApp.feature.WMSPanel";
    static final String FEATURE_WMS_DIALOG = "battleSystemApp.feature.WMSDialog";
    static final String FEATURE_TRACKING_VIEW = "battleSystemApp.core.TrackingView";
    // Specific properties
    static final String FEATURE_OWNER_PROPERTY = "battleSystemApp.FeatureOwnerProperty";
    static final String TOOL_BAR_ICON_SIZE_PROPERTY
        = "battleSystemApp.ToolBarIconSizeProperty";
    
    // Acciones
    static final String CONTEXT_MENU_ACTION_FOLLOW="Seguir";
    static final String CONTEXT_MENU_ACTION_UNFOLLOW="No Seguir";
}
