package battleSystemApp.dds;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.event.DataAvailableEvent;
import org.omg.dds.core.event.LivelinessChangedEvent;
import org.omg.dds.core.event.RequestedDeadlineMissedEvent;
import org.omg.dds.core.event.RequestedIncompatibleQosEvent;
import org.omg.dds.core.event.SampleLostEvent;
import org.omg.dds.core.event.SampleRejectedEvent;
import org.omg.dds.core.event.SubscriptionMatchedEvent;
import org.omg.dds.domain.DomainParticipant;
import org.omg.dds.domain.DomainParticipantFactory;
import org.omg.dds.pub.DataWriter;
import org.omg.dds.pub.Publisher;
import org.omg.dds.sub.DataReader;
import org.omg.dds.sub.DataReaderListener;
import org.omg.dds.sub.Sample;
import org.omg.dds.sub.SampleState;
import org.omg.dds.sub.Subscriber;
import org.omg.dds.sub.Sample.Iterator;
import org.omg.dds.topic.Topic;

import battleSystemApp.core.BMSAppFrame;
import battleSystemApp.dds.idl.*;

/**
 * Capa de comunicaciones DDS
 * 
 * @author vgonllo
 * 
 */
public class DDSCommLayer implements DataReaderListener<Msg> {
	// Logger.getLogger(BMSAppFrame.class.getName()).log(Level.SEVERE,
	// "ERROR al fijar el look and feel de la aplicación", e);

	// the DDS DomainParticipant
	private DomainParticipant dp;
	// the DDS DataReader for Msg type
	private DataReader<Msg> dr;
	// the DDS DataWriter for Msg type
	private DataWriter<Msg> dw;
	// active listeners references
	private ArrayList<DDSListener> listeners;

	/**
	 * Capa de comunicaciones DDS
	 */
	public DDSCommLayer() {
		listeners = new ArrayList<DDSListener>();
		// Configure DDS ServiceEnvironment class to be OpenSplice Mobile
		System.setProperty(
				ServiceEnvironment.IMPLEMENTATION_CLASS_NAME_PROPERTY,
				"org.opensplice.mobile.core.ServiceEnvironmentImpl");

		// Create a DDS ServiceEnvironment
		ServiceEnvironment env = ServiceEnvironment.createInstance(this
				.getClass().getClassLoader());

		// Get the DomainParticipantFactory singleton
		DomainParticipantFactory dpf = DomainParticipantFactory
				.getInstance(env);

		// Create a DomainParticipant on DomainID
		dp = dpf.createParticipant(1);

		// Create a Topic with Msg as type
		Topic<Msg> topic = dp.createTopic("C2Msg", Msg.class);

		// Create a Publisher and a Subscriber (with default QoS)
		Publisher pub = dp.createPublisher();
		Subscriber sub = dp.createSubscriber();

		// Create a DataWriter and a DataReader on ChatMessage Topic (with
		// default QoS)
		dw = pub.createDataWriter(topic);
		dr = sub.createDataReader(topic);
		
		dr.setListener(this);
	}

	public DomainParticipant getDp() {
		return dp;
	}

	public DataReader<Msg> getDr() {
		return dr;
	}

	public DataWriter<Msg> getDw() {
		return dw;
	}

	public void closeDDSEntities() {
		// Close the DDS DomainParticipant and all its child entities
		Logger.getLogger(DDSCommLayer.class.getName()).log(Level.INFO,
				"Eliminando DomainParticipants de DDS -");
		dp.close();
		
	}

	public void publish(Msg message) {
		try {
			Logger.getLogger(DDSCommLayer.class.getName()).log(Level.INFO,
					"Enviando mensaje DDS -" + message.unitID);
			// write the message to DDS
			dw.write(message);
		} catch (TimeoutException te) {
			Logger.getLogger(DDSCommLayer.class.getName()).log(Level.WARNING,
					"TIMEOUT enviando mensaje DDS", te);
		}
	}

	@Override
	public void onDataAvailable(DataAvailableEvent<Msg> dae) {
		// Get a read iterator on all available data
		final Iterator<Msg> i = dae.getSource().read();

		Logger.getLogger(DDSCommLayer.class.getName()).log(Level.INFO,
				"Recibidos datos DDS...");

		while (i.hasNext()) {
			Sample<Msg> s = i.next();

			// check if their is a data and if it's not already read.
			if (s.getData() != null
					&& s.getSampleState() == SampleState.NOT_READ) {
				// display the message
				Msg msg = s.getData();
				// send message to all listeners
				for (DDSListener listener : this.listeners) {
					listener.receivedMessage(msg);
				}

			}
		}
	}

	@Override
	public void onLivelinessChanged(LivelinessChangedEvent<Msg> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestedDeadlineMissed(
			RequestedDeadlineMissedEvent<Msg> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRequestedIncompatibleQos(
			RequestedIncompatibleQosEvent<Msg> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSampleLost(SampleLostEvent<Msg> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSampleRejected(SampleRejectedEvent<Msg> status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubscriptionMatched(SubscriptionMatchedEvent<Msg> status) {
		// TODO Auto-generated method stub

	}

	public void addListener(DDSListener listener) {
		listeners.add(listener);
	}

	public boolean removeListener(DDSListener listener) {
		return listeners.remove(listener);
	}
	
	public void removeAllListeners(){
		listeners.clear();
	}

}
