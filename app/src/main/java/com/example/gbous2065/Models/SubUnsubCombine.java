package com.example.gbous2065.Models;

import java.util.List;

public class SubUnsubCombine {
    private String fullName;
    private List<UserDocFragment> subscribedDocs;
    private List<UserDocFragment> unsubscribedDocs;

    public SubUnsubCombine(String fullName, List<UserDocFragment> subscribedDocs, List<UserDocFragment> unsubscribedDocs) {
        this.fullName = fullName;
        this.subscribedDocs = subscribedDocs;
        this.unsubscribedDocs = unsubscribedDocs;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<UserDocFragment> getSubscribedDocs() {
        return subscribedDocs;
    }

    public void setSubscribedDocs(List<UserDocFragment> subscribedDocs) {
        this.subscribedDocs = subscribedDocs;
    }

    public List<UserDocFragment> getUnsubscribedDocs() {
        return unsubscribedDocs;
    }

    public void setUnsubscribedDocs(List<UserDocFragment> unsubscribedDocs) {
        this.unsubscribedDocs = unsubscribedDocs;
    }
}
