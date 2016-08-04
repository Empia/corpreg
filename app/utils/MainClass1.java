package com.journaldev.di.test;

import java.io.FileOutputStream;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.codec.TiffImage;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.geotoolkit.image.io.plugin.RawTiffImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageInputStreamImpl;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.ImageReader;
import javax.imageio.ImageReadParam;
import java.util.Iterator;
import java.io.InputStream;
import java.io.FileInputStream;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.lang.Integer;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.io.OutputStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.TIFFEncodeParam;
import java.util.Vector;
import java.util.Random;

import java.io.IOException;
import java.util.*;

public class MainClass1 extends JPanel {
    private BufferedImage image;
    private BufferedImage imageUsn;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }
@Override
protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
}

private static void create(String[] args, FormFields fields)  {
        //JFrame f = new JFrame();
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.add(new MainClass1(args));
        //f.pack();
try {
        new MainClass1(args, fields);
        //f.setVisible(true);
} catch(Exception e){

}

}

public MainClass1(String[] args, FormFields fields) throws Exception {
        try {

  String canonicalPath = new java.io.File( "." ).getCanonicalPath();


	File file = new File(canonicalPath + "/ip.tif");
	InputStream is = new FileInputStream(canonicalPath + "/ip.tif");

  File fileUsn = new File(canonicalPath + "/usnip.tif");
	InputStream isUsn = new FileInputStream(canonicalPath + "/usnip.tif");

	ImageInputStream input = ImageIO.createImageInputStream(is);
  ImageInputStream inputUsn = ImageIO.createImageInputStream(isUsn);

//try {
    // Get the reader
    Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
    if (!readers.hasNext()) {
        throw new IllegalArgumentException("No reader for: " + file);
    }
    ImageReader reader = readers.next();

    Iterator<ImageReader> readersUsn = ImageIO.getImageReaders(inputUsn);
    if (!readersUsn.hasNext()) {
        throw new IllegalArgumentException("No reader for: " + file);
    }
    ImageReader readerUsn = readersUsn.next();
//    try {
        reader.setInput(input);
        readerUsn.setInput(inputUsn);

        System.out.println("reader.getNumImages()");
        System.out.println(reader.getNumImages(true));
        ImageReadParam param = reader.getDefaultReadParam();
        image = reader.read(0, param);

        ImageReadParam paramUsn = readerUsn.getDefaultReadParam();
        imageUsn = readerUsn.read(0, paramUsn);


        BufferedImage images[] = new BufferedImage[reader.getNumImages(true)];
        for (int i = 1; i < reader.getNumImages(true); i++) {
            images[i] = reader.read(i, param);
        }
        //        process(image);
        int numThumbs = reader.getNumThumbnails(0);

        BufferedImage imagesUsn[] = new BufferedImage[readerUsn.getNumImages(true)];
        for (int i = 1; i < readerUsn.getNumImages(true); i++) {
            imagesUsn[i] = readerUsn.read(i, param);
        }
        //        process(image);
        int numThumbsUsn = readerUsn.getNumThumbnails(0);

        System.out.println("images length"+ images.length);

        image = process(image, images, args, fields, "firstPage");
        BufferedImage image1 = process(images[1], images, args, fields, "secondPage");
        System.out.println("init secondPage");

        BufferedImage image2 = process(images[2], images, args, fields, "thirdPage");
        System.out.println("init thirdPage");

        BufferedImage image3 = process(images[3], images, args, fields, "FourthPage");
        System.out.println("init FourthPage");

        BufferedImage image4 = process(images[4], images, args, fields, "fifthPage");
        System.out.println("init fifthPage");


        imageUsn = process(imageUsn, imagesUsn, args, fields, "firstPageUsn");


        } catch (IOException e) {
            e.printStackTrace();
        }
}



