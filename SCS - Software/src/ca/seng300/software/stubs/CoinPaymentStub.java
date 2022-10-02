package ca.seng300.software.stubs;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.CoinValidatorObserver;

public class CoinPaymentStub implements CoinValidatorObserver {
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
    public void validCoinDetected(CoinValidator validator, BigDecimal value) {
        // TODO Auto-generated method stub
        this.isValid = true;
    }

    @Override
    public void invalidCoinDetected(CoinValidator validator) {
        // TODO Auto-generated method stub
        this.isValid = false;
    }
    
}
