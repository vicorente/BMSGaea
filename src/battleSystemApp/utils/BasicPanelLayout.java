package battleSystemApp.utils;

import gov.nasa.worldwind.avlist.AVList;
import gov.nasa.worldwind.event.Message;
import gov.nasa.worldwind.render.DrawContext;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

public class BasicPanelLayout implements PanelLayout {

	public BasicPanelLayout(Offset screenLocation){
		
	}
	@Override
	public Object setValue(String key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AVList setValues(AVList avList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Object> getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStringValue(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<String, Object>> getEntries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasKey(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object removeKey(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public void firePropertyChange(PropertyChangeEvent propertyChangeEvent) {
		// TODO Auto-generated method stub

	}

	@Override
	public AVList copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AVList clearList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(Message msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(DrawContext dc) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAttributes(ScrollFrameAttributes attributes) {
		// TODO Auto-generated method stub

	}

	@Override
	public ScrollFrameAttributes getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

}
