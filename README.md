# ServerBasedChat
Server Based Chat for CS 4390

To run:

1. Open two terminal windows
2. Go to the src folder on both
3. On one run "make server", this will compile and run the server on port 9879
4. On the other run "make clientA" or "make clientB", this will compile and run the client on localhost 9879
5. Send messages on the client window

Note: If you can't run make commands then simply type out
javac Server.java
java Server 9879

and

javac Client.java
java Client A 1234 localhost 9879

Clients include A-J, all secret keys are 1234

Commands include:
log on- start the log on process
Chat [client-id]- start a chat with another client
while chatting typeing and send messages
End Chat- end your current chat session
History [Client-id]- get the history with a certian client
log off- end you session
