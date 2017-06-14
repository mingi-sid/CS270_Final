package ev3;

import lejos.robotics.SampleProvider;
import lejos.hardware.Audio;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.*;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.String;
import java.net.ServerSocket;
import java.net.Socket;

public class Ev3 {
	
	
	
	public static void main(String[] args) {
		
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
		
		//aud.systemSound(0);
		
		//EV3 Setting
		
		
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
		Audio aud = ev3.getAudio();

		String str = "";
		lcd.clear();
		lcd.drawString(str, 1, 4);
		
		//Connection Setting
		ServerSocket serv = null;
		Socket sock = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		
		try {
			serv = new ServerSocket(1234);
			str = "Wating for Socket connection";
			lcd.clear();
			lcd.drawString(str, 1, 4);
			
			sock = serv.accept();
			
			in = new DataInputStream(sock.getInputStream());
			out = new DataOutputStream(sock.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		str = "Socket connection established";
		lcd.clear();
		lcd.drawString(str, 1, 4);
		
		int ir_on = 1;
		int motor_on = 0;
		
		//Motor Setting
		
		//Declares the motor to be used
		EV3LargeRegulatedMotor motor = null;
		if(motor_on == 1) motor = new EV3LargeRegulatedMotor(MotorPort.A);
		
		//Sensor Setting
		
		
		//Declare IR sensor
		EV3IRSensor ir_sensor = null;
		SampleProvider distanceMode = null;
		float value[] = null;
		if(ir_on == 1) {
			ir_sensor = new EV3IRSensor(SensorPort.S1);
			distanceMode = ir_sensor.getDistanceMode();
			value = new float[distanceMode.sampleSize()];
		}
		
		//LCD setting
		
		/* Control Section */

		str = "";
		if(ir_on == 1) {
			str = String.valueOf(value[0]);
		}
		if(motor_on == 1) {
			str = str + "\n" + String.valueOf(motor.getTachoCount());
		}
		lcd.clear();
		lcd.drawString(str, 1, 4);

		aud.systemSound(0);
		
		int buttons;
		long data = 0l;
		
		do {
			data = 0l;
			distanceMode.fetchSample(value, 0);
			float centimeter = value[0];
			buttons = keys.getButtons();

			if(ir_on == 1) {
				str = String.valueOf(value[0]);
			}
			if(motor_on == 1) {
				str = str + "\n" + String.valueOf(motor.getTachoCount());
			}
			lcd.clear();
			lcd.drawString(str, 1, 4);

			if((buttons & Keys.ID_ESCAPE) != 0) {
				data = data | KEY_ESCAPE;
			}
			//Drum machine
			if((buttons & Keys.ID_DOWN) != 0) {
				data = data | KEY_DOWN;
			}
			if((buttons & Keys.ID_ENTER) != 0) {
				data = data | KEY_ENTER;
			}
			
			
			//Drum machine ends
			//Xylophone
			if(centimeter >= 5 && centimeter < 42) {
				if((centimeter-5)/2 >= 0 && (centimeter-5)/2 < 18) {
					data = data | IRs[(int) ((centimeter-5)/2)];
				}
			}
			//Xylophone ends
			//Launchpad
			//Launchpad ends
			try {
				out.writeLong(data);
				out.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Delay.msDelay(10);
		} while((buttons & Keys.ID_ESCAPE) == 0);
		
		//Main loop ends
		str = "Terminating";
		lcd.clear();
		lcd.drawString(str, 1, 4);
		
		//Release resources
		try {
			if(sock != null) sock.close();	
			if(in != null) in.close();
			if(out != null) out.close();
			if(serv != null) serv.close();
			if(motor != null) motor.close();
			if(ir_sensor != null) ir_sensor.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Beep
		aud.systemSound(0);
	}
}
