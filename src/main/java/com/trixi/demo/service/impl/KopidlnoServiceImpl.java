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
    private final DistrictRepository districtRepository;

    /***
     * Main method for upload data from xml into database
     */
    public void saveDataFromXml() {
        try {
            BufferedInputStream in = new BufferedInputStream(new URL(Constant.baseURL).openStream());
            Document document = getDocument(in);
            NodeList villagesList = document.getElementsByTagName(Constant.villageRootTag);
            for(int i=0; i<villagesList.getLength();i++) {
                Node node = villagesList.item(i);
                Village village = parseVillage(node);
                log.info("village: {}", village);
                saveVillageToDB(village);
            }
            NodeList districtsList = document.getElementsByTagName(Constant.districtRootTag);
            for(int i=0; i<districtsList.getLength();i++) {
                Node node = districtsList.item(i);
                District district = parseDistrict(node);
                log.info("district: {}", district);
                saveDistrictToDB(district);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * Method for save District into database
     * @param district
     */
    private void saveDistrictToDB(District district) {
        GeometryMultiPoint multiPoint = district.getGeometry().getMultiPoint();
        geometryMultiPointRepository.save(multiPoint);
        geometryRepository.save(district.getGeometry());
        for (GeometryPoint point: multiPoint.getPoints()
        ) {
            point.setMultiPoint(multiPoint);
        }
        geometryPointRepository.saveAll(district.getGeometry().getMultiPoint().getPoints());
        districtRepository.save(district);
        for (LinguisticCharacteristic linguisticChar: district.getLinguisticCharacteristics()
        ) {
            linguisticChar.setDistrict(district);
        }
        linguisticCharacteristicRepository.saveAll(district.getLinguisticCharacteristics());
    }

    /***
     * Method for saving village into database
     * @param village
     */
    private void saveVillageToDB(Village village) {
        GeometryMultiPoint multiPoint = village.getGeometry().getMultiPoint();
        geometryMultiPointRepository.save(multiPoint);
        geometryRepository.save(village.getGeometry());
        for (GeometryPoint point: multiPoint.getPoints()
        ) {
            point.setMultiPoint(multiPoint);
        }
        geometryPointRepository.saveAll(village.getGeometry().getMultiPoint().getPoints());
        villageRepository.save(village);
        for (LinguisticCharacteristic linguisticChar: village.getLinguisticCharacteristics()
        ) {
            linguisticChar.setVillage(village);
        }
        linguisticCharacteristicRepository.saveAll(village.getLinguisticCharacteristics());
    }
    private Document getDocument(BufferedInputStream input) throws IOException, ParserConfigurationException, SAXException {
        ZipInputStream zis = new ZipInputStream(input);
        Document document = null;
        while (zis.getNextEntry() != null) {
            byte[] zipData = zis.readAllBytes();
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            //
            document = builder.parse(new ByteArrayInputStream(zipData));
            document.getDocumentElement().normalize();
        }
        zis.close();
        return document;
    }

    /***
     * Method for parse District object from Document.
     * @param input
     * @return District
     */
    private District parseDistrict(Node input) {
        District district = new District();
        if (input.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) input;

            Node villageNode = element.getElementsByTagName(Constant.districtVillageTag).item(0);
            String villageCode = getTagValue(Constant.districtVillageCodeTag,(Element) villageNode);

            List<LinguisticCharacteristic> linguisticChars = getLinguisticCharacteristics(element);

            Geometry geometry = getGeometry(element);

            district.setDistrictId(element.getAttribute(Constant.idAtt));
            district.setCode(getTagValue(Constant.districtCodeTag, element));
            district.setName(getTagValue(Constant.districtNameTag, element));
            district.setVillageCode(villageCode);
            district.setValidFrom(getTagValue(Constant.districtValidFromTag, element));
            district.setTransactionId(getTagValue(Constant.districtTransactionIdTag, element));
            district.setGlobalChangeProposalId(getTagValue(Constant.districtGlobalChangeProposalIdTag, element));
            district.setLinguisticCharacteristics(linguisticChars);
            district.setGeometry(geometry);
        }
        return district;
    }

    /***
     * Method for parse Village object from Document.
     * @param input
     * @return Village
     */
    private Village parseVillage(Node input){
        Village village = new Village();
        if (input.getNodeType() == Node.ELEMENT_NODE) {
            Element element = (Element) input;

            Node regionNode = element.getElementsByTagName(Constant.regionTag).item(0);
            String region = getTagValue(Constant.regionCodeTag,(Element) regionNode);

            Node pou = element.getElementsByTagName(Constant.pouTag).item(0);
            String pui = getTagValue(Constant.puiTag,(Element) pou);

            List<LinguisticCharacteristic> linguisticChars = getLinguisticCharacteristics(element);

            Geometry geometry = getGeometry(element);

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

    /***
     * Parse Geometry object from village / district
     * @param element
     * @return Geometry
     */
    private Geometry getGeometry(Element element) {
        Geometry geometry = new Geometry();

        String firstLevelTagName = Constant.geometryTag;
        String secondLevelTagName = Constant.multiPointTag;
        String definitionTagName = Constant.definitionPointTag;
        if(Objects.equals(element.getTagName(), Constant.districtRootTag)) {
            firstLevelTagName = Constant.districtGeometryTag;
            definitionTagName = Constant.districtDefinitionTag;
        }

        GeometryMultiPoint multiPoint = new GeometryMultiPoint();
        Element geometryElement = (Element) element.getElementsByTagName(firstLevelTagName)
                .item(0);
        Element definitionPointElement = (Element) geometryElement.getElementsByTagName(definitionTagName)
                .item(0);

        List<GeometryPoint> geometryPoints;
        if(Objects.equals(element.getTagName(), Constant.villageRootTag)) {
            Element multiPointElement = (Element) definitionPointElement
                    .getElementsByTagName(secondLevelTagName).item(0);
            multiPoint.setMultiPointId(multiPointElement.getAttribute(Constant.idAtt));
            multiPoint.setName(multiPointElement.getAttribute(Constant.nameAtt));
            multiPoint.setDimension(multiPointElement.getAttribute(Constant.dimensionAtt));
            Element pointMembersElement = (Element) geometryElement.getElementsByTagName(Constant.pointMembersTag)
                    .item(0);
            geometryPoints = getGeometryPoints(pointMembersElement);
        } else {
            geometryPoints = getGeometryPoints(definitionPointElement);
        }
        multiPoint.setPoints(geometryPoints);
        geometry.setMultiPoint(multiPoint);
        return geometry;
    }

    /***
     * Parse all geometryPoints
     * @param element
     * @return List<GeometryPoint>
     */
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

    /***
     * Parse all LinguisticCharacteristics
     * @param element
     * @return List<LinguisticCharacteristic>
     */
    private List<LinguisticCharacteristic> getLinguisticCharacteristics(Element element) {
        String tagName = Constant.linguisticCharacteristicTag;
        if(Objects.equals(element.getTagName(), Constant.districtRootTag)) {
            tagName = Constant.districtLinguisticCharacteristicTag;
        }
        Node linguisticChar = element.getElementsByTagName(tagName).item(0);
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

    /***
     * Get value of tag from element
     * @param tag
     * @param element
     * @return String
     */
    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

}
