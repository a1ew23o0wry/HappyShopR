package ci553.happyshop.client.customer;

import java.io.IOException;
import java.sql.SQLException;
import ci553.happyshop.catalogue.exception.underMinPaymentException;



public class CustomerController {

    public CustomerModel cusModel;
    public CustomerView cusView;



    public void doAction(String action) throws SQLException, IOException {
        switch (action) {
            case "Search":
                cusModel.search();
                break;
            case "Add to Trolley":
                cusModel.addToTrolley();
                break;
            case "Cancel":
                cusModel.cancel();
                break;
            case "Check Out":
                try {
                    cusModel.checkOut();
                } catch (underMinPaymentException e) {
                    cusView.showCheckoutError(
                            e.getMessage()
                    );
                    System.out.println(e.getMessage());
                }
                break;
            case "OK & Close":
                cusModel.closeReceipt();
                break;
            case "sort Trolley":
                cusModel.sortTrolley();
                break;
        }
    }

}
