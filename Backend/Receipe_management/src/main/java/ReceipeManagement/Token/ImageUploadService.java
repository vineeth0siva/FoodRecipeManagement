package ReceipeManagement.Token;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ImageUploadService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile imageFile) throws IOException {
        // Validate image size (optional)
        long maxFileSize = 5 * 1024 * 1024; // 5MB

        if (imageFile.getSize() > maxFileSize) {
            throw new IOException("Image size exceeds the maximum limit of 5MB");
        }

        // Generate a unique public ID for the image
        String publicId = "recipe_" + UUID.randomUUID().toString();

        // Upload the image to Cloudinary
        Map<String, Object> uploadOptions = new HashMap<>();

        uploadOptions.put("public_id", publicId);

        uploadOptions.put("folder", "recipes");

        // Perform the upload
        Map<String, Object> uploadResult = cloudinary.uploader().upload(
                imageFile.getBytes(),
                uploadOptions
        );

        // Get the image URL
        String imageUrl = (String) uploadResult.get("url");

        return imageUrl;
    }
}
