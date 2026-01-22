package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Order;
import ci553.happyshop.catalogue.Product;
import ci553.happyshop.storageAccess.DatabaseRW;
import ci553.happyshop.orderManagement.OrderHub;
import ci553.happyshop.utility.StorageLocation;
import ci553.happyshop.utility.ProductListFormatter;
import ci553.happyshop.catalogue.exception.underMinPaymentException;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO
 * You can either directly modify the CustomerModel class to implement the required tasks,
 * or create a subclass of CustomerModel and override specific methods where appropriate.
 */

//added

public class CustomerModel{
    //added
    private RemoveProductNotifier remover = new RemoveProductNotifier();
    public CustomerView cusView;
    public DatabaseRW databaseRW; //Interface type, not specific implementation
                                  //Benefits: Flexibility: Easily change the database implementation.

    private Product theProduct =null; // product found from search
    private ArrayList<Product> trolley =  new ArrayList<>(); // a list of products in trolley

    private ArrayList<Product> nameSearchResults = new ArrayList<>(); //search by name
    void setDatabaseRW(DatabaseRW db) {
        this.databaseRW = db;
    }

    // Four UI elements to be passed to CustomerView for display updates.
    private String imageName = "imageHolder.jpg";                // Image to show in product preview (Search Page)
    private String displayLaSearchResult = "No Product was searched yet"; // Label showing search result message (Search Page)
    private String displayTaTrolley = "";                                // Text area content showing current trolley items (Trolley Page)
    private String displayTaReceipt = "";// Text area content showing receipt after checkout (Receipt Page)
    private static final double MIN_ORDER_VALUE = 5.00;


    //SELECT productID, description, image, unitPrice,inStock quantity
    void search() throws SQLException {
        String productId = cusView.tfId.getText().trim();
        if(!productId.isEmpty()){
            theProduct = databaseRW.searchByProductId(productId); //search database
            if(theProduct != null && theProduct.getStockQuantity()>0){
                double unitPrice = theProduct.getUnitPrice();
                String description = theProduct.getProductDescription();
                int stock = theProduct.getStockQuantity();

                String baseInfo = String.format("Product_Id: %s\n%s,\nPrice: £%.2f", productId, description, unitPrice);
                String quantityInfo = stock < 100 ? String.format("\n%d units left.", stock) : "";
                displayLaSearchResult = baseInfo + quantityInfo;
                System.out.println(displayLaSearchResult);
            }
            else{
                theProduct=null;
                displayLaSearchResult = "No Product was found with ID " + productId;
                System.out.println("No Product was found with ID " + productId);
            }
        }else{
            theProduct=null;
            displayLaSearchResult = "Please type ProductID";
            System.out.println("Please type ProductID.");
        }
        updateView();
    }

    //name search
    public void searchByIdOrName() throws SQLException {

        String id = cusView.tfId.getText().trim();
        String name = cusView.tfName.getText().trim();

        if (!id.isEmpty()) {
            search();
        } else {
            searchByName();
        }
    }

