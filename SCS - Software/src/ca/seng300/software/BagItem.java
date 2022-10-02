package ca.seng300.software;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;

public class BagItem extends UseCase {
	
	private SelfCheckoutStation scs = getSCS();
	private scaleSoftware scaleSoftware = new scaleSoftware(scs.baggingArea);
	
	public BagItem() {
		setCustomerWantsCheckout();
	}
	
	public void bagItem(Item item) throws OverloadException {
		setPutItemInBag(true);
		if (customerCheckout() == false && getPutItemInBag() && getCurrentScanItem() == item) {
			scs.baggingArea.add(item);
			updateWeightOnScale(item.getWeight());
		}
		else{
			throw new SimulationException(
					new IllegalStateException("Error bagging item."));
		}
		
		//new statement to ensure bagging area scale is the correct weight
		if (!scaleSoftware.itemBaggedCorrectly(item, item.getWeight(), scs.baggingArea.getSensitivity())){
				throw new SimulationException(
					new IllegalStateException("Error bagging item."));
		}
	}
	
	public void removeBagItem(Item item)
	{
		setPutItemInBag(false);
		if (customerCheckout() == false && getPutItemInBag() == false && getWeightOnScale().contains(item.getWeight())) {
			scs.baggingArea.remove(item);
			removeFromScale(item.getWeight());
		}
		else {
			throw new SimulationException(
					new IllegalStateException("Item is not in Bag."));
		}
	}
}
