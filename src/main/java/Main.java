import entity.ConcertNumber;
import parser.DanceParser;
import parser.DomParser;
import parser.XmlTransformer;
import parser.XmlValidator;
// Якщо XmlValidator знаходиться у пакеті util, додайте: import util.XmlValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String xmlPath = "src/main/resources/dance.xml";
        String xsdPath = "src/main/resources/dance.xsd";
        String xslPath = "src/main/resources/transform.xsl";
        String outputPath = "src/main/resources/grouped.xml";

        logger.info("Початок виконання лабораторної роботи");

        if (!XmlValidator.validate(xmlPath, xsdPath)) {
            logger.error("Зупинка програми через невалідний XML-документ.");
            return;
        }

        DanceParser parser = new DomParser();
        List<ConcertNumber> concerts = parser.parse(xmlPath);

        concerts.sort(Comparator.comparingInt(ConcertNumber::getNumber));

        logger.info("Відсортована колекція виступів");
        concerts.forEach(c -> logger.info(c.toString()));

        logger.info("Запуск зміни структури XML (XSLT)");
        XmlTransformer.transform(xmlPath, xslPath, outputPath);

        logger.info("Програму успішно завершено");
    }
}