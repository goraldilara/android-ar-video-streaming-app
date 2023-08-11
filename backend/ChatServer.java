package backend;

import nu.pattern.OpenCV;

import java.lang.reflect.Array;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

/**
 *
 * ChatServer is a class that works as a server. The class listens the assigned port
 * and insert connected clients' ports and IP addresses into arraylists
 *
 */
public class ChatServer {


    public final static int PORT = 2020;
    private final static int BUFFER = 62024;
    private DatagramPacket packet;

    public ArrayList<InetAddress> getClient_addresses() {
        return client_addresses;
    }

    public ArrayList<Integer> getClient_ports() {
        return client_ports;
    }

    public HashSet<String> getExisting_clients() {
        return existing_clients;
    }

    private ArrayList<InetAddress> client_addresses;
    private ArrayList<Integer> client_ports;
    private HashSet<String> existing_clients;

    //username deneme
    private HashMap<InetAddress, String> client_usernames;
    private String username;
    private DatagramSocket datagramSocket;

    private String message;

    static ChatServer instance;
    private VideoStreaming videoStreaming;
    public void setClient_addresses(ArrayList<InetAddress> client_addresses) {
        this.client_addresses = client_addresses;
    }

    public void setClient_ports(ArrayList<Integer> client_ports) {
        this.client_ports = client_ports;
    }

    public void setExisting_clients(HashSet<String> existing_clients) {
        this.existing_clients = existing_clients;
    }

    /**
     * @param datagramSocket UDP's Datagram Socket
     */
    public ChatServer(DatagramSocket datagramSocket){
        this.datagramSocket = datagramSocket;
        System.out.println("Server is running and is listening on port " + datagramSocket.getLocalPort());
        this.client_addresses = new ArrayList();
        this.client_ports = new ArrayList();
        this.existing_clients = new HashSet();
        this.client_usernames = new HashMap<>();
    }

    /**
     *
     * This method listens assigned port and fills packet if a client makes a call
     *
     */
    public void getInput() throws SocketException {

        MessageBroadcast broadcastInstance = new MessageBroadcast(instance);
        byte[] buffer;
        byte[] byteArray = new byte[BUFFER];
        while (true) {
            buffer = new byte[BUFFER];
            try {
                //receive filled packet from socket
                packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);

                //assign existing client's port and address
                InetAddress clientAddress = packet.getAddress();
                int client_port = packet.getPort();

                //convert packet data into byte array
                byteArray = packet.getData();
                message = new String(packet.getData(), 0, packet.getLength());

                if(!(message.equals("New client connect dilaragoral") || message.equals("Start Watching"))){
                    broadcastInstance.broadcastMessage(byteArray);
                }
                if(message.equals("Start Watching")){
                    //startThreads();
                }

                //fill arrays with client information
                String id = clientAddress.toString() + "|" + client_port;
                if (!existing_clients.contains(id)) {
                    //client_usernames.put(clientAddress,username);
                    //System.out.println(client_usernames.get(clientAddress));
                    existing_clients.add(id);
                    client_ports.add(client_port);
                    client_addresses.add(clientAddress);
                }

                //only for one way broadcast of server
                //if(message != null){
                    //video streaming function
                //    instance.startThreads();
                //}

            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }

    /**
     * This method creates Video Streaming object and starts the thread
     */
    public void startThreads(){
        try{
            videoStreaming = new VideoStreaming(datagramSocket, instance);
            Thread streamerThread = new Thread(videoStreaming);
            streamerThread.start();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws SocketException {

        OpenCV.loadLocally();
        try{
            DatagramSocket socket = new DatagramSocket(PORT);
            instance = new ChatServer(socket);
            //first connection function
            instance.getInput();
        }catch(Exception e){
            e.printStackTrace();
        }


    }


}


