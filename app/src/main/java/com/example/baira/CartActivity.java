package com.example.baira;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.baira.adapters.CartAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends BaseActivity {
    private static final String TAG = "CartActivity";

    private RecyclerView cartRecyclerView;
    private MaterialButton checkoutButton;
    private CartAdapter adapter;
    private TextInputEditText editTextPhone;
    private TextInputEditText editTextAddress;
    private TextInputLayout tilPhone;
    private TextInputLayout tilAddress;
    private RadioGroup radioGroupDeliveryMethod;
    private RadioButton radioPickup;
    private RadioButton radioDelivery;
    private View layoutStoreInfo;
    private View layoutDeliveryInfo;
    private MaterialButton btnShowMap;

    // Координаты магазина в Бишкеке
    private static final double STORE_LATITUDE = 42.8772;  // Координаты на проспекте Чуй, 120
    private static final double STORE_LONGITUDE = 74.6031;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate: Инициализация CartActivity");

        // Find views
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        checkoutButton = findViewById(R.id.checkoutButton);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextAddress = findViewById(R.id.editTextAddress);
        tilPhone = findViewById(R.id.tilPhone);
        tilAddress = findViewById(R.id.tilAddress);
        radioGroupDeliveryMethod = findViewById(R.id.radioGroupDeliveryMethod);
        radioPickup = findViewById(R.id.radioPickup);
        radioDelivery = findViewById(R.id.radioDelivery);
        layoutStoreInfo = findViewById(R.id.layoutStoreInfo);
        layoutDeliveryInfo = findViewById(R.id.layoutDeliveryInfo);
        btnShowMap = findViewById(R.id.btnShowMap);

        Log.d(TAG, "Все view найдены");

        // Set up adapter
        adapter = new CartAdapter(CartManager.getCartItems(), product -> {
            CartManager.removeFromCart(product);
            adapter.updateCart(CartManager.getCartItems());
            updateCheckoutButton();
            showRemoveFromCartFeedback(product.getName());
        });

        // Set up recycler view
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartRecyclerView.setAdapter(adapter);

        // Restore cart from storage
        CartManager.restoreCart(this);
        adapter.updateCart(CartManager.getCartItems());
        updateCheckoutButton();

        // Set up delivery method radio buttons
        radioGroupDeliveryMethod.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "Выбран способ получения: " + (checkedId == R.id.radioPickup ? "Самовывоз" : "Доставка"));
            if (checkedId == R.id.radioPickup) {
                layoutStoreInfo.setVisibility(View.VISIBLE);
                layoutDeliveryInfo.setVisibility(View.GONE);
            } else {
                layoutStoreInfo.setVisibility(View.GONE);
                layoutDeliveryInfo.setVisibility(View.VISIBLE);
            }
        });

        // Set up map button
        btnShowMap.setOnClickListener(v -> {
            Log.d(TAG, "Нажата кнопка показать на карте");
            openLocation(STORE_LATITUDE, STORE_LONGITUDE, getString(R.string.store_name));
        });

        // Set up checkout button
        checkoutButton.setOnClickListener(v -> {
            if (CartManager.getCartItems().isEmpty()) {
                Toast.makeText(this, R.string.cart_empty, Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate inputs
            boolean isValid = true;
            String phone = editTextPhone.getText().toString().trim();

            if (phone.isEmpty()) {
                tilPhone.setError("Введите телефон");
                isValid = false;
            } else {
                tilPhone.setError(null);
            }

            // Validate address only if delivery is selected
            String address = "";
            if (radioDelivery.isChecked()) {
                address = editTextAddress.getText().toString().trim();
                if (address.isEmpty()) {
                    tilAddress.setError("Введите адрес");
                    isValid = false;
                } else {
                    tilAddress.setError(null);
                }
            } else {
                // For pickup, use store address
                address = getString(R.string.store_address);
            }

            if (!isValid) {
                return;
            }

            showPaymentDialog(phone, address);
        });
    }

    /**
     * Открывает геолокацию на карте
     */
    private void openLocation(double latitude, double longitude, String locationName) {
        Log.d(TAG, "Открытие карты для локации: " + locationName);

        // Пытаемся открыть прямую ссылку на 2GIS
        try {
            String url = "https://2gis.kg/bishkek/geo/70000001038627052";
            Log.d(TAG, "Открытие URL: " + url);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
            return;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при открытии 2GIS в браузере", e);
        }

        // Пробуем открыть в приложении 2GIS
        try {
            Uri dgisUri = Uri.parse("dgis://2gis.kg/bishkek/geo/70000001038627052");
            Intent dgisIntent = new Intent(Intent.ACTION_VIEW, dgisUri);
            dgisIntent.setPackage("kg.2gis");
            startActivity(dgisIntent);
            Log.d(TAG, "Открыто в приложении 2GIS");
            return;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при открытии приложения 2GIS", e);
            // 2GIS не установлен, пробуем Google Maps
        }

        // Пробуем открыть в Google Maps
        try {
            Uri mapUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + Uri.encode(locationName) + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
                Log.d(TAG, "Открыто в Google Maps");
                return;
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при открытии Google Maps", e);
        }

        // Если все предыдущие методы не сработали, открываем браузер с Google Maps
        try {
            Uri browserUri = Uri.parse("https://maps.google.com/maps?q=" + latitude + "," + longitude);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, browserUri);
            startActivity(browserIntent);
            Log.d(TAG, "Открыто в браузере");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при открытии карты в браузере", e);
            Toast.makeText(this, "Не удалось открыть карту. Пожалуйста, установите приложение карт.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cart;
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.cart;
    }

    private void updateCheckoutButton() {
        double total = CartManager.getTotalPrice();
        String buttonText = getString(R.string.checkout);
        if (total > 0) {
            buttonText += String.format(" (%.0f KGS)", total);
        }
        checkoutButton.setText(buttonText);

        // Update visibility of empty state
        if (CartManager.getCartItems().isEmpty()) {
            findViewById(R.id.emptyCartLayout).setVisibility(View.VISIBLE);
            findViewById(R.id.cartContentLayout).setVisibility(View.GONE);
        } else {
            findViewById(R.id.emptyCartLayout).setVisibility(View.GONE);
            findViewById(R.id.cartContentLayout).setVisibility(View.VISIBLE);
        }
    }

    private void showPaymentDialog(String phone, String address) {
        String[] paymentMethods = {
            getString(R.string.payment_cash),
            getString(R.string.payment_card)
        };

        new AlertDialog.Builder(this)
            .setTitle(R.string.payment_method)
            .setItems(paymentMethods, (dialog, which) -> {
                // Process the order
                processOrder(phone, address, paymentMethods[which]);
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    private void processOrder(String phone, String address, String paymentMethod) {
        // Собираем данные заказа
        Map<String, Object> order = new HashMap<>();
        order.put("phone", phone);
        order.put("address", address);
        order.put("paymentMethod", paymentMethod);
        order.put("timestamp", System.currentTimeMillis());
        // Если есть авторизация, можно добавить userId
        // order.put("userId", FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null);
        order.put("items", CartManager.getCartItems());
        order.put("total", CartManager.getTotalPrice());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("orders")
            .add(order)
            .addOnSuccessListener(documentReference -> {
                CartManager.clearCart();
                CartManager.saveCart(this);
                adapter.updateCart(CartManager.getCartItems());
                updateCheckoutButton();
                showSuccessScreen(phone, address, paymentMethod);
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Ошибка при оформлении заказа: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void showSuccessScreen(String phone, String address, String paymentMethod) {
        String message = "Ваш заказ успешно оформлен.\n\n" +
                "Телефон: " + phone + "\n";

        if (radioDelivery.isChecked()) {
            message += "Способ получения: " + getString(R.string.delivery) + "\n" +
                    "Адрес: " + address + "\n";
        } else {
            message += "Способ получения: " + getString(R.string.self_pickup) + "\n" +
                    "Адрес магазина: " + address + "\n";
        }

        message += "Способ оплаты: " + paymentMethod + "\n\n" +
                "Мы свяжемся с вами в ближайшее время для подтверждения заказа.";

        new AlertDialog.Builder(this)
            .setTitle(R.string.order_success_title)
            .setMessage(message)
            .setPositiveButton("ОК", (dialog, which) -> navigateToActivity(HomeActivity.class))
            .setCancelable(false)
            .show();
    }

    private void showRemoveFromCartFeedback(String productName) {
        if (productName != null) {
            Toast.makeText(
                this,
                "\"" + productName + "\" удален из корзины",
                Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        CartManager.saveCart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CartManager.restoreCart(this);
        adapter.updateCart(CartManager.getCartItems());
        updateCheckoutButton();
    }
}