/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.services.document;

import org.seedstack.business.domain.Repository;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentId;

import javax.inject.Inject;

public class DocumentServiceImpl implements DocumentService {
    @Inject
    Repository<Document, DocumentId> documentRepository;

    @Override
    public String buildTitle(DocumentId documentId) {
        Document document = documentRepository.load(documentId);
        if (document != null) {
            return document.getTitle();
        } else {
            return documentId.buildDefaultTitle();
        }
    }
}
