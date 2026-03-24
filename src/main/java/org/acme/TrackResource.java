package org.acme;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/tracks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TrackResource {

    @GET
    public List<Track> allTracks() {
        return Track.listAll();
    }

    @POST
    @Transactional
    public void addTrack(Track track) {
        track.persist();
    }
}