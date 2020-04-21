import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Program uses previously generated lexer to print each token from $DOC-
 * $TITLE-$TEXT formatted separated on preserving old line endings
 */
public class Scanner
{
    private Lexer scanner = null;

    public Scanner(Lexer lexer)
    {
        scanner = lexer;
    }

    /**
     * Returns next token object from Lexer class
     */
    public Token getNextToken() throws java.io.IOException
    {
        return scanner.yylex();
    }

    /**
     * Performs special cases handling for hyphenated tokens
     */
    public static ArrayList<String> validateHyphenatedToken(String token)
    {
        String[] tokens = token.split("-");
        int numberOfTokens = tokens.length;

        if (numberOfTokens > 3)
        {
            return getStringsWithDelimeterPadding(tokens, "-");
        }
        else if (numberOfTokens == 3)
        {
            if (tokens[1].length() > 2)
            {
                return getStringsWithDelimeterPadding(tokens, "-");
            }
        }
        
        ArrayList<String> resultToken = new ArrayList<>();
        resultToken.add(token);
        return resultToken;
    }

    /**
     * Performs special cases handling for apostrophized tokens
     */
    public static ArrayList<String> validateApostrophizedToken(String token)
    {
        String[] tokens = token.split("'");
        int numberOfTokens = tokens.length;
        ArrayList<String> resultTokens = new ArrayList<>();

        if (numberOfTokens > 3)
        {
            return getStringsWithDelimeterPadding(tokens, "'");
        }
        else if (numberOfTokens == 3)
        {
            if (tokens[0].length() == 1 && tokens[2].toLowerCase().equals("s"))
            {
                resultTokens.add(token);
                return resultTokens;
            }
            else
            {
                return getStringsWithDelimeterPadding(tokens, "'");
            }
        }
        else if (numberOfTokens == 2)
        {
            int secondTokenSize = tokens[1].length();
            if (tokens[0].length() == 1)
            {
                if (secondTokenSize > 2)
                {
                    resultTokens.add(token);
                    return resultTokens;
                }
                else if (secondTokenSize == 2)
                {
                    resultTokens.add(tokens[0]);
                    resultTokens.add(String.format("'%s", tokens[1]));
                    return resultTokens;
                }
            }
            else
            {
                if (tokens[1].toLowerCase().equals("s"))
                {
                    ArrayList<String> hyphenated = validateHyphenatedToken(tokens[0]);
                    if (hyphenated.size() > 1)
                    {
                        for (String hyphenatedToken: hyphenated)
                        {
                            resultTokens.add(hyphenatedToken);
                        }
                        resultTokens.add("'");
                        resultTokens.add(tokens[1]);
                        return resultTokens;
                    }
                    else
                    {
                        resultTokens.add(token);
                        return resultTokens;
                    }
                }
                else if (secondTokenSize == 1 || secondTokenSize == 2)
                {
                    resultTokens.add(tokens[0]);
                    resultTokens.add(String.format("'%s", tokens[1]));
                    return resultTokens;
                }
            }
            return getStringsWithDelimeterPadding(tokens, "'");
        }
        resultTokens.add(token);
        return resultTokens;
    }

    /**
     * Given an array of tokens returns a list of tokens with the specified delimeter
     * copied in between
     * @param tokens
     * @param delimeter
     * @return
     */
    public static ArrayList<String> getStringsWithDelimeterPadding(String[] tokens, String delimeter)
    {
        ArrayList<String> resultTokens = new ArrayList<>();
        for (String token: tokens)
        {
            resultTokens.add(token);
            resultTokens.add(delimeter);
        }
        resultTokens.remove(resultTokens.size() - 1);
        return resultTokens;
    }

    /**
     * Takes a $DOC-$TITLE-$TEXT formatted document from STDIN and prints space-
     * delimeted tokens
     * @param argv
     */
    public static void main(String argv[])
    {
        try
        {
            Scanner scanner = new Scanner(new Lexer(new InputStreamReader(System.in)));
            Token tok = null;

            while((tok=scanner.getNextToken()) != null)
            {
                if (tok.m_type == 3)
                {
                    ArrayList<String> validApostrophizedTokens = validateApostrophizedToken(tok.m_value);
                    for (String token: validApostrophizedTokens)
                    {
                        System.out.print(token + " ");
                    }
                }
                else if (tok.m_type == 4)
                {
                    ArrayList<String> validHyphenatedTokens = validateHyphenatedToken(tok.m_value);
                    for (String token: validHyphenatedTokens)
                    {
                        System.out.print(token + " ");
                    }
                }
                else if (tok.m_type == 5)
                {
                    System.out.println();
                }
                else
                {
                    System.out.print(tok.m_value + " ");
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
        }
    }
}
