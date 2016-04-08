package org.seedstack.hub.rest.admin;

import org.seedstack.hub.application.fetch.ImportException;
import org.seedstack.hub.rest.component.list.ComponentCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImportReport {

    private List<ComponentCard> componentCards = new ArrayList<>();

    private Map<String, String> failedSources = new HashMap<>();

    public List<ComponentCard> getComponentCards() {
        return componentCards;
    }

    public void setComponentCards(List<ComponentCard> componentCards) {
        this.componentCards = componentCards;
    }

    public Map<String, String> getFailedSources() {
        return failedSources;
    }

    public void addFailedSource(ImportException exception){
        failedSources.put(exception.getSource().getUrl(), exception.getMessage());
    }
}
