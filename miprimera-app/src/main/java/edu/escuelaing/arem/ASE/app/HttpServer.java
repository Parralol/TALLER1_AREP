package edu.escuelaing.arem.ASE.app;

import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.*; 

public class HttpServer {
    private static String key = "&apikey=b5ed8d05";
    private static String url = "http://www.omdbapi.com/?t=";
    private static Map<String, String> cache = new HashMap<String, String>();
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
            String path = "";
            while ((inputLine = in.readLine()) != null) {
                if(count == 0){
                    request = inputLine;
                    path = getPath(request);
                    request = getQuery(request);
                    
                    count +=1;
                }
                
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            out.println("");
            if(path.equals("/cliente") || "/favicon.ico" == path){
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
                        "        <form action=\"/cliente\">\r\n" + //
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
                        "                xhttp.open(\"GET\", \"/cliente?name=\"+nameVar);\r\n" + //
                        "                xhttp.send();\r\n" + //
                        "            }\r\n" + //
                        "        </script>\r\n" + //
                        "\r\n" + //
                        "    </body>\r\n" + //
                        "</html>" + inputLine;
                out.println(outputLine);
                hasprint = true;
            }else{
                outputLine = 
                "HTTP/1.1 200 OK"
                + "Content-Type:text/html; charset=ISO-8859-1\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\r\n" + //
                "<html>\r\n" + //
                " <head>\r\n" + //
                "        <title>Error de direccion</title>\r\n" + //
                "        <meta charset=\"UTF-8\">\r\n" + //
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n" + //
                "    </head>\r\n"   +
                "   <body>\r\n" + //
                "        <h1>ERROR EN EL PATH, COLOCAR /cliente</h1>\r\n" + 
                "</body>\r\n" + inputLine;
                out.println(outputLine);
            }
            if(hasprint){
                String inline = "";
                if(cache.containsKey(request)){
                    inline = cache.get(request);
                }else{
                    inline = getJson(request);
                }
                cache.put(request, inline);
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
        System.out.println(Arrays.toString(deco2));
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
        String res ="";
        if(requests.length > 1){
            String defurl = url + requests[1]+key;
            System.out.println(defurl);
            
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
        }
        return res;
    }

    /**
     * returns the path that the current user is using
     * @param request
     * @return
     */
    private static String getPath(String request){
        String[] deco1 = request.split(" ");
        String[] deco2 = deco1[1].split("\\?");
        if(deco2.length <=1){
            return deco2[0];
        }else{
            return deco2[0];
        }
    }
}
