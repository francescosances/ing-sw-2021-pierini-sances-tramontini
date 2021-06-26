# Progetto di Ingegneria del Software - A.A. 2020/2021

### Descrizione
Implementazione in Java del gioco da tavolo [Maestri del Rinascimento](http://www.craniocreations.it/prodotto/masters-of-renaissance/).  
  
È stato realizzato con l'utilizzo del pattern MVC (Model-View-Controller) implementando una logica distribuita (client/server) tramite socket, dove il server può gestire più partite (da 1 a 4 giocatori) contemporaneamente.

### Componenti del gruppo
- Matteo Pierini
- Francesco Maria Sances
- Marco Tramontini


## Funzionalità
- Regole complete
- Socket
- CLI (interafccia a linea di comando)
- GUI (interafccia grafica)
- 3 funzionalità avanzate:
    - Partite multiple
    - Persistenza
    - Resilienza alle disconnessioni


## Documentazione

### UML

### Javadoc

[Javadoc](https://github.com/francescosances/ing-sw-2021-pierini-sances-tramontini/tree/master/javadoc)

## Testing

### Coverage report
![Coverage report](https://github.com/francescosances/ing-sw-2021-pierini-sances-tramontini/blob/master/deliverables/coverage/coverage_report.png?raw=true)


## Setup
### Package
Per effettuare il package degli eseguibili di client e server, lanciare il seguente comando dalla root del progetto:
```
mvn clean package
```
I rispettivi JAR verranno creati all'interno della cartella ```/target```.  
  
Alternativamente, possono essere ottenuti direttamente da [qui](https://github.com/francescosances/ing-sw-2021-pierini-sances-tramontini/tree/master/deliverables/jars).

### Caratteri colorati
Per aumentare la leggibilità, nell'interfaccia a linea di comando del client vengono utilizzati dei caratteri colorati attraverso sequenze di escape ANSI e dei caratteri speciali Unicode. Tuttavia, nella maggior parte delle verisioni di Windows, questi non sono supportati dal terminale di default.  
La soluzione consigliata è quella di utilizzare un sottosistema Windows per Linux (WSL).

## Esecuzione
È necessario Java 11 o una versione superiore.

### Client
Il client può essere lanciato tramite un doppio click sull'eseguibile ```GC13-client.jar``` (in questo caso viene avviata automaticamente la GUI) oppure tramite il seguente comando:
```
java -jar GC13-client.jar [-c|--cli]
```
Specificando il parametro ```-c``` o ```--cli``` viene scelta come interafccia la linea di comando (CLI) anziché la GUI.

### Server
Per lanciare il server utilizzare il seguente comando:
```
java -jar GC13-server.jar [-p|--port port_number]
```
```-p``` o ```--port``` permottono di specificare la porta sulla quale il server rimane in ascolto di nuove connessioni. Se non specificata viene usata la porta 8000.