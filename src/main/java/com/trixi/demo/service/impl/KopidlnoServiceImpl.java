package com.trixi.demo.service.impl;

import com.trixi.demo.constant.Constant;
import com.trixi.demo.model.entity.LinguisticCharacteristics;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
        List<LinguisticCharacteristics> linguisticCharacteristics = new ArrayList<>();
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
            NodeList regionList = ((Element) input).getElementsByTagName("obi:Okres");
            String region = getTagValue("oki:Kod",(Element) regionList.item(0));
            log.info("regionData: {}", region);

            // get Pou data
            NodeList pouList = ((Element) input).getElementsByTagName("obi:Pou");
            String pui = getTagValue("pui:Kod",(Element) pouList.item(0));
            log.info("pui: {}", pui);

            // get LinguisticCharacteristics

            NodeList linguisticCharList = ((Element) input).getElementsByTagName("obi:MluvnickeCharakteristiky");
            Node linguisticChar = linguisticCharList.item(0);
            log.info("linguisticChar length: {}",linguisticChar.getChildNodes().getLength());
            List<String> linguisticChars = new ArrayList<>();
            for(int i=0; i < linguisticChar.getChildNodes().getLength();i++) {
                Node itemNode = linguisticChar.getChildNodes().item(i);
                if(itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element item = (Element) itemNode.getChildNodes().item(0);
                    log.info("item name: {}", item.getNodeName());
                    String linguistic = getTagValue("oki:Pad" + (i + 1), item);
                    log.info("linguistic: {}", linguistic);
                    linguisticChars.add(linguistic);
                }
            }
            log.info("linguisticChars: {}", linguisticChars);

            // set village
            village.setCode(getTagValue("obi:Kod", element));
            village.setName(getTagValue("obi:Nazev", element));
            village.setStatus(Integer.parseInt(getTagValue("obi:StatusKod", element)));
            village.setRegion(region);
            village.setPui(pui);
            village.setValidFrom(getTagValue("obi:PlatiOd", element));
            village.setTransactionId(getTagValue("obi:IdTransakce", element));
            village.setGlobalChangeProposalId(getTagValue("obi:GlobalniIdNavrhuZmeny", element));
            //village.setGlobalChangeProposalId(getTagValue("obi:GlobalniIdNavrhuZmeny", element));
        }
        return village;
    }
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }

}