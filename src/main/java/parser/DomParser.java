package parser;

import entity.ConcertNumber;
import entity.Dancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DomParser implements DanceParser {
    private static final Logger logger = LoggerFactory.getLogger(DomParser.class);

    @Override
    public List<ConcertNumber> parse(String filePath) {
        List<ConcertNumber> numbers = new ArrayList<>();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(filePath));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("ConcertNumber");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element el = (Element) node;
                    ConcertNumber.Builder concertBuilder = new ConcertNumber.Builder()
                            .id(el.getAttribute("id"))
                            .type(el.getAttribute("type"))
                            .scene(el.getAttribute("scene"))
                            .group(el.getAttribute("group"))
                            .music(el.getAttribute("music"))
                            .number(Integer.parseInt(el.getElementsByTagName("Number").item(0).getTextContent()));

                    NodeList dancerNodes = el.getElementsByTagName("Dancer");
                    for (int j = 0; j < dancerNodes.getLength(); j++) {
                        Element dEl = (Element) dancerNodes.item(j);
                        String name = dEl.getElementsByTagName("Name").item(0).getTextContent();
                        int age = Integer.parseInt(dEl.getElementsByTagName("Age").item(0).getTextContent());
                        int exp = Integer.parseInt(dEl.getElementsByTagName("Experience").item(0).getTextContent());
                        concertBuilder.addDancer(new Dancer(name, age, exp));
                    }
                    numbers.add(concertBuilder.build());
                }
            }
            logger.info("DOM Parsing завершено. Зчитано об'єктів: {}", numbers.size());
        } catch (Exception e) {
            logger.error("Помилка DOM парсингу", e);
        }
        return numbers;
    }
}