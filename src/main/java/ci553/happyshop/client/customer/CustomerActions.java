package ci553.happyshop.client.customer;

public interface CustomerActions {
    void search() throws Exception;
    void addToTrolley() throws Exception;
    void cancel() throws Exception;
    void checkOut() throws Exception;
    void closeReceipt() throws Exception;
    void sortTrolley() throws Exception;
}
