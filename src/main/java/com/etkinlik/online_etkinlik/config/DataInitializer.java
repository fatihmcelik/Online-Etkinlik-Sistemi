package com.etkinlik.online_etkinlik.config;

import com.etkinlik.online_etkinlik.model.Role;
import com.etkinlik.online_etkinlik.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    
    @Override
    public void run(String... args) throws Exception {
        
        
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
            System.out.println("ROLE_USER veritabanına otomatik eklendi.");
        }

        
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
            System.out.println("ROLE_ADMIN veritabanına otomatik eklendi.");
        }
    }
}