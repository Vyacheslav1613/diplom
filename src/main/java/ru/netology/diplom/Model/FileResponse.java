package ru.netology.diplom.Model;

public class FileResponse {
    private String filename;
    private long editedAt;
    private long size;
    private boolean error;
    private boolean selected;
    private String ext;

    public FileResponse(String filename, long editedAt, long size, boolean error, boolean selected, String ext) {
        this.filename = filename;
        this.editedAt = editedAt;
        this.size = size;
        this.error = error;
        this.selected = selected;
        this.ext = ext;
    }

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
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
