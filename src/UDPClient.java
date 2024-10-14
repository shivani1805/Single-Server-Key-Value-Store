import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class UDPClient {
  private static final int SOCKET_TIMEOUT_MS = 10000;
  private static final int MAX_BUFFER_SIZE = 1024;

  public static void main(String[] args) {
    if (args.length != 2) {
      log("Incorrect Arguments");
      throw new IllegalArgumentException("Please enter the following arguments - IP/hostname and Port number");
    }

    String serverHostname = args[0];
    int serverPort = Integer.parseInt(args[1]);

    String scriptFilePath = "res/operations-script.txt";

    log("Starting UDP Client");
    log("Attempting connection to " + serverHostname + " on port " + serverPort);

    try (DatagramSocket clientSocket = new DatagramSocket()) {
      clientSocket.setSoTimeout(SOCKET_TIMEOUT_MS);
      Scanner inputScanner = new Scanner(System.in);

      log("Connection established.");

      // Loop to ask user for input to run operations script or close the client
      while (true) {
        log("Enter 'run' to execute the operations script or 'close' to exit: ");
        String userAction = inputScanner.nextLine().trim().toLowerCase();

        if ("run".equals(userAction)) {
          executeScript(scriptFilePath, clientSocket, serverHostname, serverPort, "All Operations Performed");
        } else if ("close".equals(userAction)) {
          log("Exiting client.");
          break;
        } else {
          log("Invalid input. Please enter 'run' or 'close'.");
        }
      }

    } catch (SocketTimeoutException e) {
      log("Server connection timed out.");
    } catch (IOException e) {
      log("Error connecting to server: " + e.getMessage());
    }
  }

  /**
   * Processes a script file line by line and sends commands to the server.
   * It logs the server's responses and handles any invalid responses.
   *
   * @param operationsScriptPath The path to the script file to process.
   * @param clientSocket         The DatagramSocket to communicate with the server.
   * @param targetHostname       The server hostname.
   * @param targetPort           The server port.
   * @param completionMessage    The message to print upon completion of the script.
   * @throws IOException If there's an issue with communication.
   */
  private static void executeScript(String operationsScriptPath, DatagramSocket clientSocket, String targetHostname, int targetPort, String completionMessage) throws IOException {
    try (BufferedReader scriptReader = new BufferedReader(new FileReader(operationsScriptPath))) {
      String commandLine;
      while ((commandLine = scriptReader.readLine()) != null) {
        sendCommand(commandLine, clientSocket, targetHostname, targetPort);
      }
      log(completionMessage);
    } catch (FileNotFoundException e) {
      log("Script file not found: " + operationsScriptPath);
    }
  }

  /**
   * Processes a single command from the script file and sends it to the server.
   * It logs the server's response and handles any invalid responses.
   *
   * @param commandLine  The command to be processed (e.g., PUT key value)
   * @param clientSocket The DatagramSocket to communicate with the server.
   * @param targetHostname The server hostname.
   * @param targetPort   The server port.
   * @throws IOException If there's an issue with communication.
   */
  private static void sendCommand(String commandLine, DatagramSocket clientSocket, String targetHostname, int targetPort) throws IOException {
    byte[] sendData = commandLine.getBytes();
    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(targetHostname), targetPort);
    clientSocket.send(sendPacket);
    log("Sent to server: " + commandLine);

    byte[] receiveData = new byte[MAX_BUFFER_SIZE];
    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
    clientSocket.receive(receivePacket);
    String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
    log("Received from server: " + response);
  }

  /**
   * Logs a message with the current timestamp.
   *
   * @param message The message to log.
   */
  private static void log(String message) {
    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    System.out.println("[" + timestamp + "] " + message);
  }
}
