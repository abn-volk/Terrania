package net.volangvang.terrania.play;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

public class SoundPlayer {
    private static MediaPlayer fxPlayer = null;
    private static MediaPlayer bgPlayer = null;

    public static void playFx(Context context, String name) {
        if (fxPlayer != null) {
            fxPlayer.release();
            fxPlayer = null;
        }
        fxPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(name);
            fxPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            fxPlayer.prepare();
            fxPlayer.start();
        }
        catch (IOException ignored) {

        }
    }
}
