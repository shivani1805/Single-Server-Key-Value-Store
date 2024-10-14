# Single Server, Key-Value Store (TCP and UDP)
## Steps to run TCP Client through Terminal -
 1. Open terminal in the src folder.
 2. Use javac command to compile the file - `javac TCPClient.java`
 3. Use the command to specify the port number and hostname of the server and run the TCPClient - `java TCPClient <Hostname> <port-number>`
 4. The TCPClient starts.
 5. The user needs to input `run` to run the 15 GET, PUT and DELETE Operations. The operations are run using the `operations-script.txt` in the res folder. The user can change the commands in this script file if they want to run any other operations. 


## Steps to close TCP Client through Terminal -
1. The user can input `close` to exit.

## Steps to run TCP Server through Terminal -
 1. Open terminal in the src folder.
 2. Use javac command to compile the file - `javac TCPServer.java`
 3. Use the command to specify the port number and run the TCPServer - `java TCPServer <port-number>`
 4. The TCPServer starts and pre-populates some data using the `data-population-script.txt` in the `res` folder. The data has key of type string and value also of type string.
 5. The TCPServer then awaits the client connection.

## Steps to close TCP Server through Terminal -
 1. Press `Ctrl + C`

## Steps to run UDP Client through Terminal -
 1. Open terminal in the src folder.
 2. Use javac command to compile the file - `javac UDPClient.java`
 3. Use the command to specify the port number and hostname of the server and run the UDPClient - `java UDPClient <Hostname> <port-number>`
 4. The UDPClient starts.
 5. The user needs to input `run` to run the 15 GET, PUT and DELETE Operations. The operations are run using the `operations-script.txt` in the res folder. The user can change the commands in this script file if they want to run any other operations.


## Steps to close UDPClient through Terminal -
 1. The user can input `close` to exit.

## Steps to run UDP Server through Terminal -
 1. Open terminal in the src folder.
 2. Use javac command to compile the file - `javac UDPServer.java`
 3. Use the command to specify the port number and run the UDPServer - `java UDPServer <port-number>`
 4. The UDPServer starts and pre-populates some data using the `data-population-script.txt` in the `res` folder. The data has key of type string and value also of type string.

## Steps to close TCP Server through Terminal -
1. Press `Ctrl + C`