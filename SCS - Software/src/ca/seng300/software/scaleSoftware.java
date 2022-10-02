package ca.seng300.software;

import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ElectronicScaleObserver;
import org.lsmr.selfcheckout.devices.SimulationException;

public class scaleSoftware extends UseCase implements ElectronicScaleObserver {

  private double expectedScaleWeight;
  private double actualScaleWeight;
  private ElectronicScale thisScale;
  private boolean scaleEnabled;
  private boolean scaleOverloaded;
  private boolean userPlacingBags;

  //constructor
  public scaleSoftware(ElectronicScale scale) {
    scale.attach(this);
    expectedScaleWeight = 0;
    actualScaleWeight = 0;
    scaleEnabled = false;
    scaleOverloaded = false;
    userPlacingBags = false;
  }

  @Override
  public void enabled(AbstractDevice < ? extends AbstractDeviceObserver > device) {
      scaleEnabled = true;
  }

  @Override
  public void disabled(AbstractDevice < ? extends AbstractDeviceObserver > device) {
      scaleEnabled = false;
  }

  public boolean getScaleEnabled() {
	  return scaleEnabled;
  }
  
  /**
   * Announces that the weight on the indicated scale has changed.
   * 
   * @param scale
   *            The scale where the event occurred.
   * @param weightInGrams
   *            The new weight.
   */
  public void weightChanged(ElectronicScale scale, double weightInGrams) {
    actualScaleWeight = weightInGrams;
  }

  /**
   * Announces that excessive weight has been placed on the indicated scale.
   * 
   * @param scale
   *            The scale where the event occurred.
   */
  public void overload(ElectronicScale scale) {
    scaleOverloaded = true;
  }

  /**
   * Announces that the former excessive weight has been removed from the
   * indicated scale, and it is again able to measure weight.
   * 
   * @param scale
   *            The scale where the event occurred.
   */
  public void outOfOverload(ElectronicScale scale) {
    scaleOverloaded = false;
  }

  //used to ensure item placed in bagging area matches the expected weight
  //parameters: the scanned items expected weight in grams
  //returns: a boolean that is true if the item was bagged successfully
  public boolean itemBaggedCorrectly(Item item, double actualScaleWeight, double scaleSensitivity) {

    expectedScaleWeight = item.getWeight();

    if (actualScaleWeight < (expectedScaleWeight - scaleSensitivity)) {
      weightTooSmall();
      return false;
    } else if (actualScaleWeight > (expectedScaleWeight + scaleSensitivity)) {
      weightTooBig();
      return false;
    } else {
      return true;
    }
  }
	
public void bagCheck(double bagWeight) {
	setBagPlaced(true);
	if(bagWeight < 0.0) {
		throw new SimulationException(new IllegalArgumentException("Bag weight cannot be negative."));
	} else if (bagWeight == 0.0) {
		throw new SimulationException(new NullPointerException("Bag weight is null."));
	} else {
		actualScaleWeight += bagWeight;
	}
	userPlacingBags = false;
  }

  //runs if the scale weight is larger than what is expected
  private void weightTooBig() {
    if (userPlacingBags) {
      expectedScaleWeight = actualScaleWeight;
    } else {
      throw new SimulationException(new IllegalStateException("Unexpected item in bagging area."));
    }
  }

  //runs if the scale weight is smaller than what is expected
  private void weightTooSmall() {
    throw new SimulationException(new IllegalStateException("Item not placed in bagging area."));
  }

}
