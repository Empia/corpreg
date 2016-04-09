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


        image = process(image, images, args, fields, "page1");
        BufferedImage image1 = process(images[1], images, args, fields, "page2");
        BufferedImage image2 = process(images[2], images, args, fields, "page3");
        BufferedImage image3 = process(images[3], images, args, fields, "page4");
        BufferedImage image4 = process(images[4], images, args, fields, "page5");

        imageUsn = process(imageUsn, imagesUsn, args, fields, "usn_page1");


        } catch (IOException e) {
            e.printStackTrace();
        }
}

private BufferedImage process(BufferedImage old, BufferedImage[] images, String[] args, FormFields fields, String fileNameTitle) throws Exception {

        int w = old.getWidth();
        int h = old.getHeight();
  BufferedImage img = new BufferedImage(
          w, h, BufferedImage.TYPE_INT_ARGB);

        if (fileNameTitle == "page1") {
          Graphics2D g2d = createPage1(img,old,args, fields);
        } else if (fileNameTitle == "page2") {
          Graphics2D g2d = createPage2(img,old,args, fields);
        } else if (fileNameTitle == "page3") {
          Graphics2D g2d = createPage3(img,old,args, fields);
        } else if (fileNameTitle == "page4") {
          Graphics2D g2d = createPage4(img,old,args, fields);
        } else if (fileNameTitle == "usn_page1") {
          Graphics2D g2d = createPage1Usn(img,old,args, fields);
        } else {
          Graphics2D g2d = createPage5(img,old,args, fields);
        }

        String path = new java.io.File( "." ).getCanonicalPath();
        String fileName = args[9];


        try {

          //}
          ImageIO.write(img, "png", new File(path + "/public/"+ fileNameTitle + "_" + fileName + ".png"));
        } finally {
          return img;
        }
}

