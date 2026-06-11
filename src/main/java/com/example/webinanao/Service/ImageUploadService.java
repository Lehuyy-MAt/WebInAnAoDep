package com.example.webinanao.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "products",
                            "use_filename", true,
                            "unique_filename", true
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload ảnh thất bại: " + e.getMessage());
        }
    }

    public void deleteImage(String publicId) {
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException("Xóa ảnh thất bại: " + e.getMessage());
        }
    }

    public String getPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("/products/")) {
            return null;
        }
        try {
            String[] parts = imageUrl.split("/products/");
            String filename = parts[1];
            return "products/" + filename.substring(0, filename.lastIndexOf("."));
        } catch (Exception e) {
            return null;
        }
    }
}