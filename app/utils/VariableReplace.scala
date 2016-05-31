package utils

import java.util.HashMap
import org.docx4j.XmlUtils
import org.docx4j.openpackaging.io.SaveToZipFile
import org.docx4j.openpackaging.packages.WordprocessingMLPackage
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart
import org.docx4j.wml.Document
//remove if not needed
import scala.collection.JavaConversions._

object VariableReplace {

  def main() {



/*

    val inputfilepath = "ФЛ.docx"
    val save = false
    val outputfilepath = "ФЛ2.docx"
    val wordMLPackage = WordprocessingMLPackage.load(new java.io.File(inputfilepath))
    val documentPart = wordMLPackage.getMainDocumentPart
    val xmlObj = XmlUtils.marshaltoString(documentPart.getContents, true)
    val mappings = new HashMap[String, String]()
    mappings.put("NAME", "green")
    mappings.put("icecream", "chocolate")
    val obj = XmlUtils.unmarshallFromTemplate(xmlObj, mappings)
    documentPart.setJaxbElement(obj.asInstanceOf[Document])
    if (save) {
      val saver = new SaveToZipFile(wordMLPackage)
      saver.save(outputfilepath)
    } else {
      println(XmlUtils.marshaltoString(documentPart.getJaxbElement, true, true))
    }
*/
  val inputfilepath = "ФЛ.docx";
  val save = true;
  val outputfilepath = "ФЛ2.docx";



  val wordMLPackage = WordprocessingMLPackage
      .load(new java.io.File(inputfilepath));
  val documentPart = wordMLPackage.getMainDocumentPart();

  val mappings:HashMap[String, String]  = new HashMap();
  mappings.put("TEST", "ТЕСТ");
  mappings.put("LASTNAME", "chocolate");

  mappings.put("MIDDLENAME", "ТЕст")


mappings.put("LASTNAME", "ТЕст")
mappings.put("NAME", "ТЕст")
mappings.put("CITY", "ТЕст")
mappings.put("REGION", "ТЕст")
mappings.put("EMAIL", "ТЕст")
mappings.put("INN", "ТЕст")
mappings.put("SNILS", "ТЕст")
mappings.put("PASSPORT", "ТЕст")
mappings.put("ISSUEDBY", "ТЕст")
mappings.put("ISSUEDATE", "ТЕст")


  val start = System.currentTimeMillis();

  // Approach 1 (from 3.0.0; faster if you haven't yet caused unmarshalling to occur):

    documentPart.variableReplace(mappings);

		// Approach 2 (original)

    // unmarshallFromTemplate requires string input
    val xmlObj = XmlUtils.marshaltoString(documentPart.getJaxbElement(), true);
    // Do it...
    val obj:org.docx4j.wml.Document = XmlUtils.unmarshallFromTemplate(xmlObj, mappings).asInstanceOf[org.docx4j.wml.Document];
    // Inject result into docx
    documentPart.setJaxbElement(obj);


  val end = System.currentTimeMillis();
  val total = end - start;
//  System.out.println("Time: " + total);

  // Save it
  if (save) {
    val saver = new SaveToZipFile(wordMLPackage);
    saver.save(outputfilepath);
  } else {
    //System.out.println(XmlUtils.marshaltoString(documentPart.getJaxbElement(), true,
  }
}

}
