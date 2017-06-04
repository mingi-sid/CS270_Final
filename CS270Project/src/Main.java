import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.*;

public class Main extends Thread {
	public static void main(String [] args) throws Exception {
		Map<String, Pair<byte[], AudioFormat>> audioData = new HashMap<String, Pair<byte[], AudioFormat>>();
		
		// Reading all necessary audio files
		String[] audioFileNames = {"Kick_Nerd.wav", "Snare_JackU.wav"};
		for(int i = 0; i < audioFileNames.length; i++) {
			File file = new File(audioFileNames[i]);
		    if(file.exists()) {
		        try {
		            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
	
		            AudioFormat audioFormat = audioInputStream.getFormat();
		            
		            int size = (int)(audioFormat.getFrameSize() * audioInputStream.getFrameLength());
		            byte[] audioByte = new byte[size];
	
		            audioInputStream.read(audioByte, 0, size);
		            
		            audioData.put(audioFileNames[i], new Pair<byte[], AudioFormat>(audioByte, audioFormat));
		        } catch (UnsupportedAudioFileException | IOException e) {
		            e.printStackTrace();
		        }
		    } else {
		        System.err.println("The selected file "+audioFileNames[i]+" doesn't exist!");
		    }
		}
		System.out.println("Loaded all files");
		
		
		Thread.sleep(1000);
		
		//Main loop starts
		for(int i = 0; i < 5; i++) {
			System.out.println("1");
			new PlayerThread(audioData.get("Kick_Nerd.wav")).start();
			Thread.sleep(300);

			System.out.println("2");
			new PlayerThread(audioData.get("Kick_Nerd.wav")).start();
			new PlayerThread(audioData.get("Snare_JackU.wav")).start();
			Thread.sleep(300);

			System.out.println("3");
			new PlayerThread(audioData.get("Kick_Nerd.wav")).start();
			Thread.sleep(300);

			System.out.println("4");
			new PlayerThread(audioData.get("Snare_JackU.wav")).start();
			new PlayerThread(audioData.get("Kick_Nerd.wav")).start();
			Thread.sleep(300);
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
		//Main loop ends
		
		/*
		for(int i = 0; i < audioFileNames.length; i++) {
			Clip audio = audioClips.get(audioFileNames[i]);
			audio.close();
		}
		*/
	}
}
class PlayerThread extends Thread implements LineListener{
	byte[] audioByte;
	AudioFormat audioFormat;
	boolean playCompleted;
	PlayerThread() {}
	PlayerThread(Pair<?, ?> audioData) {
		this.audioByte = (byte[]) audioData.getLeft();
		this.audioFormat = (AudioFormat) audioData.getRight();
	}
	public void run() {
        Clip audioClip;
        DataLine.Info info = new DataLine.Info(Clip.class, audioFormat, audioByte.length);
		try {
			audioClip = (Clip) AudioSystem.getLine(info);
			audioClip.open(audioFormat, audioByte, 0, audioByte.length);
        
	        audioClip.addLineListener(this);
	        audioClip.setFramePosition(0);
	        audioClip.start();
	        System.out.println(String.valueOf(Thread.currentThread()) + "Playback started.");
	        while (!playCompleted) {
	            // wait for the playback completes
	            try {
	                Thread.sleep(100);
	            } catch (InterruptedException ex) {
	                ex.printStackTrace();
	            }
	        }
	        audioClip.removeLineListener(this);
	        audioClip.close();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return;
	}
	@Override
	public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
         
        if (type == LineEvent.Type.START) {
            System.out.println("Playback started.");
             
        } else if (type == LineEvent.Type.STOP) {
            playCompleted = true;
        }
    }
}
class Pair<L,R> {

	  private final L left;
	  private final R right;

	  public Pair(L left, R right) {
	    this.left = left;
	    this.right = right;
	  }

	  public L getLeft() { return left; }
	  public R getRight() { return right; }

	  @Override
	  public int hashCode() { return left.hashCode() ^ right.hashCode(); }

	  @Override
	  public boolean equals(Object o) {
	    if (!(o instanceof Pair)) return false;
	    Pair<?, ?> pairo = (Pair<?, ?>) o;
	    return this.left.equals(pairo.getLeft()) &&
	           this.right.equals(pairo.getRight());
	  }

}