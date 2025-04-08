package ru.viktorgezz.api_T_connector.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FigiList {

    private final List<String> figis;

    @Autowired
    public FigiList(ShareDao db) {
        this.figis = db.loadFigis();
    }

    public List<String> getFigis() {
        return figis;
    }
}
