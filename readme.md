# Software Engineering project - 2020/2021

### Description
BoardGame [Masters of Renaissance](http://www.craniocreations.it/prodotto/masters-of-renaissance/) java implementation.  

Realized utilizing MVC (Model-View-Controller) pattern, implementing a client/server logic through sockets. The server can handle more than one multiplayer (1-4 players) matches at the same time.

### Work Group
- Matteo Pierini
- Francesco Maria Sances
- Marco Tramontini


## Functionalities
- Full rules
- Socket
- CLI (Command Line Interface)
- GUI (Graphic User Interface)
- 3 advanced functionalities:
    - Simultaneous matches
    - Persistence
    - Disconnection resilience


## Documentation

### UML

[First - Model](https://github.com/francescosances/ing-sw-2021-pierini-sances-tramontini/tree/master/deliverables/uml/FirstModelUML.pdf)

[Final - High Level](https://github.com/francescosances/ing-sw-2021-pierini-sances-tramontini/tree/master/deliverables/uml/HighLevel_FinalUML.pdf)

[Final - Complete](https://github.com/francescosances/ing-sw-2021-pierini-sances-tramontini/tree/master/deliverables/uml/generated)

### Javadoc

[Javadoc](https://github.com/francescosances/ing-sw-2021-pierini-sances-tramontini/tree/master/deliverables/javadoc)

## Testing

### Coverage report
![Coverage report](https://github.com/francescosances/ing-sw-2021-pierini-sances-tramontini/blob/master/deliverables/coverage/coverage_report.png?raw=true)


## Setup
### Package
In order to build the client and server packages, input in the project root the command line.
```
mvn clean package
```
Then, JARs will be built inside the folder named ```/target```.  
  
Otherwise, you can download pre-built JARs [here](https://github.com/francescosances/ing-sw-2021-pierini-sances-tramontini/tree/master/deliverables/jars).

### Coloured characters
CLI can use coloured character to enhance understandability, achieved through ANSI escapes and Unicode special characters.
Sadly, most of Windows default terminals doesn't support them. 
It is highly recommended to make use of WSL (Windows Subsystem for Linux).   

## Execution
Java 11 or newer is needed.

### Client
Client can be launched double-clicking on the executable ```GC13-client.jar``` (GUI will be started) or through the command line:
```
java -jar GC13-client.jar [-c|--cli]
```
Write ```-c``` or ```--cli```parameters only if you want to choose CLI instead of GUI.

### Server
Use the command line
```
java -jar GC13-server.jar [-p|--port port_number]
```
to launch server.
```-p``` or ```--port``` enable to choose the port the server will use to listen to new connections. Default port is 8000.