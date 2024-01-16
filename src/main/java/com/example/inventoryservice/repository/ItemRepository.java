package com.example.inventoryservice.repository;

import com.example.inventoryservice.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
@Transactional
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByItemCodeIn(List<String> itemCodes);

    Optional<Item> findByItemCode(String itemCode);

    List<Item> findAllByQuantityGreaterThan(Integer quantity);

    @Modifying
    @Query("UPDATE Item item SET item.quantity = item.quantity - :amount WHERE item.itemCode = :itemCode")
    void reduceQuantityByItemCode(@Param("itemCode") String itemCode, @Param("amount") Integer amount);


}
