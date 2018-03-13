package org.raptor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.UnsupportedCommOperationException;

public class SerialPortCom {

  private static Enumeration<CommPortIdentifier> list;
  private static ArrayList<CommPortIdentifier> serialPortList=new ArrayList<>();
  private static SerialPort serialPort;
  private static CommPortIdentifier commPort;
  private static BufferedReader reader;
  private static InputStream inputStream;
  private static OutputStream outputStream;
  private static Logger logger=Logger.getLogger("SerialPortCom");
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
    System.out.println("enter serial port whom you want to connect");   //This println take port name form user whom he want to connect
    reader=new BufferedReader(new InputStreamReader(System.in));
    String port=reader.readLine();  //Take serial port from user whom he want to connect
    connect(port);                  //connect to port
    openStream();                   //open stream on port for communication
    configurePort();                //configure serial port
    writeDataToPort("jogendra");    //write data to port
    close();                        //close all connection

  }

  /**
   * This method check available port in system
   */
  public static void checkAvailablePort(){
    list=CommPortIdentifier.getPortIdentifiers();

  }

  /**
   * This method connect the serial port
   * @param port
   */
  public static void connect(String port){

    serialPortList.forEach(action->{
      if(action.getName().equals(port)){
        commPort=action;
        logger.log(Level.WARNING,"connecting to port..." + commPort.getName());
      }
    });
    try{
      if(commPort==null){
        logger.log(Level.WARNING,"enter a valid port name");
        try {
          String name=reader.readLine();
          connect(name);
        } catch (IOException e) {
          logger.log(Level.WARNING,"error in taking port name "+e.getMessage());
        }
      }
      serialPort=(SerialPort)commPort.open("serialportcommunication",1000);
    }catch (PortInUseException e){
      logger.log(Level.WARNING,"port not open "+e.getMessage());
    }
  }

  /**
   * This method open the input and output stream for communication
   */
  public static void openStream(){
    try {
      inputStream = serialPort.getInputStream();
      outputStream = serialPort.getOutputStream();
    }catch (IOException e){
      logger.log(Level.WARNING,"error in opening stream"+e.getMessage());
    }
  }

  /**
   * This method configure the port like adding event listner,flow control mode,port parameter
   */
  public static void configurePort(){

    try{
      serialPort.addEventListener(new EventHandling(inputStream));
    }catch (TooManyListenersException e){
      logger.log(Level.WARNING,e.getMessage());
    }
    serialPort.notifyOnDataAvailable(true);
    try{
      serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
      serialPort.setSerialPortParams(5000,SerialPort.DATABITS_8,
          SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
    }catch (UnsupportedCommOperationException e){
      logger.log(Level.WARNING,e.getMessage());
    }

  }
  /**
   * This method is used to write data in serial port
   */
  public static void writeDataToPort(String message){
    try {
      outputStream.write(message.getBytes());
      outputStream.flush();
    } catch (IOException e) {
      logger.log(Level.WARNING,e.getMessage());
    }
  }

  /**
   * This method close all open connection
   */
  public static void close(){
    serialPort.close();
    try{
      reader.close();
      inputStream.close();
      outputStream.close();
      logger.log(Level.INFO,"Connection Closed");
    }catch (IOException e){
      logger.log(Level.WARNING,"error in closing "+e.getMessage());
    }
  }
}
