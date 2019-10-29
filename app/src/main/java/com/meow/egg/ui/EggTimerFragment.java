package com.meow.egg.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meow.egg.R;
import com.meow.egg.databinding.FragmentEggTimerBinding;


public final class EggTimerFragment extends Fragment {
    private static final String TOPIC = "breakfast";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // using data binding
        final FragmentEggTimerBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_egg_timer, container, false
        );
        final EggTimerViewModel viewModel = ViewModelProviders.of(this)
                .get(EggTimerViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

}
