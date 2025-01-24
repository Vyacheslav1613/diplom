package ru.netology.diplom.Model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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

}
