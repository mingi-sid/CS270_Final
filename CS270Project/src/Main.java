import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.*;

public class Main extends Thread {
	public static void main(String [] args) throws Exception {
		Map<String, Pair<byte[], AudioFormat>> audioData = new HashMap<String, Pair<byte[], AudioFormat>>();
		Map<Long, String> audioTime = new HashMap<Long, String>();
		
		//Definition of keys code
		long KEY_ESCAPE	= 0x8000000000000000l;
		long KEY_DOWN 	= 0x4000000000000000l;
		long KEY_ENTER 	= 0x2000000000000000l;
		long KEY_LEFT 	= 0x1000000000000000l;
		long KEY_RIGHT	= 0x0800000000000000l;
		long KEY_UP		= 0x0400000000000000l;
		long IR			= 0x00ffffc000000000l;
		long IR_10		= 0x0080000000000000l;
		long IR_11		= 0x0040000000000000l;
		long IR_12		= 0x0020000000000000l;
		long IR_13		= 0x0010000000000000l;
		long IR_14		= 0x0008000000000000l;
		long IR_15		= 0x0004000000000000l;
		long IR_16		= 0x0002000000000000l;
		long IR_17		= 0x0001000000000000l;
		long IR_18		= 0x0000800000000000l;
		long IR_19		= 0x0000400000000000l;
		long IR_20		= 0x0000200000000000l;
		long IR_21		= 0x0000100000000000l;
		long IR_22		= 0x0000080000000000l;
		long IR_23		= 0x0000040000000000l;
		long IR_24		= 0x0000020000000000l;
		long IR_25		= 0x0000010000000000l;
		long IR_26		= 0x0000008000000000l;
		long IR_27		= 0x0000004000000000l;
		long[] IRs = {0x0080000000000000l,
				0x0040000000000000l,
				0x0020000000000000l,
				0x0010000000000000l,
				0x0008000000000000l,
				0x0004000000000000l,
				0x0002000000000000l,
				0x0001000000000000l,
				0x0000800000000000l,
				0x0000400000000000l,
				0x0000200000000000l,
				0x0000100000000000l,
				0x0000080000000000l,
				0x0000040000000000l,
				0x0000020000000000l,
				0x0000010000000000l,
				0x0000008000000000l,
				0x0000004000000000l};
		
		int penta = 1;
		long loop_duration = 4000;
		
		// Reading all necessary audio files
		String[] audioFileNames = {"Kick_Nerd.wav", "Snare_JackU.wav",
				"A (10).wav", "A (11).wav", "A (12).wav", "A (13).wav", "A (14).wav",
				"A (15).wav", "A (16).wav", "A (17).wav", "A (18).wav", "A (19).wav",
				"A (20).wav", "A (21).wav", "A (22).wav", "A (23).wav", "A (24).wav",
				"A (25).wav", "A (26).wav", "A (27).wav"};
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
		
		// Connect to EV3
		
		Socket sock = new Socket("10.0.1.1", 1234);
		DataInputStream in = new DataInputStream(sock.getInputStream());
		DataOutputStream out = new DataOutputStream(sock.getOutputStream());
		System.out.println("Socket Connection Established");
		
		
		//Main loop starts
		/*
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
		*/
		long servin;
		long prev_in = 0l;
		long time_gap = 150;
		int last_xylophone = -1;
		long prevloop = 0;
		while(true) {
			servin = in.readLong();
			if(servin != 0) {
				System.out.println(Long.toHexString(servin));
			}
			//Escape
			if((servin & KEY_ESCAPE) != 0l) {
				break;
			}
			//Drum machine
			if((servin & KEY_DOWN) != 0l && (prev_in & KEY_DOWN) == 0l) {
				String s = "Kick_Nerd.wav";
				new PlayerThread(audioData.get(s)).start();
				audioTime.put(System.currentTimeMillis() % loop_duration, s);
			}
			if((servin & KEY_ENTER) != 0l && (prev_in & KEY_ENTER) == 0l) {
				String s = "Snare_JackU.wav";
				new PlayerThread(audioData.get(s)).start();
				audioTime.put(System.currentTimeMillis() % loop_duration, s);
			}
			//Drum machine end
			
			//Xylophone
			if((servin & IR) != 0 && (prev_in & IR) != (servin & IR)) {
				//System.out.println("Xylophone");
				int cnt = 0;
				for(cnt = 0; cnt < 18; cnt++) {
					if((servin & IRs[cnt]) != 0) {
						break;
					}
				}
				System.out.println(cnt + 10);
				if(penta != 1) {
					String filename = "A (" + String.valueOf(cnt + 10) + ").wav";
					String s = filename;
					new PlayerThread(audioData.get(s)).start();
					audioTime.put(System.currentTimeMillis() % loop_duration, s);
				}
				if(penta == 1) {
					String filename = "A (10).wav";
					String s = filename;
					if(cnt + 10 >= 10 && cnt + 10 < 13) {
						filename = "A (10).wav";
						s = filename;
					}
					else if(cnt + 10 >= 13 && cnt + 10 < 16) {
						filename = "A (13).wav";
						s = filename;
					}
					else if(cnt + 10 >= 16 && cnt + 10 < 19) {
						filename = "A (15).wav";
						s = filename;
					}
					else if(cnt + 10 >= 19 && cnt + 10 < 21) {
						filename = "A (17).wav";
						s = filename;
					}
					else if(cnt + 10 >= 22 && cnt + 10 < 25) {
						filename = "A (20).wav";
						s = filename;
					}
					else if(cnt + 10 >= 25 && cnt + 10 < 28) {
						filename = "A (22).wav";
						s = filename;
					}
					new PlayerThread(audioData.get(s)).start();
					audioTime.put(System.currentTimeMillis() % loop_duration, s);
				}
			}
			prev_in = servin;
			//Xylophone end
			long currloop = System.currentTimeMillis();
			for(long key: audioTime.keySet()) {
				if(key > prevloop % loop_duration && key <= currloop % loop_duration) {
					new PlayerThread(audioData.get(audioTime.get(key))).start();
				}
			}
			prevloop = currloop;
		}
		
		//Release resources
		try {
			if(sock != null) sock.close();	
			if(in != null) in.close();
			if(out != null) out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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