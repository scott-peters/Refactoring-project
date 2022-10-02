package ca.seng300.software.stubs;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;

public class BagItemStub implements ElectronicScaleObserver {

	public boolean overload = false;
	public boolean outOfOverload = false;
	public boolean weightChanged = false;

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub

	}

	@Override
	public void weightChanged(ElectronicScale scale, double weightInGrams) {
		this.weightChanged = true;

	}

	@Override
	public void overload(ElectronicScale scale) {
		this.overload = true;
		this.outOfOverload = false;

	}

	@Override
	public void outOfOverload(ElectronicScale scale) {
		this.overload = false;
		this.outOfOverload = true;
	}

}
