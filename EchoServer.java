// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;

  /**
   * 
   */
  ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port,  ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
    try 
    {
        this.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
    serverUI.display("Message received: " + msg + " from " + client.getInfo("loginId"));
    
    String msgStr = (String)msg;
    if(msgStr.startsWith("#login")){
      String loginId = msgStr.substring(7);
      if(client.getInfo("loginId")== null){ 
        client.setInfo("loginId", loginId);
        serverUI.display(client.getInfo("loginId") + " has logged on.");
        this.sendToAllClients("SERVER MSG> "+client.getInfo("loginId") + " has logged on.");
      }
      else {
        try {
          serverUI.display("Client was already logged in");
          client.close();
        } catch (Exception e) {
          // TODO: handle exception
        }
      }
    }

    else this.sendToAllClients(client.getInfo("loginId") + "> " + msg);
  }
    

  public void handleMessageFromServerUI(String message){
    if(message.startsWith("#")){
        try {
          handleCommand(message);

        } catch (Exception e) {
          serverUI.display("Error handling message. Please enter valid command.");
          System.out.println(e);
        }
        
    }
    else{
      serverUI.display("SERVER MSG> " + message);
      this.sendToAllClients("SERVER MSG> " + message);
    }
  }

  private void handleCommand(String cmd) throws Exception{
    

    if(cmd.equals("#quit")){
      System.exit(0);
    }
    else if(cmd.equals("#stop")){
      this.stopListening();
    }
    else if(cmd.equals("#close")){
      this.stopListening();
      this.close();
    }
    else if(cmd.equals("#start")){
      if(isListening()){
        serverUI.display("Already listening");
      }
      else this.listen();
    }
    else if(getCommandString(cmd).equals("#setport") && !isListening()){
      int portNumber = Integer.parseInt(getCommandArgument(cmd));
      if(portNumber != getPort()){ 
        this.setPort(portNumber);
      }
      serverUI.display("New port is now: " + String.valueOf(this.getPort()));
    }
    else if(cmd.equals("#getport")){
      serverUI.display(String.valueOf(this.getPort()));
    }
    else{
      serverUI.display("Invalid command");
    }
  }

  private String getCommandString(String cmd){
    return cmd.substring(0, 8);
  }

  private String getCommandArgument(String cmd){
    return cmd.substring(9);
  }

  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    serverUI.display("Server listening for connections on port " + getPort());
      
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    serverUI.display("Server has stopped listening for connections.");
  }

  /**
   * Implementation of the Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
    serverUI.display("A new client has connected to the server.");
    
  }

  /**
   * Implementation of the Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    serverUI.display("Client: " + client.getInfo("loginId") + " has disconnected from the server.");

  }

    /**
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
    serverUI.display("A client has disconnected.");

  }



}
//End of EchoServer class