private BufferedImage process(BufferedImage old, BufferedImage[] images, String[] args, FormFields fields, String fileNameTitle) throws Exception {
        System.out.println("init process" + fileNameTitle);

        int w = old.getWidth();
        int h = old.getHeight();
        BufferedImage img = new BufferedImage(
          w, h, BufferedImage.TYPE_INT_ARGB);

        try {

        if (fileNameTitle == "firstPage") {
          Graphics2D g2d = createPage1(img,old,args, fields);
        } else if (fileNameTitle == "secondPage") {
          Graphics2D g2d = createPage2(img,old,args, fields);
        } else if (fileNameTitle == "thirdPage") {
          Graphics2D g2d = createPage3(img,old,args, fields);
        } else if (fileNameTitle == "FourthPage") {
          System.out.println("create page " + fileNameTitle);

          Graphics2D g2d = createPage4(img,old,args, fields);
        } else if (fileNameTitle == "firstPageUsn") {
          Graphics2D g2d = createPage1Usn(img,old,args, fields);
        } else {
          Graphics2D g2d = createPage5(img,old,args, fields);
        }

        String path = new java.io.File( "." ).getCanonicalPath();
        String fileName = args[9];


        //try {

          //}
          System.out.println("save" + fileNameTitle);
          ImageIO.write(img, "png", new File(path + "/public/"+ fileNameTitle + ".png"));
        //}
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
          return img;
        }
}

public int paddingForTwoRow(int step) {
  int actualStep = step + 1;
  if (actualStep == 5) {
    return 5;
  } else if (actualStep == 8) {
    return 5;
  } else if (actualStep == 11) {
    return 5;
  } else if (actualStep == 14 || actualStep == 15 || actualStep == 19 || actualStep == 22 || actualStep == 25 || actualStep == 28 || actualStep == 31 || actualStep == 32 || actualStep == 36) {
    return 5;
  } else {
    return 0;
  }
}
public int paddingForFooter(int step) {
  int actualStep = step + 1;
  if (actualStep == 5) {
    return 1;
  } else {
    return 0;
  }
}

public static void main(String[] args, FormFields fields) throws Exception {
  EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                create(args, fields);
            }
        });
}


public static void main2(String[] args, FormFields fields) throws Exception {
  //EventQueue.invokeLater(new Runnable() {
         //   @Override
         //   public void run() {
            	String[] values = {"ТЕСТ", "ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ", "ТЕСТ"};
                create(args, fields);
           //}
        //});
}




