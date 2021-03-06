package org.opengeoportal.harvester.api.util;

import org.opengeoportal.harvester.api.metadata.parser.MetadataType;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.opengeoportal.harvester.api.exception.UnsupportedMetadataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);

    public static Document load(InputStream inputStream) throws Exception {
        return getDocumentBuilder().parse(inputStream);

    }

    public static Document load(String uri) throws Exception {
        return getDocumentBuilder().parse(uri);
    }

    /**
     *
     * @param document
     * @return
     * @throws UnsupportedMetadataType if metadata type is not found or is not
     * supported.
     */
    public static MetadataType getMetadataType(Document document) throws UnsupportedMetadataType {
        MetadataType metadataType = null;
        String metadataText = "";
        try {
            //<metstdn>FGDC Content Standards for Digital Geospatial Metadata
            //<metstdv>FGDC-STD-001-1998
            String metadata = document.getElementsByTagName("metstdn").item(0).getTextContent();
            metadataText = metadata;
            if (metadata.toLowerCase().contains("fgdc")) {
                metadataType = MetadataType.FGDC;
            }
        } catch (Exception e) {/*ignore*/

            //document.getElementsByTagName("metstdn").item(0).getTextContent().toLowerCase();
        }

        try {
            //  <gmd:metadataStandardName>
            //  <gmd:spatialRepresentationInfo>
            //<gmd:metadataStandardName>
            //  <gco:CharacterString>ISO 19115:2003/19139</gco:CharacterString>
            //</gmd:metadataStandardName>
            //existence of these two tags (ignoring namespace) should be good enough
            NodeList standardNodes = document.getElementsByTagNameNS("*", "metadataStandardName");
            if (standardNodes.getLength() > 0) {
                String metadata = standardNodes.item(0).getTextContent();
                metadataText = metadata;
                if (metadata.contains("19139")) {
                    metadataType = MetadataType.ISO_19139;
                }
            }
        } catch (Exception e) {/*ignore*/

        }

        if (metadataType == null) {
            //throw an exception...metadata type is not supported
            throw new UnsupportedMetadataType("Metadata Type [" + metadataText
                    + "] is not supported.");
        }
        return metadataType;
    }

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilder documentBuilder;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        documentBuilderFactory.setValidating(false);  // dtd isn't available; would be nice to attempt to validate
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder;
    }

    /**
     * Transform document into a string.
     *
     * @param document
     * @return document string represantion or {@code null} if document is null
     * or any exception raises while transforming document.
     */
    public static String getFullText(Document document) {
        String fileContents = null;
        if (document == null) {
            return fileContents;
        }
        try {
            Source xmlSource = new DOMSource(document);
            StringWriter stringWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(stringWriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlSource, streamResult);
            fileContents = stringWriter.toString();
        } catch (TransformerConfigurationException e) {
            LOGGER.error("transformer configuration error", e);
        } catch (TransformerException e) {
            LOGGER.error("transformer error", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Problem processing full text: " + e.getMessage());
        }
        return fileContents;
    }
}
