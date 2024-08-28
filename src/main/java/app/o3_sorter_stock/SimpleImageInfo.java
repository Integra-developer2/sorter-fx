package app.o3_sorter_stock;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SuppressWarnings("all")
public class SimpleImageInfo {
   private int height;
   private int width;
   private String mimeType;

   private SimpleImageInfo() {
   }

   public SimpleImageInfo(File file) throws IOException {
      FileInputStream is = new FileInputStream(file);

      try {
         this.processStream(is);
      } finally {
         is.close();
      }

   }

   public SimpleImageInfo(InputStream is) throws IOException {
      this.processStream(is);
   }

   public SimpleImageInfo(byte[] bytes) throws IOException {
      ByteArrayInputStream is = new ByteArrayInputStream(bytes);

      try {
         this.processStream(is);
      } finally {
         is.close();
      }

   }

   private void processStream(InputStream is) throws IOException {
      int c1 = is.read();
      int c2 = is.read();
      int c3 = is.read();
      this.mimeType = null;
      this.width = this.height = -1;
      if (c1 == 71 && c2 == 73 && c3 == 70) {
         is.skip(3L);
         this.width = this.readInt(is, 2, false);
         this.height = this.readInt(is, 2, false);
         this.mimeType = "image/gif";
      } else {
         int c4;
         if (c1 == 255 && c2 == 216) {
            while(c3 == 255) {
               c4 = is.read();
               int len = this.readInt(is, 2, true);
               if (c4 == 192 || c4 == 193 || c4 == 194) {
                  is.skip(1L);
                  this.height = this.readInt(is, 2, true);
                  this.width = this.readInt(is, 2, true);
                  this.mimeType = "image/jpeg";
                  break;
               }

               is.skip((long)(len - 2));
               c3 = is.read();
            }
         } else if (c1 == 137 && c2 == 80 && c3 == 78) {
            is.skip(15L);
            this.width = this.readInt(is, 2, true);
            is.skip(2L);
            this.height = this.readInt(is, 2, true);
            this.mimeType = "image/png";
         } else if (c1 == 66 && c2 == 77) {
            is.skip(15L);
            this.width = this.readInt(is, 2, false);
            is.skip(2L);
            this.height = this.readInt(is, 2, false);
            this.mimeType = "image/bmp";
         } else {
            c4 = is.read();
            if (c1 == 77 && c2 == 77 && c3 == 0 && c4 == 42 || c1 == 73 && c2 == 73 && c3 == 42 && c4 == 0) {
               boolean bigEndian = c1 == 77;
               int ifd = 0;
               ifd = this.readInt(is, 4, bigEndian);
               is.skip((long)(ifd - 8));
               int entries = this.readInt(is, 2, bigEndian);

               for(int i = 1; i <= entries; ++i) {
                  int tag = this.readInt(is, 2, bigEndian);
                  int fieldType = this.readInt(is, 2, bigEndian);
                  long count = (long)this.readInt(is, 4, bigEndian);
                  int valOffset;
                  if (fieldType != 3 && fieldType != 8) {
                     valOffset = this.readInt(is, 4, bigEndian);
                  } else {
                     valOffset = this.readInt(is, 2, bigEndian);
                     is.skip(2L);
                  }

                  if (tag == 256) {
                     this.width = valOffset;
                  } else if (tag == 257) {
                     this.height = valOffset;
                  }

                  if (this.width != -1 && this.height != -1) {
                     this.mimeType = "image/tiff";
                     break;
                  }
               }
            }
         }
      }

      if (this.mimeType == null) {
         throw new IOException("Unsupported image type");
      }
   }

   private int readInt(InputStream is, int noOfBytes, boolean bigEndian) throws IOException {
      int ret = 0;
      int sv = bigEndian ? (noOfBytes - 1) * 8 : 0;
      int cnt = bigEndian ? -8 : 8;

      for(int i = 0; i < noOfBytes; ++i) {
         ret |= is.read() << sv;
         sv += cnt;
      }

      return ret;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public String getMimeType() {
      return this.mimeType;
   }

   public void setMimeType(String mimeType) {
      this.mimeType = mimeType;
   }

   public String toString() {
      return "MIME Type : " + this.mimeType + "\t Width : " + this.width + "\t Height : " + this.height;
   }
}
