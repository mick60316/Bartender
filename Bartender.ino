#include <SoftwareSerial.h>   // 引用程式庫

// 定義連接藍牙模組的序列埠
SoftwareSerial BTserial(12, 13); // 接收腳, 傳送腳
char val;  // 儲存接收資料的變數

int relayPin [8];
char c = ' ';


String InputData = "";

void setup()
{
  Serial.begin(9600);
  Serial.print("Sketch:   ");   Serial.println(__FILE__);
  Serial.print("Uploaded: ");   Serial.println(__DATE__);
  Serial.println(" ");
  for (int i = 0; i < 8; i++)
  {
    relayPin[i] = 4 + i;
    pinMode(relayPin[i], OUTPUT);
  }
  BTserial.begin(9600);
  Serial.println("BTserial started at 9600");

}

void loop()
{
  // Read from the Bluetooth module and send to the Arduino Serial Monitor
  if (BTserial.available())
  {
    c = BTserial.read();
    InputData += c;
    Serial.write(c);
    Serial.println(c);
  }

  if (InputData.length() >= 9)
  {
    switch (InputData.charAt(0))
    {
      case 'r':
      {
        int N = InputData.charAt(1) - '0';
        int Code = InputData.charAt(2) - '0';
        digitalWrite(relayPin[N], Code * 255) ;
        break;
      }
      case 'a':
      {
        for (int i = 0; i < 8; i++)
        {
          int N = InputData.charAt(i + 1) - '0';
          if (N!= 0)
          {
            digitalWrite (relayPin[i], HIGH);
            delay(N * 1000);
            digitalWrite (relayPin[i], LOW);
          }

        }
        break;
      }


    }
    InputData = "";

  }



  // Read from the Serial Monitor and send to the Bluetooth module
  if (Serial.available())
  {
    c = Serial.read();

    // do not send line end characters to the HM-10

    BTserial.write(c);
  }

}


