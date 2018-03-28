package service;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void init();

    String store(MultipartFile file);

    void storeMultipleFiles(List<MultipartFile> fileList, String filesLocationAsString);

    String createAndStore(String inputAreaContent);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void createDir(String dirAsString);

    void createMultipleDirs(List<String> dirAsString);

    void deleteAll();

}