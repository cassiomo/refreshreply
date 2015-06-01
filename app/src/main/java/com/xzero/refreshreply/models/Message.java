package com.xzero.refreshreply.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;


@ParseClassName("Message")
public class Message extends ParseObject {

    public void setPartnerName(String partnerName) {
        put("partnerName", partnerName);
    }

    public String getPartnerName() {
        return getString("partnerName");
    }

    public void setUserName(String userName) {
        put("userName", userName);
    }

    public String getUserName() {
        return getString("userName");
    }

    public void setPartner(String partnerId) {
        put("partnerId", partnerId);
    }

    public String getPartnerId() {
        return getString("partnerId");
    }

    public String getUserId() {
        return getString("userId");
    }

    public String getBody() {
        return getString("body");
    }

    public void setUserId(String userId) {
        put("userId", userId);
    }

    public void setBody(String body) {
        put("body", body);
    }
}

//package com.xzero.refreshreply.models;
//
//import com.parse.ParseClassName;
//import com.parse.ParseObject;
//
//
//@ParseClassName("Message")
//public class Message extends ParseObject {
//
//    public void setSenderName(String senderName) {
//        put("senderName", senderName);
//    }
//
//    public String getSenderName() {
//        return getString("senderName");
//    }
//
//    public void setSenderId(String senderId) {
//        put("senderId", senderId);
//    }
//
//    public String getSenderId() {
//        return getString("senderId");
//    }
//
//    public void setReceiverName(String receiverName) {
//        put("receiverName", receiverName);
//    }
//
//    public String getReceiverName() {
//        return getString("receiverName");
//    }
//
//    public void setReceiverId(String receiverId) {
//        put("receiverId", receiverId);
//    }
//
//    public String getReceiverId() {
//        return getString("receiverId");
//    }
//
//    public void setMessageDirection(String messageDirection) {
//        put("messageDirection", messageDirection);
//    }
//
//    public String getMessageDirection() {
//        return getString("messageDirection");
//    }
//
//    public String getBody() {
//        return getString("body");
//    }
//
//    public void setBody(String body) {
//        put("body", body);
//    }
//}