private Graphics2D createPage1(BufferedImage img, BufferedImage old, String[] args, FormFields fields) {

  Random randomGenerator = new Random();
  int randomIntA = randomGenerator.nextInt(100000);
  int randomIntB = randomGenerator.nextInt(100000);





      Graphics2D g2d = img.createGraphics();
      g2d.drawImage(old, 0, 0, null);
      g2d.setPaint(Color.black);
      g2d.setFont(new Font("Courier New", Font.PLAIN, 73));
      FontMetrics fm = g2d.getFontMetrics();

    // Cell SIZE 54 / 68
    // 88 + 59 = 147 + 59 = 206

    // First row anchor
    // 824: [88, 146, 206,265,324,383,442,501,560]
    // 927: [88, 146, 206,265,324,383,442,501,560]

  /*  Cell
        *  *
      70*  *128
        ****
        Width: 58
        Padding: 4px 4px
   */
          String g2d3s2s[] = {"А","Б", "Й", "Ц", "У", "К", "Е", "Н", "Г",
          "Ш", "Щ", "З", "Х", "Х", "Ъ", "Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д",
          "Ж", "Э", "Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю", "Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш"};

          /*
            Full field for fill name of corporation
            Limit 40
            6 rows
           */
          String fullNameString[];
          String smallNameString[];
          if ((args.length + 1) > 0) {
  	        fullNameString = args[0].split("");
          } else {
  	        fullNameString = g2d3s2s;
          }
          if ((args.length + 1) > 1) {
  	        smallNameString = args[1].split("");
          } else {
  	        smallNameString = g2d3s2s;
          }
          String zipString[];
          if ((args.length + 1) > 2) {
  	        zipString = args[2].split("");
          } else {
  	        zipString = g2d3s2s;
          }
          String regionString[];
          if ((args.length + 1) > 3) {
  	        regionString = args[3].split("");
          } else {
  	        regionString = g2d3s2s;
          }
          String addressString[];
          if ((args.length + 1) > 4) {
  	        addressString = args[4].split("");
          } else {
  	        addressString = g2d3s2s;
          }




          String fp = "firstPage";



          fillField(g2d, fields.p.getX(fp,"lastName"), fields.p.getY(fp,"lastName"), 40, fields.getField("lastName"));
          fillField(g2d, fields.p.getX(fp,"firstName"), fields.p.getY(fp,"firstName"), 40, fields.getField("firstName"));
          fillField(g2d, fields.p.getX(fp,"middleName"), fields.p.getY(fp,"middleName"), 40, fields.getField("middleName"));

          fillField(g2d, fields.p.getX(fp,"lastName_latin"), fields.p.getY(fp,"lastName_latin"), 40, fields.getField("lastName_latin"));
          fillField(g2d, fields.p.getX(fp,"firstName_latin"), fields.p.getY(fp,"firstName_latin"), 40, fields.getField("firstName_latin"));
          fillField(g2d, fields.p.getX(fp,"middleName_latin"), fields.p.getY(fp,"middleName_latin"), 40, fields.getField("middleName_latin"));

  ////////////////////////////////////////////////////////////////////////////////////////////////


          /*
            Middle field for small name of corporation
  	      Limit 40
            4 rows
           */
          // INN ROW
          fillField(g2d, fields.p.getX(fp,"inn"), fields.p.getY(fp,"inn"), 40, fields.getField("inn"));
          // GENDER
          fillField(g2d, fields.p.getX(fp,"gender"), fields.p.getY(fp,"gender"), 40, fields.getField("gender"));
          // DOB
          fillField(g2d, fields.p.getX(fp,"dob"), fields.p.getY(fp,"dob"), 40, fields.getField("dob"));
          // PLACE OB BIRTH
          String fullPob[] = fields.getField("pob");
          String[] textArray = splitByNumber(String.join("", fullPob), 41);

          fillField(g2d, fields.p.getX(fp,"pob"), fields.p.getY(fp,"pob"), 41, fields.getField("inn") );//textArray[0].split("") );
          // PLACE OB BIRTH #2
          if (textArray.length > 1) {
            fillField(g2d, fields.p.getX(fp,"pob2"), fields.p.getY(fp,"pob2"), 41, textArray[1].split("") );
          }
          // OKG GRAJDANINA

          fillField(g2d, fields.p.getX(fp,"grajdanstvo"), fields.p.getY(fp,"grajdanstvo"), 1, fields.getField("grajdanstvo"));



          g2d.dispose();

          return g2d;
}
















private String[] splitByNumber(String s, int chunkSize){
    int chunkCount = (s.length() / chunkSize) + (s.length() % chunkSize == 0 ? 0 : 1);
    String[] returnVal = new String[chunkCount];
    for(int i=0;i<chunkCount;i++){
        returnVal[i] = s.substring(i*chunkSize, Math.min((i+1)*chunkSize-1, s.length()) );
    }
    return returnVal;
}





