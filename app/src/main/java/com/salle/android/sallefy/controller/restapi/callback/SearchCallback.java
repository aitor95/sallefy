package com.salle.android.sallefy.controller.restapi.callback;

import com.salle.android.sallefy.model.SearchResult;
import com.salle.android.sallefy.model.Track;

import java.util.List;


public interface SearchCallback extends FailureCallback {
    void onSearchResultReceived(SearchResult body);
}
