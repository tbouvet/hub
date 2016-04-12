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
import org.seedstack.hub.rest.Rels;
import org.seedstack.hub.shared.Dates;
import org.seedstack.seed.rest.RelRegistry;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class WikiPageInfoAssembler extends BaseAssembler<WikiDocument, WikiPageInfo> {
    @Inject
    RelRegistry relRegistry;

    @Override
    protected void doAssembleDtoFromAggregate(WikiPageInfo targetDto, WikiDocument sourceAggregate) {
        targetDto.self(
                relRegistry.uri(Rels.WIKI)
                        .set("componentId", sourceAggregate.getId().getComponentId().getName())
                        .set("page", sourceAggregate.getId().getPath())
        );
        targetDto.setTitle(sourceAggregate.getTitle());
        targetDto.setSource(sourceAggregate.getText());
        targetDto.embedded(Rels.WIKI_REVISIONS, sourceAggregate.getRevisions().stream().map(revision -> {
            WikiPageRevision wikiPageRevision = new WikiPageRevision();
            wikiPageRevision.setAuthor(revision.getAuthor().getId());
            wikiPageRevision.setDate(Dates.asDate(revision.getDate()));
            wikiPageRevision.setMessage(revision.getMessage());
            wikiPageRevision.self(relRegistry
                    .uri(Rels.WIKI_REVISION)
                    .set("componentId", sourceAggregate.getId().getComponentId().getName())
                    .set("page", sourceAggregate.getId().getPath())
                    .set("revisionId", revision.getIndex())
            );
            wikiPageRevision.link(
                    Rels.WIKI_REVISION_DIFF,
                    relRegistry
                            .uri(Rels.WIKI_REVISION_DIFF)
                            .set("componentId", sourceAggregate.getId().getComponentId().getName())
                            .set("page", sourceAggregate.getId().getPath())
                            .set("revisionId", revision.getIndex())
            );
            return wikiPageRevision;
        }).collect(Collectors.toList()));
    }

    @Override
    protected void doMergeAggregateWithDto(WikiDocument targetAggregate, WikiPageInfo sourceDto) {
        throw new UnsupportedOperationException();
    }

}
