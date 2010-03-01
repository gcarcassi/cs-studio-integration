package org.csstudio.platform.internal.dal;

import org.epics.css.dal.RemoteException;
import org.epics.css.dal.ResponseListener;
import org.epics.css.dal.simple.AnyData;
import org.epics.css.dal.simple.ConnectionParameters;
import org.epics.css.dal.simple.RemoteInfo;
import org.epics.css.dal.simple.SimpleDALBroker;

/**
 * DAL Tests. This test class concentrates on functionalities that receive plain
 * and simple values synchronously via
 * {@link SimpleDALBroker#getValue(RemoteInfo)} and asynchronously via
 * {@link SimpleDALBroker#getValueAsync(ConnectionParameters, ResponseListener)}
 * .
 * 
 * The record under test is:
 * 
 * <pre>
 * record(ai,'Chiller:Pressure:1') field(DESC,'DEMO') field(SCAN,'.1
 * second') field(INP,'2.22') field(HIHI,'35.00') field(HIGH,'30.00')
 * field(LOW,'10.00') field(LOLO,'5.00') field(HOPR,'40.00') field(LOPR,'1.11')
 * field(HHSV,'NO_ALARM') field(LLSV,'NO_ALARM') field(HSV,'NO_ALARM')
 * field(LSV,'NO_ALARM') }
 * 
 * </pre>
 * 
 * @author Sven Wende
 * 
 */
public class DataAccessLayerTest1 extends Base {
	private static final RemoteInfo ri = new RemoteInfo(RemoteInfo.DAL_TYPE_PREFIX + "EPICS", PV, null, null);

	public void testSynchronousAccess() throws RemoteException, InstantiationException {
		assertEquals(RECORD_VAL, broker.getValue(ri));
	}

	public void testAsynchronousAccess() throws Exception {
		receiveAsync(ri);
	}
	
	public void testListenerAccess() throws Exception {
		AnyData data = receiveViaListener(ri);
		assertNotNull(data);
		assertEquals(RECORD_VAL, data.doubleValue());
	}
}
