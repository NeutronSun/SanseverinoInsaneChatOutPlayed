# Chat Client e Server

![ffewfe](logo.png)

## Panoramica (≧ڡ≦*)
Il software consente ai vari utenti collegati di chattare tra di loro.
Gli utenti saranno in grado di effettuare il login o, in caso non ne avessero uno, di creare un nuovo profilo.
Il server si occupa della (｢･ω･)｢ memorizzazione di tutti i dati degli utenti e di crittografare le password tramite l'algoritmo simmetrico hash 512 e l'aggiunta di un sale.
I messaggi inviati tra i vari client essi sono crittografati invece tramite l'algoritmo asimmetrico RSA.
Ogni client genera le proprie chiavi e spedisce la pubblica al server che si occuperà dello smistamento delle stesse.
### Comandi

Il client dispone in oltre di comodi comandi tra cui:  (￣^￣)
- /LIST --> lista utenti collegati
- /ALL --> il messaggio viene inviato a tutti
- /@user msg --> il messaggio sarà inviato ad un singolo utente
- /@user@user... --> il messaggio sarà inviato a tutti gli utenti selezionati.
- /SET STATUS--> l'utente potrà selezionare il suo stato su ONLINE o OFFLINE.
- /SET COLOR--> l'utente potrà selezionare un colore con cui gli altri utenti vedranno i suoi messaggi.

In oltre sono presenti due **EASTER EGG** molto divertenti e dal significato profondo.
# ***Descrizioni classi*** (＋´ω｀)
In questo progetto sono presenti 12 classi (8 lato server e 4 lato client) dove ognuna svolge uno specifico compito e sono sincronizzate per evitare problemi di concorrenza e di inconsistenza dei dati, vista la natura multithreading del server.

## ***Comunicazione classi***  ♥w♥
In questa sezione ci sarà solo una schema riassuntivo di come, dopo l'istante t<sub>0</sub> tutte le classi iniziano a collaborare:
- Il server viene avviato, e tutti gli oggetti condivisi(messageBox, filemanager, usermanager) vengono istanziato.
  - In caso in cui ci siano problemi con i vari file del server, le cartelle o gli stessi mancanti verranno istanziati.
  - Ora il server rimane in attessa di una richiesta di collegamento (´ｖ｀)
- Il client si avvia e vengono istanziati gli oggetti : 
	- Encryptor, il quale genera le chiavi
	- ClientReader, il quale legge i messaggi ricevuti dal server
	- KeySorter, il quale si occupa di passare le chiavi pubbliche al Client.

- Il client manda la richiesta di connessione al server, il quale accettandola avvia il ServerThread che si occupa della comunicazione tra i due.

- Il ServerThread ora inizia a chiedere al client di effettuare il login (o sign-in) e, una volta ricevuti i dati li memorizza nell'oggetto condiviso UserManager.

- Adesso che l'utente è stato effettivamente registrato all'interno del sistema come "connesso" può finalmente ricevere messaggi da parte di altri utenti:
	- Ad ogni utente viene associato un thread TheWaiter che si occupa di aspettare che ci sia un messaggio in messageBox per il client.
	- Il serverThread invece aspetta di ricevere istruzione dal client, nel caso in cui esso desideri inviare un messaggio il ServerThread spedisce al client le chiavi pubbliche dei destinatari ed esso invia gli n-messaggi criptati che il serverthread smisterà all'interno della MessageBox. ⇎_⇎

- Adesso che il messaggio è dentro la messageBox, il thread dormiente TheWaiter lo spedirà ed il client senza difficoltà lo decripterà e finalmente lo potrà leggere.

- Lato client la questione dell'invio del messaggio è leggermente più complessa:
	- Dopo che l'utente avrà specificato i destinatari tramite la formula @user, il client li inviarà al server come richiesta di chiavi e si mette in attesa che il ClientReader faccia il suo sporco lavoro.
	- Il clientReader si occuperà di leggere le chiavi e di inserirle all'interno del KeySorter. Una volta fatto tramite una notifyAll risveglierà il Client principale che cripterà gli n-messaggi con le n-chiavi.

Queto in larga scala è come tutte le classi comunicano e collaborano tra di loro.

## ***Funzionamento classi Server***

### **ServerThread**
Thread principale del server che si avvia una volta accettata la comunicazione con il server.
Sarà lui l'artefice di tutti i protocolli per consentire la ricezione e l'invio del messaggio.

### **TheWaiter** (≧ｍ≦)
Piccolo threaddino che viene avviato solo dopo che i dati dell'utente sono disponibili nel sistema. Il suo unico scopo è quello di aspettare una modifica all'interno della MessageBox.

### **MessageBox**
Può essere definito come *l'ippocampo* del programma, tutti i messaggi saranno memorizatti al suo interno (in maniera sincronizzata).
Ogni aggiunta di └|∵┌| un nuovo messaggio parte una notifyAll e il sistema nervoso del programma consente ai neuroni(TheWaiter) di leggere i messaggi appena scritti.

### **UserManager**
Classe condivisa che si occupa di gestire la memorizzazione concorrente di tutti gli utenti collegati.

### **FileManager**
Classe condivisa che si occupa della lettura e scrittura sincronizzata del file *data.txt* (ServerFiles/Files/UserData/).
Nel file saranno memorizzati i dati (¬‿¬) di ogni utente registrato.
In oltre, questa classe cifra le password tramite l'algoritmo di Hashing 512, per una maggiore sicurezza.

## ***Funzionamento Classi Client***

### **ClientThreadReader**
Thread principale del client che si occupa di leggere tutti i messaggi ricevuti dal server e, di smistarli in base al loro scopo (Es. gestire le chiavi).

### **Encryptor**
Classe che implementa l'algoritmo RSA per la generazione delle chiavi pubbliche e private di ogni client.

### **KeySorter**
Classe sincronizzata con l'unico (・ω<) scopo di mettere in condivisione le chiavi lette, e scritte, dal ClientReader ed utilizzate dal Cliet stesso per criptare i messaggi.


# ***Ulteriori informazioni***
## ***Build with***
- Java version 17

## **Bugs da correggere**
- [x] In caso di utente inesistente notificare il client dell'errore.
- [x] Evitare la presenza di più client con lo stesso nome collegati contemporaneamente.
- [x] Creare i file/cartelle in caso della loro mancanza o corruzzione.
- [x] Errori durante la disconnessione di un utente(piu' o meno).
- [ ] Programma funzionante su MacOs 凸(>皿<)凸

## **License**
Copyright (c) 22 Giugno anno 0, 2021, SafJNest and/or its affiliates. All rights reserved. SAFJNEST PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.

## ***Contatti***
### Lorenzo Sanseverino
- lorenzosanseverino2003@gmail.com
- Git: <a href="https://github.com/NeutronSun">NeutronSun</a> 
- Discord: Sun#7606.

### Mattia Cococcioni
- cococcioni.mattia@gmail.com
- Git: <a href="https://github.com/CocochDSA">CocochDSA</a> 

### Bernadette Capriotti
- bernadette.capriotti@gmail.com
- Git: <a href="https://github.com/BernadetteCapriotti">BernadetteCapriotti</a>

### Galantini Corrado
- galantini.corrado@istitutomontani.edu.it
- Git: <a href="https://github.com/XOShu4">XOShu4</a> 
- Discord: Mario Giordano#3698

### Panichi Leonardo
- panichileonardo4@gmail.com
- Git: <a href="https://github.com/Leon412">Leon412</a> 
- Discord: Leon_#7949


