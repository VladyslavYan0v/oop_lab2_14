package parser;

import entity.ConcertNumber;
import entity.Dancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SaxParser implements DanceParser {
    private static final Logger logger = LoggerFactory.getLogger(SaxParser.class);

    @Override
    public List<ConcertNumber> parse(String filePath) {
        List<ConcertNumber> numbers = new ArrayList<>();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            DanceHandler handler = new DanceHandler(numbers);
            saxParser.parse(new File(filePath), handler);
            logger.info("SAX Parsing завершено. Зчитано об'єктів: {}", numbers.size());
        } catch (Exception e) {
            logger.error("Помилка SAX парсингу", e);
        }
        return numbers;
    }

    private static class DanceHandler extends DefaultHandler {
        private final List<ConcertNumber> numbers;
        private ConcertNumber.Builder concertBuilder;
        private StringBuilder data;

        private String dancerName;
        private int dancerAge;
        private int dancerExp;

        public DanceHandler(List<ConcertNumber> numbers) {
            this.numbers = numbers;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            data = new StringBuilder();
            if (qName.equals("ConcertNumber")) {
                concertBuilder = new ConcertNumber.Builder()
                        .id(attributes.getValue("id"))
                        .type(attributes.getValue("type"))
                        .scene(attributes.getValue("scene"))
                        .group(attributes.getValue("group"))
                        .music(attributes.getValue("music"));
            } else if (qName.equals("Dancer")) {
                dancerName = null;
                dancerAge = 0;
                dancerExp = 0;
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            data.append(new String(ch, start, length));
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            String content = data.toString().trim();
            switch (qName) {
                case "Number" -> concertBuilder.number(Integer.parseInt(content));
                case "Name" -> dancerName = content;
                case "Age" -> dancerAge = Integer.parseInt(content);
                case "Experience" -> dancerExp = Integer.parseInt(content);
                case "Dancer" -> concertBuilder.addDancer(new Dancer(dancerName, dancerAge, dancerExp));
                case "ConcertNumber" -> numbers.add(concertBuilder.build());
            }
        }
    }
}