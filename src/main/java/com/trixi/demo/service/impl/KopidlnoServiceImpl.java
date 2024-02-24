package com.trixi.demo.service.impl;

import com.trixi.demo.constant.Constant;
import com.trixi.demo.model.entity.*;
import com.trixi.demo.repository.*;
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
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@Slf4j
@RequiredArgsConstructor
public class KopidlnoServiceImpl implements KopidlnoService {

    private final VillageRepository villageRepository;
    private final LinguisticCharacteristicRepository linguisticCharacteristicRepository;
    private final GeometryRepository geometryRepository;
    private final GeometryMultiPointRepository geometryMultiPointRepository;
    private final GeometryPointRepository geometryPointRepository;

    public void downloadData() {
        List<LinguisticCharacteristic> linguisticCharacteristics = new ArrayList<>();
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(Constant.baseURL).openStream());
            Document document = getDocument(in);
            NodeList villagesList = document.getElementsByTagName(Constant.villageRootTag);
            for(int i=0; i<villagesList.getLength();i++) {
                Node node = villagesList.item(i);
                Village village = parseVillage(node);
                log.info("village: {}", village);
                villageRepository.save(village);
                for (LinguisticCharacteristic linguisticChar: village.getLinguisticCharacteristics()
                     ) {
                    linguisticChar.setVillage(village);
                }
                linguisticCharacteristicRepository.saveAll(village.getLinguisticCharacteristics());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
    private void saveVillageToDB(Village village) {

    }
    private Document getDocument(BufferedInputStream input) throws IOException, ParserConfigurationException, SAXException {
        int BUFFER = 1024;
        ZipInputStream zis = new ZipInputStream(input);
        ZipEntry entry;
        BufferedOutputStream dest;
        Document document = null;
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
            //
            document = builder.parse(new ByteArrayInputStream(zipData));
            document.getDocumentElement().normalize();
        }
        zis.close();
        return document;
    }
    private Village parseVillage(Node input){
        Village village = new Village();
        if (input.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) input;

            //get region data
            Node regionNode = element.getElementsByTagName(Constant.regionTag).item(0);
            String region = getTagValue(Constant.regionCodeTag,(Element) regionNode);
            log.info("regionData: {}", region);

            //get Pou data
            Node pou = element.getElementsByTagName(Constant.pouTag).item(0);
            String pui = getTagValue(Constant.puiTag,(Element) pou);
            log.info("pui: {}", pui);

            //get LinguisticCharacteristics
            List<LinguisticCharacteristic> linguisticChars = getLinguisticCharacteristics(element);
            log.info("linguisticChars: {}", linguisticChars);

            //get Geometry
            Geometry geometry = getGeometry(element);
            geometryRepository.save(geometry);
            log.info("geometry: {}", geometry );

            // set village
            village.setVillageId(element.getAttribute(Constant.idAtt));
            village.setCode(getTagValue(Constant.villageCodeTag, element));
            village.setName(getTagValue(Constant.villageNameTag, element));
            village.setStatus(Integer.parseInt(getTagValue(Constant.villageStatusTag, element)));
            village.setRegion(region);
            village.setPui(pui);
            village.setValidFrom(getTagValue(Constant.validFromTag, element));
            village.setTransactionId(getTagValue(Constant.transactionIdTag, element));
            village.setGlobalChangeProposalId(getTagValue(Constant.globalChangeProposalIdTag, element));
            village.setLinguisticCharacteristics(linguisticChars);
            village.setNutsLau(getTagValue(Constant.nutsLauTag, element));
            village.setGeometry(geometry);
        }
        return village;
    }

    private Geometry getGeometry(Element element) {
        Geometry geometry = new Geometry();

        //parse multiPoint attributes
        GeometryMultiPoint multiPoint = new GeometryMultiPoint();
        Element geometryElement = (Element) element.getElementsByTagName(Constant.geometryTag)
                .item(0);
        Element definitionPointElement = (Element) geometryElement.getElementsByTagName(Constant.definitionPointTag)
                .item(0);
        log.info("geometryTagName: {}",element.getTagName());
        String nextPointLevelName = Constant.multiPointTag;
        if(Objects.equals(element.getTagName(), Constant.districtRootTag)) {
            nextPointLevelName = Constant.pointTag;
        }
        Element multiPointElement = (Element) definitionPointElement
                .getElementsByTagName(nextPointLevelName).item(0);
        multiPoint.setMultiPointId(multiPointElement.getAttribute(Constant.idAtt));
        multiPoint.setName(multiPointElement.getAttribute(Constant.nameAtt));
        multiPoint.setDimension(multiPointElement.getAttribute(Constant.dimensionAtt));

        List<GeometryPoint> geometryPoints;
        if(Objects.equals(element.getTagName(), Constant.villageRootTag)) {
            Element pointMembersElement = (Element) geometryElement.getElementsByTagName(Constant.pointMembersTag)
                    .item(0);
            geometryPoints = getGeometryPoints(pointMembersElement);
        } else {
            geometryPoints = getGeometryPoints(multiPointElement);
        }
        //geometryPointRepository.saveAll(geometryPoints);
        multiPoint.setPoints(geometryPoints);
        geometryMultiPointRepository.save(multiPoint);
        geometryPointRepository.saveAll(geometryPoints);
        geometry.setMultiPoint(multiPoint);
        return geometry;
    }
    private List<GeometryPoint> getGeometryPoints(Element element) {
        List<GeometryPoint> geometryPoints = new ArrayList<>();
        Node current;
        for (int i = 0; i < element.getChildNodes().getLength(); i++) {
            current = element.getChildNodes().item(i);
            if (current.getNodeType() == Node.ELEMENT_NODE) {
                Element point = (Element) element.getElementsByTagName(Constant.pointTag).item(0);
                GeometryPoint geometryPoint = new GeometryPoint();
                geometryPoint.setPointId(point.getAttribute(Constant.idAtt));
                geometryPoint.setName(point.getAttribute(Constant.nameAtt));
                geometryPoint.setDimension(point.getAttribute(Constant.dimensionAtt));
                geometryPoint.setPos(getTagValue(Constant.posTag, point));
                geometryPoints.add(geometryPoint);
            }
        }
        return geometryPoints;
    }

    private List<LinguisticCharacteristic> getLinguisticCharacteristics(Element element) {
        Node linguisticChar = element.getElementsByTagName(Constant.linguisticCharacteristicTag).item(0);
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
