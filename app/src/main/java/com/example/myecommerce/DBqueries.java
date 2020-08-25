package com.example.myecommerce;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBqueries {

    public static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static List<CategoryModel> categoryModelList = new ArrayList<>();

    public static List<List<HomePageModel>> lists = new ArrayList<>();
    public static List<String> loadedCategoriesNames = new ArrayList<>();

    public static List<String> wishList = new ArrayList<>();
    public static List<WishlistModel> wishlistModelList = new ArrayList<>();

    public static List<String> myRatedIds = new ArrayList<>();
    public static List<Long> myRating = new ArrayList<>();

    public static List<String> cartList = new ArrayList<>();
    public static List<CartItemModel> cartItemModelList = new ArrayList<>();

    public static int selectedAddress = -1;
    public static List<AddressesModel> addressesModelList = new ArrayList<>();

    public static List<RewardModel> rewardModelList = new ArrayList<>();

    public static List<MyOrderItemModel> myOrderItemModelList = new ArrayList<>();

    public static void loadCategories(final RecyclerView categoryRecyclerView, final Context context){
        categoryModelList.clear();
        firebaseFirestore.collection("CATEGORIES").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                categoryModelList.add(new CategoryModel(documentSnapshot.get("icon").toString(),documentSnapshot.get("categoryName").toString()));// documentSnapshot means document from firebase
                            }
                            CategoryAdapter categoryAdapter = new CategoryAdapter(categoryModelList);
                            categoryRecyclerView.setAdapter(categoryAdapter);
                            categoryAdapter.notifyDataSetChanged();
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadFragmentData(final RecyclerView homePageRecyclerView, final Context context, final int index, String categoryName){
        firebaseFirestore.collection("CATEGORIES")
                .document(categoryName.toUpperCase())
                .collection("TOP_DEALS").orderBy("index").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){

                                if ((long)documentSnapshot.get("view_type") == 0){

                                    List<SliderModel> sliderModelList = new ArrayList<>();
                                    long no_of_banners = (long)documentSnapshot.get("no_of_banners");
                                    for (long x = 1; x < no_of_banners + 1; x++){
                                        sliderModelList.add(new SliderModel(documentSnapshot.get("banner_" + x).toString(),
                                                documentSnapshot.get("banner_" + x + "_background").toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(0,sliderModelList));

                                }else if ((long)documentSnapshot.get("view_type") == 1){

                                    lists.get(index).add(new HomePageModel(1, documentSnapshot.get("strip_ad_banner").toString(),documentSnapshot.get("background").toString()));

                                }else if ((long)documentSnapshot.get("view_type") == 2){

                                    List<WishlistModel> viewAllProductList = new ArrayList<>();
                                    List<HorizontalProductScrollModel> horizontalProductScrollModelList = new ArrayList<>();
                                    long no_of_products = (long)documentSnapshot.get("no_of_products");
                                    for (long x = 1; x < no_of_products + 1; x++){
                                        horizontalProductScrollModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString(),
                                                documentSnapshot.get("product_image_" + x).toString(),
                                                documentSnapshot.get("product_title_" + x).toString(),
                                                documentSnapshot.get("product_subtitle_" + x).toString(),
                                                documentSnapshot.get("product_price_" + x).toString()));

                                        viewAllProductList.add(new WishlistModel(documentSnapshot.get("product_ID_" + x).toString(),
                                                documentSnapshot.get("product_image_" + x).toString(),
                                                documentSnapshot.get("product_full_title_" + x).toString(),
                                                (long)documentSnapshot.get("free_coupens_" + x),
                                                documentSnapshot.get("average_rating_" + x).toString(),
                                                (long)documentSnapshot.get("total_ratings_" + x),
                                                documentSnapshot.get("product_price_" + x).toString(),
                                                documentSnapshot.get("cutted_price_" + x).toString(),
                                                (boolean)documentSnapshot.get("COD_" + x),
                                                (boolean)documentSnapshot.get("in_stock_" + x)));
                                    }
                                    lists.get(index).add(new HomePageModel(2,documentSnapshot.get("layout_title").toString(),
                                            documentSnapshot.get("layout_background").toString(),
                                            horizontalProductScrollModelList, viewAllProductList));

                                }else if ((long)documentSnapshot.get("view_type") == 3){

                                    List<HorizontalProductScrollModel> gridLayoutModelList = new ArrayList<>();
                                    long no_of_products = (long)documentSnapshot.get("no_of_products");
                                    for (long x = 1; x < no_of_products + 1; x++){
                                        gridLayoutModelList.add(new HorizontalProductScrollModel(documentSnapshot.get("product_ID_" + x).toString(),
                                                documentSnapshot.get("product_image_" + x).toString(),
                                                documentSnapshot.get("product_title_" + x).toString(),
                                                documentSnapshot.get("product_subtitle_" + x).toString(),
                                                documentSnapshot.get("product_price_" + x).toString()));
                                    }
                                    lists.get(index).add(new HomePageModel(3,documentSnapshot.get("layout_title").toString(),
                                            documentSnapshot.get("layout_background").toString(),
                                            gridLayoutModelList));
                                }
                            }
                            HomePageAdapter homePageAdapter = new HomePageAdapter(lists.get(index));
                            homePageRecyclerView.setAdapter(homePageAdapter);
                            homePageAdapter.notifyDataSetChanged();
                            HomeFragment.swipeRefreshLayout.setRefreshing(false);
                        }else{
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void loadWishList(final Context context, final Dialog dialog, final boolean loadProductData){

        wishList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_WISHLIST")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    for (long x = 0; x < (long)task.getResult().get("list_size"); x++){
                        wishList.add(task.getResult().get("product_ID_" + x).toString());

                        if (DBqueries.wishList.contains(ProductDetailsActivity.productID)){
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = true;
                            if (ProductDetailsActivity.addToWishListBtn != null) {
                                ProductDetailsActivity.addToWishListBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimaryDark));
                            }
                        }else {
                            if (ProductDetailsActivity.addToWishListBtn != null) {
                                ProductDetailsActivity.addToWishListBtn.setSupportImageTintList(ColorStateList.valueOf(Color.parseColor("#9e9e9e")));
                            }
                            ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                        }

                        if (loadProductData) {
                            wishlistModelList.clear();
                            final String productId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        final DocumentSnapshot documentSnapshot = task.getResult();

                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId)
                                                .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){

                                                    if (task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")){
                                                        wishlistModelList.add(new WishlistModel(productId,
                                                                documentSnapshot.get("product_image_1").toString(),
                                                                documentSnapshot.get("product_title").toString(),
                                                                (long) documentSnapshot.get("free_coupens"),
                                                                documentSnapshot.get("average_rating").toString(),
                                                                (long) documentSnapshot.get("total_ratings"),
                                                                documentSnapshot.get("product_price").toString(),
                                                                documentSnapshot.get("cutted_price").toString(),
                                                                (boolean) documentSnapshot.get("COD"),
                                                                true));
                                                    }else {
                                                        wishlistModelList.add(new WishlistModel(productId,
                                                                documentSnapshot.get("product_image_1").toString(),
                                                                documentSnapshot.get("product_title").toString(),
                                                                (long) documentSnapshot.get("free_coupens"),
                                                                documentSnapshot.get("average_rating").toString(),
                                                                (long) documentSnapshot.get("total_ratings"),
                                                                documentSnapshot.get("product_price").toString(),
                                                                documentSnapshot.get("cutted_price").toString(),
                                                                (boolean) documentSnapshot.get("COD"),
                                                                false));
                                                    }
                                                    MyWishlistFragment.wishlistAdapter.notifyDataSetChanged();

                                                }else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

    }

    public static void removeFromWishlist(final int index, final Context context){

        final String removedProductId = wishList.get(index);
        wishList.remove(index);
        Map<String,Object> updateWishlist = new HashMap<>();

        for (int x = 0; x < wishList.size(); x++){
            updateWishlist.put("product_ID_" + x, wishList.get(x));
        }
        updateWishlist.put("list_size",(long)wishList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_WISHLIST")
                .set(updateWishlist).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    if (wishlistModelList.size() != 0){
                        wishlistModelList.remove(index);
                        MyWishlistFragment.wishlistAdapter.notifyDataSetChanged(); // refreshing the adapter for changes
                    }
                    ProductDetailsActivity.ALREADY_ADDED_TO_WISHLIST = false;
                    Toast.makeText(context, "Removed successfully!", Toast.LENGTH_SHORT).show();
                }else {
                    if (ProductDetailsActivity.addToWishListBtn != null) {
                        ProductDetailsActivity.addToWishListBtn.setSupportImageTintList(context.getResources().getColorStateList(R.color.colorPrimaryDark));
                    }
                    wishList.add(index,removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_wishlist_query = false;
            }
        });
    }

    public static void loadRatingList(final Context context){
        if (!ProductDetailsActivity.running_rating_query) {
            ProductDetailsActivity.running_rating_query = true;
            myRatedIds.clear();
            myRating.clear();

            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_DATA")
                    .document("MY_RATINGS").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                        for (long x = 0; x < (long) task.getResult().get("list_size"); x++) {
                            myRatedIds.add(task.getResult().get("product_ID_" + x).toString());
                            myRating.add((long) task.getResult().get("rating_" + x));

                            if (task.getResult().get("product_ID_" + x).toString().equals(ProductDetailsActivity.productID)) {
                                ProductDetailsActivity.initialRating = Integer.parseInt(String.valueOf((long) task.getResult().get("rating_" + x))) - 1;
                                if (ProductDetailsActivity.rateNowContainer != null) {
                                    ProductDetailsActivity.setRating(ProductDetailsActivity.initialRating);
                                }
                            }
                        }

                    } else {
                        String error = task.getException().getMessage();
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                    }
                    ProductDetailsActivity.running_rating_query = false;
                }
            });
        }
    }

    public static void loadCartList(final Context context, final Dialog dialog, final boolean loadProductData, final TextView badgeCount, final TextView cartTotalAmount){

        cartList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_CART")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    for (long x = 0; x < (long)task.getResult().get("list_size"); x++){
                        cartList.add(task.getResult().get("product_ID_" + x).toString());

                        if (DBqueries.cartList.contains(ProductDetailsActivity.productID)){
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = true;
                        }else {
                            ProductDetailsActivity.ALREADY_ADDED_TO_CART = false;
                        }

                        if (loadProductData) {
                            cartItemModelList.clear();
                            final String productId = task.getResult().get("product_ID_" + x).toString();
                            firebaseFirestore.collection("PRODUCTS").document(productId)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {

                                        final DocumentSnapshot documentSnapshot = task.getResult();

                                        FirebaseFirestore.getInstance().collection("PRODUCTS").document(productId)
                                                .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){

                                                    int index = 0;
                                                    if (cartList.size() >= 2) {
                                                        index = cartList.size() - 2;
                                                    }

                                                    if (task.getResult().getDocuments().size() < (long)documentSnapshot.get("stock_quantity")){
                                                        cartItemModelList.add(index,new CartItemModel(CartItemModel.CART_ITEM,
                                                                productId,
                                                                documentSnapshot.get("product_image_1").toString(),
                                                                documentSnapshot.get("product_title").toString(),
                                                                (long) documentSnapshot.get("free_coupens"),
                                                                documentSnapshot.get("product_price").toString(),
                                                                documentSnapshot.get("cutted_price").toString(),
                                                                (long) 1,
                                                                (long) documentSnapshot.get("offers_applied"),
                                                                (long) 0,
                                                                true,
                                                                (long) documentSnapshot.get("max-quantity"),
                                                                (long) documentSnapshot.get("stock_quantity")));
                                                    }else {
                                                        cartItemModelList.add(index,new CartItemModel(CartItemModel.CART_ITEM,
                                                                productId,
                                                                documentSnapshot.get("product_image_1").toString(),
                                                                documentSnapshot.get("product_title").toString(),
                                                                (long) documentSnapshot.get("free_coupens"),
                                                                documentSnapshot.get("product_price").toString(),
                                                                documentSnapshot.get("cutted_price").toString(),
                                                                (long) 1,
                                                                (long) documentSnapshot.get("offers_applied"),// documentSnapshot
                                                                (long) 0,
                                                                false,
                                                                (long) documentSnapshot.get("max-quantity"),
                                                                (long) documentSnapshot.get("stock_quantity")));
                                                    }

                                                    if (cartList.size() == 1){
                                                        cartItemModelList.add(new CartItemModel(CartItemModel.TOTAL_AMOUNT));
                                                        LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                                                        parent.setVisibility(View.VISIBLE);
                                                    }

                                                    if (cartList.size() == 0){
                                                        cartItemModelList.clear();
                                                    }

                                                    MyCartFragment.cartAdapter.notifyDataSetChanged();

                                                }else {
                                                    String error = task.getException().getMessage();
                                                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                    if (cartList.size() != 0){
                        badgeCount.setVisibility(View.VISIBLE);
                    }else {
                        badgeCount.setVisibility(View.INVISIBLE);
                    }
                    if (DBqueries.cartList.size() < 99) {
                        badgeCount.setText(String.valueOf(DBqueries.cartList.size()));
                    }else {
                        badgeCount.setText("99");
                    }
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public static void removeFromCart(final int index, final Context context, final TextView cartTotalAmount){

        final String removedProductId = cartList.get(index);
        cartList.remove(index);
        Map<String,Object> updateCartList = new HashMap<>();

        for (int x = 0; x < cartList.size(); x++){
            updateCartList.put("product_ID_" + x, cartList.get(x));
        }
        updateCartList.put("list_size",(long)cartList.size());

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_CART")
                .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    if (cartItemModelList.size() != 0){
                        cartItemModelList.remove(index);
                        MyCartFragment.cartAdapter.notifyDataSetChanged(); // refreshing the adapter for changes
                    }

                    if (cartList.size() == 0){
                        LinearLayout parent = (LinearLayout) cartTotalAmount.getParent().getParent();
                        parent.setVisibility(View.GONE);
                        cartItemModelList.clear();
                    }

                    Toast.makeText(context, "Removed successfully!", Toast.LENGTH_SHORT).show();
                }else {
                    cartList.add(index,removedProductId);
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                ProductDetailsActivity.running_cart_query = false;
            }
        });

    }

    public static void loadAddresses(final Context context, final Dialog loadingDialog){

        addressesModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .collection("USER_DATA").document("MY_ADDRESSES")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    Intent deliveryIntent;

                    if ((long)task.getResult().get("list_size") == 0){
                        deliveryIntent = new Intent(context,AddAddressActivity.class); // linking the cart proceed button to Address page
                        deliveryIntent.putExtra("INTENT","deliveryIntent");
                    }else {
                        for (long x = 1; x < (long)task.getResult().get("list_size") + 1; x++){
                            addressesModelList.add(new AddressesModel(task.getResult().get("fullname_" + x).toString(),
                                    task.getResult().get("address_" + x).toString(),
                                    task.getResult().get("pincode_" + x).toString(),
                                    (boolean)task.getResult().get("selected_" + x),
                                    task.getResult().get("mobile_no_" + x).toString()));

                            if ((boolean)task.getResult().get("selected_" + x)){
                                selectedAddress = Integer.parseInt(String.valueOf(x - 1));
                            }
                        }
                        deliveryIntent = new Intent(context,DeliveryActivity.class); // if address present linking the cart proceed button to Delivery page
                    }
                    context.startActivity(deliveryIntent);

                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                }
                loadingDialog.dismiss();
            }
        });
    }

    public static void loadRewards(final Context context, final Dialog loadingDialog, final Boolean onRewardFragment ){

        rewardModelList.clear();

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            final Date lastseenDate = task.getResult().getDate("Last seen");

                            firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_REWARDS")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()){

                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                            if (documentSnapshot.get("type").toString().equals("Discount") && lastseenDate.before(documentSnapshot.getDate("validity"))) {
                                                rewardModelList.add(new RewardModel(documentSnapshot.getId(),
                                                        documentSnapshot.get("type").toString(),
                                                        documentSnapshot.get("lower_limit").toString(),
                                                        documentSnapshot.get("upper_limit").toString(),
                                                        documentSnapshot.get("percentage").toString(),
                                                        documentSnapshot.get("body").toString(),
                                                        documentSnapshot.getTimestamp("validity").toDate(),
                                                        (Boolean) documentSnapshot.get("already_used")
                                                ));
                                            }else if (documentSnapshot.get("type").toString().equals("Flat Rs.* OFF") && lastseenDate.before(documentSnapshot.getDate("validity"))){
                                                rewardModelList.add(new RewardModel(documentSnapshot.getId(),
                                                        documentSnapshot.get("type").toString(),
                                                        documentSnapshot.get("lower_limit").toString(),
                                                        documentSnapshot.get("upper_limit").toString(),
                                                        documentSnapshot.get("amount").toString(),
                                                        documentSnapshot.get("body").toString(),
                                                        documentSnapshot.getTimestamp("validity").toDate(),
                                                        (Boolean) documentSnapshot.get("already_used")
                                                ));
                                            }
                                        }
                                        if (onRewardFragment) {
                                            MyRewardsFragment.myRewardsAdapter.notifyDataSetChanged();// refreshing the adapter
                                        }

                                    }else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                    }
                                    loadingDialog.dismiss();
                                }
                            });
                        }else {
                            loadingDialog.dismiss();
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });




    }

    public static void loadOrders(final Context context, final MyOrderAdapter myOrderAdapter){

        myOrderItemModelList.clear();
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                                firebaseFirestore.collection("ORDERS").document(documentSnapshot.getString("order_id")).collection("OrderItems")
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){

                                            for (DocumentSnapshot orderItems : task.getResult().getDocuments()){

                                                final MyOrderItemModel myOrderItemModel = new MyOrderItemModel(orderItems.getString("Product Id"),
                                                        orderItems.getString("Order Status"),
                                                        orderItems.getString("Address"),
                                                        orderItems.getString("Coupen Id"),
                                                        orderItems.getString("Cutted Price"),
                                                        orderItems.getDate("Ordered Date"),
                                                        orderItems.getDate("Packed Date"),
                                                        orderItems.getDate("Shipped Date"),
                                                        orderItems.getDate("Delivered Date"),
                                                        orderItems.getDate("Cancelled Date"),
                                                        orderItems.getString("Discounted Price"),
                                                        orderItems.getLong("Free Coupens"),
                                                        orderItems.getString("FullName"),
                                                        orderItems.getString("ORDER ID"),
                                                        orderItems.getString("Payment Method"),
                                                        orderItems.getString("Pincode"),
                                                        orderItems.getString("Product Price"),
                                                        orderItems.getLong("Product Quantity"),
                                                        orderItems.getString("User Id"),
                                                        orderItems.getString("Product Image"),
                                                        orderItems.getString("Product Title")
                                                );

                                                myOrderItemModelList.add(myOrderItemModel);
                                            }
                                            myOrderAdapter.notifyDataSetChanged();

                                        }else {
                                            String error = task.getException().getMessage();
                                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }

                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public static void clearData(){
        categoryModelList.clear();
        lists.clear();
        loadedCategoriesNames.clear();
        wishList.clear();
        wishlistModelList.clear();
        cartList.clear();
        cartItemModelList.clear();
        myRatedIds.clear();
        myRating.clear();
        addressesModelList.clear();
        rewardModelList.clear();
    }
}
