/**
 * Document Id DTO object containing identifier,
 * title, and line number where document appears
 */
public class DocumentId
{
    String id;
    String title;
    int fileLineNumber;

    public DocumentId(String id, String title, int fileLineNumber)
    {
        this.id = id;
        this.title = title;
        this.fileLineNumber = fileLineNumber;
    }

    public String toString()
    {
        return String.format("%s %s", this.id, this.title);
    }
}