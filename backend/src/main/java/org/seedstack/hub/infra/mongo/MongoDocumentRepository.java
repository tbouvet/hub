/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.mongo;

import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.DocumentRepository;
import org.seedstack.mongodb.morphia.BaseMorphiaRepository;

public class MongoDocumentRepository extends BaseMorphiaRepository<Document, DocumentId> implements DocumentRepository {
}
