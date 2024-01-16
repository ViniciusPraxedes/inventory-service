package com.example.inventoryservice.controller;

import com.example.inventoryservice.model.Item;
import com.example.inventoryservice.model.ItemRequest;
import com.example.inventoryservice.model.ItemResponse;
import com.example.inventoryservice.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private InventoryService inventoryService;
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }


    // Being used by book service
    @Operation(hidden = true)
    @PostMapping("/create")
    public Item addItem(@Valid @RequestBody ItemRequest request){
        return inventoryService.addItem(request);
    }

    // Being used by book service
    @Operation(hidden = true)
    @DeleteMapping("/{itemCode}")
    public void deleteItem(@PathVariable String itemCode){
        inventoryService.deleteItem(itemCode);
    }

    @Operation(summary = "Get all items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, one or more parameters in the request is bad formatted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authorization required", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "The code is broken", content = @Content)})
    @GetMapping("/all")
    public List<ItemResponse> getAll(){
        return inventoryService.getAll();
    }

    @Operation(summary = "Get item by item code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, one or more parameters in the request is bad formatted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authorization required", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "The code is broken", content = @Content)})
    @GetMapping("/{itemCode}")
    public ItemResponse getItem(@PathVariable String itemCode){
        return inventoryService.getItem(itemCode);
    }

    @Operation(summary = "Change quantity of item in the inventory by item code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, one or more parameters in the request is bad formatted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authorization required", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "The code is broken", content = @Content)})
    @PutMapping("/{itemCode}/{quantity}")
    public ItemResponse changeAmount(@PathVariable String itemCode, @PathVariable Integer quantity){
        return inventoryService.changeAmount(itemCode,quantity);
    }

    @Operation(summary = "Gets all items that are in stock (All items which amount is greater than 0).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, one or more parameters in the request is bad formatted", content = @Content),
            @ApiResponse(responseCode = "401", description = "Authorization required", content = @Content),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden", content = @Content),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found", content = @Content),
            @ApiResponse(responseCode = "500", description = "The code is broken", content = @Content)})
    @GetMapping("/inStock")
    public List<ItemResponse> getAllItemsInStock(){
        return inventoryService.getAllItemsInStock();
    }

    // Being used by order service
    @Operation(hidden = true)
    @GetMapping("/isItemInStockManyItems")
    public List<ItemResponse> isItemInStock(@RequestParam List<String> itemCodes){
        return inventoryService.isInStockManyItems(itemCodes);
    }

    // Being used by order service
    @Operation(hidden = true)
    @PostMapping("/decreaseQuantityManyItems")
    public ResponseEntity<?> decreaseQuantityManyItems(@RequestParam List<String> itemCodes, @RequestParam List<Integer> quantities){
        return inventoryService.decreaseQuantityManyItems(itemCodes, quantities);
    }

}
