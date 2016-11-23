int pinoLed = 4;
int pinoSensor = 2;

void setup() {
  // put your setup code here, to run once:
  pinMode(pinoLed, OUTPUT);
  pinMode(pinoSensor, INPUT);
  
  digitalWrite(pinoLed, LOW);
  Serial.begin(9600);
}

void loop() {
  delay(200);
  // put your main code here, to run repeatedly:
  boolean presenca = digitalRead(pinoSensor);
  Serial.println(presenca);
  digitalWrite(pinoLed, presenca);
}
