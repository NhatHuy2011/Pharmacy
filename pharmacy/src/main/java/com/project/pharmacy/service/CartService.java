package com.project.pharmacy.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

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

    public void addToCartForUser(AddToCartRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new AppException(ErrorCode.UNIT_NOT_FOUND));

        Price price = priceRepository.findByProductAndUnit(product, unit);

        Cart cart = user.getCart();

        if (cart == null) {
            cart = Cart.builder()
                    .user(user)
                    .totalPrice(0)
                    .cartItems(new ArrayList<>())
                    .build();
            cartRepository.save(cart);
        }

        List<CartItem> cartItems = cart.getCartItems();

        CartItem cartItem = cartItems.stream()
                .filter(item -> item.getId().equals(request.getCartId()))
                .findFirst()
                .orElse(new CartItem());

        cartItem.setCart(cart);
        cartItem.setPrice(price);
        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        cartItemRepository.save(cartItem);

        cart.setTotalPrice(cart.getTotalPrice() + price.getPrice() * request.getQuantity());
        cart.getCartItems().add(cartItem);
        cartRepository.save(cart);
    }

    public CartResponse getCartForUser() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = user.getCart();

        if (cart == null || cart.getCartItems().isEmpty()) {
            return null;
        }

        AtomicInteger totalPrice = new AtomicInteger();
        List<CartItemResponse> cartItemResponses = cart.getCartItems().stream()
                .map(cartItem -> {
                    CartItemResponse cartItemResponse = new CartItemResponse();
                    cartItemResponse.setId(cartItem.getId());

                    Product product = cartItem.getPrice().getProduct();
                    cartItemResponse.setProductName(product.getName());

                    Unit unit = cartItem.getPrice().getUnit();
                    cartItemResponse.setUnitName(unit.getName());

                    Image firstImage = imageRepository.findFirstByProductId(product.getId());
                    String url = firstImage != null ? firstImage.getSource() : null;

                    cartItemResponse.setImage(url);

                    cartItemResponse.setQuantity(cartItem.getQuantity());
                    cartItemResponse.setPrice(cartItem.getPrice().getPrice());
                    cartItemResponse.setAmount(cartItem.getQuantity() * cartItem.getPrice().getPrice());

                    totalPrice.addAndGet(cartItemResponse.getAmount());

                    return cartItemResponse;
                })
                .toList();

        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartItemResponses(cartItemResponses);
        cartResponse.setTotalPrice(totalPrice);

        return cartResponse;
    }

    public void updateCartForUser(UpdateCartRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = user.getCart();

        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        CartItem cartItem = cartItemRepository.findById(request.getCartItemId())
                        .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());

        cartItemRepository.save(cartItem);

        if (cartItem.getQuantity() <= 0) {
            cartItemRepository.delete(cartItem);
            cart.getCartItems().remove(cartItem);
        }

        cart.setTotalPrice(cart.getTotalPrice() + cartItem.getPrice().getPrice() * request.getQuantity());

        if (cart.getTotalPrice() <= 0) {
            cart.setTotalPrice(0);
        }
        cartRepository.save(cart);
    }

    public void deleteCartItemForUser(DeleteCartItemRequest request) {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = user.getCart();
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        CartItem cartItem = cartItemRepository.findById(request.getCartItemId())
                        .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItemRepository.delete(cartItem);

        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getPrice().getPrice());
        if (cart.getTotalPrice() <= 0) {
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
                .filter(item -> item.getId().equals(request.getCartId()))
                .findFirst()
                .orElse(null);

        if (cartItemTemporary == null) {
            cartItemTemporary = CartItemTemporary.builder()
                    .id(UUID.randomUUID().toString())
                    .priceId(price.getId())
                    .productName(product.getName())
                    .unitName(unit.getName())
                    .price(price.getPrice())
                    .quantity(request.getQuantity())
                    .build();
            cartItems.add(cartItemTemporary);
        } else {
            cartItemTemporary.setQuantity(cartItemTemporary.getQuantity() + request.getQuantity());
            cartItemTemporary.setPrice(cartItemTemporary.getPrice() + price.getPrice() * request.getQuantity());
        }

        temporaryCart.setTotalPrice(temporaryCart.getTotalPrice() + price.getPrice() * request.getQuantity());
    }

    public CartTemporary getCartForGuest(HttpSession session) {
        return (CartTemporary) session.getAttribute("Cart");
    }

    public void updateCartForGuest(UpdateCartRequest request, HttpSession session) {
        CartTemporary temporaryCart = (CartTemporary) session.getAttribute("Cart");

        if (temporaryCart == null || temporaryCart.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        CartItemTemporary cartItemTemporary = temporaryCart.getCartItems().stream()
                .filter(item -> item.getId().equals(request.getCartItemId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItemTemporary.setQuantity(cartItemTemporary.getQuantity() + request.getQuantity());

        if (cartItemTemporary.getQuantity() <= 0) {
            temporaryCart.getCartItems().remove(cartItemTemporary);
        }

        temporaryCart.setTotalPrice(temporaryCart.getTotalPrice() + cartItemTemporary.getPrice() * request.getQuantity());

        if (temporaryCart.getTotalPrice() <= 0) {
            temporaryCart.setTotalPrice(0);
        }

        session.setAttribute("Cart", temporaryCart);
    }

    public void deleteCartItemForGuest(DeleteCartItemRequest request, HttpSession session) {
        CartTemporary cartTemporary = (CartTemporary) session.getAttribute("Cart");

        if (cartTemporary == null || cartTemporary.getCartItems().isEmpty()) {
            throw new AppException(ErrorCode.CART_EMPTY);
        }

        CartItemTemporary cartItemTemporary = cartTemporary.getCartItems().stream()
                .filter(item -> item.getId().equals(request.getCartItemId()))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartTemporary.setTotalPrice(cartTemporary.getTotalPrice() - cartItemTemporary.getPrice());
        cartTemporary.getCartItems().remove(cartItemTemporary);

        session.setAttribute("Cart", cartTemporary);
    }

    public void transferGuestCartToUserCart(HttpSession session) {
        CartTemporary guestCart = (CartTemporary) session.getAttribute("Cart");

        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart userCart = user.getCart();
        if (userCart == null) {
            userCart = new Cart();
            userCart.setUser(user);
            userCart.setTotalPrice(0);
            cartRepository.save(userCart);
        }

        List<CartItem> cartItemsUser = userCart.getCartItems();

        for (CartItemTemporary guestItem : guestCart.getCartItems()) {
            Price price = priceRepository.findById(guestItem.getPriceId())
                    .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));

            CartItem userCartItem = cartItemsUser.stream()
                    .filter(item -> item.getPrice().getId().equals(guestItem.getPriceId()))
                    .findFirst()
                    .orElse(null);

            if (userCartItem == null) {
                userCartItem = CartItem.builder()
                        .cart(userCart)
                        .price(price)
                        .quantity(guestItem.getQuantity())
                        .build();
                cartItemRepository.save(userCartItem);
            } else {
                userCartItem.setQuantity(userCartItem.getQuantity() + guestItem.getQuantity());
                userCartItem.setPrice(price);
            }
        }

        userCart.setTotalPrice(userCart.getTotalPrice() + guestCart.getTotalPrice());
        cartRepository.save(userCart);

        session.removeAttribute("Cart");
    }
}
