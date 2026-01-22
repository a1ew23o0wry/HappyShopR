package ci553.happyshop.client.customer;

import java.io.IOException;
import java.sql.SQLException;
import ci553.happyshop.catalogue.exception.underMinPaymentException;
import ci553.happyshop.utility.sound.SoundEffect;
import ci553.happyshop.utility.sound.SoundPlayer;




public class CustomerController {

    public CustomerModel cusModel;
    public CustomerViewPort cusView;

    private SoundPlayer sound = effect -> {};  // default: do nothing

    public void setSoundPlayer(SoundPlayer sound) {
        this.sound = (sound == null) ? (effect -> {}) : sound;
    }



    public void doAction(String action) throws SQLException, IOException {
        sound.play(SoundEffect.CLICK);

        switch (action) {
            case "Search":
                cusModel.searchByIdOrName();
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
                    //plays the sound
                    sound.play(SoundEffect.SUCCESS);
                } catch (underMinPaymentException e) {
                    sound.play(SoundEffect.ERROR);

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
