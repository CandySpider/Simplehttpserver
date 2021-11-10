package com.basic.httpserver;

import com.basic.httpserver.config.Configuration;
import com.basic.httpserver.config.ConfigurationManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Driver Class for the HTTP server
 */
public class Httpserver {
    public static void main(String[] args )  {
        System.out.println("Server starting...");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.json");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        System.out.println("Using Port:" + conf.getPort());
        System.out.println("Using Webroot:" + conf.getWebroot());
       try {
           ServerSocket serverSocket = new ServerSocket(conf.getPort());
           Socket socket = serverSocket.accept();

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
                           CRLF +CRLF;
           outputStream.write(response.getBytes());


           inputStream.close();
           outputStream.close();
           socket.close();
           serverSocket.close();
       }
       catch (IOException e) {
           e.printStackTrace();
       }
    }
}
