#include "MMA7660.h"
MMA7660 accelerometer;

int pushPin = 7;
int xPin = 0;
int yPin = 1;
int xMove = 0;
int yMove = 0;

int valuePush = HIGH;
int valueX = 0;
int valueY = 0;

int currentX =0;
int currentY =0;
int nextX =0;
int nextY =0;

void setup() {
  accelerometer.init();
  pinMode(pushPin, INPUT);
  Serial.begin(9600);
  digitalWrite(pushPin,HIGH);
}

void loop() {  
  float aX, aY, aZ;
  int velocityX = 0, velocityY = 0, velocityZ = 0;
  int clickY = 0;
  
  for (int i = 0; i < 50; i++) {
    accelerometer.getAcceleration(&aX, &aY, &aZ);
    velocityX += aX;
    velocityY += aY;
    velocityZ += aZ;
  }

  if (20 < velocityX < 20 && 20 < velocityY < 20 && velocityZ > 20) {
    clickY = 1;
    Serial.println(String(0) + " " + String(0) + " " + 1);
    delay(50);
    Serial.println(String(0) + " " + String(0) + " " + 1);
    delay(40);
  }

  nextX = int(velocityY);
  nextY = int(velocityX);

  Serial.println(String(int(velocityY)) + " " + String(int(velocityX)) + " " + clickY); // output to Java program

  if (currentX < 0 && nextX > 0) {
    Serial.println(String(0) + " " + String(-10) + " " + 0); // left
    delay(40);
  }
  if (currentX > 0 && nextX < 0) {
    Serial.println(String(0) + " " + String(10) + " " + 0); // right
    delay(40);
  }
  if (currentY < 0 && nextY > 0) {
    Serial.println(String(10) + " " + String(0) + " " + 0); // up
    delay(40);
  }
  if (currentY > 0 && nextY < 0) {
    Serial.println(String(-10) + " " + String(0) + " " + 0); // down
    delay(40);
  }

  currentX = nextX;
  currentY = nextY;
  
  velocityX = 0;
  velocityY = 0;
  velocityZ = 0;
  clickY = 0;
}
