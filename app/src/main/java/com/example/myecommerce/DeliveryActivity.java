package com.example.myecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.esewa.android.sdk.payment.ESewaConfiguration;
import com.esewa.android.sdk.payment.ESewaPayment;
import com.esewa.android.sdk.payment.ESewaPaymentActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeliveryActivity extends AppCompatActivity {

    public static List<CartItemModel> cartItemModelList;
    private RecyclerView deliveryRecyclerView;
    public static CartAdapter cartAdapter;
    private Button changeOrAddNewAddressBtn;
    public static final int SELECT_ADDRESS = 0;
    private TextView totalAmount;
    private TextView fullname;
    private String name,mobileNo;
    private TextView fullAddress;
    private TextView pincode;
    private Button continueProceedBtn;
    public static Dialog loadingDialog;
    private Dialog paymentMethodDialog;
    private ImageButton esewa, cod;
    private String paymentMethod = "ESEWA";
    private ConstraintLayout orderConfirmationLayout;
    private ImageButton continueShoppingBtn;
    private TextView orderId;
    private boolean successResponse = false;
    public static boolean fromCart;
    public static boolean codOrderConfirmed = false;

    private FirebaseFirestore firebaseFirestore;
    public static boolean getQtyIDs = true;

    /////payment

    private static final String CONFIG_ENVIRONMENT = ESewaConfiguration.ENVIRONMENT_TEST;
    private static final int REQUEST_CODE_PAYMENT = 1;

    private static final String M_id = "JB0BBQ4aD0UqIThFJwAKBgAXEUkEGQUBBAwdOgABHD4DChwUAB0R";
    private static final String M_secret_key = "BhwIWQQADhIYSxILExMcAgFXFhcOBwAKBgAXEQ==";
    private static final String customer_id = FirebaseAuth.getInstance().getUid();
    private static final String order_id = UUID.randomUUID().toString().substring(0,28);

    /////payment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Delivery");

        deliveryRecyclerView = findViewById(R.id.delivery_recyclerview);
        changeOrAddNewAddressBtn = findViewById(R.id.change_or_add_address_btn);
        totalAmount = findViewById(R.id.total_cart_amount);
        fullname = findViewById(R.id.fullname);
        fullAddress = findViewById(R.id.address);
        pincode = findViewById(R.id.pincode);
        continueProceedBtn = findViewById(R.id.cart_continue_proceed_btn);
        orderConfirmationLayout = findViewById(R.id.order_confirmation_layout);
        continueShoppingBtn = findViewById(R.id.continue_shopping_btn);
        orderId = findViewById(R.id.order_id);

        ////// loading dialog
        loadingDialog = new Dialog(DeliveryActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        ////// loading dialog

        ////// payment dialog
        paymentMethodDialog = new Dialog(DeliveryActivity.this);
        paymentMethodDialog.setContentView(R.layout.payment_method);
        paymentMethodDialog.setCancelable(true);
        paymentMethodDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        paymentMethodDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        esewa = paymentMethodDialog.findViewById(R.id.esewa);
        cod = paymentMethodDialog.findViewById(R.id.cod_btn);
        ////// payment dialog
        firebaseFirestore = FirebaseFirestore.getInstance();
        getQtyIDs = true;

        //// todo: order_id = .........

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        deliveryRecyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(cartItemModelList, totalAmount, false);
        deliveryRecyclerView.setAdapter(cartAdapter);
        cartAdapter.notifyDataSetChanged();

        changeOrAddNewAddressBtn.setVisibility(View.VISIBLE);
        changeOrAddNewAddressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQtyIDs = false;
                Intent myAddressesIntent = new Intent(DeliveryActivity.this,MyAddressesActivity.class);
                myAddressesIntent.putExtra("MODE", SELECT_ADDRESS);
                startActivity(myAddressesIntent);
            }
        });

        continueProceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean allProductsAvailable = true;
                for (CartItemModel cartItemModel : cartItemModelList){
                    if (cartItemModel.isQtyError()){
                        allProductsAvailable = false;
                    }
                }
                if (allProductsAvailable){
                    paymentMethodDialog.show();
                }
            }
        });

        cod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethod = "COD";
                placeOrderDetails();
            }
        });

        esewa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentMethod = "ESEWA";
                placeOrderDetails();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        ////// accessing quantity
        if (getQtyIDs) {
            loadingDialog.show();
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

               for (int y = 0; y < cartItemModelList.get(x).getProductQuantity(); y++){
                   final String quantityDocumentName = UUID.randomUUID().toString().substring(0,20);

                   Map<String,Object> timestamp = new HashMap<>();
                   timestamp.put("time", FieldValue.serverTimestamp());
                   final int finalX = x;
                   final int finalY = y;
                   firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID())
                           .collection("QUANTITY").document(quantityDocumentName).set(timestamp)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {

                                   if (task.isSuccessful()){

                                       cartItemModelList.get(finalX).getQtyIDs().add(quantityDocumentName);

                                       if (finalY + 1 == cartItemModelList.get(finalX).getProductQuantity()){

                                           firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(finalX).getProductID())
                                                   .collection("QUANTITY").orderBy("time", Query.Direction.ASCENDING)
                                                   .limit(cartItemModelList.get(finalX).getStockQuantity()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                               @Override
                                               public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                   if (task.isSuccessful()){
                                                       List<String> serverQuantity = new ArrayList<>();

                                                       for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                                           serverQuantity.add(queryDocumentSnapshot.getId());
                                                       }

                                                       long availableQty = 0;
                                                       boolean noLongerAvailable = true;
                                                       for (String qtyId : cartItemModelList.get(finalX).getQtyIDs()){
                                                           cartItemModelList.get(finalX).setQtyError(false);
                                                           if (!serverQuantity.contains(qtyId)){

                                                               if (noLongerAvailable){
                                                                   cartItemModelList.get(finalX).setInStock(false);
                                                               }else {
                                                                   cartItemModelList.get(finalX).setQtyError(true);
                                                                   cartItemModelList.get(finalX).setMaxQuantity(availableQty);
                                                                   Toast.makeText(DeliveryActivity.this, "Sorry ! All products may not be available in required quantity...", Toast.LENGTH_SHORT).show();
                                                               }

                                                           }else {
                                                               availableQty++;
                                                               noLongerAvailable = false;
                                                           }
                                                       }
                                                       cartAdapter.notifyDataSetChanged();
                                                   }else {
                                                       String error = task.getException().getMessage();
                                                       Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                                   }
                                                   loadingDialog.dismiss();
                                               }
                                           });

                                       }

                                   }else {
                                       loadingDialog.dismiss();
                                       String error = task.getException().getMessage();
                                       Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                                   }
                               }
                           });
               }

            }
        }else {
            getQtyIDs = true;
        }
        ////// accessing quantity

        name = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getFullname();
        mobileNo = DBqueries.addressesModelList.get(DBqueries.selectedAddress).getMobileNo();
        fullname.setText(name + " - " + mobileNo);
        fullAddress.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getAddress());
        pincode.setText(DBqueries.addressesModelList.get(DBqueries.selectedAddress).getPincode());

        if (codOrderConfirmed){
            showConfirmationLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here.The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == android.R.id.home){ // back btn placed
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismiss();

        if (getQtyIDs) {
            for (int x = 0; x < cartItemModelList.size() - 1; x++) {

                if (!successResponse) {
                    for (final String qtyID : cartItemModelList.get(x).getQtyIDs()) {

                        final int finalX = x;
                        firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID())
                                .collection("QUANTITY").document(qtyID).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if (qtyID.equals(cartItemModelList.get(finalX).getQtyIDs().get(cartItemModelList.get(finalX).getQtyIDs().size() - 1))){

                                    cartItemModelList.get(finalX).getQtyIDs().clear();

                                }
                            }
                        });
                    }
                }else {
                    cartItemModelList.get(x).getQtyIDs().clear();
                }
            }
        }

    }

    @Override
    public void onBackPressed() {
        if (successResponse){
            finish();
            return;
        }
        super.onBackPressed();
    }

    private void showConfirmationLayout(){
        successResponse = true;
        codOrderConfirmed = false;
        getQtyIDs = false;
        for (int x = 0; x < cartItemModelList.size() - 1; x++){

            for (String qtyID : cartItemModelList.get(x).getQtyIDs()){

                firebaseFirestore.collection("PRODUCTS").document(cartItemModelList.get(x).getProductID())
                        .collection("QUANTITY").document(qtyID).update("user_ID", FirebaseAuth.getInstance().getUid());

            }
        }

        if (MainActivity.mainActivity != null){
            MainActivity.mainActivity.finish();
            MainActivity.mainActivity = null;
            MainActivity.showCart = false;
        }else {
            MainActivity.resetMainActivity = true;
        }
        if (ProductDetailsActivity.productDetailsActivity != null){
            ProductDetailsActivity.productDetailsActivity.finish();
            ProductDetailsActivity.productDetailsActivity  = null;
        }

        /////// sent confirmation SMS

        /// code here

        /////// sent confirmation SMS

        if (fromCart){
            loadingDialog.show();

            Map<String,Object> updateCartList = new HashMap<>();
            long cartListSize = 0;
            final List<Integer> indexList = new ArrayList<>();

            for (int x = 0; x < DBqueries.cartList.size(); x++){
                if (!cartItemModelList.get(x).isInStock()){
                    updateCartList.put("product_ID_" + cartListSize, cartItemModelList.get(x).getProductID());
                    cartListSize++;
                }else {
                    indexList.add(x);
                }
            }
            updateCartList.put("list_size", cartListSize);

            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                    .collection("USER_DATA").document("MY_CART")
                    .set(updateCartList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        for (int x = 0; x < indexList.size(); x++){
                            DBqueries.cartList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(indexList.get(x).intValue());
                            DBqueries.cartItemModelList.remove(DBqueries.cartItemModelList.size() - 1);
                        }
                    }else {
                        String error = task.getException().getMessage();
                        Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                    loadingDialog.dismiss();
                }
            });
        }

        continueProceedBtn.setEnabled(false);
        changeOrAddNewAddressBtn.setEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); // actionbar back arrow key disabled
        orderId.setText("Order ID " + order_id);
        orderConfirmationLayout.setVisibility(View.VISIBLE);

        continueShoppingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void placeOrderDetails(){

        String userID = FirebaseAuth.getInstance().getUid();
        loadingDialog.show();
        for (CartItemModel cartItemModel : cartItemModelList) {
            if (cartItemModel.getType() == CartItemModel.CART_ITEM) {

                Map<String,Object> orderDetails = new HashMap<>();
                orderDetails.put("ORDER ID", order_id);
                orderDetails.put("Product Id", cartItemModel.getProductID());
                orderDetails.put("Product Image", cartItemModel.getProductImage());
                orderDetails.put("Product Title", cartItemModel.getProductTitle());
                orderDetails.put("User Id", userID);
                orderDetails.put("Product Quantity", cartItemModel.getProductQuantity());
                if (cartItemModel.getCuttedPrice() != null) {
                    orderDetails.put("Cutted Price", cartItemModel.getCuttedPrice());
                }else {
                    orderDetails.put("Cutted Price", "");
                }
                orderDetails.put("Product Price", cartItemModel.getProductPrice());
                if (cartItemModel.getSelectedCoupenId() != null) {
                    orderDetails.put("Coupen Id", cartItemModel.getSelectedCoupenId());
                }else {
                    orderDetails.put("Coupen Id", "");
                }
                if (cartItemModel.getDiscountedPrice() != null) {
                    orderDetails.put("Discounted Price", cartItemModel.getDiscountedPrice());
                }else {
                    orderDetails.put("Discounted Price", "");
                }
                orderDetails.put("Ordered date", FieldValue.serverTimestamp());
                orderDetails.put("Packed date", FieldValue.serverTimestamp());
                orderDetails.put("Shipped date", FieldValue.serverTimestamp());
                orderDetails.put("Delivered date", FieldValue.serverTimestamp());
                orderDetails.put("Cancelled date", FieldValue.serverTimestamp());
                orderDetails.put("Order Status", "Ordered");
                orderDetails.put("Payment Method", paymentMethod);
                orderDetails.put("Address", fullAddress.getText());
                orderDetails.put("FullName", fullname.getText());
                orderDetails.put("Pincode", pincode.getText());
                orderDetails.put("Free Coupens", cartItemModel.getFreeCoupens());
                orderDetails.put("Delivery Price", cartItemModel.getDeliveryPrice());

                firebaseFirestore.collection("ORDERS").document(order_id).collection("OrderItems").document(cartItemModel.getProductID())
                        .set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()){
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                Map<String,Object> orderDetails = new HashMap<>();
                orderDetails.put("Total Items", cartItemModel.getTotalItems());
                orderDetails.put("Total Items Price", cartItemModel.getTotalItemPrice());
                orderDetails.put("Delivery Price", cartItemModel.getDeliveryPrice());
                orderDetails.put("Total Amount", cartItemModel.getTotalAmount());
                orderDetails.put("Saved Amount", cartItemModel.getSavedAmount());
                orderDetails.put("Payment Status", "not Paid");
                orderDetails.put("Order Status", "Cancelled");

                firebaseFirestore.collection("ORDERS").document(order_id).set(orderDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            if (paymentMethod.equals("ESEWA")){
                                esewa();
                            }else {
                                cod();
                            }

                        }else {
                            String error = task.getException().getMessage();
                            Toast.makeText(DeliveryActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }

    }

    private void esewa(){

        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        loadingDialog.show();

        ESewaConfiguration eSewaConfiguration = new ESewaConfiguration()
                .clientId(M_id)
                .secretKey(M_secret_key)
                .environment(CONFIG_ENVIRONMENT);

        ESewaPayment eSewaPayment = new ESewaPayment(totalAmount.getText().toString().substring(3, totalAmount.getText().length() - 2), customer_id, order_id, null);

        Intent intent = new Intent(DeliveryActivity.this, ESewaPaymentActivity.class);
        intent.putExtra(ESewaConfiguration.ESEWA_CONFIGURATION, eSewaConfiguration);

        intent.putExtra(ESewaPayment.ESEWA_PAYMENT, eSewaPayment);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == RESULT_OK) {
                String s = data.getStringExtra(ESewaPayment.EXTRA_RESULT_MESSAGE);
                Log.i("Proof of Payment", s);
                Toast.makeText(this, "SUCCESSFUL PAYMENT", Toast.LENGTH_SHORT).show();

                /// mycode
                Map<String,Object> updateStatus = new HashMap<>();
                updateStatus.put("Payment Status", "Paid");
                updateStatus.put("Order Status", "Ordered");

                firebaseFirestore.collection("ORDERS").document(order_id).update(updateStatus)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Map<String, Object> userOrder = new HashMap<>();
                                    userOrder.put("order_id", order_id);
                                    firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid()).collection("USER_ORDERS")
                                            .document(order_id).set(userOrder).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                showConfirmationLayout();
                                            }else {
                                                Toast.makeText(DeliveryActivity.this, "Failed to update user's OrderList", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else {
                                    Toast.makeText(DeliveryActivity.this, "Order CANCELLED", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                /// mycode
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Canceled By User", Toast.LENGTH_SHORT).show();
                loadingDialog.dismiss();
            } else if (resultCode == ESewaPayment.RESULT_EXTRAS_INVALID) {
                String s = data.getStringExtra(ESewaPayment.EXTRA_RESULT_MESSAGE);
                Log.i("Proof of Payment", s);
            }
        }
    }

    private void cod(){
        getQtyIDs = false;
        paymentMethodDialog.dismiss();
        Intent otpIntent = new Intent(DeliveryActivity.this,OTPverificationActivity.class);
        otpIntent.putExtra("mobileNo",mobileNo.substring(0,10));
        otpIntent.putExtra("OrderID",order_id);
        startActivity(otpIntent);
    }
}
