package ci553.happyshop.client.customer;

public class CusModAdapter implements CustomerActions {

    private final CustomerModel model;

    public CusModAdapter(CustomerModel model) {
        this.model = model;
    }

    @Override
    public void search() throws Exception { model.search(); }

    @Override
    public void addToTrolley() throws Exception { model.addToTrolley(); }

    @Override
    public void cancel() throws Exception { model.cancel(); }

    @Override
    public void checkOut() throws Exception { model.checkOut(); }

    @Override
    public void closeReceipt() throws Exception { model.closeReceipt(); }

    @Override
    public void sortTrolley() throws Exception { model.sortTrolley(); }
}
