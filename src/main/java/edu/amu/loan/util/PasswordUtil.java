package edu.amu.loan.util;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
public final class PasswordUtil {
 private PasswordUtil(){}
 public static String hash(String password) {
  try {
   byte[] salt=new byte[16]; new SecureRandom().nextBytes(salt);
   PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,120000,256);
   byte[] key=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
   return "PBKDF2$120000$"+Base64.getEncoder().encodeToString(salt)+"$"+Base64.getEncoder().encodeToString(key);
  } catch(Exception e){ throw new IllegalStateException(e); }
 }
 public static boolean verify(String password,String stored) {
  try {
   String[] p=stored.split("\\$"); if(p.length!=4 || !p[0].equals("PBKDF2")) return false;
   byte[] salt=Base64.getDecoder().decode(p[2]); byte[] expected=Base64.getDecoder().decode(p[3]);
   PBEKeySpec spec=new PBEKeySpec(password.toCharArray(),salt,Integer.parseInt(p[1]),expected.length*8);
   byte[] actual=SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
   if(actual.length!=expected.length) return false;
   int diff=0; for(int i=0;i<actual.length;i++) diff|=actual[i]^expected[i]; return diff==0;
  } catch(Exception e){ return false; }
 }
}
