package statementView;
import java.util.List;

public class ContentsViewer {

    private TextChunk wholeText;
    private TextChunk textByArticle;

    ContentsViewer(TextChunk wholeText,TextChunk textByArticle)
    {
        this.wholeText=wholeText;
        this.textByArticle=textByArticle;
    }

    public void showAll()
    {
        wholeText.printContents();
    }

    public void showPoints()
    {
        for(TextChunk subelement:wholeText.getSublist())subelement.printHeaders();

    }

    public void articleRange(String[] articles) throws IllegalArgumentException
    {
        if(articles.length==2)
        {
            TextChunk art1=getChunkFromSublist(articles[0],textByArticle);
            TextChunk art2=getChunkFromSublist(articles[1],textByArticle);
            if(!articlesInOrder(art1,art2))
            {
                throw new IllegalArgumentException("Niepoprawny argument. Numer drugiego artykułu jest mniejszy od pierwszego.");
            }
            if(art1==null) throw new IllegalArgumentException("Pierwszy z wprowadzonych artykułów nie nie istnieje.");
            if(art2==null) throw new IllegalArgumentException("Drugi z wprowadzonych artykułów nie istnieje.");
            List<TextChunk> desiredArticles=textByArticle.getSublist().subList(textByArticle.getSublist().indexOf(art1),textByArticle.getSublist().indexOf(art2)+1);
            for(TextChunk chunk:desiredArticles) {
              chunk.printContents();
            }

        }else throw new IllegalArgumentException("Niewłaściwa ilość argumentów zakresu artykułów, proszę podać dwa zakresy artykułów");
    }
    public void articleContent(String [] arguments) throws IllegalArgumentException
    {
        if(arguments.length==0) throw new IllegalArgumentException("Podany opis elementu jest niewystarczający.");
        String title=arguments[0];
        TextChunk chunk = getChunkFromSublist(title,textByArticle);
        for(int i=1;i<arguments.length;i++)
        {
            title=arguments[i];
            chunk=getChunkFromSublist(title,chunk);

        }
        if(chunk!=null && title.equals(chunk.getTitle()))chunk.printContents();
        else throw new IllegalArgumentException("Podany element nie istnieje.");
    }
    public void chunkID (String name) throws IllegalArgumentException
    {
        TextChunk chunk= searchInChunk(name,wholeText);
        if(chunk==null)throw new IllegalArgumentException("Nie ma elementu o nazwie:"+name+" , sprawdz skladnie lub poprawnosc argumentu");
        else chunk.printHeaders();

    }
    public void  chunkContents(String name)throws IllegalArgumentException
    {
        TextChunk chunk= searchInChunk(name,wholeText);
        if(chunk==null)throw new IllegalArgumentException("Nie ma elementu o nazwie:"+name+" , sprawdz skladnie lub poprawnosc argumentu");
        else chunk.printContents();

    }

    private Boolean articlesInOrder(TextChunk art1, TextChunk art2)
    {
        return indexOfArticle(art1)<indexOfArticle(art2);
    }

    private int indexOfArticle(TextChunk article)
    {
        return textByArticle.getSublist().indexOf(article);
    }

    public TextChunk getChunkFromSublist(String name, TextChunk chunk)  throws IllegalArgumentException
    {
        if(chunk.getSublist()!=null)
        {
            for(TextChunk element:chunk.getSublist())
            {
                if(element.getTitle().equals(name))return element;
            }

        }
        else throw new IllegalArgumentException("Podany element nie istnieje.");
        return null;
    }

    public TextChunk searchInChunk(String name,TextChunk chunk)
    {
        if(chunk.getTitle()!=null)
        {
            if(chunk.getTitle().equals(name))return chunk;
        }
        if(chunk.getSublist()!=null)
        {
            for(TextChunk tmp: chunk.getSublist())
            {
                TextChunk found=searchInChunk(name,tmp);
                if(found!=null)return found;
            }
        }
        return null;
    }

}
