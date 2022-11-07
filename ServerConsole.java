import java.util.Scanner;

import common.ChatIF;

public class ServerConsole implements ChatIF {

    /**
     * Instance of the server
     */
    EchoServer server;

    /**
    * Scanner to read from the console
    */
    Scanner fromConsole; 


    public ServerConsole(int port) {
        
    try 
    {
        server = new EchoServer(port, this);
      
    } 
    catch(Exception exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
    }


      /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        server.handleMessageFromServerUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }

    @Override
    public void display(String message) {

        String serverMessage = "SERVER MSG> "+ message;
        System.out.println(serverMessage);
        
    }

      //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = EchoServer.DEFAULT_PORT; //Set port to 5555
    }
	

    ServerConsole serverChat = new ServerConsole(port);


    serverChat.accept(); //wait for commands

  }


}
