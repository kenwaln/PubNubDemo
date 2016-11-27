package com.kwaln.pubnubdemo;

import android.util.Log;

import com.pubnub.api.*;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.enums.PNStatusCategory;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ken on 11/25/2016.
 */
public class PubNubConn {
    static final String ME = "PubNubConn";
    private static PubNubConn ourInstance = new PubNubConn();

    public static PubNubConn getInstance() {
        return ourInstance;
    }

    private PubNubConn() {
        init();
    }

    private boolean fInit = false;
    private PubNub pubnub;
    private Map<String, MessageReceiver> receivers = new HashMap<String, MessageReceiver>();

    private void init() {
        if (!fInit) {
            PNConfiguration pnConfiguration = new PNConfiguration();
            pnConfiguration.setSubscribeKey("sub-c-77516000-b372-11e6-936d-02ee2ddab7fe");
            pnConfiguration.setPublishKey("pub-c-442fb665-19f8-4e31-88d3-88397b20e2c5");

            pubnub = new PubNub(pnConfiguration);
            pubnub.addListener(new SubscribeCallback() {
                @Override
                public void status(PubNub pubnub, PNStatus status) {


                    if (status.getCategory() == PNStatusCategory.PNUnexpectedDisconnectCategory) {
                        // This event happens when radio / connectivity is lost
                    } else if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {

                        // Connect event. You can do stuff like publish, and know you'll get it.
                        // Or just use the connected event to confirm you are subscribed for
                        // UI / internal notifications, etc

                        if (status.getCategory() == PNStatusCategory.PNConnectedCategory) {
                            fInit = true;
                            pubnub.subscribe()
                                    .channels(Arrays.asList(receivers.keySet().toArray(new String[] {})))
                                    .execute();
                        }
                    } else if (status.getCategory() == PNStatusCategory.PNReconnectedCategory) {

                        // Happens as part of our regular operation. This event happens when
                        // radio / connectivity is lost, then regained.
                    } else if (status.getCategory() == PNStatusCategory.PNDecryptionErrorCategory) {

                        // Handle messsage decryption error. Probably client configured to
                        // encrypt messages and on live data feed it received plain text.
                    }
                }

                @Override
                public void message(PubNub pubnub, PNMessageResult message) {
                    // Handle new message stored in message.message
                    Log.i(ME, "Got Msg");
                    if (receivers.containsKey(message.getChannel())) {
                        MessageReceiver rcv = receivers.get(message.getChannel());
                        rcv.processMsg(message.getMessage());
                    } else {
                        // Message has been received on channel stored in
                        // message.getSubscription()
                    }
                }

                @Override
                public void presence(PubNub pubnub, PNPresenceEventResult presence) {

                }
            });

        }
    }

    public void addReceiver(String channel, MessageReceiver rcv) {
        Log.i(ME,"Adding receiver");
        receivers.put(channel, rcv);
        pubnub.subscribe()
                .channels(Arrays.asList(channel))
                .execute();
    }

    public void post(String room, ChatMsg body) {
        Log.i("PubNubConn", "posting Msg");
        pubnub.publish()
                .message(body)
                .channel(room)
                .async(new PNCallback<PNPublishResult>() {
                    @Override
                    public void onResponse(PNPublishResult result, PNStatus status) {
                        // handle publish result, status always present, result if successful
                        // status.isError to see if error happened
                    }
                });
    }
}

