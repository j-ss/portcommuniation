package org.raptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

public class SerialPortCom {

  private static Enumeration<CommPortIdentifier> list;
  private static ArrayList<CommPortIdentifier> serialPortList;
  private static SerialPort serialPort;
  private static CommPortIdentifier commPort;
  private static BufferedReader reader;
  private static InputStream inputStream;
  private static OutputStream outputStream;
  public static void main(String[] args) throws IOException {

    checkAvailablePort();        //This method check the available communication port in system
    /*
      find serial port
     */
    while (list.hasMoreElements()){
      CommPortIdentifier port=list.nextElement();
      if(port.getPortType()==CommPortIdentifier.PORT_SERIAL){
        serialPortList.add(port);
      }
    }
    serialPortList.forEach(action->System.out.println(action.getName()));
    System.out.println("enter serial port whom you want to connect");
    reader=new BufferedReader(new InputStreamReader(System.in));
    String port=reader.readLine();  //Take serial port from user whom he want to connect
    connect(port);                  //connect to port
    openStream();                   //open stream on port for communication
    configurePort();                //configure serial port
    writeDataToPort("jogendra");    //write data to port
    close();                        //close all connection

  }

  public static void checkAvailablePort(){
    list=CommPortIdentifier.getPortIdentifiers();

  }

  public static void connect(String port){

    serialPortList.forEach(action->{
      if(action.getName().equals(port)){
        commPort=action;
        System.out.println("connecting to port..." + commPort.getName());
      }
    });
    try{
      if(commPort==null){
        System.out.println("enter a valid port name");
        try {
          String name=reader.readLine();
          connect(name);
        } catch (IOException e) {
          System.out.println("error in taking port name "+e.getMessage());
        }
      }
      serialPort=(SerialPort)commPort.open("serialportcommunication",1000);
    }catch (PortInUseException e){
      System.out.println("port not open "+e.getMessage());
    }
  }

  public static void openStream(){
    try {
      inputStream = serialPort.getInputStream();
      outputStream = serialPort.getOutputStream();
    }catch (IOException e){
      System.out.println("error in opening stream"+e.getMessage());
    }
  }

  public static void configurePort(){

    try{
      serialPort.addEventListener(new EventHandling(inputStream));
    }catch (TooManyListenersException e){
      System.out.println(e.getMessage());
    }
    serialPort.notifyOnDataAvailable(true);
    try{
      serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
      serialPort.setSerialPortParams(5000,SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
    }catch (UnsupportedCommOperationException e){
      System.out.println(e.getMessage());
    }

  }
  public static void writeDataToPort(String message){
    try {
      outputStream.write(message.getBytes());
      outputStream.flush();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
  public static void close(){
    serialPort.close();
    try{
      reader.close();
      inputStream.close();
      outputStream.close();
      System.out.println("connection closed");
    }catch (IOException e){
      System.out.println("error in closing "+e.getMessage());
    }
  }
}