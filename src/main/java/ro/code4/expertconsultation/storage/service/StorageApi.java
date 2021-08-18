package ro.code4.expertconsultation.storage.service;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Objects;

public interface StorageApi {

    static String resolveUniqueName(final MultipartFile document) {
        return new Date().getTime() + StringUtils.cleanPath(Objects.requireNonNull(document.getOriginalFilename()));
    }

    String storeFile(final MultipartFile document) throws Exception;

    byte[] loadFile(String documentURI);

    void deleteFile(String documentURI);
}
