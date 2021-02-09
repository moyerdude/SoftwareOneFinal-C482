/**
 * FXML Controller class
 *
 * @author Stephen Moyer
 */
package Model;


public class OutSourced extends Part {

    private String companyName;

    public OutSourced(int partID, String name, double price, int numInStock, int min, int max, String company) {
        
        
        setPartID(partID);
        setName(name);
        setPrice(price);
        setInStock(numInStock);
        setMin(min);
        setMax(max);
        setCompanyName(company);
    }
    
    //Setter
     public void setCompanyName(String name) {
        this.companyName = name;
    }
    //Getter
    public String getCompanyName() {
        return companyName;
    }

}