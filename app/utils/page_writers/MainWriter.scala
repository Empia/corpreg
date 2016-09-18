package com.journaldev.di.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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
import java.util._








object MainWriter {
  def apply(fields: FormFields) = {


  var canonicalPath:String = new java.io.File( "." ).getCanonicalPath();

  var file:File = new File(canonicalPath + "/ip.tif");
  var is:InputStream = new FileInputStream(canonicalPath + "/ip.tif");

  var input:ImageInputStream = ImageIO.createImageInputStream(is);



    var readers:Iterator[ImageReader] = ImageIO.getImageReaders(input);
    var reader:ImageReader = readers.next();
    reader.setInput(input);
    System.out.println("reader.getNumImages()");
    System.out.println(reader.getNumImages(true));
    var param:ImageReadParam = reader.getDefaultReadParam();
    val image:BufferedImage = reader.read(0, param);



    val images = Array.ofDim[BufferedImage](reader.getNumImages(true))
    for (i <- 0 until reader.getNumImages(true)) {
      images(i) = reader.read(i, param)
    }
    val numThumbs = reader.getNumThumbnails(0)


/*
        image = process(image, images, args, fields, "firstPage");
        BufferedImage image1 = process(images[1], images, args, fields, "secondPage");
        System.out.println("init secondPage");

        BufferedImage image2 = process(images[2], images, args, fields, "thirdPage");
        System.out.println("init thirdPage");

        BufferedImage image3 = process(images[3], images, args, fields, "FourthPage");
        System.out.println("init FourthPage");

        BufferedImage image4 = process(images[4], images, args, fields, "fifthPage");
        System.out.println("init fifthPage");
*/


    val key = "Sample"
    //val bufferedImage = new BufferedImage(170, 30, BufferedImage.TYPE_INT_RGB)
    val fp = "firstPage";
    val sp = "secondPage";
    val tp = "thirdPage";
    val fthp = "FourthPage";
    val fifthp = "fifthPage";

    for (i <- 0 until images.length) {
      val graphics = images(i).getGraphics
      graphics.setColor(Color.black);
      graphics.setFont(new Font("Courier New", Font.PLAIN, 73));




    fillField(graphics, fields.p.getX(fp, "lastName"), fields.p.getY(fp, "lastName"), 40, fields.getField("lastName"))
    fillField(graphics, fields.p.getX(fp, "firstName"), fields.p.getY(fp, "firstName"), 40, fields.getField("firstName"))
    fillField(graphics, fields.p.getX(fp, "middleName"), fields.p.getY(fp, "middleName"), 40, fields.getField("middleName"))
    fillField(graphics, fields.p.getX(fp, "lastName_latin"), fields.p.getY(fp, "lastName_latin"), 40, fields.getField("lastName_latin"))
    fillField(graphics, fields.p.getX(fp, "firstName_latin"), fields.p.getY(fp, "firstName_latin"), 40, fields.getField("firstName_latin"))
    fillField(graphics, fields.p.getX(fp, "middleName_latin"), fields.p.getY(fp, "middleName_latin"), 40, 
      fields.getField("middleName_latin"))
    fillField(graphics, fields.p.getX(fp, "inn"), fields.p.getY(fp, "inn"), 40, fields.getField("inn"))
    fillField(graphics, fields.p.getX(fp, "gender"), fields.p.getY(fp, "gender"), 40, fields.getField("gender"))
    fillField(graphics, fields.p.getX(fp, "dob"), fields.p.getY(fp, "dob"), 40, fields.getField("dob"))
    val fullPob = fields.getField("pob")
   
/*
    val textArray = splitByNumber(String.join("", fullPob), 41)
    fillField(graphics, fields.p.getX(fp, "pob"), fields.p.getY(fp, "pob"), 41, fields.getField("inn"))
    if (textArray.length > 1) {
      fillField(graphics, fields.p.getX(fp, "pob2"), fields.p.getY(fp, "pob2"), 41, textArray(1).split(""))
    }
*/   



    fillField(graphics, fields.p.getX(fp, "grajdanstvo"), fields.p.getY(fp, "grajdanstvo"), 1, fields.getField("grajdanstvo"))



      ImageIO.write(images(i), "jpg", new File(canonicalPath + s"/public/test/ip_test${i}.jpg"))
      println("Image Created")
    }    


}

  def randomString(length: Int): String = {
    val chars = ('A' to 'Z')
    randomStringFromCharList(length, chars)
  }
  def randomStringFromCharList(length: Int, chars: Seq[Char]): String = {
    val sb = new StringBuilder
    for (i <- 1 to length) {
      val randomNum = util.Random.nextInt(chars.length)
      sb.append(chars(randomNum))
    }
    sb.toString
  }

  private def fillField(g2d: Graphics, 
      xpos: Int, 
      ypos: Int, 
      letters: Int, 
      fullNameString: Array[String]) {
    val fm = g2d.getFontMetrics
    val dot = Array(".")
    var a = 0
    var xposS:Int = xpos

    while (a < fullNameString.length && a < letters) {
      xposS = xposS + 6 + paddingForTwoRow(a)
      if (fullNameString(a).contains(".")) {
        println("dott found")
        g2d.drawString(" ", xposS, ypos)
      } else {
//        g2d.drawString(fullNameString(a), xposS, ypos)


        g2d.drawString(randomString(2), xposS, ypos)

      }
      xposS = xposS + fm.stringWidth(fullNameString(a))
      xposS = xposS + 2
      a += 1
    }
}
  private def splitByNumber(s: String, chunkSize: Int): Array[String] = {
    val chunkCount = (s.length / chunkSize) + (if (s.length % chunkSize == 0) 0 else 1)
    val returnVal = Array.ofDim[String](chunkCount)
    for (i <- 0 until chunkCount) {
      returnVal(i) = s.substring(i * chunkSize, Math.min((i + 1) * chunkSize - 1, s.length))
    }
    returnVal
  }

def paddingForTwoRow(step: Int): Int = {
  val actualStep = step + 1
  if (actualStep == 5) {
    4
  } else if (actualStep == 8) {
    4
  } else if (actualStep == 11) {
    4
  } else if (actualStep == 14 || actualStep == 15 || actualStep == 19 || 
    actualStep == 22 || 
    actualStep == 25 || 
    actualStep == 28 || 
    actualStep == 31 || 
    actualStep == 32 || 
    actualStep == 36) {
    4
  } else {
    0
  }
}

def paddingForFooter(step: Int): Int = {
  val actualStep = step + 1
  if (actualStep == 5) {
    1
  } else {
    0
  }
}

/*
  private def fillField(g2d: Graphics2D, 
      xpos: Int, 
      ypos: Int, 
      letters: Int, 
      fullNameString: Array[String]) {
    val fm = g2d.getFontMetrics
    val dot = Array(".")
    var a = 0
    while (a < fullNameString.length && a < letters) {
      xpos = xpos + 6 + paddingForTwoRow(a)
      if (fullNameString(a).contains(".")) {
        println("dott found")
        g2d.drawString(" ", xpos, ypos)
      } else {
        g2d.drawString(fullNameString(a), xpos, ypos)
      }
      xpos = xpos + fm.stringWidth(fullNameString(a))
      xpos = xpos + 2
      a += 1
    }
  }
*/








}