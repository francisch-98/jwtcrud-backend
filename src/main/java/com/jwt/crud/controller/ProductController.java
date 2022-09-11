package com.jwt.crud.controller;

import com.jwt.crud.dto.Message;
import com.jwt.crud.dto.ProductDto;
import com.jwt.crud.entity.Product;
import com.jwt.crud.service.ProductService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired
    ProductService productService;

    @GetMapping("/list")
    public ResponseEntity<List<Product>> list(){
        List<Product> list = productService.list();
        return new ResponseEntity<List<Product>>(list, HttpStatus.OK);
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<Product> getById(@PathVariable("id") int id){
        if(!productService.existsById(id))
            return new ResponseEntity(new Message("no existe"), HttpStatus.NOT_FOUND);
        Product product = productService.getOne(id).get();
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @GetMapping("/detailname/{name}")
    public ResponseEntity<Product> getByName(@PathVariable("name") String name){
        if(!productService.existsByName(name))
            return new ResponseEntity(new Message("no existe"), HttpStatus.NOT_FOUND);
        Product product = productService.getByName(name).get();
        return new ResponseEntity<Product>(product, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProductDto productDto){
        if(StringUtils.isBlank(productDto.getName()))
            return new ResponseEntity(new Message("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        if(productDto.getPrice() == null || productDto.getPrice()<0)
            return new ResponseEntity(new Message("El precio debe ser mayor de 0"), HttpStatus.BAD_REQUEST);
        if(productService.existsByName(productDto.getName()))
            return new ResponseEntity(new Message("Ese nombre ya existe"), HttpStatus.BAD_REQUEST);
        Product product = new Product(productDto.getName(), productDto.getPrice());
        productService.save(product);
        return new ResponseEntity(new Message("Producto Creado"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable("id")int id, @RequestBody ProductDto productDto){
        if(!productService.existsById(id))
            return new ResponseEntity(new Message("no existe"), HttpStatus.NOT_FOUND);
        if(productService.existsByName(productDto.getName()) && productService.getByName(productDto.getName()).get().getId() != id)
            return new ResponseEntity(new Message("Ese nombre ya existe"), HttpStatus.BAD_REQUEST);
        if(StringUtils.isBlank(productDto.getName()))
            return new ResponseEntity(new Message("El nombre es obligatorio"), HttpStatus.BAD_REQUEST);
        if(productDto.getPrice()<0)
            return new ResponseEntity(new Message("El precio debe ser mayor de 0"), HttpStatus.BAD_REQUEST);

        Product product = productService.getOne(id).get();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        productService.save(product);
        return new ResponseEntity(new Message("Producto Actualizado"), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id")int id){
        if(!productService.existsById(id))
            return new ResponseEntity(new Message("no existe"), HttpStatus.NOT_FOUND);
        productService.delete(id);
        return new ResponseEntity(new Message("Producto eliminado"), HttpStatus.OK);
    }
}
