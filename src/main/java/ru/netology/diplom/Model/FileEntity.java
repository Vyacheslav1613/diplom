package ru.netology.diplom.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "files", schema = "netology")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "filename", nullable = false, length = 255)
    private String filename;

    @Column(name = "edited_at", nullable = false)
    private Long editedAt;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "is_error", nullable = false)
    private Boolean isError;

    @Column(name = "is_selected", nullable = false)
    private Boolean isSelected;

    @Column(name = "extension", nullable = false, length = 10)
    private String extension;

    @Column(name = "file_data")
    private byte[] fileData;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(Long editedAt) {
        this.editedAt = editedAt;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Boolean getIsError() {
        return isError;
    }

    public void setIsError(Boolean isError) {
        this.isError = isError;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
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

    public FileEntity() {
    }

}
