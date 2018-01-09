package statementView;

import java.util.LinkedList;


public class TextChunk {


    private String title;
    private String subtitle;
    private LinkedList<String> contents;
    private LinkedList<TextChunk> sublist;


    public TextChunk(String title, String subtitle, LinkedList<String> contents, LinkedList<TextChunk> sublist) {

        this.title = title;
        this.subtitle = subtitle;
        this.contents = contents;
        this.sublist = sublist;

    }

    public void printHeaders() {

        if(title.matches("^DZIAŁ\\sX{0,3}(IX|IV|V?I{0,3})[A-Z]$") || title.matches("^Rozdział\\s\\w+$") || title.matches("^[A-ZĄĆŹÓŻĘŁŃŚ,]{2,}" )) {

            if(subtitle==null)
            {
                System.out.println(title);
            }
            else System.out.println(title+"\n"+subtitle);
            if (sublist != null) {
                for (TextChunk child : this.sublist) {
                    child.printHeaders();
                }
            }
        }
    }

    public void printContents()
    {
        if(this.title!=null)System.out.println(this.title);
        if(this.subtitle!=null)System.out.println(this.subtitle);
        if(this.contents!=null)
        {
            for(String line:contents)System.out.println(line);
        }
        if(this.sublist!=null)
        {
            for(TextChunk child:sublist)
            {
                child.printContents();
            }
        }
}
        public LinkedList<TextChunk> getSublist()
        {
            return this.sublist;
        }

        public String getTitle()
        {
            return this.title;

        }
        public String getSubtitle()
    {
        return this.subtitle;

    }
        public LinkedList<String> getData()
    {
        return contents;
    }
}
