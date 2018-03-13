package org.raptor;

import java.io.IOException;
import java.io.InputStream;
import javax.comm.CommPortIdentifier;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;

/**
 * This class handle the event on serial port
 */
public class EventHandling implements SerialPortEventListener {

  private InputStream inputStream;
  private static StringBuilder portData=null;
  public EventHandling(InputStream inputStream){
    this.inputStream=inputStream;
  }
  @Override
  public void serialEvent(SerialPortEvent serialPortEvent) {
    CommPortIdentifier port=(CommPortIdentifier)serialPortEvent.getSource();
    switch (serialPortEvent.getEventType()){

      case SerialPortEvent.DATA_AVAILABLE:{
        int sizeOfData=0;
        // checking size of data available in inpurstream of port
        try{
          sizeOfData=inputStream.available();
        }catch (IOException e){
          System.out.println(e.getMessage());
        }
        // read that data and store in string
        if(sizeOfData>0){
          byte[] data=new byte[sizeOfData];
          try {
            inputStream.read(data);
            portData.append(new String(data));
          } catch (IOException e) {
            System.out.println(e.getMessage());
          }
        }
        break;
      }
    }

  }
  public static StringBuilder getData() {
    return portData;
  }
}
