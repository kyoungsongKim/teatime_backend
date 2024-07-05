package castis.domain.filesystem.service;

import castis.domain.filesystem.entity.FileMeta;
import castis.domain.filesystem.repository.FileSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileSystemService {
    private final FileSystemRepository fileSystemRepository;

    @Value("${file-system.save-directory}")
    private String saveDirectory;

    @PostConstruct
    public void init() {
        makeBasePath(saveDirectory);
    }

    public void makeBasePath(String path) {
        File dir = new File(path);
        System.out.println("makeBasePath: " + path + " exists? " + (dir.exists() ? "Y" : "N"));
        if (!dir.exists()) {
            boolean mkdirSuccess = dir.mkdirs();
            System.out.println("makeBasePath: " + path + " mkdirSuccess? " + (mkdirSuccess ? "Y" : "N"));
        }
    }

    @Transactional
    public FileMeta getFileMeta(Long fileId) {
        return fileSystemRepository.findById(fileId).orElse(null);
    }

    public FileMeta parseFileMeta(MultipartFile file) {

        String originalName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        // 확장자 추출(ex : .png)
        String extension = originalName.substring(originalName.lastIndexOf("."));

        // uuid와 확장자 결합
        String savedName = uuid + extension;

        return FileMeta.builder()
                .name(savedName)
                .originalName(originalName)
                .path(saveDirectory + savedName)
                .size(file.getSize())
                .build();
    }

    @Transactional
    public FileMeta saveFile(MultipartFile file, FileMeta fileMeta) throws IllegalStateException, IOException {
        if (file == null || file.isEmpty() || fileMeta == null) {
            return null;
        }

        File uploadFile = new File(fileMeta.getPath());
        file.transferTo(uploadFile);

        return fileSystemRepository.save(fileMeta);
    }

    @Transactional
    public FileMeta saveFile(MultipartFile file) throws IllegalStateException, IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        FileMeta fileMeta = parseFileMeta(file);
        File uploadFile = new File(fileMeta.getPath());
        file.transferTo(uploadFile);

        return fileSystemRepository.save(fileMeta);
    }
}
