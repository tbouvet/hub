/**
 * Copyright (c) 2015-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.hub.domain.model.document;

import org.seedstack.business.domain.BaseFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Optional;

public class DocumentFactoryImpl extends BaseFactory<Document> implements DocumentFactory {
    public static final Charset TEXT_CHARSET = Charset.forName("UTF-8");
    public static final int MAX_DOCUMENT_SIZE = 2 * 1024 * 1024;

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
    public Document createTextDocument(DocumentId documentId, TextFormat textFormat, File file) {
        TextDocument textDocument = new TextDocument(documentId, textFormat, TEXT_CHARSET);
        try {
            textDocument.setText(new String(Files.readAllBytes(file.toPath()), TEXT_CHARSET));
        } catch (IOException e) {
            throw new DocumentException("Cannot read text file " + file.getAbsolutePath());
        }
        return textDocument;
    }

    @Override
    public BinaryDocument createBinaryDocument(DocumentId documentId, File file) {
        BinaryDocument binaryDocument = new BinaryDocument(documentId, "octet/stream");
        try {
            binaryDocument.setData(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            throw new DocumentException("Cannot read binary file " + file.getAbsolutePath());
        }
        return binaryDocument;
    }

    private Optional<TextFormat> detectTextFormat(File file) {
        for (TextFormat textFormat : TextFormat.values()) {
            for (String extension : textFormat.getValidExtensions()) {
                if (file.getName().endsWith(extension)) {
                    return Optional.of(textFormat);
                }
            }
        }

        return Optional.empty();
    }
}
