package ru.mirea.gribinichenko.mireaproject.ui.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.mirea.gribinichenko.mireaproject.databinding.FragmentSharedBinding;

public class SharedFragment extends Fragment {
    private FragmentSharedBinding binding;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSharedBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        sharedPreferences = requireActivity().getSharedPreferences("MyProfile", Context.MODE_PRIVATE);

        binding.saveButton.setOnClickListener(v -> saveProfile());

        loadProfile();

        return view;
    }

    private void saveProfile() {
        String username = binding.editName.getText().toString();
        String userSecondName = binding.editSecondName.getText().toString();
        int age = Integer.parseInt(binding.editAge.getText().toString());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Name", username);
        editor.putString("SecondName", userSecondName);
        editor.putInt("Age", age);
        editor.apply();
    }

    private void loadProfile() {
        String username = sharedPreferences.getString("Name", "");
        String userSecondName = sharedPreferences.getString("SecondName", "");
        int age = sharedPreferences.getInt("Age", 0);

        binding.editName.setText(username);
        binding.editSecondName.setText(userSecondName);
        binding.editAge.setText(String.valueOf(age));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}