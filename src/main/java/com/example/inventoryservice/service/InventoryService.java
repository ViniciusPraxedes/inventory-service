package com.example.inventoryservice.service;

import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.model.ItemRequest;
import com.example.inventoryservice.model.ItemResponse;
import com.example.inventoryservice.repository.ItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.ls.LSInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {
    private ItemRepository itemRepository;

    public InventoryService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }


    //Adds new item to the inventory
    // Being used by book service
    public Item addItem (ItemRequest request){

        // Check if item exists in the inventory
        if (itemRepository.findByItemCode(request.getItemCode()).isPresent()){
            throw new IllegalStateException("Item already exists in the inventory");
        }

        // Create new item
        Item item = Item.builder()
                .itemCode(request.getItemCode())
                .quantity(request.getQuantity())
                .build();

        // Saves item to the database
        itemRepository.save(item);


        return item;
    }



    //Deletes an item in the inventory
    // Being used by book service
    public void deleteItem(String itemCode){

        // Check if item exists in the inventory
        if (itemRepository.findByItemCode(itemCode).isEmpty()){
            throw new IllegalStateException("Item not found");
        }

        // Delete item
        Item item = itemRepository.findByItemCode(itemCode).get();
        itemRepository.delete(item);
    }

    //Get all items from the database
    public List<ItemResponse> getAll(){

        // Get all items
        List<Item> items = itemRepository.findAll();

        // Map all items to item response
        return items.stream().map(this::mapToItemResponse).toList();
    }

    // Get one single item from the inventory
    public ItemResponse getItem(String itemCode){

        // Check if item exists in the inventory
        if (itemRepository.findByItemCode(itemCode).isEmpty()){
            return ItemResponse.builder().build();
        }

        // Get item and map it to the response dto
        Item item = itemRepository.findByItemCode(itemCode).get();
        ItemResponse response = ItemResponse.builder()
                .itemCode(item.getItemCode())
                .isInStock(item.getQuantity() > 0)
                .quantity(item.getQuantity())
                .build();
        return response;
    }

    // Change amount of item in the inventory
    public ItemResponse changeAmount(String itemCode, Integer amount){

        // Check if item exists in the inventory
        if (itemRepository.findByItemCode(itemCode).isEmpty()){
            throw new IllegalStateException("Item not found");
        }

        itemRepository.findByItemCode(itemCode).get().setQuantity(amount);
        itemRepository.save(itemRepository.findByItemCode(itemCode).get());

        ItemResponse response = mapToItemResponse(itemRepository.findByItemCode(itemCode).get());

        return response;
    }

    public List<ItemResponse> getAllItemsInStock() {
        List<Item> itemsInStock = itemRepository.findAllByQuantityGreaterThan(0);

        return itemsInStock.stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());
    }

    //Return list of items in the inventory
    //MustReturnBoolean
    // Being used by order service
    @Transactional(readOnly = true)
    public List<ItemResponse> isInStockManyItems(List<String> itemCodes){

        //Check if items exist
        for (int i = 0; i < itemCodes.size(); i++){
            if (itemRepository.findByItemCode(itemCodes.get(i)).isEmpty()){
                throw new IllegalStateException("Item with code:" + itemCodes.get(i) +" Not found");
            }
        }
        return itemRepository.findByItemCodeIn(itemCodes).stream()
                .map(Item ->
                        ItemResponse.builder()
                                .itemCode(Item.getItemCode())
                                .isInStock(Item.getQuantity() > 0)
                                .quantity(Item.getQuantity())
                                .build()).toList();
    }

    //Reduces the quantity of A LIST of item in the inventory
    // Being used by order service
    public ResponseEntity<?> decreaseQuantityManyItems(List<String> itemCodes, List<Integer> amounts){
        List<Item> items = itemRepository.findByItemCodeIn(itemCodes);

        // check if the amount - the quantity in the inventory is less than 0
        for (int i = 0; i < amounts.size(); i++){
            if (itemRepository.findByItemCode(itemCodes.get(i)).get().getQuantity()-amounts.get(i) < 0){
                System.out.println(itemCodes.get(i));
                return new ResponseEntity<>("There is not enough of the item: "+" in the inventory", HttpStatus.BAD_REQUEST);
            }
        }

        // Check if all items exist
        if (items.size() < itemCodes.size()) {
            return new ResponseEntity<>("One or more items do not exist in the inventory", HttpStatus.NOT_FOUND);
        }

        // Check if any item has zero quantity
        for (Item item : items) {
            if (item.getQuantity() <= 0) {
                return new ResponseEntity<>("Item: " + item.getItemCode() + " has zero units in the inventory", HttpStatus.BAD_REQUEST);
            }
        }
        // Reduce quantities
        for (int i = 0; i < itemCodes.size(); i++) {
            itemRepository.reduceQuantityByItemCode(itemCodes.get(i), amounts.get(i));
        }

        return new ResponseEntity<>("Quantities reduced successfully for items: " + itemCodes, HttpStatus.OK);
    }


    //Maps Item objects to ItemResponse objects
    private ItemResponse mapToItemResponse(Item item){

        boolean isInStock = false;

        if (item.getQuantity() > 0){
            isInStock = true;
        }

        return ItemResponse.builder()
                .itemCode(item.getItemCode())
                .isInStock(isInStock)
                .quantity(item.getQuantity())
                .build();
    }

}
