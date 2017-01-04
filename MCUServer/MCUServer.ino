#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>

const char* ssid = "thank_for";
const char* password = "94267812";


const int LED_PIN = 14;
bool LED_IS_ON = false;

const char* BRIGHTNESS_LEVEL_QUERY_NAME = "level";
const int MAX_BRIGHTNESS_LEVEL = 255;


ESP8266WebServer server(80);

const int led = 13;


// Returns value of the specified GET query param
String getQueryValue(String paramName){
  for (uint8_t i=0; i<server.args(); i++){
    if (server.argName(i) == paramName) {
      return server.arg(i);
    }
  }
  return "";
}


// Toggle LED
void toggleLED(){
  if (LED_IS_ON){
    
//    digitalWrite(LED_PIN, LOW);
    analogWrite(LED_PIN, 0);
    LED_IS_ON = false;
  } else {
//    digitalWrite(LED_PIN, HIGH);
    analogWrite(LED_PIN, MAX_BRIGHTNESS_LEVEL);
    LED_IS_ON = true;
  }
}

// Retuns JSON with current LED state
void LEDstatus(){
  if (LED_IS_ON){
    server.send(200, "text/plain", "{'status': 'on'}");
  } else {
    server.send(200, "text/plain", "{'status': 'off'}");
  }
}

// API to adjust LED brightness
void set_brightness(){
  if (!LED_IS_ON){
    server.send(400, "application/json", "{'message': 'Can not update disabled LED'}");
    return;
  }
  String brightnessString = getQueryValue(BRIGHTNESS_LEVEL_QUERY_NAME);
  int brightnessValue = 0;

  if (brightnessString == ""){
    brightnessValue = 0;
  } else {
    char carray[4];
    brightnessString.toCharArray(carray, sizeof(carray));
    brightnessValue = atoi(carray);
  }

  if (0 <= brightnessValue <= 255){
      analogWrite(LED_PIN, brightnessValue);
      server.send(200, "application/json", "{'status': 'brightness changed'}");
  } else {
      server.send(400, "application/json", "{'message': 'Incorrect value, correct values is between 0 and 255'}");
  }
}


void handleRoot() {
  digitalWrite(led, 1);
  server.send(200, "text/plain", "hello from esp8266!");
  digitalWrite(led, 0);
}

void handleNotFound(){
  digitalWrite(led, 1);
  String message = "File Not Found\n\n";
  message += "URI: ";
  message += server.uri();
  message += "\nMethod: ";
  message += (server.method() == HTTP_GET) ? "GET":"POST";
  message += "\nArguments: ";
  message += server.args();
  message += "\n";
  for (uint8_t i=0; i<server.args(); i++){
    message += " " + server.argName(i) + ": " + server.arg(i) + "\n";
  }
  server.send(404, "text/plain", message);
  digitalWrite(led, 0);
}


void handleLED(){
  Serial.print("Toggling the LED.\n");
  toggleLED();
  if (LED_IS_ON) {
    server.send(200, "application/json", "{'message': 'LED is on'}");
  } else {
    server.send(200, "application/json", "{'message': 'LED is off'}");
  }
  
}


void setup(void){
  pinMode(led, OUTPUT);
  pinMode(LED_PIN, OUTPUT);
  digitalWrite(led, 0);
  Serial.begin(115200);
  WiFi.begin(ssid, password);
  Serial.println("");

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  if (MDNS.begin("esp8266")) {
    Serial.println("MDNS responder started");
  }

  server.on("/", handleRoot);

  server.on("/inline", [](){
    server.send(200, "text/plain", "this works as well");
  });

  server.on("/toggle", handleLED);
  server.on("/status", LEDstatus);
  server.on("/set", set_brightness);

  server.onNotFound(handleNotFound);

  server.begin();
  Serial.println("HTTP server started");
}

void loop(void){
  server.handleClient();
}
