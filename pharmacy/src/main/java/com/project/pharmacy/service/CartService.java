package com.project.pharmacy.service;

import com.project.pharmacy.dto.request.AddToCartRequest;
import com.project.pharmacy.dto.request.DeleteCartItemRequest;
import com.project.pharmacy.dto.request.UpdateCartRequest;
import com.project.pharmacy.dto.response.CartItemResponse;
import com.project.pharmacy.dto.response.CartResponse;
import com.project.pharmacy.entity.*;
import com.project.pharmacy.exception.AppException;
import com.project.pharmacy.exception.ErrorCode;
import com.project.pharmacy.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService {
    ProductRepository productRepository;

    UserRepository userRepository;

    PriceRepository priceRepository;

    UnitRepository unitRepository;

    CartRepository cartRepository;

    CartItemRepository cartItemRepository;

    ImageRepository imageRepository;

    public void addToCartForUser(AddToCartRequest request){
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()-> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

        Price price = priceRepository.findByProductAndUnit(product, unit);

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = user.getCart();

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setTotalPrice(0);
            cartRepository.save(cart);
        }

        List<CartItem> cartItems = cart.getCartItems();

        if (cartItems == null) {
            cartItems = new ArrayList<>();
            cart.setCartItems(cartItems);
        }

        CartItem cartItem = cartItems.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .orElse(new CartItem());

        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setUnit(unit);
        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItem.setPrice(cartItem.getPrice() + price.getPrice()*request.getQuantity());
        cartItemRepository.save(cartItem);

        cart.setTotalPrice(cart.getTotalPrice() + price.getPrice()*request.getQuantity());
        cartRepository.save(cart);
    }

   public CartResponse getCartForUser(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = user.getCart();
        if(cart == null || cart.getCartItems().isEmpty()){
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        AtomicInteger totalPrice = new AtomicInteger();
        List<CartItemResponse> cartItemResponses = cart.getCartItems().stream()
                .map(cartItem -> {
                    CartItemResponse cartItemResponse = new CartItemResponse();
                    cartItemResponse.setId(cartItem.getId());

                    Product product = cartItem.getProduct();
                    cartItemResponse.setProductId(product.getId());
                    cartItemResponse.setProductName(product.getName());

                    Unit unit = cartItem.getUnit();
                    cartItemResponse.setUnitId(unit.getId());
                    cartItemResponse.setUnitName(unit.getName());

                    Image firstImage = imageRepository.findFirstByProductId(product.getId());
                    String url = firstImage != null ? firstImage.getSource() : null;

                    cartItemResponse.setImage(url);

                    cartItemResponse.setQuantity(cartItem.getQuantity());
                    int price = cartItem.getQuantity() * cartItem.getPrice();
                    cartItemResponse.setPrice(price);

                    totalPrice.addAndGet(cartItemResponse.getPrice());

                    return cartItemResponse;
                })
                .toList();

        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartItemResponses(cartItemResponses);
        cartResponse.setTotalPrice(totalPrice);

        return cartResponse;
    }

    public void updateCartForUser(UpdateCartRequest request){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = user.getCart();

        if(cart == null || cart.getCartItems().isEmpty()){
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(()->new AppException(ErrorCode.UNIT_NOT_FOUND));

        Price price = priceRepository.findByProductAndUnit(product, unit);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().equals(product) && item.getUnit().equals(unit))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItem.setPrice(cartItem.getPrice() + price.getPrice() * request.getQuantity());

        cartItemRepository.save(cartItem);

        if(cartItem.getQuantity() <= 0){
            cartItemRepository.delete(cartItem);
            cart.getCartItems().remove(cartItem);
        }

        cart.setTotalPrice(cart.getTotalPrice() + price.getPrice()*request.getQuantity());

        if(cart.getTotalPrice() + price.getPrice()*request.getQuantity() <= 0){
            cart.setTotalPrice(0);
        }
        cartRepository.save(cart);
    }

    public void deleteCartItemForUser(DeleteCartItemRequest request){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

        Cart cart = user.getCart();
        if(cart == null || cart.getCartItems().isEmpty()){
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().equals(product) && item.getUnit().equals(unit))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItemRepository.delete(cartItem);

        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getPrice());
        cart.getCartItems().remove(cartItem);
        cartRepository.save(cart);
    }
}
