import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TCPServer {
  private static final Map<String, String> store = new HashMap<>();
  private static final String SCRIPT_FILE_PATH = "res/data-population-script.txt";

  public static void main(String[] args) throws IOException {
    if (args.length < 1) {
      log("Incorrect Arguments");
      throw new IllegalArgumentException("Please enter the following arguments - Port number");
    }

    int port = Integer.parseInt(args[0]);

    // Start listening on the given port number
    log("TCP Server is starting on port " + port);

    // Prepopulate data
    populateData(SCRIPT_FILE_PATH);

    try (ServerSocket serverSocket = new ServerSocket(port)) {
      while (true) {
        try {
          log("Waiting for client connection...");
          Socket clientSocket = serverSocket.accept();
          log("Connection established with " +
                  clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

          handleClient(clientSocket);
        } catch (IOException e) {
          log("Error accepting client connection: " + e.getMessage());
        }
      }
    }
  }

  // Method to prepopulate data from the script file
  private static void populateData(String scriptPath) {
    try (BufferedReader scriptReader = new BufferedReader(new FileReader(scriptPath))) {
      String line;
      while ((line = scriptReader.readLine()) != null) {
        handleCommand(line);
      }
      log("Data Population Completed");
    } catch (FileNotFoundException e) {
      log("Script file not found: " + scriptPath);
    } catch (IOException e) {
      log("Error reading script file: " + e.getMessage());
    }
  }

  // Process individual commands from the script file for prepopulation
  private static void handleCommand(String command) {
    String[] parts = command.split(" ");
    if (parts.length < 1) {
      log("Malformed command: " + command);
      return;
    }

    String operation = parts[0].toUpperCase();
    switch (operation) {
      case "PUT":
        if (parts.length != 3) {
          log("Error: PUT command requires 2 arguments (key, value) for command: " + command);
          return;
        }
        store.put(parts[1], parts[2]);
        log("Prepopulated: Key=" + parts[1] + ", Value=" + parts[2]);
        break;
      case "GET":
        if (parts.length != 2) {
          log("Error: GET command requires 1 argument (key) for command: " + command);
          return;
        }
        String value = store.get(parts[1]);
        if (value != null) {
          log("Prepopulated GET: Key=" + parts[1] + ", Value=" + value);
        } else {
          log("Error: Key not found for command: " + command);
        }
        break;
      case "DELETE":
        if (parts.length != 2) {
          log("Error: DELETE command requires 1 argument (key) for command: " + command);
          return;
        }
        if (store.remove(parts[1]) != null) {
          log("Prepopulated DELETE: Key=" + parts[1]);
        } else {
          log("Error: Key not found for command: " + command);
        }
        break;
      default:
        log("Error: Unknown command for prepopulation: " + command);
    }
  }

  // Handle client connection and requests
  private static void handleClient(Socket clientSocket) {
    try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)) {

      String clientInput;
      while ((clientInput = input.readLine()) != null) {
        log("Received: " + clientInput);

        String response = handleRequest(clientInput);
        output.println(response);

        log("Sent: " + response);
      }

    } catch (IOException e) {
      log("Error in client communication: " + e.getMessage());
    } finally {
      try {
        clientSocket.close();
      } catch (IOException e) {
        log("Error closing client socket: " + e.getMessage());
      }
    }
  }

  // Process PUT, GET, DELETE Requests
  private static String handleRequest(String request) {
    String[] parts = request.split(" ");
    if (parts.length < 1) {
      return "Error: Malformed request";
    }

    String command = parts[0].toUpperCase();
    switch (command) {
      case "PUT":
        if (parts.length != 3) {
          return "Error: PUT command requires 2 arguments (key, value)";
        }
        store.put(parts[1], parts[2]);
        return "Success: Key=" + parts[1] + ", Value=" + parts[2] + " stored.";
      case "GET":
        if (parts.length != 2) {
          return "Error: GET command requires 1 argument (key)";
        }
        String value = store.get(parts[1]);
        return value != null ? "Success: Key=" + parts[1] + ", Value=" + value : "Error: Key not found";
      case "DELETE":
        if (parts.length != 2) {
          return "Error: DELETE command requires 1 argument (key)";
        }
        return store.remove(parts[1]) != null ? "Success: Key=" + parts[1] + " deleted." : "Error: Key not found";
      default:
        return "Error: Unknown command";
    }
  }

  // Log messages with timestamps
  private static void log(String message) {
    String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    System.out.println("[" + timestamp + "] " + message);
  }
}
