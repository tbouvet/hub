/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.infra.file;

import org.seedstack.business.domain.BaseFactory;
import org.seedstack.hub.domain.model.component.Component;
import org.seedstack.hub.domain.model.component.Description;
import org.seedstack.hub.domain.model.document.BinaryDocument;
import org.seedstack.hub.domain.model.document.Document;
import org.seedstack.hub.domain.model.document.DocumentException;
import org.seedstack.hub.domain.model.document.DocumentFactory;
import org.seedstack.hub.domain.model.document.DocumentId;
import org.seedstack.hub.domain.model.document.TextDocument;
import org.seedstack.hub.domain.model.document.TextFormat;
import org.seedstack.hub.domain.model.document.WikiDocument;
import org.seedstack.hub.domain.model.user.UserId;
import org.seedstack.hub.domain.services.text.TextService;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

class DocumentFactoryImpl extends BaseFactory<Document> implements DocumentFactory {
    private static final Charset TEXT_CHARSET = Charset.forName("UTF-8");
    private static final int MAX_DOCUMENT_SIZE = 2 * 1024 * 1024;

    private final MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
    @Inject
    private TextService textService;

    @Override
    public Document createDocument(DocumentId documentId, File directory) {
        File file = new File(directory, documentId.getPath());

        if (!file.canRead()) {
            throw new DocumentException("Cannot read file " + file.getAbsolutePath());
        }

        if (file.length() > MAX_DOCUMENT_SIZE) {
            throw new DocumentException("File " + file.getAbsolutePath() + " exceeds allowed max document size (" + MAX_DOCUMENT_SIZE / 1024 + " kb)");
        }

        Optional<TextFormat> textFormat = detectTextFormat(file);
        if (textFormat.isPresent()) {
            return createTextDocument(documentId, textFormat.get(), file);
        } else {
            return createBinaryDocument(documentId, file);
        }
    }

    @Override
    public TextDocument createTextDocument(DocumentId documentId, TextFormat textFormat, File file) {
        TextDocument textDocument = new TextDocument(documentId, textFormat, TEXT_CHARSET);
        try {
            textDocument.setText(new String(Files.readAllBytes(file.toPath()), TEXT_CHARSET));
        } catch (IOException e) {
            throw new DocumentException("Cannot read text file " + file.getAbsolutePath());
        }
        return textDocument;
    }

    @Override
    public TextDocument createTextDocument(DocumentId documentId, TextFormat textFormat, String body) {
        TextDocument textDocument = new TextDocument(documentId, textFormat, TEXT_CHARSET);
        textDocument.setText(body);
        return textDocument;
    }

    @Override
    public WikiDocument createWikiDocument(DocumentId documentId, String body, UserId author, String message) {
        WikiDocument wikiDocument = new WikiDocument(documentId);
        wikiDocument.addRevision(body, author, message);
        return wikiDocument;
    }

    @Override
    public BinaryDocument createBinaryDocument(DocumentId documentId, File file) {
        BinaryDocument binaryDocument = new BinaryDocument(documentId, detectContentType(file));
        try {
            binaryDocument.setData(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new DocumentException("Cannot read binary file " + file.getAbsolutePath());
        }
        return binaryDocument;
    }

    @Override
    public BinaryDocument createBinaryDocument(DocumentId documentId, String fileName, byte[] content) {
        BinaryDocument binaryDocument = new BinaryDocument(documentId, detectContentType(fileName));
        binaryDocument.setData(content);
        return binaryDocument;
    }

    @Override
    public Stream<Document> createDocuments(Component component, File directory) {
        Set<DocumentId> documents = new HashSet<>();

        Description description = component.getDescription();
        if (description != null) {
            if (description.getReadme() != null) {
                documents.add(description.getReadme());
            }
            if (description.getIcon() != null) {
            documents.add(description.getIcon());
            }
            if (description.getImages() != null) {
                documents.addAll(description.getImages());
            }
        }
        documents.addAll(component.getDocs());

        return documents.stream()
                .map(documentId -> buildDocumentStream(documentId, directory))
                .flatMap(Function.identity());
    }

    private Stream<Document> buildDocumentStream(DocumentId documentId, File directory) {
        Document document = createDocument(documentId, directory);
        if (document instanceof TextDocument) {
            return Stream.concat(
                    Stream.of(document),
                    textService.findReferences((TextDocument) document).stream().map(referenceDocumentId -> createDocument(referenceDocumentId, directory))
            );
        } else {
            return Stream.of(document);
        }
    }

    private Optional<TextFormat> detectTextFormat(File file) {
        for (TextFormat textFormat : TextFormat.values()) {
            for (String extension : textFormat.validExtensions()) {
                if (file.getName().endsWith(extension)) {
                    return Optional.of(textFormat);
                }
            }
        }

        return Optional.empty();
    }

    private String detectContentType(File file) {
        String contentType = mimetypesFileTypeMap.getContentType(file);
        return contentType == null || contentType.isEmpty() ? "application/octet-stream" : contentType;
    }

    private String detectContentType(String fileName) {
        String contentType = mimetypesFileTypeMap.getContentType(fileName);
        return contentType == null || contentType.isEmpty() ? "application/octet-stream" : contentType;
    }
}
