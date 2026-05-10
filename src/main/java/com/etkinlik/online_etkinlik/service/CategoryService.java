package com.etkinlik.online_etkinlik.service;

import com.etkinlik.online_etkinlik.model.Category;
import com.etkinlik.online_etkinlik.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Kategori bulunamadı"));
    }

    @Transactional
    public void save(Category category) {
        // Otomatik slug (URL dostu isim) oluşturma
        if (category.getName() != null) {
            category.setSlug(category.getName().toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-]", ""));
        }
        categoryRepository.save(category);
    }

    @Transactional
    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Bu kategoriye ait etkinlikler olduğu için silinemez!");
        }
    }
}