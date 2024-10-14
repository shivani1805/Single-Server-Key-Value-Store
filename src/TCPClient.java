import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class TCPClient {
  private static final int TIMEOUT = 10000;

  public static void main(String[] args) {
    if (args.length != 2) {
      log("Incorrect Arguments");
      throw new IllegalArgumentException("Please enter the following arguments - IP/hostname and Port number");
    }

    String hostname = args[0];
    int port = Integer.parseInt(args[1]);

    String operationsScript = "res/operations-script.txt";

    log("Starting TCP Client");
    log("Attempting connection to " + hostname + " on port " + port);

    try (Socket clientSocket = new Socket(hostname, port)) {
      clientSocket.setSoTimeout(TIMEOUT);

      PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      Scanner scanner = new Scanner(System.in);

      log("Connection established.");

      // Loop to ask user for input to run operations script or close the client
      while (true) {
        log("Enter 'run' to execute the operations script or 'close' to exit: ");
        String userInput = scanner.nextLine().trim().toLowerCase();

        if ("run".equals(userInput)) {
          processScript(operationsScript, out, in, "All Operations Performed");
        } else if ("close".equals(userInput)) {
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
   * @param scriptFilePath The path to the script file to process.
   * @param out            The output stream to the server.
   * @param in             The input stream from the server.
   * @param completionMsg  The message to print upon completion of the script.
   * @throws IOException If there's an issue with communication.
   */
  private static void processScript(String scriptFilePath, PrintWriter out, BufferedReader in, String completionMsg) throws IOException {
    try (BufferedReader scriptReader = new BufferedReader(new FileReader(scriptFilePath))) {
      String line;
      while ((line = scriptReader.readLine()) != null) {
        processCommand(line, out, in);
      }
      log(completionMsg);
    } catch (FileNotFoundException e) {
      log("Script file not found: " + scriptFilePath);
    }
  }

  /**
   * Processes a single command from the script file and sends it to the server.
   * It logs the server's response and handles any invalid responses.
   *
   * @param command The command to be processed (e.g., PUT key value)
   * @param out     The output stream to the server.
   * @param in      The input stream from the server.
   * @throws IOException If there's an issue with communication.
   */
  private static void processCommand(String command, PrintWriter out, BufferedReader in) throws IOException {
    if (command.trim().isEmpty()) {
      return;
    }

    out.println(command);
    log("Command sent: " + command);

    try {
      String response = in.readLine();
      if (response != null) {
        log("Received: " + response);
      } else {
        log("No response from server.");
      }
    } catch (SocketTimeoutException e) {
      log("No response from server within timeout for command: " + command);
    }
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
