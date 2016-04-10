package com.journaldev.di.test;

import java.io.*;
import java.util.*;


public class FormFields
{
  public Map<String, String> hashmap = new HashMap<String, String>();

  public void put(String id, String val) {
    hashmap.put(id, val);
  }

  public String[] getField(String fieldId) {
    String foundedVal = hashmap.getOrDefault(fieldId, "ФФ");
    String[] ary = foundedVal.split("");
    return ary;
  }

/*
fields.getField("lastName")
fields.getField("firstName")
fields.getField("middleName")
fields.getField("lastName_latin")
fields.getField("firstName_latin")
fields.getField("middleName_latin")
fields.getField("inn")
fields.getField("gender")
fields.getField("dob")
fields.getField("pob")
fields.getField("pob2")
fields.getField("grajdanstvo")
fields.getField("zip")
fields.getField("subject")
fields.getField("area")
fields.getField("area_title")
fields.getField("area_title_big")
fields.getField("city")
fields.getField("city_title")
fields.getField("nasel")
fields.getField("nasel_title")
fields.getField("nasel_title_big")
fields.getField("street")
fields.getField("street_title")
fields.getField("street_title_big")
fields.getField("house")
fields.getField("house_num")
fields.getField("corpus")
fields.getField("corpus_num")
fields.getField("appartment")
fields.getField("appartment_num")
fields.getField("vid_document")
fields.getField("series")
fields.getField("issue_date")
fields.getField("issuer")
fields.getField("issuer2")
fields.getField("issuer3")
fields.getField("issuer_code1")
fields.getField("issuer_code2")



fields.getField("vid_doc")
fields.getField("page3_doc_number")
fields.getField("page3_doc_number
fields.getField("page3_doc_number
fields.getField("page3_doc_number
fields.getField("page3_doc_number
fields.getField("page3_doc_number


fields.getField("activity_type")


fields.getField("fio")
fields.getField("vidat")
fields.getField("phone")
fields.getField("email")


usn_page("inn")
usn_page("kpp")
usn_page("nalogovaya")
usn_page("priznak")
usn_page("fio1")
usn_page("fio2")
usn_page("fio3")
usn_page("fio4")
usn_page("perehodit1")
usn_page("perehodit2")
usn_page("tax_type")
usn_page("fio1")
usn_page("fio2")
usn_page("fio3")
usn_page("usn_date")

*/


}
