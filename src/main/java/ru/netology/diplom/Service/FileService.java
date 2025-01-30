package ru.netology.diplom.Service;


import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.diplom.Model.FileEntity;
import ru.netology.diplom.Repository.FileRepository;

import java.io.IOException;
import java.util.List;

@Service
public class FileService {
    private final FileRepository fileRepository;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public FileEntity saveFile(String username, String filename, MultipartFile file) throws IOException {
        byte[] fileData = file.getBytes();
        FileEntity fileEntity = new FileEntity();
        fileEntity.setUsername(username);
        fileEntity.setFilename(filename);
        fileEntity.setEditedAt(System.currentTimeMillis());
        fileEntity.setSize(file.getSize());
        fileEntity.setIsError(false);
        fileEntity.setIsSelected(false);
        fileEntity.setExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
        fileEntity.setFileData(fileData);
        return fileRepository.save(fileEntity);
    }

    public FileEntity findByFilename(String filename) {
        return fileRepository.findByFilename(filename);
    }

    public void deleteFile(FileEntity fileEntity) {
        fileRepository.delete(fileEntity);
    }

    public FileEntity findByFilenameAndUsername(String filename, String username) {
        return fileRepository.findByFilenameAndUsername(filename, username);
    }

    public List<FileEntity> findAllByUsername(String username) {
        return fileRepository.findAllByUsername(username);
    }

    public void updateFile(FileEntity fileEntity) {
        fileRepository.save(fileEntity);
    }

}

