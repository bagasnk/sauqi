package com.project.sauqi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sauqi.dao.CartRepo;
import com.project.sauqi.dao.ProductRepo;
import com.project.sauqi.dao.UserRepo;
import com.project.sauqi.entity.Cart;
import com.project.sauqi.entity.Product;
import com.project.sauqi.entity.User;

@RestController
@RequestMapping("/carts")
@CrossOrigin
public class CartController {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
    private ProductRepo productRepo;

    @Autowired
    private CartRepo cartRepo;

    @GetMapping
    public Iterable<Cart> getAllCart(){
        return cartRepo.findAll();
    }
    
    @GetMapping("/user/{userId}")
    public Iterable<Cart> getUserCarts(@PathVariable int userId){
        return cartRepo.findByUserId(userId);
    }
    
    @PostMapping("/addCart/{userId}/{productId}")
    public Cart addToCart(@RequestBody Cart cart, @PathVariable int userId, @PathVariable int productId){
        Product findProduct = productRepo.findById(productId).get();
        User findUser = userRepo.findById(userId).get();
        
        findProduct.setStock(findProduct.getStock() - cart.getQuantity()); //<<<<<< baru stock user
        cart.setProduct(findProduct);
        cart.setUser(findUser);
        return cartRepo.save(cart);
    }
    
    @PutMapping("/updateQty/{cartId}/{productId}")
    public Cart updateCartQty (@PathVariable int cartId, @PathVariable int productId){
        Cart findCart = cartRepo.findById(cartId).get();
        Product findProduct = productRepo.findById(productId).get();
        findCart.setQuantity(findCart.getQuantity()+1);
        findProduct.setStock(findProduct.getStock()-1);
        return cartRepo.save(findCart);
    }
    
    @DeleteMapping("/{cartId}")
	public void deleteCart(@PathVariable int cartId) {
		Cart findCart = cartRepo.findById(cartId).get();
		cartRepo.deleteById(cartId);
	}
    
    @DeleteMapping("/{cartId}/{productId}")
    public void deleteCartProduct(@PathVariable int cartId,@PathVariable int productId) {
    	 Cart findCart = cartRepo.findById(cartId).get();
         Product findProduct = productRepo.findById(productId).get();
         findProduct.setStock(findProduct.getStock() + findCart.getQuantity());
         productRepo.save(findProduct);
         cartRepo.deleteById(cartId);
    }
    
}
