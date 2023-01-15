import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<>(); // store all the clients and broadcast message to every client in this list
    private Socket socket; //socket to establish connection between client and server
    private BufferedReader bufferedReader; //read messages sent from the clients
    private BufferedWriter bufferedWriter; //send data to clients
    private String clientUsername;//username of the client
    public ClientHandler(Socket socket){
        try{
            this.socket=socket;
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader= new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername=bufferedReader.readLine();
            clientHandlers.add(this);
            broadCastMessage("SERVER: " + clientUsername + " has entered the chat");


        }catch (IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }

    }
    @Override
    public void run() {
         String messageFromClient;
         while(socket.isConnected()){
             try{
                 messageFromClient=bufferedReader.readLine();
                 broadCastMessage(messageFromClient);

             }catch(IOException e){
                 closeEverything(socket,bufferedReader,bufferedWriter);
                 break;
             }
         }
    }
    public void broadCastMessage(String messageToSend){
        for (ClientHandler clientHandler:clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();

                }
            }catch(IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
            }
        }
    }
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadCastMessage("SERVER " + clientUsername + " has left the chat");
    }
    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        removeClientHandler();
        try{
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(bufferedWriter!=null){
                bufferedWriter.close();
            }
            if(socket!=null){
                socket.close();
            }
        }catch (IOException e ){
            e.printStackTrace();
        }
    }
}
