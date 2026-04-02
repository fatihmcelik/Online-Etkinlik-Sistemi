package com.etkinlik.online_etkinlik.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "speakers")
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // YENİ: Unvan — Dr., Prof., Uzm. vb.
    private String title;

    @Column(columnDefinition = "TEXT")
    private String bio;

    // YENİ: Konuşmacı fotoğrafı
    private String photoUrl;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "event_speakers",
        joinColumns = @JoinColumn(name = "speaker_id"),
        inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    private Set<Event> events = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
    public Set<Event> getEvents() { return events; }
    public void setEvents(Set<Event> events) { this.events = events; }
}