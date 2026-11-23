package parser;

import entity.ConcertNumber;
import entity.Dancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class StaxParser implements DanceParser {
    private static final Logger logger = LoggerFactory.getLogger(StaxParser.class);

    @Override
    public List<ConcertNumber> parse(String filePath) {
        List<ConcertNumber> numbers = new ArrayList<>();
        ConcertNumber.Builder concertBuilder = null;

        String dancerName = null;
        int dancerAge = 0;
        int dancerExp = 0;
        String currentData = "";

        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

            while (reader.hasNext()) {
                int event = reader.next();

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        String startTag = reader.getLocalName();
                        if (startTag.equals("ConcertNumber")) {
                            concertBuilder = new ConcertNumber.Builder()
                                    .id(reader.getAttributeValue(null, "id"))
                                    .type(reader.getAttributeValue(null, "type"))
                                    .scene(reader.getAttributeValue(null, "scene"))
                                    .group(reader.getAttributeValue(null, "group"))
                                    .music(reader.getAttributeValue(null, "music"));
                        } else if (startTag.equals("Dancer")) {
                            dancerName = null;
                            dancerAge = 0;
                            dancerExp = 0;
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        currentData = reader.getText().trim();
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        String endTag = reader.getLocalName();
                        if (concertBuilder != null) {
                            switch (endTag) {
                                case "Number" -> concertBuilder.number(Integer.parseInt(currentData));
                                case "Name" -> dancerName = currentData;
                                case "Age" -> dancerAge = Integer.parseInt(currentData);
                                case "Experience" -> dancerExp = Integer.parseInt(currentData);
                                case "Dancer" -> concertBuilder.addDancer(new Dancer(dancerName, dancerAge, dancerExp));
                                case "ConcertNumber" -> numbers.add(concertBuilder.build());
                            }
                        }
                        break;
                }
            }
            logger.info("StAX Parsing завершено. Зчитано об'єктів: {}", numbers.size());
        } catch (Exception e) {
            logger.error("Помилка StAX парсингу", e);
        }

        return numbers;
    }
}