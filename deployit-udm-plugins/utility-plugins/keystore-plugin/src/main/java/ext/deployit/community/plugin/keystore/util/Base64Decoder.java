package ext.deployit.community.plugin.keystore.util;

import  javax.xml.bind.*;

class Base64Decoder {

  public static void main(String[] args) {
    try {
      System.out.write(DatatypeConverter.parseBase64Binary(args[0]));
    } catch (Exception e) {
      System.err.println("Parsing Base64 failed: " + e.getMessage());
      System.exit(1);
    }
  }
}
