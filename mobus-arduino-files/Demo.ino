
#include "ModbusSlave.h"
#include "TimerOne.h"

#define  LED 13
#define  _PWM 3


/* Modbus RTU common parameters, the Master MUST use the same parameters */
enum
      {
        COMM_BPS = 38400, /* baud rate */
        MB_SLAVE = 2,	/* modbus slave id */
        PARITY = 'n' /* none parity */
      };


/* slave registers */
enum {     
        // Endereços Reservados do ModBus  
        MB_POT,        /* RSSI UPLINK */
        MB_PWM,        /* RSSI DOWNLINK */
        MB_LED,        /* TEMPERATURA */
        MB_LUM,        /* LUMINOSIDADE */     
        MB_V12,        /* Tensao */
        MB_BAT,        /* BATERIA */  
        MB_AD4,        /* PC0 */      
        MB_AD5,        /* PC1 */
        MB_END,        /* Endereço */
        MB_RS0,        /* RESERVDO */
        MB_RS1,        /* RESERVDO */
        MB_RS2,        /* RESERVDO */
        MB_RS3,        /* RESERVDO */
        MB_REGS   /* total number of registers on slave */
};
int regs[MB_REGS];

unsigned long Count=0; 

////////////////////////////////////////////////////////////

void setup()
{
  pinMode(LED, OUTPUT);
  Timer1.initialize(1000); // timer em micro segundos 
  Timer1.attachInterrupt(intTimer1);
  regs[MB_LED] = 1000; 
  Serial.begin(COMM_BPS); 
}

void loop()
{
  // modbus_update() is the only method used in loop(). It returns the total error
  // count since the slave started. You don't have to use it but it's useful
  // for fault finding by the modbus master.
  
  /* Verifica se tem alguma requisição Modbus para receber */
  update_mb_slave(MB_SLAVE, regs, MB_REGS);
  
  regs[MB_POT] = analogRead(A0); // update data to be read by the master to adjust the PWM
  
  analogWrite(_PWM, regs[MB_PWM]); // constrain adc value from the arduino master to 255
}

void intTimer1()
{
  Count++;
  
  if (Count%regs[MB_LED] == 0)
  {
    digitalWrite(LED,!digitalRead(LED));   
  }
}

