package org.raptor;

import javax.comm.CommPortIdentifier;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;

public class EventHandler implements SerialPortEventListener{

    private InputStream inputStream;
    private static StringBuilder portData=null;
    public EventHandler(InputStream inputStream)
    {
        this.inputStream=inputStream;
    }
    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {

        CommPortIdentifier port=(CommPortIdentifier) serialPortEvent.getSource();
        switch (serialPortEvent.getEventType())
        {
            case SerialPortEvent.DATA_AVAILABLE: {

                int sizeOfData=0;

                //here checking size of data in inputstream of port
                try {
                     sizeOfData=inputStream.available();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }

                // read that data and store in string
                if(sizeOfData>0)
                 {
                     byte[] data=new byte[sizeOfData];

                     try {
                         inputStream.read(data);
                         portData.append(new String(data));
                     } catch (IOException e) {
                         System.out.println(e.getMessage());
                     }
                 }
            }
        }
    }

    public static StringBuilder getData() {
        return portData;
    }
}
