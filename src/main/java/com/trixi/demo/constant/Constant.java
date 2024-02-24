package com.trixi.demo.constant;

public class Constant {

    public static final String baseURL = "https://www.smartform.cz/download/kopidlno.xml.zip";

    //Tags
    public static final String villagesTag = "vf:Obce";
    public static final String villageRootTag = "vf:Obec";
    public static final String regionTag = "obi:Okres";
    public static final String regionCodeTag = "oki:Kod";
    public static final String pouTag = "obi:Pou";
    public static final String puiTag = "pui:Kod";
    public static final String villageCodeTag = "oki:Kod";
    public static final String villageNameTag = "obi:Nazev";
    public static final String villageStatusTag = "obi:StatusKod";
    public static final String validFromTag = "obi:PlatiOd";
    public static final String transactionIdTag = "obi:IdTransakce";
    public static final String globalChangeProposalIdTag = "obi:GlobalniIdNavrhuZmeny";
    public static final String nutsLauTag = "obi:NutsLau";
    public static final String geometryTag = "obi:Geometrie";
    public static final String definitionPointTag = "obi:DefinicniBod";
    public static final String multiPointTag = "gml:MultiPoint";
    public static final String pointTag = "gml:Point";
    public static final String pointMembersTag = "gml:pointMembers";
    public static final String posTag = "gml:pos";
    public static final String linguisticCharacteristicTag = "obi:MluvnickeCharakteristiky";

    //District tags
    public static final String districtRootTag = "vf:CastObce";
    public static final String districtsTag = "vf:CastiObci";
    public static final String districtVillageTag = "coi:Obec";
    public static final String districtVillageCodeTag = "obi:Kod";
    public static final String districtCodeTag = "coi:Kod";
    public static final String districtNameTag = "coi:Nazev";
    public static final String districtValidFromTag = "coi:PlatiOd";
    public static final String districtTransactionIdTag = "coi:IdTransakce";
    public static final String districtGlobalChangeProposalIdTag = "coi:GlobalniIdNavrhuZmeny";
    public static final String districtLinguisticCharacteristicTag = "coi:MluvnickeCharakteristiky";
    public static final String districtGeometryTag = "coi:Geometrie";
    public static final String districtDefinitionTag = "coi:DefinicniBod";

    //Attributes
    public static final String idAtt = "gml:id";
    public static final String nameAtt = "srsName";
    public static final String dimensionAtt = "srsDimension";
}
