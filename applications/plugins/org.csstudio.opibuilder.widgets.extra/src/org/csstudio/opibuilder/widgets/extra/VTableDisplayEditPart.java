package org.csstudio.opibuilder.widgets.extra;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.csstudio.opibuilder.model.AbstractPVWidgetModel;
import org.csstudio.opibuilder.properties.IWidgetPropertyChangeHandler;
import org.csstudio.opibuilder.visualparts.BorderFactory;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.csstudio.utility.pvmanager.widgets.VTableWidget;
import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.wb.swt.SWTResourceManager;
import org.epics.vtype.Alarm;
import org.epics.vtype.AlarmSeverity;
import org.epics.vtype.ValueUtil;

public class VTableDisplayEditPart extends AbstractSelectionWidgetEditpart<VTableDisplayFigure, VTableDisplayModel> {

	/**
	 * Create and initialize figure.
	 */
	@Override
	protected VTableDisplayFigure doCreateFigure() {
		VTableDisplayFigure figure = new VTableDisplayFigure(this);
		configure(figure.getSWTWidget(), getWidgetModel(), figure.isRunMode());
		return figure;
	}

	private static void configure(VTableWidget widget, VTableDisplayModel model,
			boolean runMode) {
		if (runMode) {
			widget.setPvFormula(model.getPvFormula());
		}
	}

	@Override
	protected void registerPropertyChangeHandlers() {
		// The handler when PV value changed.
		IWidgetPropertyChangeHandler reconfigure = new IWidgetPropertyChangeHandler() {
			public boolean handleChange(final Object oldValue,
					final Object newValue, final IFigure figure) {
				configure(getFigure().getSWTWidget(), getWidgetModel(),
						getFigure().isRunMode());
				return false;
			}
		};
		setPropertyChangeHandler(AbstractPVWidgetModel.PROP_PVNAME, reconfigure);
		((VTableDisplayFigure) getFigure()).getSWTWidget().addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if ("value".equals(event.getPropertyName())) {
					setFigureBorder(createBorderFromAlarm(((VTableWidget) event.getSource()).getAlarm()));
				}
			}
		});
		setFigureBorder(calculateBorder());
	}
	
	@Override
	public Border calculateBorder() {
		if (getWidgetModel().isAlarmSensitive()) {
			return createBorderFromAlarm((((VTableDisplayFigure) getFigure()).getSWTWidget()).getAlarm());
		} else {
			return super.calculateBorder();
		}
	}
	
	protected Border createBorderFromAlarm(Alarm alarm) {
		if (alarm.getAlarmSeverity() == AlarmSeverity.NONE) {
			return super.calculateBorder();
		} else {
			java.awt.Color awtColor = new java.awt.Color(ValueUtil.colorFor(alarm.getAlarmSeverity()));
			RGB swtColor = SWTResourceManager.getColor(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue()).getRGB();
			return BorderFactory.createBorder(BorderStyle.LINE,
					getWidgetModel().getBorderWidth(), swtColor,
					"");
		}
	}


}
