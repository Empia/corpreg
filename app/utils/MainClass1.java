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


public class MainClass1 extends JPanel {
    private BufferedImage image;
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(image.getWidth(), image.getHeight());
    }
@Override
protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);
}

private static void create(String[] args) {
        //JFrame f = new JFrame();
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.add(new MainClass1(args));
        //f.pack();
        new MainClass1(args);
        //f.setVisible(true);


}

public MainClass1(String[] args) {
        try {

  String canonicalPath = new java.io.File( "." ).getCanonicalPath();


	File file = new File(canonicalPath + "/1111501_5.01000_05.tif");
	InputStream is = new FileInputStream(canonicalPath + "/1111501_5.01000_05.tif");
	ImageInputStream input = ImageIO.createImageInputStream(is);

//try {
    // Get the reader
    Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
    if (!readers.hasNext()) {
        throw new IllegalArgumentException("No reader for: " + file);
    }   
    ImageReader reader = readers.next();
//    try {
        reader.setInput(input);
        System.out.println("reader.getNumImages()");
        System.out.println(reader.getNumImages(true));
        ImageReadParam param = reader.getDefaultReadParam();
        image = reader.read(0, param);

        BufferedImage images[] = new BufferedImage[reader.getNumImages(true)];
        for (int i = 1; i < reader.getNumImages(true); i++) {
            //SeekableStream ss = new FileSeekableStream(input_dir + file[i]);
            //ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
            //PlanarImage op = new NullOpImage(decoder.decodeAsRenderedImage(0), 
            //        null, OpImage.OP_IO_BOUND, null);
            images[i] = reader.read(i, param);//op.getAsBufferedImage();
        }        
        //        process(image);
        int numThumbs = reader.getNumThumbnails(0);
        //    }
        //    finally {
        //        reader.dispose();
        //    }
        //}
        //finally {
        //    input.close();
        //}
        image = process(image, images, args);


        } catch (IOException e) {
            e.printStackTrace();
        }
}

private BufferedImage process(BufferedImage old, BufferedImage[] images, String[] args) {

		Random randomGenerator = new Random();
		int randomIntA = randomGenerator.nextInt(100000);
		int randomIntB = randomGenerator.nextInt(100000);


        int w = old.getWidth();
        int h = old.getHeight();
        BufferedImage img = new BufferedImage(
                w, h, BufferedImage.TYPE_INT_ARGB);
        
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
        int xpos = 70;
        int ypos = 824;
        System.out.println("fullNameString.length" + fullNameString.length);


        for(int a = 0; a < fullNameString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(fullNameString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(fullNameString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }

        xpos = 70;
        ypos = 927;
        for(int a = 0; a < fullNameString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(fullNameString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(fullNameString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }
        xpos = 70;
        ypos = 1025;
        for(int a = 0; a < fullNameString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(fullNameString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(fullNameString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }
        xpos = 70;
        ypos = 1124;
        for(int a = 0; a < fullNameString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(fullNameString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(fullNameString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        } 
        xpos = 70;       
        ypos = 1222;
        for(int a = 0; a < fullNameString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(fullNameString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(fullNameString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }
        xpos = 70;
        ypos = 1319;
        for(int a = 0; a < fullNameString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(fullNameString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(fullNameString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }
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
        xpos = 70;
        ypos = 1541; 
        for(int a = 0; a < smallNameString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(smallNameString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(smallNameString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }
        xpos = 70;
        ypos = 1640;
        for(int a = 0; a < smallNameString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(smallNameString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(smallNameString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }
        xpos = 70;
        ypos = 1738;
        for(int a = 0; a < smallNameString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(smallNameString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(smallNameString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }
        xpos = 70;
        ypos = 1834;
        for(int a = 0; a < smallNameString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(smallNameString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(smallNameString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }

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
        // ZIP
        xpos = 579;
        ypos = 2192; 
        for(int a = 0; a < zipString.length && a < 6; a++) {
          xpos = xpos + 15 + paddingForFooter(a); // first padding 
          g2d.drawString(zipString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(zipString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }


        String regionString[];
        if ((args.length + 1) > 3) {
	        regionString = args[3].split("");
        } else {
	        regionString = g2d3s2s;
        }          
        // REGION
        xpos = 1789;
        ypos = 2192; 
        for(int a = 0; a < regionString.length && a < 2; a++) {
          xpos = xpos + 15 + paddingForFooter(a); // first padding 
          g2d.drawString(regionString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(regionString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }

        String addressString[];
        if ((args.length + 1) > 4) {
	        addressString = args[4].split("");
        } else {
	        addressString = g2d3s2s;
        }  
        /*
         Address
         */
        // Area small
        xpos = 71;
        ypos = 2442;
        for(int a = 0; a < addressString.length && a < 10; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(addressString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(addressString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }
        String areaSmallString[];
        if ((args.length + 1) > 5) {
	        areaSmallString = args[5].split("");
        } else {
	        areaSmallString = g2d3s2s;
        }  
        // Area
        xpos = 766;
        ypos = 2437; 
        for(int a = 0; a < areaSmallString.length && a < 28; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(areaSmallString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(areaSmallString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        } 

        String areaBigString[];
        if ((args.length + 1) > 6) {
	        areaBigString = args[6].split("");
        } else {
	        areaBigString = g2d3s2s;
        }     
        // Area big
        xpos = 71;
        ypos = 2549; 
        for(int a = 0; a < areaBigString.length && a < 40; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(areaBigString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(areaBigString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }

        String cityString[];
        if ((args.length + 1) > 7) {
	        cityString = args[7].split("");
        } else {
	        cityString = g2d3s2s;
        }  
        // City
        xpos = 71;
        ypos = 2730;	
        for(int a = 0; a < cityString.length && a < 10; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(cityString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(cityString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }
        String cityTitleString[];
        if ((args.length + 1) > 8) {
	        cityTitleString = args[8].split("");
        } else {
	        cityTitleString = g2d3s2s;
        }          
        // City Title
        xpos = 771;
        ypos = 2730; 
        for(int a = 0; a < cityTitleString.length && a < 28; a++) {
          xpos = xpos + 15 + paddingForTwoRow(a); // first padding 
          g2d.drawString(cityTitleString[a], xpos, ypos);
          xpos = xpos + fm.stringWidth(cityTitleString[a]); // second padding 
          xpos = xpos + 15; // third padding
          //xpos = xpos + (86 - fm.stringWidth(g2d3s2s));
          //System.out.println("xpos" + xpos);
        }


        g2d.dispose();

        try {        
          String fileName = args[9];
          		
          TIFFEncodeParam params = new TIFFEncodeParam();
          String path = new java.io.File( "." ).getCanonicalPath();
          OutputStream out = new FileOutputStream(path + "/public/"+ fileName +".tif"); 
          ImageEncoder encoder = ImageCodec.createImageEncoder("tiff", out, params);
          Vector vector = new Vector();   
          //vector.add(img);
          for (int i = 1; i < images.length; i++) {
              vector.add(images[i]); 
          }
          params.setExtraImages(vector.iterator()); 
          encoder.encode(img); 
          out.close(); 

          ImageIO.write(img, "png", new File(path + "/public/"+ fileName + "_page1.png"));
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

public static void main(String[] args) throws Exception {
  EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                create(args);
            }
        });
}


public static void main2(String[] args) throws Exception {
  //EventQueue.invokeLater(new Runnable() {
         //   @Override
         //   public void run() {
            	String[] values = {"ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ","ТЕСТ", "ТЕСТ"};
                create(args);
           //}
        //});
}



}