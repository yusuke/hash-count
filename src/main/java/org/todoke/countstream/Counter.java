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

import twitter4j.Status;
import twitter4j.StatusDeletionNotice;

/**
 * @author Yusuke Yamamoto - yusuke at mac.com
 */
public class Counter implements twitter4j.StatusListener {
    private final Callback CALLBACK;

    public Counter(Callback callback) {
        this.CALLBACK = callback;
    }

    public void onStatus(Status status) {
        CALLBACK.increment(status);
    }

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
    }

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
    }

    public void onScrubGeo(long userId, long upToStatusId) {
    }

    public void onException(Exception ex) {
    }
}
