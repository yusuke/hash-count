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
package org.todoke.countsearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.todoke.countstream.Callback;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("usage: java org.todoke.countsearch.Main [term] [YYYY-MM-DD]");
            System.exit(-1);
        }
        int count;
        String[] terms = args[0].split(",");
        try {
            Date date = format.parse(args[1]);
            for (String term : terms) {
                GregorianCalendar gc = new GregorianCalendar();
                gc.setTime(date);
                do {
                    String dateStr = format.format(gc.getTime());
                    count = search(term, dateStr);
                    System.out.println(term + " total: " + count + " tweets on " + dateStr);
                    gc.add(GregorianCalendar.DATE, -1);
                }while(count != 0);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    static int search(String term, String date) {
        Callback callback = new Callback(new String[]{term});
        Twitter twitter = new TwitterFactory().getInstance();
        int count = 0;
        try {
            int page = 1;
            List<Tweet> tweets;
            do {
                Query query = new Query(term);
                query.rpp(100);
                query.until(date);
                query.setPage(page);
                QueryResult qs = twitter.search(query);
                tweets = qs.getTweets();
                boolean outOfRange = false;
                for (Tweet tweet : tweets) {

                    try {
                        if (tweet.getCreatedAt().after(format.parse(date))) {
                            callback.increment(tweet.getText(), tweet.getCreatedAt());
                            count++;
                        } else {
    //                        System.out.println(tweet.getCreatedAt());
                            outOfRange = true;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
                if(outOfRange){
                    break;
                }
                page++;
            } while (tweets.size() != 0);
        } catch (TwitterException e) {
            e.printStackTrace();
//            System.exit(-1);
        }
        return count;
    }
}
