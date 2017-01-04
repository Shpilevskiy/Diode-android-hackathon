#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>

const char* ssid = "thank_for";
const char* password = "94267812";


const int LED_PIN = 14;

const char* BRIGHTNESS_LEVEL_QUERY_NAME = "level";
const int MIN_BRIGHTNESS_LEVEL = 0;
const int MAX_BRIGHTNESS_LEVEL = 255;

int BRIGHTNESS_LEVEL = 0;


ESP8266WebServer server(80);


// Returns value of the specified GET query param
String getQueryValue(String paramName){
  for (uint8_t i=0; i<server.args(); i++){
    if (server.argName(i) == paramName) {
      return server.arg(i);
    }
  }
  return "";
}

// Reads current brightness level
int readBrightnessLevel(){
  return BRIGHTNESS_LEVEL;
}

// Sets current brightness level
void writeBrightnessLevel(int level){
  BRIGHTNESS_LEVEL = level;
  analogWrite(LED_PIN, level);
}

// Toggle LED
void toggleLED(){
  if (BRIGHTNESS_LEVEL > MIN_BRIGHTNESS_LEVEL){
    writeBrightnessLevel(MIN_BRIGHTNESS_LEVEL);
  } else {
    writeBrightnessLevel(MAX_BRIGHTNESS_LEVEL);
  }
}

// Retuns JSON with current LED state
void LEDstatus(){
  if (BRIGHTNESS_LEVEL == MIN_BRIGHTNESS_LEVEL){
    server.send(200, "text/plain", "{'status': 'off'}");
  } else {
    server.send(200, "text/plain", "{'status': 'on'}");
  }
}

// API to adjust LED brightness
void set_brightness(){
  if (BRIGHTNESS_LEVEL == MIN_BRIGHTNESS_LEVEL){
    server.send(400, "application/json", "{'message': 'Can not update disabled LED'}");
    return;
  }

  String brightnessString = getQueryValue(BRIGHTNESS_LEVEL_QUERY_NAME);
  int brightnessValue = MIN_BRIGHTNESS_LEVEL;

  if (brightnessString == ""){
    brightnessValue = MIN_BRIGHTNESS_LEVEL;
  } else {
    char carray[4];
    brightnessString.toCharArray(carray, sizeof(carray));
    brightnessValue = atoi(carray);
  }

  if (MIN_BRIGHTNESS_LEVEL <= brightnessValue <= MAX_BRIGHTNESS_LEVEL){
      writeBrightnessLevel(brightnessValue);
      Serial.print("Brightness level adjusted: ");
      Serial.print(brightnessString + '\n');
      
      server.send(200, "application/json", "{'status': 'brightness changed'}");
  } else {
      server.send(400, "application/json", "{'message': 'Incorrect value, correct values is between 0 and 255'}");
  }
}

// API endpoint to read current brightness level
void getBrignessLevel(){
  int brightnessLevel = readBrightnessLevel();

  char buffer[50];

  sprintf(buffer, "{'level': %d}", brightnessLevel);
  String response(buffer);
  server.send(200, "application/json", response);
}


void handleRoot() {
  server.send(200, "text/plain", "hello from esp8266!");
}

void handleTest() {
  server.send(200, "text/plain", "{'status': 'on'}");
}

void handleNotFound(){
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
}


void handleLED(){
  Serial.print("Toggling the LED.\n");
  toggleLED();
  if (readBrightnessLevel() > MIN_BRIGHTNESS_LEVEL) {
    server.send(200, "application/json", "{'message': 'LED is on'}");
  } else {
    server.send(200, "application/json", "{'message': 'LED is off'}");
  }
  
}


void setup(void){
  pinMode(LED_PIN, OUTPUT);

 
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
  server.on("/toggle", handleLED);
  server.on("/status", LEDstatus);
  server.on("/set", set_brightness);
  server.on("/brightness", getBrignessLevel);
  server.on("/test", handleTest);

  server.onNotFound(handleNotFound);

  server.begin();
  Serial.println("HTTP server started");
}

void loop(void){
  server.handleClient();
}
