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
public class DetailImageFragment_backUp extends Fragment {

    private Context context;
    private OnFragmentInteractionListener mListener;
    private ImageManager imageManager;



    public DetailImageFragment_backUp(Context context) {
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
//            String stringUri = args.getString("img");
//            Uri uri = Uri.parse(stringUri);

//            //base64로 받아와 처리, 용량문제로 Intent 전송이 되지않는다(intent를 Uri로 넘겨주고 Bundle로 넘길때 Base64로 변환해서 넘겼다) -> 첫번째 이미지로만 뜬다, 이미지가 옆으로 돌아간다.
//            String base64str =args.getString("img");
//            byte[] decodedByteArray = Base64.decode(base64str,Base64.NO_WRAP);
//            Bitmap decodeBitmap = BitmapFactory.decodeByteArray(decodedByteArray,0,decodedByteArray.length);
////            Log.e("frg", String.valueOf(decodeBitmap));
//            imageView.setImageBitmap(decodeBitmap);
            //절대경로로 받은 이미지 처리.
            String imgFileUri = args.getString("img");
            imageView.setImageBitmap(imageManager.resizeBitmapImage(imageManager.DecodeBitmapFile(imgFileUri),600));


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
