package name.matco.checkmate.ui;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class UIUtils {
	
	public static void playCheckSound() {
		final float duration = 0.2f; // seconds
		final int sampleRate = 8000;
		final int numSamples = (int) (duration * sampleRate);
		final double sample[] = new double[numSamples];
		final double freqOfTone = 15000; // hz
		
		final byte generatedSnd[] = new byte[2 * numSamples];
		
		// generate tone
		// fill out the array
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
		}
		
		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample) {
			// scale to maximum amplitude
			final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
			
		}
		
		// play tone
		final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				sampleRate, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
				AudioTrack.MODE_STATIC);
		audioTrack.write(generatedSnd, 0, generatedSnd.length);
		audioTrack.play();
	}
	
	public static void playCheckmateSound() {
		final float duration = 3f; // seconds
		final int sampleRate = 8000;
		final int numSamples = (int) (duration * sampleRate);
		final double sample[] = new double[numSamples];
		final double freqOfTone = 440; // hz
		
		final byte generatedSnd[] = new byte[2 * numSamples];
		
		// generate tone
		// fill out the array
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
		}
		
		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample) {
			// scale to maximum amplitude
			final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
			
		}
		
		// play tone
		final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				sampleRate, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
				AudioTrack.MODE_STATIC);
		audioTrack.write(generatedSnd, 0, generatedSnd.length);
		audioTrack.play();
	}
	
}