public int paddingForTwoRow(int step) {
  int actualStep = step + 1;
  if (actualStep == 5) {
    return 1;
  } else if (actualStep == 8) {
    return 1;
  } else if (actualStep == 11) {
    return 1;
  } else if (actualStep == 14 || actualStep == 15 || actualStep == 19 || actualStep == 22 || actualStep == 25 || actualStep == 28 || actualStep == 31 || actualStep == 32 || actualStep == 36) {
    return 1;
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
            	String[] values = {"ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ", "ТЕСТ"};
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
      g2d.setFont(new Font("Courier New", Font.PLAIN, 46));
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
          if ((args.length + 1) > 0) {
  	        fullNameString = args[0].split("");
          } else {
  	        fullNameString = g2d3s2s;
          }





          fillField(g2d, 410, 769, 40, fields.getField("lastName"));
          fillField(g2d, 410, 882, 40, fields.getField("firstName"));
          fillField(g2d, 410, 1005, 40, fields.getField("middleName"));
          fillField(g2d, 410, 1234, 40, fields.getField("lastName_latin"));

          fillField(g2d, 410, 1343, 40, fields.getField("firstName_latin"));
          fillField(g2d, 410, 1471, 40, fields.getField("middleName_latin"));

  ////////////////////////////////////////////////////////////////////////////////////////////////
          String smallNameString[];
          if ((args.length + 1) > 1) {
  	        smallNameString = args[1].split("");
          } else {
  	        smallNameString = g2d3s2s;
          }

          /*
            Middle field for small name of corporation
  	      Limit 40
            4 rows
           */
          // INN ROW
          fillField(g2d, 655, 1597, 40, fields.getField("inn"));
          // GENDER
          fillField(g2d, 360, 1736, 40, fields.getField("gender"));
          // DOB
          fillField(g2d, 510, 1980, 40, fields.getField("dob"));
          // PLACE OB BIRTH
          fillField(g2d, 80, 2164, 40, fields.getField("pob"));


          String zipString[];
          if ((args.length + 1) > 2) {
  	        zipString = args[2].split("");
          } else {
  	        zipString = g2d3s2s;
          }
          /*
            Footre field
            Index, code
           */
          // PLACE OB BIRTH #2
          int xpos = 80;
          int ypos = 2262;
          fillField(g2d, 80, 2262, 6, fields.getField("pob2"));


          String regionString[];
          if ((args.length + 1) > 3) {
  	        regionString = args[3].split("");
          } else {
  	        regionString = g2d3s2s;
          }
          // OKG GRAJDANINA
          xpos = 480;
          ypos = 2504;
          fillField(g2d, 480, 2504, 1, fields.getField("grajdanstvo"));


          String addressString[];
          if ((args.length + 1) > 4) {
  	        addressString = args[4].split("");
          } else {
  	        addressString = g2d3s2s;
          }



          g2d.dispose();

          return g2d;
}























private Graphics2D createPage2(BufferedImage img, BufferedImage old, String[] args, FormFields fields) {

  Random randomGenerator = new Random();
  int randomIntA = randomGenerator.nextInt(100000);
  int randomIntB = randomGenerator.nextInt(100000);





      Graphics2D g2d = img.createGraphics();
      g2d.drawImage(old, 0, 0, null);
      g2d.setPaint(Color.black);
      g2d.setFont(new Font("Courier New", Font.PLAIN, 46));


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




    fillField(g2d, 545, 615, 4, fields.getField("zip")); // ZIP
    fillField(g2d, 1710, 615, 2, fields.getField("subject")); // SUBJECT

    fillField(g2d, 72, 799, 4, fields.getField("area")); // AREA
    fillField(g2d, 778, 799 , 4, fields.getField("area_title")); // AREA TITLE
    fillField(g2d, 72, 909, 4, fields.getField("area_title_big")); // AREA 2 BIG

    fillField(g2d, 72, 1083, 4, fields.getField("city")); // CITY
    fillField(g2d, 778, 1083, 4, fields.getField("city_title")); // CITY TITLE

    fillField(g2d, 72, 1255, 4, fields.getField("nasel")); // NASEL PUNKT
    fillField(g2d, 778, 1255, 4, fields.getField("nasel_title")); // NASEL PUNKT
    fillField(g2d, 72, 1371, 4, fields.getField("nasel_title_big")); // NASEL PUNKT BIG

    fillField(g2d, 72, 1543, 4, fields.getField("street")); // STREET
    fillField(g2d, 776, 1543, 4, fields.getField("street_title")); // STREET
    fillField(g2d, 72, 1655, 4, fields.getField("street_title_big")); // STREET


    fillField(g2d, 72, 1825, 4, fields.getField("house")); // HOUSE
    fillField(g2d, 728, 1825, 4, fields.getField("house_num")); // HOUSE NUMBER
    fillField(g2d, 1286, 1825, 4, fields.getField("corpus")); // CORPUS
    fillField(g2d, 1948, 1817, 4, fields.getField("corpus_num")); // CORPUS NUMBER


    fillField(g2d, 728, 1939, 4, fields.getField("appartment")); // APPARTMENT
    fillField(g2d, 1948, 1939, 4, fields.getField("appartment_num")); // APPARTMENT NUMBER

    fillField(g2d, 486, 2235, 2, fields.getField("vid_document")); // VID DOCUMENTS

    fillField(g2d, 672, 2414, 4, fields.getField("series")); // SERIES NUMBER
    fillField(g2d, 426, 2525, 4, fields.getField("issue_date")); // DATA VIDACHI
    fillField(g2d, 422, 2652, 4, fields.getField("issuer")); // KEM VIDAN
    fillField(g2d, 72, 2767, 4, fields.getField("issuer2")); // KEM VIDAN 2
    fillField(g2d, 72, 2887, 4, fields.getField("issuer3")); // KEM VIDAN 3

    fillField(g2d, 536, 3029, 3, fields.getField("issuer_code1")); // KOD PODR 1
    fillField(g2d, 778, 3029, 3, fields.getField("issuer_code2")); // KOD PODR 2

    g2d.dispose();
    return g2d;
}

private void fillField(Graphics2D g2d, int xpos, int ypos, int letters, String[] fullNameString) {
  FontMetrics fm = g2d.getFontMetrics();
  for(int a = 0; a < fullNameString.length && a < letters; a++) {
    xpos = xpos + 15 + paddingForTwoRow(a); // first padding
    g2d.drawString(fullNameString[a], xpos, ypos);
    xpos = xpos + fm.stringWidth(fullNameString[a]); // second padding
    xpos = xpos + 15; // third padding
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
      g2d.setFont(new Font("Courier New", Font.PLAIN, 46));


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




    fillField(g2d, 260, 738, 1, fields.getField("vid_doc")); // VID
    fillField(g2d, 557, 878, 2, fields.getField("page3_doc_number")); // NOMER DOCUMENTA

    fillField(g2d, 445, 1033, 2, fields.getField("page3_doc_number") ); // NOMER DOCUMENTA
    fillField(g2d, 432, 1166, 2, fields.getField("page3_doc_number") ); // NOMER DOCUMENTA
    fillField(g2d, 90, 1278, 2, fields.getField("page3_doc_number") ); // NOMER DOCUMENTA
    fillField(g2d, 90, 1386, 2, fields.getField("page3_doc_number") ); // NOMER DOCUMENTA
    fillField(g2d, 500, 1556, 2, fields.getField("page3_doc_number") ); // NOMER DOCUMENTA


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
      g2d.setFont(new Font("Courier New", Font.PLAIN, 46));
      FontMetrics fm = g2d.getFontMetrics();


          fillField(g2d, 924, 779, 1, fields.getField("activity_type")); // VID DEYATELNOSTY


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
      g2d.setFont(new Font("Courier New", Font.PLAIN, 46));
      FontMetrics fm = g2d.getFontMetrics();



      fillField(g2d, 264, 513, 5, fields.getField("fio")); // FIO
      fillField(g2d, 256, 1011, 1, fields.getField("vidat")); // VIDAT
      fillField(g2d, 872, 1223, 1, fields.getField("phone")); // PHONE
      fillField(g2d, 290, 1345, 1, fields.getField("email")); // EMAIL

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
      g2d.setFont(new Font("Courier New", Font.PLAIN, 46));
      FontMetrics fm = g2d.getFontMetrics();


      fillField(g2d, 758, 105, 5, fields.getField("inn")); // INN
      fillField(g2d, 758, 200, 1, fields.getField("kpp")); // KPP
      fillField(g2d, 834, 475, 1, fields.getField("nalogovaya")); // NALOGOVAYA
      fillField(g2d, 1870, 479, 1, fields.getField("priznak")); // PRIZNAK

      fillField(g2d, 75, 661, 1, fields.getField("fio1")); // FIO 1
      fillField(g2d, 75, 763, 1, fields.getField("fio2")); // FIO 2
      fillField(g2d, 75, 861, 1, fields.getField("fio3")); // FIO 3
      fillField(g2d, 75, 961, 1, fields.getField("fio4")); // FIO 3



      fillField(g2d, 1050, 1115, 1, fields.getField("perehodit1")); // PEREHODIT
      fillField(g2d, 935, 1335, 1, fields.getField("perehodit2")); // PEREHODIT

      fillField(g2d, 286, 1975, 1, fields.getField("tax_type")); // TYPE NALOGOPLATILSHIKA

      fillField(g2d, 82, 2061, 1, fields.getField("fio1")); // FIO 1
      fillField(g2d, 82, 2159, 1, fields.getField("fio2")); // FIO 2
      fillField(g2d, 82, 2257, 1, fields.getField("fio3")); // FIO 3

      fillField(g2d, 657, 2625, 1, fields.getField("usn_date")); // DATE


      g2d.dispose();
      return g2d;
}

}
