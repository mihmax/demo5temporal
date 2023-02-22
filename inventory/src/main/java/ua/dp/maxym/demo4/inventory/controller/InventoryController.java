package ua.dp.maxym.demo4.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.dp.maxym.demo4.inventory.domain.InventoryRepository;

import java.util.stream.Collectors;

@RestController
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping({"/", "/list"})
    public String list() {
        return String.format("""
            Contents of inventory:
            <br/><br/> 
            %1$s
            """, inventoryRepository.findAll().stream().map(Object::toString)
                .collect(Collectors.joining("<br/>")));
    }

}
