package com.etkinlik.online_etkinlik.dto;

import java.time.LocalDateTime;

public class EventDto {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private String categoryName;
    private String locationName;
    private String locationCity;

    // Entity'den DTO'ya dönüştürücü (Mapper işlevi görür)
    public static EventDto fromEntity(com.etkinlik.online_etkinlik.model.Event event) {
        EventDto dto = new EventDto();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setStartDate(event.getStartDate());
        
        if (event.getCategory() != null) {
            dto.setCategoryName(event.getCategory().getName());
        }
        if (event.getLocation() != null) {
            dto.setLocationName(event.getLocation().getName());
            dto.setLocationCity(event.getLocation().getCity());
        }
        return dto;
    }

    // Getter ve Setter'lar
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getLocationCity() { return locationCity; }
    public void setLocationCity(String locationCity) { this.locationCity = locationCity; }
}