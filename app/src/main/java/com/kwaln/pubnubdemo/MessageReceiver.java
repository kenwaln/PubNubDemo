package com.kwaln.pubnubdemo;


import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by Ken on 11/26/2016.
 */

public interface MessageReceiver {
    void processMsg(JsonNode msg);
}
