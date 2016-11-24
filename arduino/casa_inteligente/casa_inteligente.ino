#define TEMPO_NOVA_TENTATIVA 5000

// Carrega biblioteca para economia de energia
#include <Narcoleptic.h>

// Configurações da shield ethernet
#include <SPI.h>
#include <Ethernet.h>

//IPAddress ip(10, 60, 20, 15);
//IPAddress server("m13.cloudmqtt.com");
char server[] = "m13.cloudmqtt.com";
EthernetClient ethClient;

// Endereco MAC da shield ethernet
byte mac[] = {
  0xf8, 0xb1, 0x56, 0xfc, 0x4c, 0xc4
};


// Configurações da comunicação com mosquitto
#include <PubSubClient.h>

#define PORTA_MQTT 18768
#define USUARIO_MQTT "mdzjtvif"
#define SENHA_MQTT "snX5gG5TJs8P"
char MENSAGEM_BROKER[100];
//#define PORTA_MQTT 1883

void callback(char* topic, byte* payload, unsigned int length);

PubSubClient client(server, PORTA_MQTT, callback, ethClient);
void callback(char* topic, byte* payload, unsigned int length) {
  int i = 0;
  Serial.println("Menssagem recebida:  " + String(topic));

  for(i=0; i<length; i++)
  {
    MENSAGEM_BROKER[i] = payload[i];
  }
  MENSAGEM_BROKER[i] = '\0';
  String msgString = String(MENSAGEM_BROKER);

  Serial.println("Payload: " + msgString);
}


// Configurações da funcionalidade 01 - Acionamento automático do ar condicionado
 
// Conecte pino 1 do sensor (esquerda) ao +5V
// Conecte pino 2 do sensor ao pino PINO_SENSOR_TEMP
// Conecte pino 4 do sensor ao GND
// Conecte o resistor de 10K entre pin 2 (dados) e ao pino 1 (VCC) do sensor (Apenas se quiser aumentar a precisão)
// Conecte o pino positivo do LED que sinalizará que o ar está ligado no pino PINO_LED_AR

#include "DHT.h"

#define TIPO_SENSOR DHT11
#define PINO_SENSOR_TEMP A1
#define TEMPERATURA_ACIONAMENTO 15
#define PINO_LED_AR 2
#define TP_AR_TEMPERATURA "casa/ar/temperatura"
#define TP_AR_STATUS "casa/ar/status"

//float temperatura;
int status_ar = LOW;

DHT dht(PINO_SENSOR_TEMP, TIPO_SENSOR);


// Configurações da funcionalidade 02 - Acionamento automático das luzes da garagem



// Configurações da funcionalidade 03 - Alarme com utilização de sensor de movimento


void setup() {
  Serial.begin(115200);

  conectar_ethernet();

//  conectar_mosquitto();

  // Setup da funcionalidade 01
  setup_ar();
}

void loop() {
  if (!client.connected())
  {
    conectar_mosquitto();
  }
  ler_temperatura();
  Narcoleptic.delay(5000);
}


// Métodos da funcionalidade 01

void setup_ar() {
  pinMode(PINO_SENSOR_TEMP, INPUT);
  pinMode(PINO_LED_AR, OUTPUT);
  dht.begin();
}

int ler_temperatura() {
  float temperatura = dht.readTemperature();
  // testa se retorno é valido, caso contrário algo está errado.
  if (isnan(temperatura)) {
    Serial.println("Falha ao ler temperatura.");
  } 
  else {
    Serial.print("Temperatura: ");
    Serial.println(temperatura);
    if (temperatura > TEMPERATURA_ACIONAMENTO)  {
      Serial.print("Temperatura > ");
      Serial.println(TEMPERATURA_ACIONAMENTO);
 
      digitalWrite(PINO_LED_AR, HIGH);
      informar_temperatura(temperatura, HIGH);
    } else {
      digitalWrite(PINO_LED_AR, LOW);
      informar_temperatura(temperatura, LOW);
    }
  }
}

void informar_temperatura(float temperatura, int status_ar_) {
  // Variável auxiliar para implementar gambiarra para converter para char[]
  char aux_char[10];
  String(temperatura).toCharArray(aux_char, 10);
  client.publish(TP_AR_TEMPERATURA, aux_char);
  
  if (status_ar != status_ar_) {
    Serial.println("Informa mudanca de status do ar condicionado.");
    status_ar = status_ar_;
    String(status_ar).toCharArray(aux_char, 10);
    client.publish(TP_AR_STATUS, aux_char);
  }
}


// Métodos auxiliares

void conectar_mosquitto() {
//  client.setServer(server, PORTA_MQTT);

  while (!client.connected()) {
    Serial.print("Conectando com mosquitto broker...");
    if (client.connect("casa", "mdzjtvif", "snX5gG5TJs8P")) {
//    if (client.connect("casa")) {
      Serial.println(" conectado!");
    } else {
      Serial.print("falha, rc=");
      Serial.println(client.state());
    }
    delay(TEMPO_NOVA_TENTATIVA);
  }
}

void conectar_ethernet() {
  while (true) {
    Serial.print("Conectando na rede com Ethernet Shield utilizando DHCP... ");
    if (Ethernet.begin(mac)) break;
    Narcoleptic.delay(TEMPO_NOVA_TENTATIVA);
  }
  printIPAddress();
}

void printIPAddress() {
  Serial.print("IP: ");
  for (byte thisByte = 0; thisByte < 4; thisByte++) {
    Serial.print(Ethernet.localIP()[thisByte], DEC);
    Serial.print(".");
  }
  Serial.println("");
}
