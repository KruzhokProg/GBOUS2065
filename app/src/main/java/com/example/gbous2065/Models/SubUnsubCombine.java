package com.example.gbous2065.Models;

import java.util.List;

public class SubUnsubCombine {
    private List<UserDocFragment> subscribedDocs;
    private List<UserDocFragment> unsubscribedDocs;

    public SubUnsubCombine(List<UserDocFragment> subscribedDocs, List<UserDocFragment> unsubscribedDocs) {
        this.subscribedDocs = subscribedDocs;
        this.unsubscribedDocs = unsubscribedDocs;
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
