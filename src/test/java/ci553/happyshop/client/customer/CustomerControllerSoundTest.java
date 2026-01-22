package ci553.happyshop.client.customer;

import ci553.happyshop.catalogue.exception.underMinPaymentException;
import ci553.happyshop.utility.sound.SoundEffect;
import ci553.happyshop.utility.sound.SoundPlayer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerControllerSoundTest {

    // Records which sounds were played
    static class RecordingSoundPlayer implements SoundPlayer {
        final List<SoundEffect> played = new ArrayList<>();
        @Override public void play(SoundEffect effect) { played.add(effect); }
    }

    // Fake view
    static class FakeView implements CustomerViewPort {
        String lastError;
        @Override public void showCheckoutError(String message) { lastError = message; }
    }

    //control whether checkout succeeds or fails
    static class FakeModel extends CustomerModel {
        boolean failMinPayment = false;

        @Override
        public void checkOut() throws underMinPaymentException {
            if (failMinPayment) throw new underMinPaymentException("MIN PAYMENT");
        }
    }

    @Test
    void checkoutBlocked_playsClickThenError_andShowsMessage() throws SQLException, IOException {
        // Arrange
        CustomerController controller = new CustomerController();
        FakeModel model = new FakeModel();
        model.failMinPayment = true;

        FakeView view = new FakeView();
        RecordingSoundPlayer sound = new RecordingSoundPlayer();

        controller.cusModel = model;
        controller.cusView = view;
        controller.setSoundPlayer(sound);

        // Act
        controller.doAction("Check Out");

        // Assert
        assertEquals(List.of(SoundEffect.CLICK, SoundEffect.ERROR), sound.played);
        assertEquals("MIN PAYMENT", view.lastError);
    }

    @Test
    void checkoutSuccess_playsClickThenSuccess() throws SQLException, IOException {
        // Arrange
        CustomerController controller = new CustomerController();
        FakeModel model = new FakeModel(); // success by default
        FakeView view = new FakeView();
        RecordingSoundPlayer sound = new RecordingSoundPlayer();

        controller.cusModel = model;
        controller.cusView = view;
        controller.setSoundPlayer(sound);

        // Act
        controller.doAction("Check Out");

        // Assert
        assertEquals(List.of(SoundEffect.CLICK, SoundEffect.SUCCESS), sound.played);
        assertNull(view.lastError);
    }
}
