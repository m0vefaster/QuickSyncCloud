import java.io.*;
import java.util.*;
import java.lang.*;

class ListOfFiles implements Serializable
{
    static String homeDir = System.getProperty("user.home");
    static String folder = "QuickSync";
    static String path = homeDir + "/" + folder ;
    ArrayList<String> list;
    ArrayList<String> list2;
    
    ListOfFiles( )
    {

    }
    
    ListOfFiles(ArrayList<String> list)
    {
        this.list=list;
    }
    
    ArrayList<String> getArrayListOfFiles()
    {
        return this.list;
    }

    ArrayList<String> getList ( )
    {
         list = new ArrayList<String>();
         return removeAbsolutePath(getListHelper(list,path));
    }
    
    ArrayList<String> getListHelper (ArrayList<String> list, String path)
    {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        
        for(int i=0;i< listOfFiles.length;i++)
        {
            if(listOfFiles[i].isFile()  && listOfFiles[i].getName().charAt(0) != '.' )//&& listOfFiles[i].length()!=0)
            {
                list.add(path+"/"+ listOfFiles[i].getName());
            }
            else if(listOfFiles[i].isDirectory() && listOfFiles[i].getName().charAt(0) != '.')// && listOfFiles[i].length()!=0)
            {
                getListHelper(list,path+"/"+ listOfFiles[i].getName());
            }
        }
        
        return list;
    }
    
    
    ArrayList<String> getList2 ( )
    {
         list2 = new ArrayList<String>();
         return removeAbsolutePath(getListHelper(list2,path));
    }

    ArrayList<String> getListHelper2 (ArrayList<String> list2, String path)
    {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();

        for(int i=0;i< listOfFiles.length;i++)
        {
            if(listOfFiles[i].isFile()  && listOfFiles[i].getName().charAt(0) != '.' && listOfFiles[i].length()!=0)
            {
                list.add(path+"/"+ listOfFiles[i].getName());
            }
            else if(listOfFiles[i].isDirectory() && listOfFiles[i].getName().charAt(0) != '.' && listOfFiles[i].length()!=0)
            {
                getListHelper2(list,path+"/"+ listOfFiles[i].getName());
            }
        }

        return list2;
    }
	
    void setList(ArrayList<String> list){
        this.list = list;
    }

    ArrayList<String> removeAbsolutePath(ArrayList<String> list)
    { 
      if(list==null)
        return null;

      for(int i=0;i<list.size();i++)
      {
        String fileName = list.get(i);
        fileName=fileName.substring(fileName.indexOf("QuickSync")+10);
        list.set(i,fileName);
      }
      
      return list;
    }

    void printFileList()
    {
        if(list == null){
            return;
        }

        Iterator<String> itr = list.iterator();
        String file;
        while(itr.hasNext()){
            file = itr.next();
            System.out.print(file+",");
        }
        System.out.println();
    }

}
