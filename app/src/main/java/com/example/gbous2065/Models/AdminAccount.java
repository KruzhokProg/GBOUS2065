package com.example.gbous2065.Models;

import java.util.Map;

public class AdminAccount {
    private Map<String, AdminDocHistory>[] docs;

    public Map<String, AdminDocHistory>[] getDocs() {
        return docs;
    }

    public void setDocs(Map<String, AdminDocHistory>[] docs) {
        this.docs = docs;
    }
}
