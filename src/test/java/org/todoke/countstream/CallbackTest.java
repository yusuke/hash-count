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

import junit.framework.TestCase;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class CallbackTest extends TestCase {
    Callback callback;
    File testDir;
    File testCountFile;

    public void setUp() throws Exception {
        testDir = new File("test");
        testDir.mkdirs();
        testCountFile = new File("test" + File.separator + "test.txt");
        callback = new Callback(testDir);
    }

    public void tearDown() throws Exception {
        File[] files = testDir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                File[] files2 = file.listFiles();
                for (File file2 : files2) {
                    file2.delete();
                }
            }
            file.delete();
        }
        testDir.delete();
    }

    public void testCount() throws Exception {
        int count;
        count = Callback.getCount(testCountFile);
        assertEquals(0, count);
        Callback.storeCount(testCountFile, 100);

        count = Callback.getCount(testCountFile);
        assertEquals(100, count);
    }

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH");

    public void testIncrementCreatesDirectory() throws Exception {
        callback.increment("test", format.parse("2011-03-02-12"));
        assertTrue(new File("test" + File.separator + "2011-03-02-12").exists());
        assertTrue(new File("test" + File.separator + "2011-03-02-12" + File.separator + "count.txt").exists());
    }

    public void testIncrementIncrementsCount() throws Exception {
        callback.increment("test", format.parse("2011-03-02-13"));
        callback.increment("test", format.parse("2011-03-02-13"));
        int count = Callback.getCount(new File("test" + File.separator + "2011-03-02-13" + File.separator + "count.txt"));
        assertEquals(2, count);
    }
}
