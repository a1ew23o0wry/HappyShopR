package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.Product;
import ci553.happyshop.storageAccess.DatabaseRW;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CustomerModelNameSearchTest {

    private CustomerModel model;

    @BeforeEach
    void setup() {
        model = new CustomerModel();
        model.setDatabaseRW(new FakeDb());
    }

    @Test
    void blankName_returnsEmptyList() throws SQLException {
        ArrayList<Product> results = model.searchByNameCore("   ");
        assertTrue(results.isEmpty());
    }

    @Test
    void nameSearch_filtersOutOfStock() throws SQLException {
        ArrayList<Product> results = model.searchByNameCore("fruit");
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> p.getStockQuantity() > 0));
    }

    @Test
    void nameSearch_sortsByDescriptionThenId() throws SQLException {
        ArrayList<Product> results = model.searchByNameCore("fruit");
        assertEquals("Apple", results.get(0).getProductDescription());
        assertEquals("Banana", results.get(1).getProductDescription());
    }

    static class FakeDb implements DatabaseRW {

        @Override
        public ArrayList<Product> searchProduct(String keyword) {
            ArrayList<Product> list = new ArrayList<>();

            if (keyword.equalsIgnoreCase("fruit")) {
                list.add(new Product("0010", "Banana", "b.jpg", 2.00, 5));
                list.add(new Product("0002", "Apple", "a.jpg", 1.00, 10));
                list.add(new Product("0003", "Apricot", "c.jpg", 3.00, 0));
            }

            return list;
        }

        @Override
        public Product searchByProductId(String productId) { throw new UnsupportedOperationException(); }

        @Override
        public ArrayList<Product> purchaseStocks(ArrayList<Product> proList) { throw new UnsupportedOperationException(); }

        @Override
        public void updateProduct(String id, String des, double price, String imageName, int stock) { throw new UnsupportedOperationException(); }

        @Override
        public void deleteProduct(String id) { throw new UnsupportedOperationException(); }

        @Override
        public void insertNewProduct(String id, String des, double price, String image, int stock) { throw new UnsupportedOperationException(); }

        @Override
        public boolean isProIdAvailable(String productId) { throw new UnsupportedOperationException(); }
    }
}
