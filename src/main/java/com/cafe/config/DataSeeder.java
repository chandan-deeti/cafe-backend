package com.cafe.config;

import com.cafe.entity.*;
import com.cafe.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try {
            log.info("Starting data seeding...");
            seedUsers();
            seedCategoriesAndMenu();
            log.info("✅ Data seeding complete.");
        } catch (Exception e) {
            log.warn("⚠️ Data seeding skipped or partially completed: {}", e.getMessage());
        }
    }

    private void seedUsers() {
        try {
            if (userRepository.existsByEmail("admin@cafe.com")) {
                log.info("Users already seeded, skipping...");
                return;
            }

            userRepository.save(User.builder()
                    .fullName("Cafe Admin")
                    .email("admin@cafe.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .enabled(true)
                    .build());

            userRepository.save(User.builder()
                    .fullName("Staff Member")
                    .email("staff@cafe.com")
                    .password(passwordEncoder.encode("staff123"))
                    .role(Role.ROLE_STAFF)
                    .enabled(true)
                    .build());

            userRepository.save(User.builder()
                    .fullName("Test Customer")
                    .email("customer@cafe.com")
                    .password(passwordEncoder.encode("customer123"))
                    .role(Role.ROLE_CUSTOMER)
                    .enabled(true)
                    .build());

            log.info("✅ Users seeded: admin@cafe.com / staff@cafe.com / customer@cafe.com");
        } catch (Exception e) {
            log.error("Error seeding users: {}", e.getMessage(), e);
        }
    }

    private void seedCategoriesAndMenu() {
        try {
            if (categoryRepository.existsByName("Hot Drinks")) {
                log.info("Categories already seeded, skipping...");
                return;
            }

            // Categories
            Category hot = categoryRepository.save(Category.builder()
                    .name("Hot Drinks").description("Warm beverages").active(true).build());
            Category cold = categoryRepository.save(Category.builder()
                    .name("Cold Drinks").description("Chilled beverages").active(true).build());
            Category food = categoryRepository.save(Category.builder()
                    .name("Food").description("Snacks and meals").active(true).build());
            Category desserts = categoryRepository.save(Category.builder()
                    .name("Desserts").description("Sweet treats").active(true).build());

            // Menu items with inventory
            createItem("Espresso", "Rich Italian espresso", new BigDecimal("2.50"), hot, 100, 20);
            createItem("Cappuccino", "Espresso with steamed milk foam", new BigDecimal("3.50"), hot, 80, 15);
            createItem("Latte", "Espresso with lots of steamed milk", new BigDecimal("4.00"), hot, 80, 15);
            createItem("Americano", "Espresso diluted with hot water", new BigDecimal("3.00"), hot, 90, 20);

            createItem("Iced Latte", "Chilled latte over ice", new BigDecimal("4.50"), cold, 60, 10);
            createItem("Cold Brew", "Slow-steeped cold coffee", new BigDecimal("5.00"), cold, 40, 8);
            createItem("Iced Matcha", "Premium matcha with cold milk", new BigDecimal("5.50"), cold, 50, 10);

            createItem("Croissant", "Buttery flaky croissant", new BigDecimal("3.00"), food, 30, 5);
            createItem("Club Sandwich", "Triple-decker toasted sandwich", new BigDecimal("8.50"), food, 20, 5);
            createItem("Avocado Toast", "Smashed avocado on sourdough", new BigDecimal("9.00"), food, 25, 5);

            createItem("Cheesecake", "Classic New York cheesecake", new BigDecimal("6.00"), desserts, 15, 3);
            createItem("Brownie", "Fudgy chocolate brownie", new BigDecimal("4.00"), desserts, 20, 5);

            log.info("✅ Menu items seeded with inventory.");
        } catch (Exception e) {
            log.error("Error seeding categories and menu: {}", e.getMessage(), e);
        }
    }

    private void createItem(String name, String desc, BigDecimal price,
                            Category category, int stock, int lowThreshold) {
        MenuItem item = menuItemRepository.save(MenuItem.builder()
                .name(name)
                .description(desc)
                .price(price)
                .available(stock > 0)
                .active(true)
                .category(category)
                .build());

        inventoryItemRepository.save(InventoryItem.builder()
                .menuItem(item)
                .quantity(stock)
                .lowStockThreshold(lowThreshold)
                .unit("pcs")
                .build());
    }
}
