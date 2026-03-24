package org.acme;


import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth") // The base URL (e.g., localhost:8080/auth)
@Produces(MediaType.APPLICATION_JSON) // We send data back as JSON
@Consumes(MediaType.APPLICATION_JSON) // We expect to receive data as JSON
public class UserResource {

    // --- SIGN UP LOGIC ---
    @POST // Only triggers when the browser sends a "POST" request
    @Path("/signup") // The full URL is now localhost:8080/auth/signup
    @Transactional // REQUIRED for writing to a DB; it locks the row so data isn't corrupted
    public Response signup(User user) {
        // Step 1: Check if this name is already in the database
        if (User.findByName(user.username) != null) {
            // If found, stop and tell the browser "Conflict" (Error 409)
            return Response.status(Response.Status.CONFLICT).entity("User already exists").build();
        }
        
        // Step 2: If the name is free, save this new user to the H2 database
        user.persist(); 
        
        // Step 3: Tell the browser "Success" (200 OK)
        return Response.ok(user).build();
    }

    // --- LOGIN LOGIC ---
    @POST
    @Path("/login")
    public Response login(User user) {
        // Step 1: Look in the 'users' table for a row with this username
        User existingUser = User.findByName(user.username);
        
        // Step 2: Check if the user exists AND if the password matches exactly
        if (existingUser != null && existingUser.password.equals(user.password)) {
            // If they match, let them in!
            return Response.ok(existingUser).build();
        } else {
            // If wrong password or name, send "Unauthorized" (Error 401)
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid login").build();
        }
    }
}