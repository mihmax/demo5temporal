package ua.dp.maxym.demo4.inventory.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ua.dp.maxym.demo4.inventory.domain.Inventory;
import ua.dp.maxym.demo4.inventory.domain.InventoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;

    @GetMapping("/")
    public String index() {
        return String.format("""
                                     Contents of inventory:
                                     <br/><br/>
                                     %1$s
                                     <br/><br/>
                                     For REST methods, see <a href="/swagger-ui.html">Swagger UI</a>
                                     """, inventoryRepository.findAll().stream().map(Object::toString)
                                                             .collect(Collectors.joining("<br/>")));
    }

    @GetMapping("/inventory")
    public ResponseEntity<List<Inventory>> findAll() {
        return new ResponseEntity<>(inventoryRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/inventory/{goods}")
    public ResponseEntity<Inventory> findByGoods(@PathVariable("goods") String goods) {
        var inventory = inventoryRepository.findById(goods);
        return inventory.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                        .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

}
