package ca.seng300.software;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.products.Product;

public class ProductDatabase{
	private ArrayList<Product> allProducts;

    //constructor
    public ProductDatabase() {
    	 allProducts = new ArrayList<Product>();
    }
    
    //Add a new product
    public void addProduct(Product toAdd) {
    	if(!allProducts.contains(toAdd)) {
    		allProducts.add(toAdd);
    	}
    }
    
    //Remove an existing product
    public void removeProduct(Product toRemove) {
    	if(allProducts.contains(toRemove)) {
    		allProducts.remove(toRemove);
    	}
    }
    
    

    //returns productArray for
    public ArrayList<Product> getProductArrayList() {
        return new ArrayList<Product>(allProducts);
    }

    // Look up a product based on a barcode
    public BarcodedProduct lookUpBarcode(Barcode currentBarcode) {
    	BarcodedProduct currentProduct = null;
    	
    	for(Product potentialProduct: allProducts) {
    		if(potentialProduct instanceof BarcodedProduct) {
    			BarcodedProduct potentialBarcodedProduct = (BarcodedProduct)potentialProduct;
    			if(potentialBarcodedProduct.getBarcode().toString() == currentBarcode.toString()){
    				currentProduct = potentialBarcodedProduct;
    		}
    	}    	
    }
    	
    	return currentProduct;

    }
    
    
    
}