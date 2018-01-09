package statementView;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileCruncher   //gets the input into lines and filters them if they are bad it throws them away
{


    private final Pattern badLine=Pattern.compile("(^©.*)|(^(\\d{4}-\\d{2}-\\d{2}))|(.?)");
    private final Pattern pauseEnd=Pattern.compile("(-)$");

    public LinkedList<String> lineList(Path source)throws IOException{

        LinkedList<String> result=new LinkedList<>();

        try(Scanner scanner = new Scanner(source)){

            String analyzed ;
            Matcher mat;

              while(scanner.hasNext())
              {
                  analyzed=scanner.nextLine();
                  mat = badLine.matcher(analyzed);
                  if(!mat.matches())result.add(analyzed);
              }


        }catch(IOException e)
        {
            System.out.println("Błąd operacji wejścia/wyjścia na pliku. Plik nie istnieje, bądź podana została niepoprawna ścieżka.");
        }

        return result;
    }
    public void wrapEndings(LinkedList<String> lineList)
    {

      for(int i=0;i<lineList.size();i++)
        {
            String analyzed=lineList.get(i);
            Matcher pauseE = pauseEnd.matcher(analyzed);
            if(pauseE.find()){

                String[] tmp=lineList.get(i+1).split("^\\S*\\s");

                String resultConcated=analyzed.substring(0,analyzed.length()-1)+tmp[0];
                lineList.set(i,resultConcated);
                if(tmp.length>1)lineList.set(i+1,tmp[1]);
                else lineList.remove(i+1);

            }

        }

    }


}