private Graphics2D createPage2(BufferedImage img, BufferedImage old, String[] args, FormFields fields) {

  Random randomGenerator = new Random();
  int randomIntA = randomGenerator.nextInt(100000);
  int randomIntB = randomGenerator.nextInt(100000);





      Graphics2D g2d = img.createGraphics();
      g2d.drawImage(old, 0, 0, null);
      g2d.setPaint(Color.black);
      g2d.setFont(new Font("Courier New", Font.PLAIN, 73));


      String g2d3s2s[] = {"А","Б", "Й", "Ц", "У", "К", "Е", "Н", "Г",
            "Ш", "Щ", "З", "Х", "Х", "Ъ", "Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д",
          "Ж", "Э", "Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю", "Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш"};
      /*
        Full field for fill name of corporation
        Limit 40
        6 rows
       */
      String fullNameString[];
      if ((args.length + 1) > 0) {
        fullNameString = args[0].split("");
      } else {
        fullNameString = g2d3s2s;
      }

    String sp = "secondPage";


    fillField(g2d, fields.p.getX(sp, "zip"), fields.p.getY(sp, "zip"), 4, fields.getField("zip")); // ZIP
    fillField(g2d, fields.p.getX(sp, "subject"), fields.p.getY(sp, "subject"), 2, fields.getField("subject")); // SUBJECT

    fillField(g2d, fields.p.getX(sp,"area"), fields.p.getY(sp,"area"), 4, fields.getField("area")); // AREA
    fillField(g2d, fields.p.getX(sp,"area_title"), fields.p.getY(sp,"area_title") , 4, fields.getField("area_title")); // AREA TITLE
    fillField(g2d, fields.p.getX(sp,"area_title_big"), fields.p.getY(sp,"area_title_big"), 4, fields.getField("area_title_big")); // AREA 2 BIG

    fillField(g2d, fields.p.getX(sp,"city"), fields.p.getY(sp,"city"), 4, fields.getField("city")); // CITY
    fillField(g2d, fields.p.getX(sp,"city_title"), fields.p.getY(sp,"city_title"), 4, fields.getField("city_title")); // CITY TITLE

    fillField(g2d, fields.p.getX(sp,"nasel"), fields.p.getY(sp,"nasel"), 4, fields.getField("nasel")); // NASEL PUNKT
    fillField(g2d, fields.p.getX(sp,"nasel_title"), fields.p.getY(sp,"nasel_title"), 4, fields.getField("nasel_title")); // NASEL PUNKT
    fillField(g2d, fields.p.getX(sp,"nasel_title_big"), fields.p.getY(sp,"nasel_title_big"), 4, fields.getField("nasel_title_big")); // NASEL PUNKT BIG


    fillField(g2d, fields.p.getX(sp,"street"), fields.p.getY(sp,"street"), 4, fields.getField("street")); // STREET


    String fullStreet[] = fields.getField("street_title");
    String[] textArray = splitByNumber(String.join("", fullStreet), 28);
    String[] textArray1 = {""};
    if (textArray.length > 1) {
      int n=textArray.length-1;
      textArray1=new String[n];
      System.arraycopy(textArray,1,textArray1,0,n);

    }
    if (textArray.length > 0) {
      fillField(g2d, fields.p.getX(sp,"street_title"), fields.p.getY(sp,"street_title"), 28, textArray[0].split("")
      ); // STREET


      fillField(g2d, fields.p.getX(sp,"street_title_big"), fields.p.getY(sp,"street_title_big"), 41,
      String.join("", textArray1).split("")  ); // STREET
    }


    fillField(g2d, fields.p.getX(sp,"house"), fields.p.getY(sp,"house"), 4, fields.getField("house")); // HOUSE
    fillField(g2d, fields.p.getX(sp,"house_num"), fields.p.getY(sp,"house_num"), 4, fields.getField("house_num")); // HOUSE NUMBER
    fillField(g2d, fields.p.getX(sp,"corpus"), fields.p.getY(sp,"corpus"), 4, fields.getField("corpus")); // CORPUS
    fillField(g2d, fields.p.getX(sp,"corpus_num"), fields.p.getY(sp,"corpus_num"), 4, fields.getField("corpus_num")); // CORPUS NUMBER


    fillField(g2d, fields.p.getX(sp,"appartment"), fields.p.getY(sp,"appartment"), 4, fields.getField("appartment")); // APPARTMENT
    fillField(g2d, fields.p.getX(sp,"appartment_num"), fields.p.getY(sp,"appartment_num"), 4, fields.getField("appartment_num")); // APPARTMENT NUMBER

    fillField(g2d, fields.p.getX(sp,"vid_document"), fields.p.getY(sp,"vid_document"), 2, fields.getField("vid_document")); // VID DOCUMENTS

    fillField(g2d, fields.p.getX(sp,"series"), fields.p.getY(sp,"series"), 4, fields.getField("series")); // SERIES NUMBER
    fillField(g2d, fields.p.getX(sp,"issue_date"), fields.p.getY(sp,"issue_date"), 4, fields.getField("issue_date")); // DATA VIDACHI


    String fullIssuer[] = fields.getField("issuer");
    String[] fullIssuerArray = splitByNumber(String.join("", fullIssuer), 35);
    String[] fullIssuerArray1 = {""};
    String[] fullIssuerArray2 = {""};

    if (fullIssuerArray.length > 1) {
      int n=fullIssuerArray.length-1;
      fullIssuerArray1=new String[n];
      System.arraycopy(fullIssuerArray,1,fullIssuerArray1,0,n);
    }

    if (fullIssuerArray.length > 0) {
      fillField(g2d, fields.p.getX(sp,"issuer"), fields.p.getY(sp,"issuer"), 35, fullIssuerArray[0].split("") ); // KEM VIDAN


      String[] splitted = splitByNumber(String.join("", fullIssuerArray1), 40);
      fillField(g2d, fields.p.getX(sp,"issuer2"), fields.p.getY(sp,"issuer2"), 40, String.join("", fullIssuerArray1).split("")  ); // KEM VIDAN 2

      if (splitted.length > 1) {
        fillField(g2d, fields.p.getX(sp,"issuer3"), fields.p.getY(sp,"issuer3"), 40, splitted[1].split("")  ); // KEM VIDAN 3
      }

    }


    String issuer_code = String.join("", fields.getField("issuer_code1"));

    // fillField(g2d, fields.p.getX(sp,"issuer_code1"), fields.p.getY(sp,"issuer_code1"), 3, issuer_code.split("-")[0].split("")  ); // KOD PODR 1
    // fillField(g2d, fields.p.getX(sp,"issuer_code2"), fields.p.getY(sp,"issuer_code2"), 3, issuer_code.split("-")[1].split("") ); // KOD PODR 2

    g2d.dispose();
    return g2d;
}



