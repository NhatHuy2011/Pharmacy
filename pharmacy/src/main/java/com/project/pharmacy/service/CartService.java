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
import com.project.pharmacy.utils.CartItemTemporary;
import com.project.pharmacy.utils.CartTemporary;
import jakarta.servlet.http.HttpSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
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
            return null;
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
                    cartItemResponse.setPrice(cartItem.getQuantity() * cartItem.getPrice());

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

        if(cartItem.getQuantity() <= 0){
            cartItemRepository.delete(cartItem);
            cart.getCartItems().remove(cartItem);
        }

        cart.setTotalPrice(cart.getTotalPrice() + price.getPrice()*request.getQuantity());

        if(cart.getTotalPrice() <= 0){
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
        if(cart.getTotalPrice() <= 0){
            cart.setTotalPrice(0);
        }
        cart.getCartItems().remove(cartItem);
        cartRepository.save(cart);
    }

    public void addToCartForGuest(AddToCartRequest request, HttpSession session) {
        CartTemporary temporaryCart = (CartTemporary) session.getAttribute("Cart");

        if (temporaryCart == null) {
            temporaryCart = new CartTemporary();
            session.setAttribute("Cart", temporaryCart);
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

        Price price = priceRepository.findByProductAndUnit(product, unit);

        List<CartItemTemporary> cartItems = temporaryCart.getCartItems();

        CartItemTemporary cartItemTemporary = cartItems.stream()
                .filter(item -> item.getProductId().equals(product.getId()) && item.getUnitId().equals(unit.getId()))
                .findFirst()
                .orElse(null);

        if (cartItemTemporary == null) {
            cartItemTemporary = new CartItemTemporary();
            cartItemTemporary.setProductId(product.getId());
            cartItemTemporary.setProductName(product.getName());
            cartItemTemporary.setUnitId(unit.getId());
            cartItemTemporary.setUnitName(unit.getName());
            cartItemTemporary.setPrice(price.getPrice() * request.getQuantity());
            cartItemTemporary.setQuantity(request.getQuantity());
            cartItems.add(cartItemTemporary);
        } else {
            cartItemTemporary.setQuantity(cartItemTemporary.getQuantity() + request.getQuantity());
            cartItemTemporary.setPrice(cartItemTemporary.getPrice() + price.getPrice() * request.getQuantity());
        }

        temporaryCart.setTotalPrice(temporaryCart.getTotalPrice() + price.getPrice() * request.getQuantity());
    }

    public CartTemporary getCartForGuest(HttpSession session){
        return (CartTemporary) session.getAttribute("Cart");
    }

    public void updateCartForGuest(UpdateCartRequest request, HttpSession session){
        CartTemporary temporaryCart = (CartTemporary) session.getAttribute("Cart");

        if(temporaryCart == null || !temporaryCart.getCartItems().isEmpty()){
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(()->new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(()->new AppException(ErrorCode.UNIT_NOT_FOUND));

        Price price = priceRepository.findByProductAndUnit(product, unit);

        CartItemTemporary cartItemTemporary = temporaryCart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()) && item.getUnitId().equals(unit.getId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));


        cartItemTemporary.setQuantity(cartItemTemporary.getQuantity() + request.getQuantity());
        cartItemTemporary.setPrice(cartItemTemporary.getPrice() + price.getPrice() * request.getQuantity());

        if(cartItemTemporary.getQuantity() <=0 ){
            temporaryCart.getCartItems().remove(cartItemTemporary);
        }

        temporaryCart.setTotalPrice(temporaryCart.getTotalPrice() + price.getPrice()*request.getQuantity());

        if(temporaryCart.getTotalPrice() <= 0){
            temporaryCart.setTotalPrice(0);
        }

        session.setAttribute("Cart", temporaryCart);
    }

    public void deleteCartItemForGuest(DeleteCartItemRequest request, HttpSession session){
        CartTemporary cartTemporary = (CartTemporary) session.getAttribute("Cart");

        if(cartTemporary == null || !cartTemporary.getCartItems().isEmpty()){
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

        CartItemTemporary cartItemTemporary = cartTemporary.getCartItems().stream()
                .filter(item -> item.getProductId().equals(product.getId()) && item.getUnitId().equals(unit.getId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartTemporary.setTotalPrice(cartTemporary.getTotalPrice() - cartItemTemporary.getPrice());
        cartTemporary.getCartItems().remove(cartItemTemporary);

        session.setAttribute("Cart", cartTemporary);
    }

    public void transferGuestCartToUserCart(HttpSession session){
        CartTemporary guestCart = (CartTemporary) session.getAttribute("Cart");

        log.info("Cart{}", guestCart);

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart userCart = user.getCart();
        if(userCart == null){
            userCart = new Cart();
            userCart.setUser(user);
            userCart.setTotalPrice(0);
            cartRepository.save(userCart);
        }

        List<CartItem> cartItemsUser = userCart.getCartItems();

        for (CartItemTemporary guestItem : guestCart.getCartItems()){
            Product product = productRepository.findById(guestItem.getProductId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            Unit unit = unitRepository.findById(guestItem.getUnitId())
                    .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));


            CartItem userCartItem = cartItemsUser.stream()
                    .filter(item -> item.getProduct().getId().equals(guestItem.getProductId())
                            && item.getUnit().getId().equals(guestItem.getUnitId()))
                    .findFirst()
                    .orElse(null);

            if(userCartItem == null){
                userCartItem = new CartItem();
                userCartItem.setCart(userCart);
                userCartItem.setProduct(product);
                userCartItem.setUnit(unit);
                userCartItem.setQuantity(guestItem.getQuantity());
                userCartItem.setPrice(guestItem.getPrice());
                cartItemRepository.save(userCartItem);
            } else {
                userCartItem.setQuantity(userCartItem.getQuantity() + guestItem.getQuantity());
                userCartItem.setPrice(userCartItem.getPrice() + guestItem.getPrice());
            }
        }

        userCart.setTotalPrice(userCart.getTotalPrice() + guestCart.getTotalPrice());
        cartRepository.save(userCart);

        session.removeAttribute("Cart");
    }
}
