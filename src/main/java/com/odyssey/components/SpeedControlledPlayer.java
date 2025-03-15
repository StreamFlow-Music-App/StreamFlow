package com.odyssey.components;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;

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
        boolean result = super.decodeFrame();
        if (!result) {
            return false; // End of stream
        }

        if (playbackSpeed > 1.0f) {
            // Skip extra frames for fast playback
            int framesToSkip = (int) (playbackSpeed - 1.0f);
            for (int i = 0; i < framesToSkip; i++) {
                if (!super.decodeFrame()) {
                    return false; // End of stream
                }
            }
        } else if (playbackSpeed < 1.0f) {
            // Repeat frames for slow playback
            int repeatCount = (int) (1.0f / playbackSpeed);
            for (int i = 1; i < repeatCount; i++) {
                if (!super.decodeFrame()) {
                    return false; // End of stream
                }
            }
        }

        return true;
    }
}
