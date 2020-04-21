import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import opennlp.tools.stemmer.PorterStemmer;

/**
 * Program used to generate preprocessed (normalized, filtered, and stemmed)
 * collection of documents given a tokenized data file as input
 */
public class Preprocessor
{
    public static Set<String> stopWords = getStopWords("./stopwords.txt");

    /**
     * Generates a set of stop words given a text file with line-delimited stop word tokens
     * @param fileName
     * @return
     */
    public static Set<String> getStopWords(String fileName)
    {
        Set<String> stopWordSet = new HashSet<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) 
        {
            String stopWord;
            while ((stopWord = bufferedReader.readLine()) != null) 
            {
                stopWordSet.add(stopWord);
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        return stopWordSet;
    }

    /**
     * Takes $DOC-$TITLE-$TEXT tokenized input from STDIN and applies additional 
     * preprocessing techniques, prints results line by line
     */
    public static void preprocessTokens()
    {
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext())
        {
            Boolean validLine = false;
            String line = scanner.nextLine();
            String[] lineTokens;

            if (line.startsWith("$DOC") || line.startsWith("$TITLE") || line.startsWith("$TEXT"))
            {
                System.out.println(line);
            }
            else
            {
                lineTokens = line.split(" ");
                
                for (String token: lineTokens)
                {
                    String normalizedToken = normalizeToken(token);
                    if (!normalizedToken.equals(""))
                    {
                        validLine = true;
                        System.out.print(normalizedToken + " ");
                    }
                }
                if (validLine)
                {
                    System.out.println();
                }
            }
        }
        scanner.close();
    }

    /**
     * Given a token returns the a copy of the token in its normalized 
     * and preprocessed form
     */
    public static String normalizeToken(String token)
    {
        String normalizedToken = "";
        PorterStemmer stemmer = new PorterStemmer();
        token = token.toLowerCase();

        if (token.length() == 1)
        {
            if (!token.matches("[a-zA-Z]"))
            {
                return normalizedToken;
            }
            else
            {
                normalizedToken = token;
            }
        }
        /* Make sure token is not stop word or number */
        else if (!token.matches("(-|\\+)?\\d*\\.?\\d+") && !stopWords.contains(token)) 
        {
            normalizedToken = stemmer.stem(token);
        }
        return normalizedToken;
    }

    public static void main(String[] args)
    {
        preprocessTokens();
    }
}