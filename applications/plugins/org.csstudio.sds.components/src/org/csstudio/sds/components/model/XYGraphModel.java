package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.util.ColorAndFontUtil;

public class XYGraphModel extends AbstractWidgetModel {

	public static final String PROP_SHOW_TOOLBAR = "PROP_SHOW_TOOLBAR";
	public static final String PROP_TRANSPARENT = "PROP_TRANSPARENT";
	public static final String PROP_SHOW_TITLE = "PROP_SHOW_TITLE";
	public static final String PROP_TITLE = "PROP_TITLE";
	public static final String PROP_SHOW_LEGEND = "PROP_SHOW_LEGEND";
	public static final String PROP_TITLE_COLOR = "PROP_TITLE_COLOR";
	public static final String PROP_TITLE_FONT = "PROP_TITLE_FONT";
	public static final String ID = "org.csstudio.sds.components.XYGraph";

	@Override
	protected void configureProperties() {
		addBooleanProperty(PROP_SHOW_TOOLBAR, "Show Toolbar", WidgetPropertyCategory.Display, false);
		addBooleanProperty(PROP_TRANSPARENT, "Transparent", WidgetPropertyCategory.Display, true);
		addBooleanProperty(PROP_SHOW_TITLE, "Show Title", WidgetPropertyCategory.Display, false);
		addStringProperty(PROP_TITLE, "Title", WidgetPropertyCategory.Display, "");
		addColorProperty(PROP_TITLE_COLOR, "Title Color", WidgetPropertyCategory.Display, "#000000");
		addFontProperty(PROP_TITLE_FONT, "Title Font", WidgetPropertyCategory.Display, ColorAndFontUtil.toFontString("Arial", 8));
		addBooleanProperty(PROP_SHOW_LEGEND, "Show Legend", WidgetPropertyCategory.Display, false);
	}

	@Override
	public String getTypeID() {
		return ID;
	}

	public boolean isToolbarVisible() {
		return getBooleanProperty(PROP_SHOW_TOOLBAR).getPropertyValue();
	}

	public boolean isLegendVisible() {
		return getBooleanProperty(PROP_SHOW_LEGEND).getPropertyValue();
	}

	public boolean isTransparent() {
		return getBooleanProperty(PROP_TRANSPARENT).getPropertyValue();
	}

	public boolean isTitleVisible() {
		return getBooleanProperty(PROP_SHOW_TITLE).getPropertyValue();
	}

	public String getTitle() {
		return getStringProperty(PROP_TITLE).getPropertyValue();
	}

}
