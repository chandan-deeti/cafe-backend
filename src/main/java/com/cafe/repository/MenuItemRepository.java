package com.cafe.repository;

import com.cafe.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByActiveTrueAndAvailableTrue();
    List<MenuItem> findByCategoryIdAndActiveTrue(Long categoryId);
    List<MenuItem> findByActiveTrue();

    @Query("SELECT m FROM MenuItem m WHERE m.active = true AND " +
           "(LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<MenuItem> searchByKeyword(String keyword);
}
