package ro.any.c12153.shared;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import ro.any.c12153.dbutils.DbConn;

/**
 *
 * @author C12153
 */
public class App {
    public static String getProperty(String propertyName) throws Exception{
        try(InputStream input = App.class.getClassLoader().getResourceAsStream("config.properties")){
            Properties prop = new Properties();
            prop.load(input);
            return prop.getProperty(propertyName);
        }
    }
    
    public static String getBeanMess(String key, Locale locale){
        String rezultat = null;
        try {    
            rezultat = ResourceBundle.getBundle(getProperty("beans_messages_properties_file_name"), locale).getString(key);
        } catch (Exception ex) {
            log(Logger.getLogger(App.class.getName()), Level.SEVERE, null, ex);
            rezultat = "???" + key + "???";
        }
        return rezultat;
    }
    
    public static String getStringFromResource(String path) throws Exception{
        try(InputStream input = App.class.getClassLoader().getResourceAsStream(path);
            InputStreamReader ireader = new InputStreamReader(input, StandardCharsets.UTF_8);
            BufferedReader breader = new BufferedReader(ireader);){
            
            return breader.lines()
                    .map(x -> x.trim())
                    .filter(x -> !x.isEmpty())
                    .collect(Collectors.joining(" "));
        }
    }
    
    public static String getSql(String filename) throws Exception{
        return getStringFromResource(getProperty("sql_resource_folder") + filename  + ".sql");
    }
    
    public static DbConn getConn(String userId) throws Exception{
        return new DbConn(
                (DataSource) new InitialContext().lookup(getProperty("datasource_jndi")),
                Integer.valueOf(getProperty("datasource_batch_size")),
                userId
        );
    }
    
    public static void log(Logger logger, Level level, String userId, Exception exception){        
        try{
            final String ERROR_LOG_CLASS_DOMAIN = getProperty("error_log_class_domain");
            
            String rezultat = exception.getClass().getName();
            if (Utils.stringNotEmpty(exception.getMessage())) rezultat += ": " + exception.getMessage();
            
            rezultat += "\n" + Arrays.asList(exception.getStackTrace()).stream()
                    .filter(x -> x.getClassName().startsWith(ERROR_LOG_CLASS_DOMAIN))
                    .map(x -> "\t\tat " + x.getClassName() +  "." + x.getMethodName() + ":" + x.getLineNumber())
                    .collect(Collectors.joining("\n"));

            logger.log(Level.SEVERE, "{0} - {1}", new String[]{userId, rezultat});
        } catch (Exception ex) {
        }
    }
}
