package org.acme;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Path("/songs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class SongResource {

    // Initialize Cloudinary with your credentials
    // Tip: In a real cloud app, you'd use System.getenv("CLOUDINARY_URL") for security!
    Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
        "cloud_name", System.getenv("df5zzuedx"), 
        "api_key",System.getenv("176433637264182"),
        "api_secret", System.getenv("bblSeKnVNYZWauqb8FUXPKePgTI"),
        "secure", true
    ));

    @GET
    public List<Song> getAllSongs() {
        // Fetches all songs from the Cloud Database
        return Song.listAll();
    }

    @POST
    @Path("/upload")
    @Transactional
    public Response upload(@MultipartForm SongFormData formData) {
        File tempFile = null;
        try {
            // 1. Check if file exists
            if (formData.fileData == null) {
                return Response.status(Response.Status.BAD_REQUEST).entity("No file uploaded").build();
            }

            // 2. Create a temporary file on the Cloud Server's RAM
            tempFile = File.createTempFile("upload-", ".mp3");
            Files.copy(formData.fileData, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // 3. Upload to Cloudinary
            // We use resource_type: "video" because Cloudinary categorizes audio under video
            Map uploadResult = cloudinary.uploader().upload(tempFile, 
                ObjectUtils.asMap("resource_type", "video"));

            // 4. Get the secure HTTPS URL from the Cloud
            String cloudUrl = (String) uploadResult.get("secure_url");

            // 5. Save the metadata to the Database
            Song song = new Song();
            song.title = (formData.title != null) ? formData.title : "Unknown Title";
            song.artist = (formData.artist != null) ? formData.artist : "Unknown Artist";
            song.genre = (formData.genre != null) ? formData.genre : "Unknown Genre";
            
            // This is now a web link (https://res.cloudinary.com/...), not a local path!
            song.filePath = cloudUrl; 
            
            song.persist();

            return Response.ok(song).build();

        } catch (Exception e) {
            e.printStackTrace();
            return Response.serverError()
                .entity("Cloud Upload Failed: " + e.getMessage())
                .build();
        } finally {
            // Clean up the temporary file to save server memory
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}