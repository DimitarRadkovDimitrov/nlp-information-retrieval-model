/**
 * Class used by JFLEX generated Lexer class to store tokens by
 * the regex matched
 */
class Token
{
    public final static int LABEL = 0;
    public final static int WORD = 1;
    public final static int NUMBER = 2;
    public final static int APOSTROPHIZED = 3;
    public final static int HYPHENATED = 4;
    public final static int NEWLINE = 5;
    public final static int PUNCTUATION = 6;

    public int m_type;
    public String m_value;
    public int m_line;
    public int m_column;

    Token (int type, String value, int line, int column)
    {
        m_type = type;
        m_value = value;
        m_line = line;
        m_column = column;
    }

    /**
     * Returns type of token in string form
     */
    public String toString()
    {
        switch (m_type)
        {
            case LABEL:
                return "LABEL";
            case WORD:
                return "WORD";
            case NUMBER:
                return "NUMBER";
            case APOSTROPHIZED:
                return "APOSTROPHIZED";
            case HYPHENATED:
                return "HYPHENATED";
            case NEWLINE:
                return "NEWLINE";
            case PUNCTUATION:
                return "PUNCTUATION";
            default:
                return "UNKNOWN(" + m_value + ")";
        }
    }
}
