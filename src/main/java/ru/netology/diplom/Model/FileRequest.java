package ru.netology.diplom.Model;

public class FileRequest {
    private String filename;
    private long editedAt;
    private long size;
    private boolean isError;
    private boolean isSelected;
    private String extension;
    private byte[] fileData;

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(long editedAt) {
        this.editedAt = editedAt;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}

