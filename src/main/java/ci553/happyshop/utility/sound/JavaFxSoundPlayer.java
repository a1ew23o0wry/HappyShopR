package ci553.happyshop.utility.sound;

import javafx.scene.media.AudioClip;

import java.net.URL;

public class JavaFxSoundPlayer implements SoundPlayer {

    private final double volume;
    private final boolean enabled;

    public JavaFxSoundPlayer(boolean enabled, double volume) {
        this.enabled = enabled;
        this.volume = volume;
    }

    //if sound is enabled and the sound effect exists
    @Override
    public void play(SoundEffect effect) {
        if (!enabled)
        {return;}
        String path = pathFor(effect);
        if (path == null) return;
        URL url = getClass().getResource(path);
        if (url == null) return;

        //create clip from the path and play
        AudioClip clip = new AudioClip(url.toExternalForm());
        clip.setVolume(volume);
        clip.play();
    }

    private String pathFor(SoundEffect effect) {
        switch (effect) {
            case CLICK:
                return "/sounds/click.mp3";
            case SUCCESS:
                return "/sounds/success.mp3";
            case ERROR:
                return "/sounds/error.mp3";
        }
        return null;
    }
}
