import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.*;

public class Main extends Thread {
	public static void main(String [] args) throws Exception {
		Map<String, Clip> audioClips = new HashMap<String, Clip>();
		
		// Reading all necessary audio files
		String[] audioFileNames = {"Kick_Nerd.wav", "Snare_JackU.wav"};
		for(int i = 0; i < audioFileNames.length; i++) {
			File file = new File(audioFileNames[i]);
		    if(file.exists()) {
		        try {
		            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
	
		            AudioFormat audioFormat = audioInputStream.getFormat();
	
		            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);
	
		            Clip audioClip = (Clip) AudioSystem.getLine(info);
		            
		            audioClip.open(audioInputStream);
		            audioClips.put(audioFileNames[i], audioClip);
		        } catch (UnsupportedAudioFileException | IOException e) {
		            e.printStackTrace();
		        } catch (LineUnavailableException e) {
		            e.printStackTrace();
		        }
		    } else {
		        System.err.println("The selected file "+audioFileNames[i]+" doesn't exist!");
		    }
		}
		
		
		for(int i = 0; i < 5; i++) {
			new PlayerThread(audioClips.get("Kick_Nerd.wav")).start();
			Thread.sleep(400);

			new PlayerThread(audioClips.get("Kick_Nerd.wav")).start();
			new PlayerThread(audioClips.get("Snare_JackU.wav")).start();
			Thread.sleep(400);

			new PlayerThread(audioClips.get("Kick_Nerd.wav")).start();
			Thread.sleep(400);

			new PlayerThread(audioClips.get("Snare_JackU.wav")).start();
			new PlayerThread(audioClips.get("Kick_Nerd.wav")).start();
			Thread.sleep(400);
		}
		/*
		String waiting = "Waiting...";
		
		RemoteEV3 ev3 = new RemoteEV3("10.0.1.1");
		ev3.setDefault();
		
		Keys keys1 = ev3.getKeys();
		
		
		int buttons;
		do {
			buttons = keys1.getButtons();
			if((buttons & Keys.ID_ENTER) != 0) {
				play("Kick_Nerd.wav");
			}
			Delay.msDelay(50);
		} while((buttons & Keys.ID_ESCAPE) == 0);
		*/
		for(int i = 0; i < audioFileNames.length; i++) {
			Clip audio = audioClips.get(audioFileNames[i]);
			audio.close();
		}
	}
}
class PlayerThread extends Thread implements LineListener{
	Clip audioClip;
	boolean playCompleted;
	PlayerThread() {}
	PlayerThread(Clip audioClip) {
		this.audioClip = audioClip;
	}
	public void run() {
        this.audioClip.addLineListener(this);
        audioClip.setFramePosition(0);
        audioClip.start();
        audioClip.setFramePosition(0);
	}
	@Override
	public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
         
        if (type == LineEvent.Type.START) {
            System.out.println("Playback started.");
             
        } else if (type == LineEvent.Type.STOP) {
            playCompleted = true;
            System.out.println("Playback completed.");
        }
    }
}