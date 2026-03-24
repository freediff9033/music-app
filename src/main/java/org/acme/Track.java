package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Track extends PanacheEntity {
    public String title;
    public String artist;
    public String audioUrl;
}