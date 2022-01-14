package src;

import src.config.Configuration;
import src.config.ConfigurationManager;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Driver Class for the HTTP server
 */

public class Httpserver extends Thread implements  Runnable {
    private static boolean serverIsOpen = true;
    private static boolean serverIsRunning = false;
    private static boolean serverIsInMaintenance = false;
    private static ServerSocket serverSocket = null;
    private static String rootDirectory = "src/main/java/com.basic.httpserver";
    //response content types
    static final String HTML = "text/html";
    static final String CSS = "text/css";
    static final String JPG = "image/jpeg";
    static final String MAINTENANCE_CSS = "maintenance.css";
    static final String OK_200 = "200 OK";
    static final String NOT_FOUND_404 = "";
    static final String REQUEST_TIMEOUT_408 = "";
    private Socket clientSocket = null;


    public Httpserver(Socket socket) {
        clientSocket = socket;
    }
    private static File search(File file, String filename) {
        if (file.isDirectory()) {
            if (file.canRead()) {
                for (File temp : file.listFiles()) {
                    if (temp.isDirectory()) {
                        File wantedFile = search(temp, filename);
                        if(wantedFile!=null) {
                            return wantedFile;
                        }
                    } else {
                        if (filename.equals(temp.getName().toLowerCase())) {
                            return new File(temp.getAbsoluteFile().toString());
                        }
                    }
                }
            }
        }
        return null;
    }
    public static Object read(String filepath) throws NullPointerException, IOException, FileNotFoundException {
        File file = new File(filepath);
        if(!file.exists()) {
            String filename = filepath.split("/")[filepath.split("/").length-1];
            File root = new File(rootDirectory);
            if (root.isDirectory()) {
                File wantedFile = search(root, filename);
                if(wantedFile!=null) {
                    file = wantedFile;
                } else {
                    throw new FileNotFoundException();
                }
            } else {
                throw new FileNotFoundException();
            }
        }

        String s="";
        Scanner scanner = new Scanner(file);
        while (scanner.hasNextLine()) {
            s += scanner.nextLine();
        }
        scanner.close();
        return s.getBytes();
    }


    public void sendResponse(Object[] data, OutputStream out) throws NullPointerException, IllegalArgumentException {


        if(!data[2].equals(OK_200)&&!data[2].equals(NOT_FOUND_404)&&!data[2].equals(REQUEST_TIMEOUT_408)) {
            throw new IllegalArgumentException();
        }

        String CRLF = "\r\n";
        byte[] bytes = (byte[])data[0];
        String contentType = (String)data[1];
        String status = (String)data[2];
        byte[] responsePart1 = null;

        responsePart1 = ("HTTP/1.1 " + status + CRLF + "Content-Type: " + contentType + CRLF + "Content-Length: " + bytes.length + CRLF + CRLF).getBytes();
        byte[] responsePart2 = bytes;
        byte[] responsePart3 = (CRLF + CRLF).getBytes();

        byte[] response = new byte[responsePart1.length + responsePart2.length + responsePart3.length];
        System.arraycopy(responsePart2, 0, response, responsePart1.length, responsePart2.length);
        System.arraycopy(responsePart1, 0, response, 0, responsePart1.length);
        System.arraycopy(responsePart3, 0, response, responsePart1.length + responsePart2.length, responsePart3.length);

        try {
            out.write(response);
        } catch (IOException e) {
            System.err.println("Error writing response!");
            System.exit(-1);
        }
    }

    public void run() {
        System.out.println("New Communication Thread Started");
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream outputStream = clientSocket.getOutputStream();

            String inputLine;
            try {
                while ((inputLine = bufferedReader.readLine()) != null) {
                    if(inputLine.startsWith("GET")) {
                        //sendResponse(getResource(inputLine.split(" ")[1].substring(1).replace("%20", " ")), out);
                    }
                    if (inputLine.trim().equals("")) {
                        break;
                    }
                }
            } catch (IOException e1) {
                System.err.println("Error reading request");
            }

            outputStream.close();
            bufferedReader.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Problem with Communication Server");
            System.exit(-1);
        }
    }

    public static void main(String[] args ) throws InterruptedException {
        System.out.println("Server starting...");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        System.out.println("Using Port:" + conf.getPort());
        System.out.println("Using Webroot:" + conf.getWebroot());
        while(serverIsOpen==true) {
            Thread.sleep(25);
            if(serverIsRunning==true) {
                try {
                    ServerSocket serverSocket = new ServerSocket(conf.getPort());
                    Socket socket = serverSocket.accept();
                     new Httpserver(socket).start();
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();


                    String htmlDecoy = Files.readString(Path.of("D:/IdeaProjects/Simplehttpserver/src/main/resources/index.html"), StandardCharsets.US_ASCII);
                    String html = "<html><head><title>Simple Java HTTP server</title></head><body><h1>Nice</h1></body></html>";

                    final String CRLF = "\n\r"; //13,10
                    String response =
                            "HTTP/1.1 200 OK" +//Status line :: HTTP VERSION RESPONSE_CODE RESPONSE_MESSAGE
                                    "Content-length:" + html.getBytes().length + CRLF + // header
                                    CRLF +
                                    html +
                                    CRLF + CRLF;
                    outputStream.write(response.getBytes());


                    inputStream.close();
                    outputStream.close();
                    socket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }



}
