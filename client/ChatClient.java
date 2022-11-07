// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  /**
   * User login id.
   */
  private String loginId;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  public ChatClient(String loginId, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginId = loginId;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if(message.startsWith("#")){
          try {
            handleCommand(message);

          } catch (Exception e) {
            clientUI.display("Error handling message. Please enter valid command.");
          }
          
      }
      else sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }

  private void handleCommand(String cmd) throws Exception{
    
    if(cmd.equals("#quit")){
      clientUI.display("Quitting console");
      quit();
    }
    else if(cmd.equals("#logoff")){
      try {
        this.closeConnection();
      } catch (IOException e) {
        clientUI.display("Logoff error. Terminating client.");
        quit();
      }
    }
    else if(cmd.startsWith("#login")){
      try {
        sendToServer(cmd);
      } catch (IOException e) {
        clientUI.display("Login error. Terminating client.");
        quit();
      }

    }
    else if(cmd.startsWith("#sethost") && !isConnected()){
      String param = getCommandArgument(cmd);
      setHost(param);
      clientUI.display("Set host to: " + param);
    }
    else if(cmd.startsWith("#setport") && !isConnected()){
      String param = getCommandArgument(cmd);
      setPort(Integer.parseInt(param));
      clientUI.display("Set port to: " + param);
    }
    else if(cmd.equals("#gethost")){
      clientUI.display(getHost());
      
    }
    else if(cmd.equals("#getport")){
      clientUI.display(String.valueOf(getPort()));
      
    }
    else if(isConnected()){
      clientUI.display("Could not execute command. Client is connected");
    }
    else{
      clientUI.display("Invalid command");
    }
  }

  private String getCommandString(String cmd){
    return cmd.substring(0, 8);
  }

  private String getCommandArgument(String cmd){
    return cmd.substring(9);
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }

	/**
	 * Implementation of the Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  @Override
	protected void connectionClosed() {
    clientUI.display("The connection has been closed");
	}

	/**
	 * Implementation of the Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  @Override
	protected void connectionException(Exception exception) {
    clientUI.display("The server has shut down");
    System.exit(0);
	}

  /**
	 * Hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
  @Override
	protected void connectionEstablished() {
    try {
      sendToServer("#login " + this.loginId);
    } catch (Exception e) {
      clientUI.display("Error establishing connection. Login failed.");
      System.exit(0);
    }
	}

}
//End of ChatClient class
