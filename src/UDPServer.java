import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UDPServer {

  private static final Map<String, String> keyValueStore = new HashMap<>();
  private static final String DATA_POPULATION_SCRIPT = "res/data-population-script.txt";
  private static final int BUFFER_SIZE = 1024;

  public static void main(String[] args) {
    if (args.length < 1) {
      log("Incorrect Arguments");
      throw new IllegalArgumentException("Please enter the following arguments - Port number");
    }

    int portNumber = Integer.parseInt(args[0]);

    // Start listening on the given port number
    log("UDP Server is starting on port " + portNumber);

    // Prepopulate data
    prepopulateData(DATA_POPULATION_SCRIPT);

    try (DatagramSocket socket = new DatagramSocket(portNumber)) {
      byte[] buffer = new byte[BUFFER_SIZE];

      while (true) {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String message = new String(packet.getData(), 0, packet.getLength());
        log("Received from " + packet.getAddress() + ":" + packet.getPort() + " - " + message);

        String response = processRequest(message);
        byte[] responseData = response.getBytes();
        DatagramPacket responsePacket = new DatagramPacket(responseData, responseData.length,
                packet.getAddress(), packet.getPort());
        socket.send(responsePacket);
        log("Sent to " + packet.getAddress() + ":" + packet.getPort() + " - " + response);
      }
    } catch (IOException e) {
      log("Error in UDP Server: " + e.getMessage());
    }
  }

  // Method to prepopulate data from the script file
  private static void prepopulateData(String scriptFilePath) {
    try (BufferedReader scriptReader = new BufferedReader(new FileReader(scriptFilePath))) {
      String line;
      while ((line = scriptReader.readLine()) != null) {
        processCommand(line);
      }
      log("Data Population Completed");
    } catch (FileNotFoundException e) {
      log("Script file not found: " + scriptFilePath);
    } catch (IOException e) {
      log("Error reading script file: " + e.getMessage());
    }
  }

  // Process individual commands from the script file for prepopulation
  private static void processCommand(String command) {
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
        keyValueStore.put(parts[1], parts[2]);
        log("Prepopulated: Key=" + parts[1] + ", Value=" + parts[2]);
        break;
      case "GET":
        if (parts.length != 2) {
          log("Error: GET command requires 1 argument (key) for command: " + command);
          return;
        }
        String value = keyValueStore.get(parts[1]);
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
        if (keyValueStore.remove(parts[1]) != null) {
          log("Prepopulated DELETE: Key=" + parts[1]);
        } else {
          log("Error: Key not found for command: " + command);
        }
        break;
      default:
        log("Error: Unknown command for prepopulation: " + command);
    }
  }

  // Process PUT, GET, DELETE Requests
  private static String processRequest(String request) {
    String[] parts = request.split(" ");
    if (parts.length < 1) {
      return "Error: Malformed request";
    }

    String command = parts[0].toUpperCase();
    switch (command) {
      case "PUT":
        if (parts.length != 3) return "Error: PUT command requires 2 arguments (key, value)";
        keyValueStore.put(parts[1], parts[2]);
        return "PUT Success: Key=" + parts[1] + ", Value=" + parts[2];
      case "GET":
        if (parts.length != 2) return "Error: GET command requires 1 argument (key)";
        return keyValueStore.getOrDefault(parts[1], "Error: Key not found");
      case "DELETE":
        if (parts.length != 2) return "Error: DELETE command requires 1 argument (key)";
        if (keyValueStore.remove(parts[1]) != null) {
          return "DELETE Success: Key=" + parts[1];
        } else {
          return "Error: Key not found";
        }
      default:
        return "Error: Unknown command";
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
