package com.example.locationlog_02.Activitys.MyMapLog.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.locationlog_02.Manager.ImageManager;
import com.example.locationlog_02.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DetailImageFragment extends Fragment {

    private Context context;
    private OnFragmentInteractionListener mListener;
    private ImageManager imageManager;



    public DetailImageFragment(Context context) {
        // Required empty public constructor
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_detail_image, container, false);
        ImageView imageView = view.findViewById(R.id.fragment_detailimage_imageView);

        imageManager = new ImageManager(context);

        if(getArguments() !=null){
            Bundle args = getArguments();

            //Uri로 처리 -> 속도가 느려

            //절대경로로 받은 이미지 처리.
            String imgFileUri = args.getString("img");
            imageView.setImageBitmap(imageManager.resizeBitmapImage(imageManager.DecodeBitmapFile(imgFileUri),1400));


        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
