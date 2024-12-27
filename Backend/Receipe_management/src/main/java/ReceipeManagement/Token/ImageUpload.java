package ReceipeManagement.Token;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;
@Service
public class ImageUpload {
    private final Cloudinary cloudinary;

    // Constructor injection for Cloudinary
    public ImageUpload(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadImage(MultipartFile imageFile) throws IOException {
        // Upload the file to Cloudinary and get the response
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                imageFile.getBytes(),
                ObjectUtils.asMap("public_id", UUID.randomUUID().toString())
        );

        // Extract and return the URL from the response map
        return (String) uploadResult.get("url");
    }
}
