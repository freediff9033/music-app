package org.acme;

import jakarta.ws.rs.FormParam;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;

public class SongFormData { // <--- MUST be exactly SongFormData
    @FormParam("file")
    public InputStream fileData;

    @FormParam("fileName")
    public String fileName;

    @FormParam("title")
    public String title;

    @FormParam("artist")
    public String artist;

    @FormParam("genre")
    public String genre;
}