package parser;
import entity.ConcertNumber;
import java.util.List;

public interface DanceParser {
    List<ConcertNumber> parse(String filePath);
}