    ArrayList<Product> searchByNameCore(String rawName) throws SQLException {

        String name = (rawName == null) ? "" : rawName.trim();
        nameSearchResults.clear();
        theProduct = null;

        if (name.isEmpty()) {
            displayLaSearchResult = "Please type a product name";
            return new ArrayList<>();
        }

        //
        nameSearchResults = databaseRW.searchProduct(name);

        // dont show in stock
        nameSearchResults.removeIf(p -> p == null || p.getStockQuantity() <= 0);

        // Make results stable/predictable
        nameSearchResults.sort(java.util.Comparator
                .comparing(Product::getProductDescription, String.CASE_INSENSITIVE_ORDER)
                .thenComparing(Product::getProductId));

        if (nameSearchResults.isEmpty()) {
            displayLaSearchResult = "No products match \"" + name + "\"";
            return new ArrayList<>();
        }

        // If exactly one match, behave like normal search (set theProduct + show details)
        if (nameSearchResults.size() == 1) {
            theProduct = nameSearchResults.get(0);
            Product p = theProduct;

            displayLaSearchResult = String.format(
                    "Product_Id: %s\n%s,\nPrice: £%.2f%s",
                    p.getProductId(),
                    p.getProductDescription(),
                    p.getUnitPrice(),
                    (p.getStockQuantity() < 100 ? "\n" + p.getStockQuantity() + " units left." : "")
            );
            return new ArrayList<>(nameSearchResults);
        }

        // Multiple matches show a short list user pick an ID
        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(nameSearchResults.size())
                .append(" matches for \"").append(name).append("\".\n")
                .append("Type an ID (from below) into the ID box to select:\n\n");

        int limit = Math.min(10, nameSearchResults.size());
        for (int i = 0; i < limit; i++) {
            Product p = nameSearchResults.get(i);
            sb.append(p.getProductId())
                    .append(" - ")
                    .append(p.getProductDescription())
                    .append(" (£")
                    .append(String.format("%.2f", p.getUnitPrice()))
                    .append(")\n");
        }
        if (nameSearchResults.size() > limit) {
            sb.append("\n...and ").append(nameSearchResults.size() - limit).append(" more");
        }

        displayLaSearchResult = sb.toString();
        return new ArrayList<>(nameSearchResults);
    }
    void searchByName() throws SQLException {
        searchByNameCore(cusView.tfName.getText());
        updateView();
    }


    void addToTrolley(){
        if(theProduct!= null){

            // trolley.add(theProduct) — Product is appended to the end of the trolley.
            // To keep the trolley organized, add code here or call a method that:
            //TODO
            // 1. Merges items with the same product ID (combining their quantities).
            // 2. Sorts the products in the trolley by product ID.

            Product item = new Product(
                    theProduct.getProductId(),
                    theProduct.getProductDescription(),
                    theProduct.getProductImageName(),
                    theProduct.getUnitPrice(),
                    theProduct.getStockQuantity()
            );
            item.setOrderedQuantity(1);

            trolley.add(item);

            //new
            trolley = groupProductsById(trolley);

            trolley.sort(Comparator.comparing((Product::getProductId)));

            displayTaTrolley = ProductListFormatter.buildString(trolley); //build a String for trolley so that we can show it
        }
        else{
            displayLaSearchResult = "Please search for an available product before adding it to the trolley";
            System.out.println("must search and get an available product before add to trolley");

            //added
            remover.cusView = cusView;



        }
        displayTaReceipt=""; // Clear receipt to switch back to trolleyPage (receipt shows only when not empty)
        updateView();
    }

    void sortTrolley() {

    }
    private double calculateTotal(ArrayList<Product> trolley) {
        for (Product p : trolley) {
            System.out.println(
                    "DEBUG trolley item: " + p.getProductId() +
                            " price=" + p.getUnitPrice() +
                            " orderedQty=" + p.getOrderedQuantity() +
                            " stockQty=" + p.getStockQuantity()
            );
        }
        double total = 0.0;
        for (Product p : trolley) {
            total += p.getUnitPrice() * p.getOrderedQuantity();
        }
        return total;
    }

