package statementView;



import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import statementView.TextChunk;

import org.apache.commons.cli.*;

public class MainMenu {
    public static void main(String[] arg)
    {
        final List<String> constitution= Arrays.asList("^Rozdział\\s\\w+$","^[A-ZĄĆŹÓŻĘŁŃŚ,]{2,}(\\s+[A-ZĄĆŹÓŻĘŁŃŚ,]+)*$","^\\S+\\.\\s\\d+[a-z]*\\..*$");
        final List<String> uokik= Arrays.asList("^DZIAŁ\\sX{0,3}(IX|IV|V?I{0,3})[A-Z]$","^Rozdział\\s\\w+$","^\\S+\\.\\s\\d+[a-z]*\\..*$");
        final List<String> belowTitles= Arrays.asList("^\\S+\\.\\s\\d+[a-z]*\\..*$","^\\d+\\.\\s+.+$","^\\d+[a-z]*[)]\\s+.+$","^[a-z][)].*$");



        Options options= new Options();
        HelpFormatter formatter=new HelpFormatter();


        CommandLineParser parser=new DefaultParser();
        CommandLine commandLine;

        Option filepath=new Option("p","path",true,"Ścieżka do pliku wejściowego");
        filepath.setRequired(true);
        options.addOption(filepath);

        Option mode=new Option("m","mode",true,"Tryb działania programu, np. 'l' dla spisu treści albo 'c' dla wyświetlania treści ");
        mode.setRequired(true);
        options.addOption(mode);

        Option article=new Option("a","article",true,"Konkretny artykuł lub jego konkretna część, np. 'Art. 1.,1.,1),a)'");
        article.setRequired(false);
        options.addOption(article);

        Option articleRange=new Option("ar","article_range",true,"Zakres artykułów");
        articleRange.setRequired(false);
        options.addOption(articleRange);


        Option chapter=new Option("ch","chapter",true,"Cały Rozdział lub podrozdział  np. 'DZIAŁ IA' lub 'Rozdział 1'");
        chapter.setRequired(false);
        options.addOption(chapter);

        Option all=new Option("w","whole_statement",false,"Wyświetlenie całej treści pliku");
        all.setRequired(false);
        options.addOption(all);


        try
        {
            commandLine=parser.parse(options,arg);
        }catch (ParseException e)
        {
            System.out.println(e.getMessage());
            formatter.printHelp("\nPoprawna składnia argumentów programu to:"+"\n <ścieżka do pliku> <tryb działania> '' <element>'' ",options);
            System.exit(1);
            return;
        }

        Path path;



        LinkedList<String> text=new LinkedList<>();

        FileCruncher fileCruncher=new FileCruncher();

        try{
             path=Paths.get(commandLine.getOptionValue("p"));
             text= fileCruncher.lineList(path);
            fileCruncher.wrapEndings(text);
        }
        catch (IOException e) {
            System.out.println("Podana ścieżka nie wskazuje poprawnego pliku");
        }

        Objectifier objectifierChapters;

        if(text.stream().anyMatch(line->line.matches(uokik.get(0))))
        {
            objectifierChapters=new Objectifier(uokik);
        }
        else
        {
            objectifierChapters=new Objectifier(constitution);
        }

        TextChunk statementWhole=objectifierChapters.objectifyText(text);
        objectifierChapters.deleteTitles(text,uokik,belowTitles.get(0));
        Objectifier objectifierArticles=new Objectifier(belowTitles);
        TextChunk statementArticles=objectifierArticles.objectifyText(text);

        ContentsViewer printer=new ContentsViewer(statementWhole,statementArticles);

        try{

            if(commandLine.getOptionValue("m").equals("l"))
            {
                if(commandLine.hasOption("w"))
                {
                    printer.showPoints();
                }
                if(commandLine.hasOption("a"))
                {
                    System.out.println("Brak możliwości wyświetlenia spisu treści dla artykułu");
                }
                if(commandLine.hasOption("ar"))
                {
                    System.out.println("Brak możliwości wyświetlenia spisu treści dla zakresu artykułów");
                }
                if(commandLine.hasOption("ch"))
                {
                    printer.chunkID(commandLine.getOptionValue("ch"));
                }



            }else if(commandLine.getOptionValue("m").equals("c"))
            {
                if(commandLine.hasOption("w"))
                {
                    printer.showAll();
                }
                if(commandLine.hasOption("a"))
                {
                    printer.articleContent(commandLine.getOptionValue("a").split(","));
                }
                if(commandLine.hasOption("ar"))
                {
                    printer.articleRange(commandLine.getOptionValue("ar").split("-"));
                }
                if(commandLine.hasOption("ch"))
                {
                    printer.chunkContents(commandLine.getOptionValue("ch"));
                }
            }else System.out.println("Podano niewłasciwy argument dla opcji 'm' ");

        }catch (IllegalArgumentException ex)
        {
            System.out.println(ex.getMessage());
            formatter.printHelp("\nPoprawna składnia argumentów programu to:"+"\n <ścieżka do pliku> <tryb działania> <element> ",options);
        }







    }
}
