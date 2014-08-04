/*
 * 
 * Copyright 2007-2012 Audrius Valunas
 * 
 * This file is part of OpenACS.

 * OpenACS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OpenACS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OpenACS.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.openacs;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpAuthentication {

    static public final int AUTHTYPE_BASIC = 1;
    static public final int AUTHTYPE_MD5 = 2;

    static public boolean Authenticate(String user, String pass, Integer authtype, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String auth = request.getHeader("Authorization");
        if (authtype == null) {
            return true;
        }
        if (authtype == AUTHTYPE_BASIC) {
            if (auth == null) {
                response.setHeader("WWW-Authenticate", "Basic realm=\"OpenACS\"");
                response.setStatus(response.SC_UNAUTHORIZED);
                return false;
            }
            if (auth.startsWith("Basic ")) {
                String userPassBase64 = auth.substring(6);
                String userPassDecoded = null;
                try {
                    InputStream i = javax.mail.internet.MimeUtility.decode(new ByteArrayInputStream(userPassBase64.getBytes()), "base64");
                    byte[] d = new byte[i.available()];
                    i.read(d);
                    userPassDecoded = new String(d);
                } catch (MessagingException ex) {
                    Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (userPassBase64.endsWith("==")) {
                    userPassDecoded = userPassDecoded.substring(0, userPassDecoded.length() - 2);
                } else if (userPassBase64.endsWith("=")) {
                    userPassDecoded = userPassDecoded.substring(0, userPassDecoded.length() - 1);
                }

                String[] upa = userPassDecoded.split(":");
                System.out.println("CLIENT: up=" + userPassBase64 + " d=" + userPassDecoded + " user='" + upa[0] + "' pass='" + upa[1] + "'");
                System.out.println("CLIENT: user=" + user + " pass=" + pass);
                if (upa[0].equalsIgnoreCase(user) && upa[1].equals(pass)) {
                    return true;
                }
                Logger.getLogger(client.class.getName()).log(Level.WARNING, "Basic auth failed for user=" + upa[0] + " pass=" + upa[1]);
            }

            response.setStatus(response.SC_FORBIDDEN);
            return false;
        }
        if (authtype == AUTHTYPE_MD5) {
            if (auth == null) {
                // TODO generate nonce 
                byte[] nonce = new byte[16];
                Random r = new Random();
                r.nextBytes(nonce);
                // dcd98b7102dd2f0e8b11d0f600bfb0c093
                //response.setHeader("WWW-Authenticate", "Digest realm=\"OpenACS\",qop=\"auth,auth-int\",nonce=\""+ cvtHex(nonce)+"\"");
                response.setHeader("WWW-Authenticate", "Digest realm=\"OpenACS\",qop=\"auth\",nonce=\"" + cvtHex(nonce) + "\"");
                response.setStatus(response.SC_UNAUTHORIZED);
                return false;
            }

            if (auth.startsWith("Digest ")) {
                //String [] a = auth.substring(6).split(",");
                ByteArrayInputStream bi = new ByteArrayInputStream(auth.substring(6).replace(',', '\n').replaceAll("\"", "").getBytes());
                Properties p = new Properties();
                p.load(bi);
                p.setProperty("method", request.getMethod());
                for (Entry<Object, Object> e : p.entrySet()) {
                    System.out.println("Entry " + e.getKey() + " -> " + e.getValue());
                }
                MessageDigest digest = null;
                try {
                    digest = MessageDigest.getInstance("MD5");
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
                }
                postDigest(digest, p, user, pass);
                String udigest = (String) p.getProperty("response");
                String d = cvtHex(digest.digest());
                System.out.println("respone: got='" + udigest + "' expected: '" + d + "'");
                if (d.equals(udigest)) {
                    return true;
                }
                Logger.getLogger(client.class.getName()).log(Level.WARNING, "MD5 auth failed for user=" + user);
            }
            response.setStatus(response.SC_FORBIDDEN);
            return false;

        }
        return true;
    }

    static private void postDigest(MessageDigest digest, Properties p, String username, String password) {
        boolean passwordIsA1Hash = false;

        String qop = (String) p.getProperty("qop");
        String realm = (String) p.getProperty("realm");
        String algorithm = (String) p.getProperty("algorithm");
        String nonce = (String) p.getProperty("nonce");
        String cnonce = (String) p.getProperty("cnonce");
        String method = (String) p.getProperty("method");
        String nc = (String) p.getProperty("nc");
        String digestURI = (String) p.getProperty("uri");
        if (algorithm == null) {
            algorithm = digest.getAlgorithm();
        }
        digest.reset();

        String hA1 = null;
        // 3.2.2.2 A1
        if (algorithm == null || algorithm.equals("MD5")) {
            if (passwordIsA1Hash) {
                hA1 = password;
            } else {
                String A1 = username + ":" + realm + ":" + password;
                hA1 = H(A1, digest);
            }
        } else if (algorithm.equals("MD5-sess")) {
            if (passwordIsA1Hash) {
                hA1 = password + ":" + nonce + ":" + cnonce;
            } else {
                String A1 = username + ":" + realm + ":" + password;
                hA1 = H(A1, digest) + ":" + nonce + ":" + cnonce;
            }
        } else {
            throw new IllegalArgumentException("Unsupported algorigthm: " + algorithm);
        }

        String hA2 = null;//(String) info.getInfo(A2HASH);
        if (hA2 == null) {
            // No, compute it based on qop
            String A2 = null;
            if (qop == null || qop.equals("auth")) {
                A2 = method + ":" + digestURI;
            } else {
                throw new IllegalArgumentException("Unsupported qop=" + qop);
            }
            hA2 = H(A2, digest);
        }

        if (qop == null) {
            String extra = nonce + ":" + hA2;
            KD(hA1, extra, digest);
        } else if (qop.equals("auth")) {
            String extra = nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + hA2;
            KD(hA1, extra, digest);
        }
    }

    static private String H(String data, MessageDigest digest) {
        digest.reset();
        byte[] x = digest.digest(data.getBytes());
        return cvtHex(x);
    }
    private static char[] MD5_HEX = "0123456789abcdef".toCharArray();

    static private String cvtHex(byte[] data) {
        char[] hash = new char[32];
        for (int i = 0; i < 16; i++) {
            int j = (data[i] >> 4) & 0xf;
            hash[i * 2] = MD5_HEX[j];
            j = data[i] & 0xf;
            hash[i * 2 + 1] = MD5_HEX[j];
        }
        return new String(hash);
    }

    static private void KD(String secret, String data, MessageDigest digest) {
        String x = secret + ":" + data;
        digest.reset();
        digest.update(x.getBytes());
    }
}
