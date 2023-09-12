package ru.mirea.gribinichenko.mireaproject.ui.music;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.mirea.gribinichenko.mireaproject.R;
import ru.mirea.gribinichenko.mireaproject.databinding.FragmentPlayerBinding;

public class PlayerFragment extends Fragment{


    private final Handler handler = new Handler();
    private Handler mHandler = new Handler();
    MediaPlayer mediaPlayer;
    private FragmentPlayerBinding binding;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private boolean play = false;
    private String mParam1;
    private String mParam2;

    public PlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance(String param1, String param2) {
        PlayerFragment fragment = new PlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPlayerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        binding.button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (play == false) {
                    play = true;
                    binding.button3.setImageResource(android.R.drawable.ic_media_pause);
                    binding.textView1.setText("Полчаса");
                    binding.textView2.setText("Velial Squad");
                    binding.image.setImageResource(R.drawable.velials);

                    Intent serviceIntent = new Intent(getActivity(), PlayerService.class);
                    ContextCompat.startForegroundService(getActivity(), serviceIntent);
                } else {
                    getActivity().stopService(new Intent(getActivity(), PlayerService.class));
                    binding.button3.setImageResource(android.R.drawable.ic_media_play);
                    binding.textView1.setText("SongName");
                    binding.textView2.setText("GroupName");
                    binding.image.setImageResource(R.drawable.ym);
                    play = false;
                }
            }
        });

        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return root;
    }
}
