package com.journaldev.di.test;

import java.io.*;
import java.util.*;


public class FormFields
{
  public Map<String, String> hashmap = new HashMap<String, String>();


  public String[] getField(String fieldId) {
    String foundedVal = hashmap.getOrDefault(fieldId, "ФФ");
    String[] ary = foundedVal.split("");
    return ary;
  }

}
