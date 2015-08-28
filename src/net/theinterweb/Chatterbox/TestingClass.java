package net.theinterweb.Chatterbox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class TestingClass {
    public static void main(String[] args) throws IOException 
    {
    URL connection = new URL("http://checkip.amazonaws.com/");
    URLConnection con = connection.openConnection();
    String str = null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
    str = reader.readLine();
    System.out.println(str);
     }
}
