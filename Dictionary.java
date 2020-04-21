/**
 * Dictionary DTO with key identifying stem and offset 
 * used for index to postings file
 */
public class Dictionary
{
    String key;
    int offset;
    
    public Dictionary(String key, int offset)
    {
        this.key = key;
        this.offset = offset;
    }
}