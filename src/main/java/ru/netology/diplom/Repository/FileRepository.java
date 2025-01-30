package ru.netology.diplom.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import ru.netology.diplom.Model.FileEntity;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<FileEntity, Long> {

    List<FileEntity> findAllByUsername(@Param("username") String username);

    Optional<FileEntity> findById(@Param("id") Long id);

    FileEntity findByFilename(@Param("filename") String filename);

    List<FileEntity> findAllByExtension(@Param("extension") String extension);

    FileEntity findLatestFileByUsername(@Param("username") String username);
}