private void fillField(Graphics2D g2d, int xpos, int ypos, int letters, String[] fullNameString) {
  FontMetrics fm = g2d.getFontMetrics();
  String[] dot = {"."};
  for(int a = 0; a < fullNameString.length && a < letters; a++) {
    xpos = xpos + 4 + paddingForTwoRow(a); // first padding

    if (fullNameString[a].contains(".")) {
      System.out.println("dott found");
      g2d.drawString(" ", xpos, ypos);
    } else {
      g2d.drawString(fullNameString[a], xpos, ypos);
    }
    xpos = xpos + fm.stringWidth(fullNameString[a]); // second padding
    xpos = xpos + 2; // third padding
    //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
    //System.out.println("xpos" + xpos);
  }
}

private Graphics2D createPage3(BufferedImage img, BufferedImage old, String[] args, FormFields fields) {

  Random randomGenerator = new Random();
  int randomIntA = randomGenerator.nextInt(100000);
  int randomIntB = randomGenerator.nextInt(100000);





      Graphics2D g2d = img.createGraphics();
      g2d.drawImage(old, 0, 0, null);
      g2d.setPaint(Color.black);
      g2d.setFont(new Font("Courier New", Font.PLAIN, 73));


      String g2d3s2s[] = {"А","Б", "Й", "Ц", "У", "К", "Е", "Н", "Г",
            "Ш", "Щ", "З", "Х", "Х", "Ъ", "Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д",
          "Ж", "Э", "Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю", "Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш"};

              /*
                Full field for fill name of corporation
                Limit 40
                6 rows
               */
              String fullNameString[];
              if ((args.length + 1) > 0) {
      	        fullNameString = args[0].split("");
              } else {
      	        fullNameString = g2d3s2s;
              }


              String tp = "thirdPage";


    fillField(g2d, fields.p.getX(tp, "vid_doc"), fields.p.getY(tp,"vid_doc"), 1, fields.getField("vid_doc")); // VID
    fillField(g2d, fields.p.getX(tp, "page3_doc_number"), fields.p.getY(tp,"page3_doc_number"), 2, fields.getField("page3_doc_number")); // NOMER DOCUMENTA

    fillField(g2d, fields.p.getX(tp, "page3_doc_number1"), fields.p.getY(tp,"page3_doc_number1"), 2, fields.getField("page3_doc_number1") ); // NOMER DOCUMENTA
    fillField(g2d, fields.p.getX(tp, "page3_doc_number2"), fields.p.getY(tp,"page3_doc_number2"), 2, fields.getField("page3_doc_number2") ); // NOMER DOCUMENTA
    fillField(g2d, fields.p.getX(tp, "page3_doc_number3"), fields.p.getY(tp,"page3_doc_number3"), 2, fields.getField("page3_doc_number3") ); // NOMER DOCUMENTA
    fillField(g2d, fields.p.getX(tp, "page3_doc_number4"), fields.p.getY(tp,"page3_doc_number4"), 2, fields.getField("page3_doc_number4") ); // NOMER DOCUMENTA
    fillField(g2d, fields.p.getX(tp, "page3_doc_number5"), fields.p.getY(tp,"page3_doc_number5"), 2, fields.getField("page3_doc_number5") ); // NOMER DOCUMENTA


    g2d.dispose();



      return g2d;
}


