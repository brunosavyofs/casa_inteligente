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
//  f8:b1:56:fc:4d:d3
//  0xf8, 0xb1, 0x56, 0xfc, 0x4c, 0xc4
  0xf8, 0xb1, 0x56, 0xfc, 0x4d, 0xd3
};


// Configurações da comunicação com mosquitto
#include <PubSubClient.h>

#define PORTA_MQTT 18768
#define USUARIO_MQTT "mdzjtvif"
#define SENHA_MQTT "snX5gG5TJs8P"
char MENSAGEM_BROKER[100];

void callback(char* topic, byte* payload, unsigned int length);

PubSubClient client(server, PORTA_MQTT, callback, ethClient);


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

int status_ar = LOW;

DHT dht(PINO_SENSOR_TEMP, TIPO_SENSOR);


// Configurações da funcionalidade 02 - Acionamento automático das luzes da garagem

//Carrega a biblioteca do sensor ultrassonico
#include <Ultrasonic.h>

//Define os pinos para o trigger e echo
#define PINO_TRIGGER 8
#define PINO_ECHO 9
#define PINO_LED_OCUPACAO 12
// Distância que define se a garagem está ocupada
#define DISTANCIA_OCUPADA 10
#define TP_GARAGEM_STATUS "casa/garagem/status"

int status_garagem = LOW;


Ultrasonic ultrasonic(PINO_TRIGGER, PINO_ECHO);

// Configurações da funcionalidade 03 - Alarme com utilização de sensor de movimento e buzzer

#define PINO_BUZZER 10
#define PINO_SENSOR_MOV 3
#define PINO_LED_ALARME 7
#define DELAY_SONORO 500
#define FREQ_BUZZER 1500
#define TP_ALARME_STATUS "casa/alarme/status"

boolean alarme_disparado = false;


void setup() {
  Serial.begin(9600);

  conectar_ethernet();

  // Setup da funcionalidade 01
  setup_ar();

  // Setup da funcionalidade 02
  setup_garagem();

  // Setup da funcionalidade 03
  setup_alarme();
}

void loop() {
  if (!client.connected())
  {
    conectar_mosquitto();
  }
  client.loop();

  ler_temperatura();

  medir_distancia();

  verificar_movimento();

  if (alarme_disparado) {
    //Ligando o buzzer com uma frequencia de 1500 hz.
    tone(PINO_BUZZER, FREQ_BUZZER);   
    delay(DELAY_SONORO);
    
    //Desligando o buzzer.
    noTone(PINO_BUZZER);
    delay(DELAY_SONORO);
  } 

  delay(500);
}

// Metodo para manipular mensagens recebidas do broker
void callback(char* topic, byte* payload, unsigned int length) {
  int i = 0;
  Serial.println("Tópico:  " + String(topic));

  for(i=0; i<length; i++) {
    MENSAGEM_BROKER[i] = payload[i];
  }
  MENSAGEM_BROKER[i] = '\0';
  String msgString = String(MENSAGEM_BROKER);

  Serial.println("Mensagem: " + msgString);

  //TODO: Utilizar um case
  if (String(topic) == String(TP_AR_STATUS)) {
    ligar_ar(msgString.toInt());
  }
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
    informar_temperatura(temperatura);
  }
}

void informar_temperatura(float temperatura) {
  // Variável auxiliar para implementar gambiarra para converter para char[]
  char aux_char[10];
  String(temperatura).toCharArray(aux_char, 10);
  client.publish(TP_AR_TEMPERATURA, aux_char);
}

void ligar_ar(int status) {
    digitalWrite(PINO_LED_AR, status);

    // Código para ligar o ar
}


// Métodos da funcionalidade 02

void setup_garagem() {
  pinMode(PINO_LED_OCUPACAO, OUTPUT);
}

float medir_distancia() {
  long tempo = ultrasonic.timing();
  int distancia = ultrasonic.convert(tempo, Ultrasonic::CM);

  if (distancia <= DISTANCIA_OCUPADA) {
    informar_ocupacao(HIGH);
  } else {
    informar_ocupacao(LOW);
  }
}

void informar_ocupacao(int status) {
  if (status_garagem != status) {
    digitalWrite(PINO_LED_OCUPACAO, status);
    status_garagem = status;
    Serial.println("mudei um status");
    client.publish(TP_GARAGEM_STATUS, status == 0 ? "0" : "1");
  }
}


// Métodos da funcionalidade 03


void setup_alarme() {
  pinMode(PINO_SENSOR_MOV, INPUT);
  pinMode(PINO_LED_ALARME, OUTPUT);
  pinMode(PINO_BUZZER, OUTPUT);
}

void acionar_alarme() {
  if (!alarme_disparado) {
    client.publish(TP_ALARME_STATUS, "1");
    alarme_disparado = true;
    digitalWrite(PINO_LED_ALARME, HIGH);
  } else {
    digitalWrite(PINO_LED_ALARME, LOW);
  }
}

void verificar_movimento() {  
  Serial.println(digitalRead(PINO_SENSOR_MOV));
  if (digitalRead(PINO_SENSOR_MOV) == HIGH) {
    acionar_alarme();
  }
}


// Métodos auxiliares
 
void conectar_mosquitto() {

  while (!client.connected()) {
    Serial.print("Conectando com mosquitto broker...");
    if (client.connect("casa", USUARIO_MQTT, SENHA_MQTT)) {
      Serial.println(" conectado!");
      client.subscribe("casa/ar/status");
    } else {
      Serial.print("falha, rc=");
      Serial.println(client.state());
      delay(TEMPO_NOVA_TENTATIVA);
    }
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
