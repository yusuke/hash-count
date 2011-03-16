/*
 * Copyright 2011 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.todoke.countstream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Status;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class Callback {
    static Logger logger = LoggerFactory.getLogger(Callback.class);
    private final String[] terms;

    Callback(String[] terms) {
        this.terms = terms;
        for(int i=0;i<terms.length;i++){
            terms[i] = terms[i].toLowerCase();
        }
    }

    public void increment(Status status) {
        logger.info("got: " + status.toString());
        increment(status.getText(), status.getCreatedAt());
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");

    public void increment(String text, Date date) {
        String dateStr = format.format(date);
        String lowerCase = text.toLowerCase();
        for (String term : terms) {
            if (lowerCase.contains(term)) {
                synchronized (term) {
                    String path = term + File.separator + dateStr + File.separatorChar;
                    File countPath = new File(path);
                    if (!countPath.exists()) {
                        countPath.mkdirs();
                    }
                    File countFile = new File(path + File.separator + "count.txt");
                    int count = getCount(countFile);
                    count++;
                    storeCount(countFile, count);
                }
            }
        }
    }

    static int getCount(File path) {
        int count = 0;
        if (path.exists()) {
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                fis = new FileInputStream(path);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                count = Integer.parseInt(br.readLine());
            } catch (IOException ioe) {
                logger.error("ioe", ioe);
            } finally {
                if (null != br) {
                    try {
                        br.close();
                    } catch (IOException ignore) {

                    }
                }
                if (null != isr) {
                    try {
                        isr.close();
                    } catch (IOException ignore) {

                    }
                }
                if (null != fis) {
                    try {
                        fis.close();
                    } catch (IOException ignore) {

                    }
                }
            }
        }
        return count;
    }

    static void storeCount(File path, int count) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            fos = new FileOutputStream(path);
            osw = new OutputStreamWriter(fos);
            bw = new BufferedWriter(osw);
            bw.write(String.valueOf(count));
        } catch (IOException ioe) {
            logger.error("ioe", ioe);
        } finally {
            if (null != bw) {
                try {
                    bw.close();
                } catch (IOException ignore) {

                }
            }
            if (null != osw) {
                try {
                    osw.close();
                } catch (IOException ignore) {

                }
            }
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException ignore) {

                }
            }
        }
    }
}