private Graphics2D createPage4(BufferedImage img, BufferedImage old, String[] args, FormFields fields) {

  Random randomGenerator = new Random();
  int randomIntA = randomGenerator.nextInt(100000);
  int randomIntB = randomGenerator.nextInt(100000);


        String g2d3s2s[] = {"А","Б", "Й", "Ц", "У", "К", "Е", "Н", "Г",
              "Ш", "Щ", "З", "Х", "Х", "Ъ", "Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д",
            "Ж", "Э", "Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю", "Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш"};

  String fullNameString[];
  if ((args.length + 1) > 0) {
    fullNameString = args[0].split("");
  } else {
    fullNameString = g2d3s2s;
  }



      Graphics2D g2d = img.createGraphics();
      g2d.drawImage(old, 0, 0, null);
      g2d.setPaint(Color.black);
      g2d.setFont(new Font("Courier New", Font.PLAIN, 73));
      FontMetrics fm = g2d.getFontMetrics();

      String v = "60.22";

      String tp = "FourthPage";


          fillField(g2d, fields.p.getX(tp, "activity_type"), fields.p.getY(tp,"activity_type"), 6, v.split(""));//fields.getField("activity_type")); // VID DEYATELNOSTY


      g2d.dispose();

      return g2d;
}

private Graphics2D createPage5(BufferedImage img, BufferedImage old, String[] args, FormFields fields) {

  Random randomGenerator = new Random();
  int randomIntA = randomGenerator.nextInt(100000);
  int randomIntB = randomGenerator.nextInt(100000);


        String g2d3s2s[] = {"А","Б", "Й", "Ц", "У", "К", "Е", "Н", "Г",
              "Ш", "Щ", "З", "Х", "Х", "Ъ", "Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д",
            "Ж", "Э", "Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю", "Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш"};

  String fullNameString[];
  if ((args.length + 1) > 0) {
    fullNameString = args[0].split("");
  } else {
    fullNameString = g2d3s2s;
  }



      Graphics2D g2d = img.createGraphics();
      g2d.drawImage(old, 0, 0, null);
      g2d.setPaint(Color.black);
      g2d.setFont(new Font("Courier New", Font.PLAIN, 73));
      FontMetrics fm = g2d.getFontMetrics();

      String fp = "fifthPage";

      fillField(g2d, fields.p.getX(fp,"fio"), fields.p.getY(fp,"fio"), 5, fields.getField("fio")); // FIO
      fillField(g2d, fields.p.getX(fp,"vidat"), fields.p.getY(fp,"vidat"), 1, fields.getField("vidat")); // VIDAT
      fillField(g2d, fields.p.getX(fp,"phone"), fields.p.getY(fp,"phone"), 1, fields.getField("phone")); // PHONE
      fillField(g2d, fields.p.getX(fp,"email"), fields.p.getY(fp,"email"), 1, fields.getField("email")); // EMAIL

      g2d.dispose();
      return g2d;
}


