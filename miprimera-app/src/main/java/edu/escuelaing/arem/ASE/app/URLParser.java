package edu.escuelaing.arem.ASE.app;

import java.net.MalformedURLException;
import java.net.URL;

public class URLParser {
    
    public static void main( String[] args ) throws MalformedURLException
    {
        URL myurl = new URL("https://docs.oracle.com:80/javase/tutorial/networking/index.html?Parralol#LugardeReferencia");
        System.out.println("Host" +" "+ myurl.getHost());
        System.out.println("Authority" +" "+ myurl.getAuthority());
        System.out.println("Path" +" "+ myurl.getPath());
        System.out.println("Protocol" +" "+ myurl.getProtocol());
        System.out.println("Port" +" "+ myurl.getPort());
        System.out.println("Query" +" "+ myurl.getQuery());
        System.out.println("Ref:" +" "+ myurl.getRef());
        System.out.println("Host" +" "+ myurl.getFile());

    }
}
