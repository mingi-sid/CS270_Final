import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.*;

public class MediaPlayer implements LineListener {
	Map<String, Clip> audioFiles = new HashMap<String, Clip>();
    boolean playCompleted;
	public void play(String filename) {
		if(!(this.audioFiles.containsKey(filename))) {
			File file = new File(filename);
		    if(file.exists()) {
		        try {
		            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);

		            AudioFormat audioFormat = audioInputStream.getFormat();

		            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);

		            Clip audioClip = (Clip) AudioSystem.getLine(info);
		            

		            audioClip.addLineListener(this);
		            audioClip.open(audioInputStream);
		            audioFiles.put("filename", audioClip);

		            audioClip.start();
		            
		            while (!playCompleted) {
		                // wait for the playback completes
		                try {
		                    Thread.sleep(100);
		                } catch (InterruptedException ex) {
		                    ex.printStackTrace();
		                }
		            }
		             
		            
		            audioClip.close();
		            playCompleted = false;
		            System.out.println("Played "+filename);
		        } catch (UnsupportedAudioFileException | IOException e) {
		            e.printStackTrace();
		        } catch (LineUnavailableException e) {
		            e.printStackTrace();
		        }
		    } else {
		        System.err.println("The selected file doesn't exist!");
		    }
		}
		else {
	        Clip audioClip = audioFiles.get(filename);
	
	        audioClip.start();
	        
	        audioClip.close();
	        System.out.println("Played "+filename+" from memory");
		}
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
