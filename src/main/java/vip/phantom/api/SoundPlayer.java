package vip.phantom.api;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer implements LineListener {

    private final boolean debugMode = false;

    private String lastPathPlayed = "asdljhaksjd";
    private Clip currentAudioClip = null;
    private long currentMicroSecond = 0;
    private float currentVolume = 1;

    public SoundPlayer() {
    }

    public boolean start(String audioFilePath) {
        if (audioFilePath == null) {
            return false;
        }
        if (currentAudioClip != null) {
            stop();
        }
        final File audioFile = new File(audioFilePath);
        if (!audioFile.exists()) {
            System.err.println("The audio doesn't exist: " + audioFile.getAbsolutePath());
        }
        try {
            final AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            final AudioFormat format = audioStream.getFormat();

            final DataLine.Info info = new DataLine.Info(Clip.class, format);

            currentAudioClip = (Clip) AudioSystem.getLine(info);

            currentAudioClip.addLineListener(this);
            currentAudioClip.open(audioStream);
            if (audioFilePath.equalsIgnoreCase(lastPathPlayed)) {
                if (debugMode) {
                    System.out.println("Starting at: " + currentMicroSecond);
                }
                currentAudioClip.setMicrosecondPosition(currentMicroSecond);
            }

            currentAudioClip.start();
            setVolume(currentVolume);
            lastPathPlayed = audioFilePath;
//            while (!playCompleted) {
//                // wait for the playback completes
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//            }
//            audioClip.close();
            return true;
        } catch (UnsupportedAudioFileException ex) {
            System.out.println("The specified audio file is not supported.");
            ex.printStackTrace();
        } catch (LineUnavailableException ex) {
            System.out.println("Audio line for playing back is unavailable.");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Error playing the audio file.");
            ex.printStackTrace();
        }
        return false;
    }

    public void stop() {
        if (currentAudioClip != null) {
            currentMicroSecond = currentAudioClip.getMicrosecondPosition();
            currentAudioClip.close();
        }
    }

    public void skip(int msToSkip) {
        if (currentAudioClip != null) {
            currentAudioClip.setMicrosecondPosition(currentAudioClip.getMicrosecondPosition() + msToSkip);
        }
    }

    public void setVolume(float volume) {
        if (currentAudioClip != null) {
            currentVolume = volume;

            FloatControl gainControl = (FloatControl) currentAudioClip.getControl(FloatControl.Type.MASTER_GAIN);
            float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(Math.min(Math.max(dB, -80), 6.0206f));
        }
    }

    public void increaseVolume(float toIncrease) {
        if (currentAudioClip != null) {
            setVolume(currentVolume + toIncrease);
        }
    }

    public boolean isPlaying() {
        return currentAudioClip != null;
    }

    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();

        if (type == LineEvent.Type.START) {
            if (debugMode) {
                System.out.println("Playback started.");
            }

        } else if (type == LineEvent.Type.STOP) {
            currentAudioClip = null;
            if (debugMode) {
                System.out.println("Playback paused/finished.");
            }
        }
    }

    public long getCurrentMicroSecond() {
        return currentMicroSecond;
    }

    public void setCurrentMicroSecond(long currentMicroSecond) {
        this.currentMicroSecond = currentMicroSecond;
    }

    public String getLastPathPlayed() {
        return lastPathPlayed;
    }

    public void setLastPathPlayed(String lastPathPlayed) {
        this.lastPathPlayed = lastPathPlayed;
    }

    public Clip getCurrentAudioClip() {
        return currentAudioClip;
    }

    public void setCurrentAudioClip(Clip currentAudioClip) {
        this.currentAudioClip = currentAudioClip;
    }

    public float getCurrentVolume() {
        return currentVolume;
    }

    public void setCurrentVolume(float currentVolume) {
        this.currentVolume = currentVolume;
    }
}
