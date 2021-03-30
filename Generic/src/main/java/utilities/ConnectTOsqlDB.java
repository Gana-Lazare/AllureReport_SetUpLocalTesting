package utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectTOsqlDB {
public static Properties properties;
public static Connection connection = null;

public static Properties loadFile(){
    properties = new Properties();
    try {
        File file = new File("C:\\Users\\micromedia\\Desktop\\FreeCrm\\Generic\\src\\main\\java\\utilities\\secret.properties");
        FileInputStream fis = new FileInputStream(file );
        properties.load(fis);
    }
    catch (IOException e){}
    return  properties;
}

public static Connection connectToDB() throws Exception {
    properties = loadFile();
   String  driver= properties.getProperty("MYSQLJDBC.driver");
    String  url= properties.getProperty("MYSQLJDBC.url");
    String  userName= properties.getProperty("MYSQLJDBC.userName");
    String  password= properties.getProperty("MYSQLJDBC.password");

Class.forName(driver);
    connection  =  DriverManager.getConnection(url,userName,password);
    if (connection!=null) System.out.println( "Connection established");
    connection.close();
    return connection;
}

    public static void main(String[] args) throws Exception {
    connectToDB();
    }
}
