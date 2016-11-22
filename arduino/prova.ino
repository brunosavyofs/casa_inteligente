// Carrega a biblioteca do sensor ultrassonico
#include <Ultrasonic.h>

// Carrega bibliotecas para a shield ethernet
#include <SPI.h>
#include <Ethernet.h>
#include <PubSubClient.h>

// Carrega biblioteca para economia de energia
#include <Narcoleptic.h>

// Define os pinos para o trigger e echo
#define PINO_TRIGGER 4
#define PINO_ECHO 5
#define LED_VERMELHO 8
#define LED_VERDE 9

// Endereco MAC da shield ethernet
byte mac[] = {
  0x64, 0x1c, 0x67, 0x79, 0xc5, 0xb3
};
bool ocupado = true;
IPAddress ip(10, 30, 2, 226);
IPAddress server(10, 30, 2, 127);
EthernetClient ethClient;
PubSubClient client(ethClient);

// Inicializa o sensor nos pinos definidos acima
Ultrasonic ultrasonic(PINO_TRIGGER, PINO_ECHO);

// Metodo para conexao com o broker
void reconnect()
{
  while (!client.connected()) {
    Serial.print("Attempting MQTT connection...");
    if (client.connect("garagem", "m", "m"))
    {
      Serial.println("connected");
    }
    else
    {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      delay(5000); // Wait 5 seconds before retrying
    }
  }
}
 
void setup()
{
  Serial.begin(9600);
  pinMode(LED_VERMELHO,OUTPUT);
  pinMode(LED_VERDE,OUTPUT);

  Narcoleptic.disableTimer1();
  Narcoleptic.disableTimer2();
//  Narcoleptic.disableSerial();
  Narcoleptic.disableADC();
  Narcoleptic.disableWire();
//  Narcoleptic.disableSPI();

  // Inicializa conexao Ethernet
  if (Ethernet.begin(mac) == 0)
  {
    Serial.println("Failed to configure Ethernet using DHCP");
    for (;;);
  }
  
  // print your local IP address:
  printIPAddress();

  client.setServer(server, 8883);
}
 
void loop()
{
  if (!client.connected())
  {
    reconnect();
  }
  client.loop();
  
  // Le as informacoes do sensor, em cm e pol
  float cmMsec;
  long microsec = ultrasonic.timing();
  cmMsec = ultrasonic.convert(microsec, Ultrasonic::CM);

  if(cmMsec > 10)
  {
    if(ocupado) controlarLeds(LOW, HIGH);    
  }
  else
  {
    if(!ocupado) controlarLeds(HIGH, LOW);
  }

//  delay(1000);

  Narcoleptic.delay(500); // During this time power consumption is minimised
}

void controlarLeds(int vermelho, int verde)
{
  ocupado = !ocupado;
  digitalWrite(LED_VERDE,verde);
  digitalWrite(LED_VERMELHO,vermelho);
  client.publish("casa/garagem/situacao", ocupado ? "1" : "0");
}

void printIPAddress()
{
  Serial.print("My IP address: ");
  for (byte thisByte = 0; thisByte < 4; thisByte++)
  {
    Serial.print(Ethernet.localIP()[thisByte], DEC);
    Serial.print(".");
  }
  Serial.println();
}


























