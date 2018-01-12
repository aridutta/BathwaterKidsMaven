/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bathwater.elasticsearch.helper.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bathwater.elasticsearch.helper.JestClientFactory;

import io.searchbox.core.Search;
import io.searchbox.core.Suggest;
import io.searchbox.core.SuggestResult;

/**
 *
 * @author rajeshk
 */
public class ESItemQueries {

    private static final String ITEMS_INDEX = "1".equals(System.getProperty("PRODUCTION_MODE")) ? "items" : "items_dev";

    private static final String ITEMS_TYPE = "item";

    public List<String> searchItems(List<String> keywords) throws IOException {

        StringBuilder queries = new StringBuilder();

        for (String keyword : keywords) {
            queries.append(keyword).append(" ");
        }

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("{\"query\": { \"bool\":");
        queryBuilder.append("{\"filter\": [ {\"term\": { \"ownerID\": \"1\" }}],");
        queryBuilder.append("\"must\": [");
        queryBuilder.append("{\"multi_match\": {\"query\": \"").append(queries.toString()).append("\",");
        queryBuilder.append("\"type\":\"best_fields\",");
        queryBuilder.append("\"fields\":[ \"itemName\", \"description^2\" ],");
        queryBuilder.append("\"tie_breaker\": 0.3,");
        queryBuilder.append("\"minimum_should_match\": \"30%\"}}]}}}");

        String query = queryBuilder.toString();

        Search.Builder searchBuilder = new Search.Builder(query).addIndex(ITEMS_INDEX).addType(ITEMS_TYPE);

        List<String> result = JestClientFactory.getJestClient().execute(searchBuilder.build()).getSourceAsStringList();

        return result;
    }

    public List<String> itemSuggest(String text) {
        List<String> items = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append("{\"suggest\" : {");
        queryBuilder.append("\"text\" : \"").append(text).append("\",");
        queryBuilder.append("\"completion\" : {");
        queryBuilder.append("\"field\" : \"itemName\"}}}");

        Suggest.Builder suggestBuilder = new Suggest.Builder(queryBuilder.toString());

        try {
            SuggestResult suggestResult = JestClientFactory.getJestClient().execute(suggestBuilder.build());
            if (suggestResult.isSucceeded()) {
                List<SuggestResult.Suggestion> suggestions = suggestResult.getSuggestions("suggest");
                for (SuggestResult.Suggestion suggestion : suggestions) {
                    List<Map<String, Object>> options = suggestion.options;
                    for (Map<String, Object> obj : options) {
                        items.add((String) obj.get("text"));
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ESItemQueries.class.getName()).log(Level.SEVERE, null, ex);
        }

        return items;
    }

}
