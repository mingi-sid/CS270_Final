

public class Main extends Thread {
	public static void main(String [] args) throws Exception {
		MediaPlayer player = new MediaPlayer();
		for(int i = 0; i < 5; i++) {
			player.play("Kick_Nerd.wav");
			Thread.sleep(400);

			player.play("Kick_Nerd.wav");
			player.play("Snare_JackU.wav");
			Thread.sleep(400);

			player.play("Kick_Nerd.wav");
			Thread.sleep(400);

			player.play("Kick_Nerd.wav");
			player.play("Snare_JackU.wav");
			Thread.sleep(400);
		}
		/*
		String wating = "Waiting...";
		
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
	}
}
