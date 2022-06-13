
#include <ESP8266WiFi.h>
#include<FirebaseArduino.h>
#include <ArduinoJson.h>




//
// Copyright 2015 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

// FirebaseDemo_ESP8266 is a sample that demo the different functions
// of the FirebaseArduino API.


static const uint8_t D0 = 2;
static const uint8_t D1 = 4;
static const uint8_t D2 = 3;
static const uint8_t D6 = 7;
static const uint8_t D8 = 8;

#include <ArduinoJson.h>
#include <DHT.h>
#define DHT_PIN D6
#define DHTTYPE DHT11                                        // Initialize dht type as DHT 11
DHT dht(DHT_PIN, DHTTYPE);


// Set these to run example.
#define FIREBASE_HOST "https://cpi-project-b42b5-default-rtdb.firebaseio.com/"
#define FIREBASE_AUTH "K58KQNBK2d0jyOJy2NQ9Mbdk6V6VVYgwkoDfoeWA"
#define PROJECT_ID "cpi-project-b42b5" 
#define WIFI_SSID "INGENIUMS-AP"
#define WIFI_PASSWORD "ingeniums$2022"


float mov;

void setup() {
 // Serial.begin(256000);
  Serial.begin(115200);
  //Serial.begin(9600);
  dht.begin();   
  pinMode(D1, OUTPUT);
  pinMode(D0, OUTPUT);
  pinMode(D8, INPUT);
  pinMode(D2, INPUT);
  pinMode(D6, INPUT);

   //std::shared_ptr<StaticJsonDocument<FIREBASE_JSONDOCUMENT_SIZE>> buffer_;
  // connect to wifi.
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("connecting");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println();
  Serial.print("connected: ");
  Serial.println(WiFi.localIP());
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.pushInt("MOVE_SENSOR", 0);
  Firebase.pushInt("SON_SENSOR", 0);
}

int n1 = 0;
int n2 = 0;
int n3 = 0;
int n4 = 0;

void loop() {
  //==============================*Movement Sensor*==================================================== 
  mov = digitalRead(D2);
  
   if(mov == 1){
    Serial.print("Mouvemenet : ");
    Serial.println(mov);
    digitalWrite(D0, HIGH);
    Firebase.pushInt("MOVE_SENSOR", 1);
    }
   else{
    Serial.print("Mouvemenet : ");
    Serial.println(mov);
      Serial.println("no move");
      digitalWrite(D0, LOW);   
      Firebase.pushInt("MOVE_SENSOR",0); 
           
  }
  //===========================================================================================

  
 
 
 //==============================*Sound Sensor*==================================================== 
  n4 = digitalRead(D8);
  if(n4 >=50){
    Serial.print("Son : ");
    Serial.println(n4);
    digitalWrite(D1, HIGH);
    Firebase.pushInt("SON_SENSOR", 1);
    }
   else{
    Serial.print("Son : ");
    Serial.println(n4);
      Serial.println("pas de son"); 
      digitalWrite(D1, LOW);
      Firebase.pushInt("SON_SENSOR", 0); 
      }
  
   
  //===========================================================================================

   

   //==============================*DHT11 Sensor*==================================================== 

    dht.read(DHT_PIN);
    int temperature = dht.readTemperature();
    Serial.print("Temperature : ");
    Serial.println(temperature);
      Firebase.setInt("DHT11_SENSOR", temperature);
      Firebase.setInt("DHT11_SENSOR_GRAPH", temperature);
      if (temperature >=28){
        Serial.println("Temperature notification : on ");
      Firebase.setInt("TEMPERATURE_NOTIFY", 1);
      delay(1000);
    }
      else{
        Firebase.setInt("TEMPERATURE_NOTIFY", 0);
      delay(1000);
      }
      delay(1000);
 
  //===========================================================================================
    
}

  
