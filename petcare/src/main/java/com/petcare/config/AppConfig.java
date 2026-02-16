package com.petcare.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.petcare.entity.User;
import com.petcare.entity.Role;
import com.petcare.entity.Status;
import com.petcare.entity.Category;
import com.petcare.repository.UserRepository;
import com.petcare.repository.CategoryRepository;

@Configuration
public class AppConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner createAdminUser(UserRepository userRepo, BCryptPasswordEncoder encoder) {
        return args -> {
            if (!userRepo.findByEmail("admin@petcare.com").isPresent()) {
                User admin = new User();
                admin.setName("Admin User");
                admin.setEmail("admin@petcare.com");
                admin.setPhone("9999999999");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole(Role.ADMIN);
                admin.setStatus(Status.ACTIVE);
                userRepo.save(admin);
                System.out.println("Admin user created: admin@petcare.com / admin123");
            }
        };
    }

    @Bean
    public CommandLineRunner createDefaultCategories(CategoryRepository categoryRepo) {
        return args -> {
            if (categoryRepo.count() == 0) {
                // Create default categories
                Category food = new Category();
                food.setName("Pet Food");
                food.setDescription("Nutritious food for your pets");
                categoryRepo.save(food);

                Category toys = new Category();
                toys.setName("Toys");
                toys.setDescription("Fun toys to keep your pets entertained");
                categoryRepo.save(toys);

                Category grooming = new Category();
                grooming.setName("Grooming");
                grooming.setDescription("Grooming supplies and accessories");
                categoryRepo.save(grooming);

                Category healthcare = new Category();
                healthcare.setName("Healthcare");
                healthcare.setDescription("Medical supplies and supplements");
                categoryRepo.save(healthcare);

                Category accessories = new Category();
                accessories.setName("Accessories");
                accessories.setDescription("Collars, leashes, and other accessories");
                categoryRepo.save(accessories);

                Category bedding = new Category();
                bedding.setName("Bedding");
                bedding.setDescription("Comfortable beds and blankets");
                categoryRepo.save(bedding);

                System.out.println("Default categories created: Pet Food, Toys, Grooming, Healthcare, Accessories, Bedding");
            }
        };
    }
}