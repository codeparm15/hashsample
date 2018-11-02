package com.hashsample;

import java.util.concurrent.*;
import org.bouncycastle.crypto.digests.SHA256Digest;

/**
 * Created by parmjit on 11/1/18.
 */
public class HashKeyGenerator {

    private boolean found = false;
    private long count = 0;
    private ConcurrentHashMap<String, String> hashKey = new ConcurrentHashMap<String, String>();
    private String previous = "0000000000000000000000000000000000000000000000000000000000000000";

    public synchronized void increment() {
        count = count + 1000000000;
    }

    public synchronized void setFound(boolean value) {
        found = value;
    }

    public synchronized boolean getFound() {
        return found;
    }

    public static void main(String[] args) throws Exception {

        HashKeyGenerator generator = new HashKeyGenerator();
        generator.generateKey();
    }

    public void generateKey() throws Exception {

        ExecutorService executor = Executors.newCachedThreadPool();

        for (int j = 0; j < 500; j++) {
            executor.submit(new Runnable() {
                public void run() {
                    long start = count;
                    increment();
                    long end = count;
                    int block =1;
                    String name = "parmjitsingh";
                    SHA256Digest SHA = new SHA256Digest();
                    byte[] digest = new byte[32];
                    byte[] textBytes;
                    long nonce = 0L;

                    for (long k = start; k < end && !getFound(); k++) {

                        try {
                            nonce = k;
                            String originalString = block + ""+ nonce + name + previous;
                            textBytes = (originalString).getBytes("UTF-8");
                            SHA.update(textBytes, 0, textBytes.length);
                            SHA.doFinal(digest, 0);
                            boolean eightFound = digest[0] == 0 && digest[1] == 0 && digest[2] == 0 && digest[3] == 0 && digest[4] == 0 && digest[5] == 0
                                    && digest[6] == 0 && digest[7] == 0;
                            if(eightFound) {
                                String sha256hex = "";
                                for (final byte element : digest)
                                {
                                    sha256hex += Integer.toString((element & 0xff) + 0x100, 16).substring(1);
                                }
                                System.out.println(">>>>>>>>>" + originalString + ":::::"+ sha256hex);
                                hashKey.put(originalString, sha256hex);
                                boolean tenFound = digest[0] == 0 && digest[1] == 0 && digest[2] == 0 && digest[3] == 0 && digest[4] == 0 && digest[5] == 0
                                        && digest[6] == 0 && digest[7] == 0 && digest[8] == 0 && digest[9] == 0;
                                if (tenFound) {
                                    System.out.println("HOOOOOOOORRRRRAAAAYYYYYY>>>>>>>>>" + originalString + ":::::"+ sha256hex);
                                    setFound(true);
                                }
                            }
                        } catch (Exception ex) {

                        }

                    }

                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.DAYS);
    }

}

