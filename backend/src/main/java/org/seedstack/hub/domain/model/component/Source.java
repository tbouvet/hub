package org.seedstack.hub.domain.model.component;

import org.seedstack.business.domain.BaseValueObject;
import org.seedstack.hub.domain.services.fetch.VCSType;

import java.net.URL;

public class Source extends BaseValueObject {
    private VCSType vcsType;
    private String url;

    public Source(VCSType vcsType, URL url) {
        this.vcsType = vcsType;
        this.url = url.toString();
    }
    public Source(VCSType vcsType, String url) {
        this.vcsType = vcsType;
        this.url = url;
    }

    private Source() {
    }

    public VCSType getVcsType() {
        return vcsType;
    }

    public String getUrl() {
        return url;
    }
}