    void checkOut() throws IOException, SQLException, underMinPaymentException{






        if(!trolley.isEmpty()){
            // Group the products in the trolley by productId to optimize stock checking
            // Check the database for sufficient stock for all products in the trolley.
            // If any products are insufficient, the update will be rolled back.
            // If all products are sufficient, the database will be updated, and insufficientProducts will be empty.
            // Note: If the trolley is already organized (merged and sorted), grouping is unnecessary.
            ArrayList<Product> groupedTrolley= groupProductsById(trolley);
            double total = calculateTotal(groupedTrolley);
            if (total < MIN_ORDER_VALUE) {
                throw new underMinPaymentException(
                        String.format("Minimum order value is £%.2f - Your total is £%.2f.", MIN_ORDER_VALUE, total)
                );
            }

            ArrayList<Product> insufficientProducts= databaseRW.purchaseStocks(groupedTrolley);


            if(insufficientProducts.isEmpty()){ // If stock is sufficient for all products
                //get OrderHub and tell it to make a new Order
                OrderHub orderHub =OrderHub.getOrderHub();
                Order theOrder = orderHub.newOrder(trolley);
                trolley.clear();
                displayTaTrolley ="";
                displayTaReceipt = String.format(
                        "Order_ID: %s\nOrdered_Date_Time: %s\n%s",
                        theOrder.getOrderId(),
                        theOrder.getOrderedDateTime(),
                        ProductListFormatter.buildString(theOrder.getProductList())
                );
                System.out.println(displayTaReceipt);

            }
            else{ // Some products have insufficient stock — build an error message to inform the customer
                StringBuilder errorMsg = new StringBuilder();
                for(Product p : insufficientProducts){
                    errorMsg.append("\u2022 "+ p.getProductId()).append(", ")
                            .append(p.getProductDescription()).append(" (Only ")
                            .append(p.getStockQuantity()).append(" available, ")
                            .append(p.getOrderedQuantity()).append(" requested)\n");
                }
                theProduct=null;

                //TODO
                // Add the following logic here:
                // 1. Remove products with insufficient stock from the trolley.
                // 2. Trigger a message window to notify the customer about the insufficient stock, rather than directly changing displayLaSearchResult.
                //You can use the provided RemoveProductNotifier class and its showRemovalMsg method for this purpose.
                //remember close the message window where appropriate (using method closeNotifierWindow() of RemoveProductNotifier class)
                displayLaSearchResult = "Checkout failed due to insufficient stock for the following products:\n" + errorMsg.toString();
                System.out.println("stock is not enough");

                //PRODUCT REMOVAL POPUP


            }
        }
        else{
            displayTaTrolley = "Your trolley is empty";
            System.out.println("Your trolley is empty");
        }




        updateView();
    }

    /**
     * Groups products by their productId to optimize database queries and updates.
     * By grouping products, we can check the stock for a given `productId` once, rather than repeatedly
     */
    private ArrayList<Product> groupProductsById(ArrayList<Product> proList) {
        Map<String, Product> grouped = new HashMap<>();
        for (Product p : proList) {
            String id = p.getProductId();
            if (grouped.containsKey(id)) {
                Product existing = grouped.get(id);
                existing.setOrderedQuantity(existing.getOrderedQuantity() + p.getOrderedQuantity());
            } else {
                //revise - just to get it to work
                // Copy product AND its current orderedQuantity
                Product copy = new Product(
                        p.getProductId(),
                        p.getProductDescription(),
                        p.getProductImageName(),
                        p.getUnitPrice(),
                        p.getStockQuantity()
                );
                copy.setOrderedQuantity(p.getOrderedQuantity());
                grouped.put(id, copy);
            }
        }
        return new ArrayList<>(grouped.values());
    }

    void cancel(){
        trolley.clear();
        displayTaTrolley="";
        updateView();
    }
    void closeReceipt(){
        displayTaReceipt="";
    }

    void updateView() {
        if(theProduct != null){
            imageName = theProduct.getProductImageName();
            String relativeImageUrl = StorageLocation.imageFolder +imageName; //relative file path, eg images/0001.jpg
            // Get the full absolute path to the image
            Path imageFullPath = Paths.get(relativeImageUrl).toAbsolutePath();
            imageName = imageFullPath.toUri().toString(); //get the image full Uri then convert to String
            System.out.println("Image absolute path: " + imageFullPath); // Debugging to ensure path is correct
        }
        else{
            imageName = "imageHolder.jpg";
        }
        cusView.update(imageName, displayLaSearchResult, displayTaTrolley,displayTaReceipt);
    }
     // extra notes:
     //Path.toUri(): Converts a Path object (a file or a directory path) to a URI object.
     //File.toURI(): Converts a File object (a file on the filesystem) to a URI object

    //for test only
    public ArrayList<Product> getTrolley() {
        return trolley;
    }
}
