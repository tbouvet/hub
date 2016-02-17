package org.seedstack.hub.domain.model.document;

public class BinaryDocument extends Document {
    private byte[] data;

    public BinaryDocument(DocumentId documentId, String contentType) {
        super(documentId, contentType);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
