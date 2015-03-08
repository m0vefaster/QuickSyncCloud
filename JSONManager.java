import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONManager {

    public static JSONObject getJSON(ArrayList<String> files)
    {
        JSONObject obj = new JSONObject();
        //System.out.println("JSONManager:getJSON:Making JSON from ArrayList");
        //System.out.println("JSONManager:getJSON:The ArrayList is"+files);
        obj.put("type", "ArrayList");
        obj.put("value", files);
        return obj;
    }
    
    public static JSONObject getJSON(HashMap<String,ArrayList<String> > files)
    {
        JSONObject obj = new JSONObject();
        //System.out.println("JSONManager:getJSON:Making JSON from HashMap");
        //System.out.println("JSONManager:getJSON:The HashMap is:"+files);
        obj.put("type", "HashMap");
        obj.put("value", files);

        return obj;
	    }
    
    public static JSONObject getJSON(String message)
    {
        JSONObject obj = new JSONObject();
        //System.out.println("JSONManager:getJSON:Making JSON from Message");
        obj.put("type", "Control");
        obj.put("value", message);
        //System.out.println("Message is:" + obj.toString());
        return obj;
    }
    public static JSONObject getJSON(File file)
    {
        JSONObject obj = new JSONObject();
        //System.out.println("JSONManager:getJSON:Making JSON from File");
        final String EoL = System.getProperty("line.separator");
        List<String> lines;
        try {
            //lines = Files.readAllLines(Paths.get(file.getAbsolutePath()), Charset.forName("ISO-8859-1"));
            lines = Files.readAllLines(Paths.get(file.getAbsolutePath()), Charset.defaultCharset());
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(line).append(EoL);
            }
            final String content = sb.toString();
            ////System.out.println(content);
            String fileName = file.getAbsolutePath();
        	fileName=fileName.substring(fileName.indexOf("QuickSync")+10);
            obj.put("type", "File"+fileName);
            obj.put("value", content);
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return obj;
    }

    public static Object convertStringToJSON(String str)
    {
        //System.out.println("JSONManager:convertStringToJSON:Converting String to JSON");
        JSONParser parser=new JSONParser();
        JSONObject obj = null;
        try {
            obj = (JSONObject) parser.parse(str);
        } catch (ParseException e) {
            //e.printStackTrace();
	}

        return obj;
    }
    
}
