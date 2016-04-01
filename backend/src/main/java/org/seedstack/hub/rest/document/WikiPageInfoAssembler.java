/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.rest.document;

import org.seedstack.business.assembler.BaseAssembler;
import org.seedstack.hub.domain.model.document.WikiDocument;
import org.seedstack.hub.rest.shared.Dates;

import java.util.stream.Collectors;

public class WikiPageInfoAssembler extends BaseAssembler<WikiDocument, WikiPageInfo> {

    @Override
    protected void doAssembleDtoFromAggregate(WikiPageInfo targetDto, WikiDocument sourceAggregate) {
        targetDto.setRevisions(sourceAggregate.getRevisions().stream().map(revision -> {
            WikiPageInfo.Revision wikiPageRevision = new WikiPageInfo.Revision();
            wikiPageRevision.setAuthor(revision.getAuthor().getId());
            wikiPageRevision.setDate(Dates.asDate(revision.getDate()));
            wikiPageRevision.setMessage(revision.getMessage());
            return wikiPageRevision;
        }).collect(Collectors.toList()));
    }

    @Override
    protected void doMergeAggregateWithDto(WikiDocument targetAggregate, WikiPageInfo sourceDto) {
        throw new UnsupportedOperationException();
    }

}
