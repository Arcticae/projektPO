package statementView;

import javax.xml.soap.Text;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import statementView.TextChunk;
public class Objectifier {      //must have a data deleter which indicates data b4 regex nr 1.

    private List<String> activeRegexes;

    public Objectifier(List<String> regexes)
    {
        this.activeRegexes=regexes;
    }

    public TextChunk objectifyText(LinkedList<String> text)
    {
        LinkedList<String> preambule=preambuleData(text);

        LinkedList<TextChunk> sublist=sublistCreator(text,activeRegexes.get(0));
        TextChunk txtChunk= new TextChunk(null,null,preambule,sublist);
        return txtChunk;
    }



    private LinkedList<String> preambuleData(LinkedList<String> text)
    {
            LinkedList<String> preambule= new LinkedList<>();

            for(String line : text)
            {
                if(!line.matches(activeRegexes.get(0)))
                {
                    preambule.add(line);
                }else break;
            }
            return preambule;

    }

    private LinkedList<TextChunk> sublistCreator(LinkedList<String> text,String firstRegex)
    {
        if(firstRegex==null)return null;

        LinkedList<TextChunk> result=new LinkedList<>();

        LinkedList<LinkedList<String>> divisionByRegex=divideTextByRegex(text,firstRegex);

        if(divisionByRegex.size()==0)return null;

        for(LinkedList<String> slice:divisionByRegex)
        {
            TextChunk chunk=createChunk(slice,whichRegex(slice.get(0)));
            result.add(chunk);
        }
        return result;


    }

    private LinkedList<LinkedList<String>> divideTextByRegex(LinkedList<String> text,String firstRegex)
    {
        LinkedList<LinkedList<String>> divisionByRegex=new LinkedList<LinkedList<String>>();
        List<String> higherRegexes=this.activeRegexes.subList(0,activeRegexes.indexOf(firstRegex)+1);

        for(String line: text)
        {
            if(matchesOneRegex(higherRegexes,line))
            {
                String regex=whichRegex(line);
                LinkedList<String> slice = new LinkedList<>();
                slice.add(line);
                higherRegexes=activeRegexes.subList(0,activeRegexes.indexOf(regex)+1);

                for(String lineTwo : text.subList(text.indexOf(line)+1,text.size()))
                {
                    if(!matchesOneRegex(higherRegexes,lineTwo))slice.add(lineTwo);
                    else break;
                }

                divisionByRegex.add(slice);

            }
        }

    return divisionByRegex;
    }

    private TextChunk createChunk(LinkedList<String> slice,String regex)
    {
        String title=getChunkTitle(slice.get(0),regex);
        String subtitle=null;
        if(hasTitle(regex))subtitle=slice.get(1);
        LinkedList<String> contents=sectionContents(slice,regex,title);
        LinkedList<String> rawData=getRawData(contents,regex);
        String subRegex=getSubRegex(contents);
        LinkedList<TextChunk> sublist=sublistCreator(contents,subRegex);
        TextChunk newTextChunk= new TextChunk(title,subtitle,rawData,sublist);
        return newTextChunk;

    }
    private Boolean hasTitle(String regex)
    {
        if(regex.equals("^DZIAŁ\\sX{0,3}(IX|IV|V?I{0,3})[A-Z]$") || regex.equals("^Rozdział\\s\\w+$"))return true;
        return false;

    }
    private String getSubRegex(LinkedList<String> text)
    {
        for(String line:text)
        {
            if(matchesOneRegex(activeRegexes,line))
            {
                return whichRegex(line);
            }
        }
        return null;
    }
    private LinkedList<String> getRawData(LinkedList<String> input,String regex){

    List<String> lowerRegexes=activeRegexes.subList(activeRegexes.indexOf(regex),activeRegexes.size());
    LinkedList<String> rawData=new LinkedList<>();
    for(String line:input)
    {
        if(matchesOneRegex(lowerRegexes,line))break;
        else rawData.add(line);
    }
    return rawData;

    }
    private LinkedList<String> sectionContents(LinkedList<String> list, String regex,String title)
    {
        if(list.get(0).equals(title))
        {
            list.remove(0);
        }
        else
        {
            list.set(0,list.get(0).replace(title+" ",""));
        }
        if(regex.equals("^DZIAŁ\\sX{0,3}(IX|IV|V?I{0,3})[A-Z]$") || regex.equals("^Rozdział\\s\\w+$"))
        {
            list.remove(0);
        }
        return list;
    }
    private Boolean matchesOneRegex(List<String> regexes,String line)
    {
        for(String regex:regexes)
        {
            if(line.matches(regex))return true;
        }
        return false;

    }

    private String getChunkTitle(String line,String regex)
    {
        switch (regex)
        {
            case "^DZIAŁ\\sX{0,3}(IX|IV|V?I{0,3})[A-Z]$":
                return line;
            case "^Rozdział\\s\\w+$":
                return line;
            case "^[A-ZĄĆŹÓŻĘŁŃŚ,]{2,}(\\s+[A-ZĄĆŹÓŻĘŁŃŚ,]+)*$":
                return line;
            case "^\\S+\\.\\s\\d+[a-z]*\\..*$":
                return line.split(" ")[0]+" "+line.split(" ")[1];
            case "^\\d+\\.\\s+.+$":
                return line.split(" ")[0];
            case "^\\d+[a-z]*[)]\\s+.+$":
                return line.split(" ")[0];
            case "^[a-z][)].*$":
                return line.split(" ")[0];
            default:
                return null;
        }


    }

    public LinkedList<String> deleteTitles(LinkedList<String> text, List<String> regexList,String regex)
    {
        for(Iterator<String> iter=text.listIterator();iter.hasNext();)
        {
            String line=iter.next();
            if(matchesOneRegex(regexList,line)&&!line.matches(regex))
            {
                iter.remove();
            }
        }
        return text;
    }

    private String whichRegex(String line)
    {
        for(String reg:this.activeRegexes)
        {
            if(line.matches(reg))return reg;
        }
        return null;
    }

}
