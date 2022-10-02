package ca.seng300.software.stubs;

import java.util.Currency;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;

public class BanknotePaymentStub implements BanknoteValidatorObserver {
    public boolean isValid = false;

    @Override
    public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
        // TODO Auto-generated method stub
        this.isValid = true;
    }

    @Override
    public void invalidBanknoteDetected(BanknoteValidator validator) {
        // TODO Auto-generated method stub
        this.isValid = false;
    }
    
}
