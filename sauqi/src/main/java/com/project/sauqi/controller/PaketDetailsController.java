package com.project.sauqi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.sauqi.dao.PaketDetailsRepo;
import com.project.sauqi.dao.PaketRepo;
import com.project.sauqi.dao.ProductRepo;
import com.project.sauqi.entity.Paket;
import com.project.sauqi.entity.PaketDetails;
import com.project.sauqi.entity.Product;

@RestController
@RequestMapping("/paketDetails")
@CrossOrigin
public class PaketDetailsController {
	
	@Autowired
	private ProductRepo productRepo;
	
	@Autowired
    private PaketDetailsRepo paketDetailsRepo;

    @Autowired
    private PaketRepo paketRepo;
	@PostMapping("/addPaketDetails/{paketId}/{productId}")
	public Paket addPaketDetails(@RequestBody PaketDetails paketDetails, @PathVariable int paketId, @PathVariable int productId) {
        Paket findPaket= paketRepo.findById(paketId).get();
        Product findProduct = productRepo.findById(productId).get();
        paketDetails.setPaket(findPaket);
        paketDetails.setProducts(findProduct);
        
        if (findPaket.getPaketDetails().isEmpty()) {
			findPaket.setStock(findProduct.getStock());
			System.out.println(findProduct.getStock());
			
			
		}else{       	
        	findPaket.getPaketDetails().forEach(val ->{
        		if (findPaket.getStock() > val.getProducts().getStock()) {
        			System.out.println(findProduct.getStock());
        			
        		}else {
        			findPaket.setStock(findPaket.getStock());
        			System.out.println(findProduct.getStock());
        		}
        	});
        }
        paketRepo.save(findPaket);
        findPaket.setStock(findPaket.getStock());
        paketDetailsRepo.save(paketDetails);
        
        return paketRepo.save(findPaket);
    }
	
	@GetMapping
    public Iterable<PaketDetails> getAllPaketDetails(){
        return paketDetailsRepo.findAll();
    }
}
