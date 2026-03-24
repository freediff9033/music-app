package org.acme;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Song extends PanacheEntity {
    public String title;
    public String artist;
    public String genre;
    public String uploadedBy;
    public String filePath; // <--- ADD THIS LINE AND SAVE
}