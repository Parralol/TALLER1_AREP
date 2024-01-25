package edu.escuelaing.arem.ASE.app;

import java.net.*;
import java.util.List;
import java.util.Scanner;
import java.io.*; 

public class HttpServer {
    static String key = "&apikey=b5ed8d05";
    static String url = "http://www.omdbapi.com/?t=";
    public static void main(String[] args) throws IOException {

        boolean hasprint = false;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine, outputLine;
            int count = 0;
            String request="";

            while ((inputLine = in.readLine()) != null) {
                if(count == 0){
                    request = inputLine;
                    request = getQuery(request);
                    System.out.println(request);

                    count +=1;
                }
                
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            out.println("");
            if(!hasprint){
            outputLine = "HTTP/1.1 200 OK"
                    + "Content-Type:text/html; charset=ISO-8859-1\r\n"
                    + "\r\n"
                    + "<!DOCTYPE html>\r\n" + //
                    "<html>\r\n" + //
                    "    <head>\r\n" + //
                    "        <title>Form Example</title>\r\n" + //
                    "        <meta charset=\"UTF-8\">\r\n" + //
                    "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                    "    </head>\r\n" + //
                    "    <body>\r\n" + //
                    "        <h1>Form with GET</h1>\r\n" + //
                    "        <form action=\"/hello\">\r\n" + //
                    "            <label for=\"name\">Name:</label><br>\r\n" + //
                    "            <input type=\"text\" id=\"name\" name=\"name\" value=\"John\"><br><br>\r\n" + //
                    "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\r\n" + //
                    "        </form> \r\n" + //
                    "        <div id=\"getrespmsg\"></div>\r\n" + //
                    "\r\n" + //
                    "        <script>\r\n" + //
                    "            function loadGetMsg() {\r\n" + //
                    "                let nameVar = document.getElementById(\"name\").value;\r\n" + //
                    "                const xhttp = new XMLHttpRequest();\r\n" + //
                    "                xhttp.onload = function() {\r\n" + //
                    "                    document.getElementById(\"getrespmsg\").innerHTML =\r\n" + //
                    "                    this.responseText;\r\n" + //
                    "                }\r\n" + //
                    "                xhttp.open(\"GET\", \"/hello?name=\"+nameVar);\r\n" + //
                    "                xhttp.send();\r\n" + //
                    "            }\r\n" + //
                    "        </script>\r\n" + //
                    "\r\n" + //
                    "        <h1>Form with POST</h1>\r\n" + //
                    "        <form action=\"/hellopost\">\r\n" + //
                    "            <label for=\"postname\">Name:</label><br>\r\n" + //
                    "            <input type=\"text\" id=\"postname\" name=\"name\" value=\"John\"><br><br>\r\n" + //
                    "            <input type=\"button\" value=\"Submit\" onclick=\"loadPostMsg(postname)\">\r\n" + //
                    "        </form>\r\n" + //
                    "        \r\n" + //
                    "        <div id=\"postrespmsg\"></div>\r\n" + //
                    "        \r\n" + //
                    "        <script>\r\n" + //
                    "            function loadPostMsg(name){\r\n" + //
                    "                let url = \"/hellopost?name=\" + name.value;\r\n" + //
                    "\r\n" + //
                    "                fetch (url, {method: 'POST'})\r\n" + //
                    "                    .then(x => x.text())\r\n" + //
                    "                    .then(y => document.getElementById(\"postrespmsg\").innerHTML = y);\r\n" + //
                    "            }\r\n" + //
                    "        </script>\r\n" + //
                    "    </body>\r\n" + //
                    "</html>" + inputLine;
                out.println(outputLine);
                hasprint = true;
            }
            if(hasprint){
                String inline = getJson(request);
                System.out.println(inline);
                out.println(inline);
            }
            out.flush();
            
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();
    }

    /**
     * Allows to split the GET or POST request just to get the query
     * @param text
     * @return
     */
    private static String getQuery(String text){
        String[] deco1 = text.split(" ");
        String[] deco2 = deco1[1].split("\\?");
        if(deco2.length >=2){
            String[] deco3 = deco2[1].split("\\#");
            return deco3[0];
        }else{
            return deco2[0];
        }
    }

    /**
     * Permite recibir el Json que se esta buscando
     * @param request
     * @return
     */
    private static String getJson(String request){
        String[] requests = request.split("=");
        String defurl = url + requests[1]+key;
        System.out.println(defurl);
        String res ="";
        try{
            URL api = new URL(defurl);
            HttpURLConnection connection = (HttpURLConnection) api.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responsecode = connection.getResponseCode();
            System.out.println("CONNECTION STATUS" + "----->  " + responsecode);
            String inline = "";
            Scanner scanner = new Scanner(api.openStream());
                
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }
            scanner.close();
            res = inline;
                
        }catch(IOException e){
                    System.out.println(e.getMessage());
        }
        return res;
        
    }
}
