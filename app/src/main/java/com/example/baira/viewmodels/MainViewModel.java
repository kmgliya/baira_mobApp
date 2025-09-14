package com.example.baira.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.baira.models.Category;
import com.example.baira.models.Product;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private final MutableLiveData<List<Category>> _categories = new MutableLiveData<>();
    public LiveData<List<Category>> categories = _categories;

    private final MutableLiveData<List<Product>> _products = new MutableLiveData<>();
    public LiveData<List<Product>> products = _products;

    public void loadCategories() {
        firestore.collection("categories").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot categoriesSnapshot = task.getResult();
                List<Category> categoriesList = new ArrayList<>();
                for (DocumentSnapshot doc : categoriesSnapshot.getDocuments()) {
                    Category category = doc.toObject(Category.class);
                    if (category != null) {
                        categoriesList.add(category);
                    }
                }
                _categories.setValue(categoriesList);
            } else {
                // Handle error
            }
        });
    }

    public void loadProducts() {
        firestore.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot productsSnapshot = task.getResult();
                List<Product> productsList = new ArrayList<>();
                for (DocumentSnapshot doc : productsSnapshot.getDocuments()) {
                    Product product = doc.toObject(Product.class);
                    if (product != null) {
                        productsList.add(product);
                    }
                }
                _products.setValue(productsList);
            } else {
                // Handle error
            }
        });
    }
} 