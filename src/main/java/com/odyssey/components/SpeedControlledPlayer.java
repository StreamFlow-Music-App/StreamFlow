package com.odyssey.components;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SpeedControlledPlayer extends AdvancedPlayer {
    private float playbackSpeed = 1.0f;

    public SpeedControlledPlayer(InputStream stream) throws JavaLayerException {
        super(stream);
    }

    public void setPlaybackSpeed(float speed) {
        if (speed > 0) {
            this.playbackSpeed = speed;
        }
    }

    @Override
    protected boolean decodeFrame() throws JavaLayerException {
        if (playbackSpeed == 1.0f) {
            // Normal playback speed
            return super.decodeFrame();
        } else if (playbackSpeed > 1.0f) {
            // Speed up playback by skipping frames
            int framesToSkip = (int) (playbackSpeed - 1.0f);
            for (int i = 0; i < framesToSkip; i++) {
                if (!super.decodeFrame()) {
                    return false; // End of stream
                }
            }
            return super.decodeFrame();
        } else {

            boolean result = super.decodeFrame();
            if (result) {
                try {

                    long sleepTime = (long) (1000 / (getFrameRate() * playbackSpeed));
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return result;
        }



    }

    private float getFrameRate() {

        return 24.0f;
    }
}