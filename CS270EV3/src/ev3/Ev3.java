package ev3;

import lejos.robotics.SampleProvider;
import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.*;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.utility.Delay;

import java.lang.String;

public class Ev3 {
	private static EV3IRSensor ir_sensor;
	
	
	
	public static void main(String[] args) {

		//aud.systemSound(0);
		
		//EV3 Setting
		EV3 ev3 = (EV3) BrickFinder.getLocal();
		TextLCD lcd = ev3.getTextLCD();
		Keys keys = ev3.getKeys();
	
		//Motor Setting
		
		//Declares the motor to be used
		EV3LargeRegulatedMotor motor = new EV3LargeRegulatedMotor(MotorPort.A);
		
		//Sensor Setting
			
		//Declare IR sensor
		ir_sensor = new EV3IRSensor(SensorPort.S1);
		SampleProvider distanceMode = ir_sensor.getDistanceMode();
		float value[] = new float[distanceMode.sampleSize()];
		
		
		//LCD setting

		int lcd_x = lcd.getTextWidth();
		
		/* Control Section */

		String str = String.valueOf(value[0]) + "\n" + String.valueOf(motor.getTachoCount());
		lcd.clear();
		lcd.drawString(str, 1, 4);
		Delay.msDelay(500);
		
		do {
			distanceMode.fetchSample(value, 0);
			float centimeter = value[0];
			str = String.valueOf(centimeter) + "\n" + String.valueOf(motor.getTachoCount());
			lcd.clear();
			lcd.drawString(str, lcd_x / 2 - 4, 1);
			Delay.msDelay(500);
		} while(keys.getButtons() != Keys.ID_ESCAPE);
		
		//Beep
		//aud.systemSound(0);
		Delay.msDelay(5000);
	}
}
