# Progetto di Ingegneria del Software - A.A. 2020/2021

### Descrizione
Descrizione

### Componenti del gruppo
- Matteo Pierini
- Francesco Maria Sances
- Marco Tramontini


## Funzionalità
- Regole complete
- Socket
- CLI
- GUI
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
![Coverage report](https://github.com/francescosances/ing-sw-2021-pierini-sances-tramontini/blob/master/coverage/coverage_report.png?raw=true)


## Setup
Per effettuare il package degli eseguibili di client e server, lanciare il seguente comando dalla root del progetto:
```
mvn clean package
```
I rispettivi JAR verranno creati all'interno della cartella ```/target```.

## Esecuzione

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