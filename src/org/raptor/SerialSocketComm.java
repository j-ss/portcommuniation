package org.raptor;

import javax.comm.*;
import java.io.*;
import java.util.*;

public class SerialSocketComm {


    Enumeration<CommPortIdentifier> list;
    List<CommPortIdentifier> availPortList=new ArrayList<>();
    CommPortIdentifier port;
    SerialPort serialPort;
    InputStream inputStream;
    OutputStream outputStream;
    BufferedReader reader=null;
    public static void main(String[] args) throws IOException{

        SerialSocketComm object=new SerialSocketComm();
        //check for available serial port in system
        object.checkAvailablePort();
        object.availPortList.forEach(action->System.out.println(action.getName()));
        System.out.println("enter serial port whom you want to connect");

        object.reader=new BufferedReader(new InputStreamReader(System.in));

        String port=object.reader.readLine();
        //call connect method
        object.connect(port);
        //call stream method
        object.openStream();
        //caonfigure port
        object.configurePort();
        //write data to port
        object.writeDataToPort("hello");

        //responce from port
        System.out.println(EventHandler.getData());

        //close port and all connection
        object.close();



    }

    //check for available serial port in system

    public  void checkAvailablePort()
    {

        list=CommPortIdentifier.getPortIdentifiers();
        while(list.hasMoreElements())
        {
            port=list.nextElement();
            if(port.getPortType()==CommPortIdentifier.PORT_SERIAL)
            {
                availPortList.add(port);
            }

        }
        port=null;
    }

    //connect to port

    public void connect(String portName)
    {
        availPortList.forEach(action->{
            if(action.getName().toString().equals(portName)) {
                port = action;
                System.out.println("connecting to port..." + port.getName());
            }
        });


        try
        {
            if(port==null)
            {
                System.out.println("enter a valid port name");
                try {
                    String s=reader.readLine();
                    connect(s);
                } catch (IOException e) {
                    System.out.println("error in taking port name "+e.getMessage());
                }
            }
            serialPort=(SerialPort) port.open(this.getClass().getName(),1000);
        }
        catch (PortInUseException e)
        {
            System.out.println("port not open "+e.getMessage());
        }
    }

    //open input output stream on connected port

    public void openStream()
    {
        try {
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    //configure serialPort

    public void configurePort()
    {
        try
        {
            serialPort.addEventListener(new EventHandler(inputStream));
        }
        catch (TooManyListenersException e) {
            System.out.println(e.getMessage());
        }

        serialPort.notifyOnDataAvailable(true);

        try
        {
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
            serialPort.setSerialPortParams(5000,SerialPort.DATABITS_8,
                                            SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);

        } catch (UnsupportedCommOperationException e) {
            System.out.println(e.getMessage());
        }

    }

    //write method

    public void writeDataToPort(String str)
    {
        try {
            outputStream.write(str.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void close()
    {
        serialPort.close();

        try
        {
            inputStream.close();
            outputStream.close();
            System.out.println("All connection close");
        } catch (IOException e) {
            System.out.println("error in closing stream");
        }

    }
}