private Graphics2D createPage1Usn(BufferedImage img, BufferedImage old, String[] args, FormFields fields) {

  Random randomGenerator = new Random();
  int randomIntA = randomGenerator.nextInt(100000);
  int randomIntB = randomGenerator.nextInt(100000);


        String g2d3s2s[] = {"А","Б", "Й", "Ц", "У", "К", "Е", "Н", "Г",
              "Ш", "Щ", "З", "Х", "Х", "Ъ", "Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д",
            "Ж", "Э", "Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю", "Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш"};

  String fullNameString[];
  if ((args.length + 1) > 0) {
    fullNameString = args[0].split("");
  } else {
    fullNameString = g2d3s2s;
  }

      Graphics2D g2d = img.createGraphics();
      g2d.drawImage(old, 0, 0, null);
      g2d.setPaint(Color.black);
      g2d.setFont(new Font("Courier New", Font.PLAIN, 73));
      FontMetrics fm = g2d.getFontMetrics();

      String fps = "firstPageUsn";

      fillField(g2d, fields.p.getX(fps, "inn"), fields.p.getY(fps, "inn"), 12, fields.getField("inn")); // INN
      fillField(g2d, fields.p.getX(fps, "kpp"), fields.p.getY(fps, "kpp"), 9, fields.getField("kpp")); // KPP
      fillField(g2d, fields.p.getX(fps, "nalogovaya"), fields.p.getY(fps, "nalogovaya"), 4, fields.getField("nalogovaya")); // NALOGOVAYA
      fillField(g2d, fields.p.getX(fps, "priznak"), fields.p.getY(fps, "priznak"), 1, fields.getField("priznak")); // PRIZNAK

      fillField(g2d, fields.p.getX(fps, "fio1"), fields.p.getY(fps, "fio1"), 40, fields.getField("lastName") ); // FIO 1
      fillField(g2d, fields.p.getX(fps, "fio2"), fields.p.getY(fps, "fio2"), 40, fields.getField("firstName") ); // FIO 2
      fillField(g2d, fields.p.getX(fps, "fio3"), fields.p.getY(fps, "fio3"), 40, fields.getField("middleName") ); // FIO 3
      fillField(g2d, fields.p.getX(fps, "fio4"), fields.p.getY(fps, "fio4"), 40, fields.getField("fio4")); // FIO 3



      fillField(g2d, fields.p.getX(fps,"perehodit1"), fields.p.getY(fps,"perehodit1"), 1, fields.getField("perehodit1")); // PEREHODIT
      fillField(g2d, fields.p.getX(fps,"perehodit2"), fields.p.getY(fps,"perehodit2"), 1, fields.getField("perehodit2")); // PEREHODIT

      //fillField(g2d, fields.p.getX(fps,"tax_type"), fields.p.getY(fps,"tax_type"), 1, fields.getField("tax_type")); // TYPE NALOGOPLATILSHIKA

      // bottom line
      fillField(g2d, fields.p.getX(fps,"bottom_fio1"), fields.p.getY(fps,"bottom_fio1"), 40, fields.getField("lastName")); // FIO 1
      fillField(g2d, fields.p.getX(fps,"bottom_fio2"), fields.p.getY(fps,"bottom_fio2"), 40, fields.getField("firstName")); // FIO 2
      fillField(g2d, fields.p.getX(fps,"bottom_fio3"), fields.p.getY(fps,"bottom_fio3"), 40, fields.getField("middleName")); // FIO 3

      fillField(g2d, fields.p.getX(fps,"usn_date"), fields.p.getY(fps,"usn_date"), 10, fields.getField("usn_date")); // DATE


      g2d.dispose();
      return g2d;
}

}
