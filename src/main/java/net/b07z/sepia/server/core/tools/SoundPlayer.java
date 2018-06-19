package net.b07z.sepia.server.core.tools;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.tritonus.share.sampled.file.TAudioFileFormat;

/**
 * Plays audio files in a thread or linear.
 * 
 * @author as documented in MP3SPI
 *
 */
public class SoundPlayer {
	
	//observers
	private boolean isPlaying = false;
	public boolean useThread = false;
	private boolean abort = false;
	private double duration = 0.0d;
	private double elapsedTime = 0.0d;
	private double startTime = 0.0d;
	
	/**
	 * Play from URL.
	 * @param url - URL of audio stream
	 */
	public void play(URL url){
		duration = 0.0d;
		elapsedTime = 0.0d;		startTime = 0.0d;
		if (url.toString().trim().isEmpty()){
			return;
		}
		try {
			AudioInputStream in = AudioSystem.getAudioInputStream(url);
			if (url.toString().endsWith(".wav") || url.toString().endsWith(".wave")){
			    duration = getDurationWavFile(in);
			}
			playStream(in, useThread);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Play from file.
	 * @param file - file to play
	 */
	public void play(File file){
		duration = 0.0d;
		elapsedTime = 0.0d;		startTime = 0.0d;
		try {
			AudioInputStream in = AudioSystem.getAudioInputStream(file);
			if (file.getName().endsWith(".wav") || file.getName().endsWith(".wave")){
			    duration = getDurationWavFile(in);
			}
			playStream(in, useThread);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Play from resource which can be either a file path or URL (starting with "http"...).
	 * @param resource - String pointing to resource
	 */
	public void play(String resource){
		duration = 0.0d;
		elapsedTime = 0.0d;		startTime = 0.0d;
		if (resource.trim().isEmpty()){
			return;
		}
		//Stream from URL
		if (resource.toLowerCase().startsWith("http") || resource.toLowerCase().startsWith("ftp") || resource.toLowerCase().startsWith("scp")){
			try {
				URL url = new URL(resource);
				AudioInputStream in = AudioSystem.getAudioInputStream(url);
				if (url.toString().endsWith(".wav") || url.toString().endsWith(".wave")){
				    duration = getDurationWavFile(in);
				}
				playStream(in, useThread);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		//Stream from local file
		}else{
			try {
				File file = new File(resource);
				AudioInputStream in = AudioSystem.getAudioInputStream(file);
				if (file.getName().endsWith(".wav") || file.getName().endsWith(".wave")){
				    duration = getDurationWavFile(in);
				}
				playStream(in, useThread);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Play from audio stream.
	 * @param in - AudioInputStream
	 */
	public void playStream(AudioInputStream in, boolean use_a_thread){
		//if there is sound already, abort it and wait for it to finish
		boolean freeToPlay = true;
		if (isPlaying){
			abort = true;
			freeToPlay = waitForSound(3000);
		}
			
		if (freeToPlay){
			isPlaying = true;
			abort = false;
			//use thread
			if (use_a_thread){
				Thread thread = new Thread(){
				    public void run(){
				    	playStream(in);
				    }
				};
				thread.start();
			}
			//don't use a thread
			else{
				playStream(in);
			}
			
		}else{
			System.err.println("SoundPlayer is not free to play sound. Maybe it got stuck :-(");
		}
	}
	
	/**
	 * Check if player is playing or at least thinks it is still playing.
	 * @return - true/false
	 */
	public boolean isPlaying(){
		return isPlaying;
	}
	
	/**
	 * Get total duration of sound file in seconds. You might want to wait a couple of ms before checking that value or update later.
	 * @return - seconds as double. If duration can't be read returns -1.
	 */
	public double getDuration(){
		if (duration > 0.0)
			return duration;
		else
			return -1;
	}
	
	public double getElapsedTime(){
		elapsedTime = (System.currentTimeMillis() - startTime)/1000.0d;
		return elapsedTime;
	}
	
	/**
	 * Stop player.
	 * @return - true if sound ended as planned / false if got no end signal.
	 */
	public boolean stop(){
		abort = true;
		boolean regularStop = waitForSound(2000);
		isPlaying = false;
		abort = false;
		return regularStop;
	}
	
	/**
	 * If the player is threaded you can use this to wait for the sound to finish or until a maximum wait-time is reached.
	 * 
	 * @param max_wait_ms - max time to wait in milliseconds
	 * @return true if sound ended / false if max_wait exceeded
	 */
	public boolean waitForSound(long max_wait_ms){
		long waited = 0;
		while(isPlaying & waited < max_wait_ms){
			//sleep
			try {	Thread.sleep(50);	} catch (InterruptedException e) {	e.printStackTrace();	}
			waited += 100;
		}
		if (isPlaying){
			return false;
		}else{
			return true;
		}
	}
	
	//-------------------------------------------------------------
	
	//Play the stream
	private void playStream(AudioInputStream in){
		try {   
		    AudioInputStream din = null;
		    AudioFormat baseFormat = in.getFormat();
		    AudioFormat decodedFormat = new AudioFormat(
			    		AudioFormat.Encoding.PCM_SIGNED,
			    		baseFormat.getSampleRate(),
			            16,
			            baseFormat.getChannels(),
			            baseFormat.getChannels() * 2,
			            baseFormat.getSampleRate(),
			            false
		            );
		    //decoded stream
		    din = AudioSystem.getAudioInputStream(decodedFormat, in);
		    // Play now.
		    rawplay(decodedFormat, din);
		    in.close();
		    isPlaying = false;
		    abort = false;
	    
		}catch (Exception e){
			//Handle exception.
			isPlaying = false;
			abort = false;
		}
	}
	
	//low level player
	private void rawplay(AudioFormat targetFormat, AudioInputStream din) throws IOException,                                                                                                LineUnavailableException
	{
		byte[] data = new byte[4096];
		SourceDataLine line = getLine(targetFormat);
		if (line != null)
		{
			// Start
		    line.start();
		    startTime = System.currentTimeMillis();
		    @SuppressWarnings("unused")
			int nBytesRead = 0, nBytesWritten = 0;
		    while (nBytesRead != -1 && !abort)
		    {
		        nBytesRead = din.read(data, 0, data.length);
		        if (nBytesRead != -1)
		        	nBytesWritten = line.write(data, 0, nBytesRead);
		    }
		    // Stop
		    line.drain();
		    line.stop();
		    line.close();
		    din.close();
		}
	}

	//get system source to play from 
	private SourceDataLine getLine(AudioFormat audioFormat) throws LineUnavailableException
	{
		SourceDataLine res = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		res = (SourceDataLine) AudioSystem.getLine(info);
		res.open(audioFormat);
		return res;
	} 
	
	//get duration of sound file
	public static double getDurationWavFile(AudioInputStream in){
		double duration = (in.getFrameLength()+0.0) / in.getFormat().getFrameRate();
		if (duration < 0){
			duration = -1;
		}
		return duration;
	}
	public static double getDuration(File file){
		try {
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
			return getDuration(fileFormat);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	public static double getDuration(URL url){
		try {
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(url);
			return getDuration(fileFormat);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}
	private static double getDuration(AudioFileFormat fileFormat) {
		try {
		    if (fileFormat instanceof TAudioFileFormat) {
		        Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
		        //--debug:
				for (Entry<?, ?> entry : properties.entrySet()) {
				  String key = (String) entry.getKey();
				  Object value = entry.getValue();
				  System.out.println("key: " + key + " - value: " + value);		//debug
				}
				//--------
		        String key = "duration";
		        Long microseconds = (Long) properties.get(key);
		        int mili = (int) (microseconds / 1000);
		        return mili/1000.0d;
		    }
		    return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

}
