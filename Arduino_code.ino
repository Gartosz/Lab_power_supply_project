#include <Wire.h>
#include <Adafruit_INA219.h>
//initialize current sensor
Adafruit_INA219 ina219;
//set variables
int manualCurrentPin = A3;
int pwmPin = 3;
int pwmValue = 0;
float expected = 20.0;

void setup(void) 
{
  pinMode(manualCurrentPin, INPUT);
  pinMode(pwmPin, OUTPUT);

  //set 2nd timer (pins 3 & 11) to 31,4 kHz
  TCCR2B = 0b00000001;
  TCCR2A = 0b00000011;

  Serial.begin(115200);

  //begin INA219 operations and set mode
  if (! ina219.begin()) {
    Serial.println("Failed to find INA219 chip");
    while (1) { delay(10); }
  }
  ina219.setCalibration_16V_400mA();

void loop(void) 
{
    
  float shuntvoltage = 0;
  float busvoltage = 0;
  float current_mA = 0;
  float loadvoltage = 0;
  float power_mW = 0;

  //get INA219 values
  shuntvoltage = ina219.getShuntVoltage_mV();
  busvoltage = ina219.getBusVoltage_V();
  current_mA = ina219.getCurrent_mA();
  power_mW = ina219.getPower_mW();
  loadvoltage = busvoltage + (shuntvoltage / 1000);

  //adjust PWM cycle to expected and measure current
  if (expected < current_mA)
    pwmValue = constrain(--pwmValue, 0, 255);

  else if (expected > current_mA)
    pwm = constrain(++pwmValue, 0, 255);

  analogWrite(pwmPin,pwmValue);



}
