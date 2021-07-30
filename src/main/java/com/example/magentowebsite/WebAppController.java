package com.example.magentowebsite;

import java.util.List;

import com.github.chen0040.magento.MagentoClient;
import com.github.chen0040.magento.models.Cart;
import com.github.chen0040.magento.models.CartItem;
import com.github.chen0040.magento.models.Category;
import com.github.chen0040.magento.models.CategoryProduct;
import com.github.chen0040.magento.models.Product;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class WebAppController {
    String magento_site_url = "https://test-store2.flocash.com";

    @GetMapping("/greeting")
	public String displayServiceCategories(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
		model.addAttribute("name", name);
		return "greeting";
	}

    @GetMapping("/")
    public String homePage(){
        return "homepage";
    }

    @GetMapping("/categories")
    public String displayCategories(Model model) {
        MagentoClient client = new MagentoClient(magento_site_url);

        // list categories
        Category default_category = client.categories().all();
        List <Category> categories = default_category.getChildren_data();
        model.addAttribute("categories", categories);
        return "categories";
    }

    @GetMapping("/category")
    public String getCategoryProducts(@RequestParam(name="id", required=true) String id, Model model){
        MagentoClient client = new MagentoClient(magento_site_url);
        long categoryId = Long.parseLong( id );
        Category category = client.categories().getCategoryByIdClean(categoryId);
        List<CategoryProduct> products = client.categories().getProductsInCategory(categoryId);
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        return "category_products";
    }

    @GetMapping("/product")
    public String getProductDetails(@RequestParam(name="sku", required=true) String sku, Model model) {
        MagentoClient client = new MagentoClient(magento_site_url);
        Product product = client.products().getProductBySku(sku);
        List<String> imageUrls = client.media().getProductMediaAbsoluteUrls(sku);
        // List<String> imageUrls = client.media().getProductMediaRelativeUrls(sku);
        model.addAttribute("product", product);
        model.addAttribute("imageUrls", imageUrls);
        // System.out.println(product.getPrice());
        // System.out.println(imageUrls);
        return "product_details";
    }

    @GetMapping("/add-to-cart")
    public RedirectView addToCart(@RequestParam(name="sku", required=true) String sku, RedirectAttributes attributes){
        MagentoClient client = new MagentoClient(magento_site_url);
        String cartId = client.guestCart().newCart();

        CartItem item = new CartItem();
        item.setQty(1);
        item.setSku(sku);

        // Add new item to shopping cart
        client.guestCart().addItemToCart(cartId, item);
        attributes.addAttribute("id", cartId);
        // System.out.println(cart.getItems());
        return new RedirectView("cart");
    }

    @GetMapping("/cart")
    public String getCart(@RequestParam(name="id", required=true) String id, Model model){
        MagentoClient client = new MagentoClient(magento_site_url);
        Cart cart = client.guestCart().getCart(id);
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cart.getItems());
        return "cart";
    }

}
