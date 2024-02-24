package com.trixi.demo.service.impl;

import com.trixi.demo.constant.Constant;
import com.trixi.demo.model.entity.Geometry;
import com.trixi.demo.model.entity.GeometryMultiPoint;
import com.trixi.demo.model.entity.LinguisticCharacteristic;
import com.trixi.demo.model.entity.Village;
import com.trixi.demo.repository.VillageRepository;
import com.trixi.demo.service.KopidlnoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class KopidlnoServiceImpl implements KopidlnoService {

    private final VillageRepository villageRepository;
    public void save(Village village){
        log.info("input: {}", village);
        villageRepository.save(village);
    }

    public void downloadData() {
        List<LinguisticCharacteristic> linguisticCharacteristics = new ArrayList<>();
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(Constant.baseURL).openStream());
            unzipAndUse(in);
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
    private void unzipAndUse(BufferedInputStream input) throws IOException, ParserConfigurationException, SAXException {
        int BUFFER = 1024;
        ZipInputStream zis = new ZipInputStream(input);
        ZipEntry entry;
        BufferedOutputStream dest;
        while ((entry = zis.getNextEntry()) != null) {
            int count;
            byte[] data = new byte[BUFFER];

            // create dirs to file
            String fileName = entry.getName();
            log.info("fileName: {}", entry.getName());
            File file = new File(fileName);
            if (entry.isDirectory()) { //unzip only files
                file.mkdir();
                continue;
            }
            byte[] zipData = zis.readAllBytes();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(zipData));
            document.getDocumentElement().normalize();
            NodeList nodeList = document.getElementsByTagName("vf:Obec");
            List<Village> villages = new ArrayList<>();
            for(int i=0; i<nodeList.getLength();i++) {
                Node node = nodeList.item(i);
                Village village = parseVillage(node);
                villages.add(village);
                log.info("village:{}", village);
                log.info("node values: {}", node.getNodeValue());
                log.info("node attributes: {}", node.getAttributes().item(0));
                log.info("node nodename: {}", node.getNodeName());
                log.info("node localname: {}", node.getLocalName());
            }
        }
        zis.close();
    }
    private Village parseVillage(Node input){
        Village village = new Village();
        if (input.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) input;

            //get region data
            Node regionNode = element.getElementsByTagName("obi:Okres").item(0);
            String region = getTagValue("oki:Kod",(Element) regionNode);
            log.info("regionData: {}", region);

            //get Pou data
            Node pou = element.getElementsByTagName("obi:Pou").item(0);
            String pui = getTagValue("pui:Kod",(Element) pou);
            log.info("pui: {}", pui);



            //get LinguisticCharacteristics

            List<LinguisticCharacteristic> linguisticChars = getLinguisticCharacteristics(element);
            log.info("linguisticChars: {}", linguisticChars);

            //get Geometry
            Geometry geometry = getGeometry(element);
            log.info("geometry: {}", geometry );
            /*
            GeometryMultiPoint multiPoint = new GeometryMultiPoint();
            Element geometryElement = (Element) element.getElementsByTagName("obi:Geometrie")
                    .item(0);
            Element definitionPointElement = (Element) geometryElement.getElementsByTagName("obi:DefinicniBod")
                    .item(0);
            Element multiPointElement = (Element) definitionPointElement.getElementsByTagName("gml:MultiPoint")
                    .item(0);
            multiPoint.setMultiPointId(multiPointElement.getAttribute("gml:id"));
            multiPoint.setName(multiPointElement.getAttribute("srsName"));
            multiPoint.setDimension(multiPointElement.getAttribute("srsDimension"));
            log.info("multiPoint: {}", multiPoint );

             */

            // set village
            village.setVillageId(element.getAttribute("gml:id"));
            village.setCode(getTagValue("obi:Kod", element));
            village.setName(getTagValue("obi:Nazev", element));
            village.setStatus(Integer.parseInt(getTagValue("obi:StatusKod", element)));
            village.setRegion(region);
            village.setPui(pui);
            village.setValidFrom(getTagValue("obi:PlatiOd", element));
            village.setTransactionId(getTagValue("obi:IdTransakce", element));
            village.setGlobalChangeProposalId(getTagValue("obi:GlobalniIdNavrhuZmeny", element));
            village.setLinguisticCharacteristics(linguisticChars);
            village.setNutsLau(getTagValue("obi:NutsLau", element));
        }
        return village;
    }

    private Geometry getGeometry(Element element) {
        Geometry geometry = new Geometry();

        //parse multiPoint attributes
        GeometryMultiPoint multiPoint = new GeometryMultiPoint();
        Element geometryElement = (Element) element.getElementsByTagName("obi:Geometrie")
                .item(0);
        Element definitionPointElement = (Element) geometryElement.getElementsByTagName("obi:DefinicniBod")
                .item(0);
        Element multiPointElement = (Element) definitionPointElement.getElementsByTagName("gml:MultiPoint")
                .item(0);
        multiPoint.setMultiPointId(multiPointElement.getAttribute("gml:id"));
        multiPoint.setName(multiPointElement.getAttribute("srsName"));
        multiPoint.setDimension(multiPointElement.getAttribute("srsDimension"));




        geometry.setMultiPoint(multiPoint);
        return geometry;
    }

    private List<LinguisticCharacteristic> getLinguisticCharacteristics(Element element) {
        Node linguisticChar = element.getElementsByTagName("obi:MluvnickeCharakteristiky").item(0);
        List<LinguisticCharacteristic> linguisticChars = new ArrayList<>();
        Node current;
        for (int i = 0; i < linguisticChar.getChildNodes().getLength(); i++) {
            current = linguisticChar.getChildNodes().item(i);
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                LinguisticCharacteristic linguisticCharacteristic = new LinguisticCharacteristic();
                linguisticCharacteristic.setValue(current.getTextContent());
                linguisticChars.add(linguisticCharacteristic);
            }
        }
        return linguisticChars;
    }
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

}
