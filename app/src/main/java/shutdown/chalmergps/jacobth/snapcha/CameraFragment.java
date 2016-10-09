package shutdown.chalmergps.jacobth.snapcha;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SlidingDrawer;

import com.nikoyuwono.toolbarpanel.ToolbarPanelLayout;
import com.nikoyuwono.toolbarpanel.ToolbarPanelListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class CameraFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FrameLayout preview;
    private View view;

    private Camera mCamera;
    private CameraPreview mPreview;
    private Button sendButton;
    private Button captureButton;
    private Button backButton;
    private ImageButton switchButton;
    private ImageButton contactButton;
    private Button slideButton;
    private ToolbarPanelLayout layout;
    public static byte[] image;
    private static final int cameraFrontId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private static final int cameraBackId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static int cameraId;
    private String receiver;

    private OnFragmentInteractionListener mListener;

    public CameraFragment() {
        // Required empty public constructor
    }

    public static CameraFragment newInstance(String param1, String param2) {
        CameraFragment fragment = new CameraFragment();
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
        cameraId = cameraBackId;
        receiver = mParam1;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mCamera = getCameraInstance();
        view = inflater.inflate(R.layout.fragment_camera, container, false);

        mPreview = new CameraPreview(view.getContext(), mCamera);
        preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        preview.addView(mPreview);

        captureButton = (Button) view.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an image from the mCamera
                captureButton.setVisibility(View.INVISIBLE);
                mCamera.takePicture(null, null, mPicture);
                CustomViewPager mPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
                mPager.setPagingEnabled(false);
            }
        }
        );

        backButton = (Button)view.findViewById(R.id.back_button_long);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomViewPager mPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
                mPager.setCurrentItem(MainActivity.CONTACT_PAGE);
            }
        });

        switchButton = (ImageButton) view.findViewById(R.id.button_switch);
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchCamera();
            }
        });

        sendButton = (Button)view.findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String receiver = ContactFragment.receiver;
                if(receiver != "") {
                    MainActivity.isReturn = true;
                    //postData(image, receiver);
                    ContactFragment.receiver = "";
                    String[] s = new String[1];
                    s[0] = receiver;
                    System.out.println(s[0]);
                    MainActivity.getNames.put(receiver, true);
                    // SendImageThread sendImageThread = new SendImageThread(s);
                    // sendImageThread.start();
                    mCallback.onArticleSelected(s);
                }
                else {
                    Fragment fr = new SendListFragment();
                    ContactFragment.FragmentChangeListener fc=(ContactFragment.FragmentChangeListener)getActivity();
                    fc.replaceFragment(fr);
                }

                mCamera.stopPreview();
                mCamera.startPreview();

                CustomViewPager mPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
                mPager.setPagingEnabled(true);
                mPager.setCurrentItem(0);

                captureButton.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.INVISIBLE);
            }
        });

        contactButton = (ImageButton)view.findViewById(R.id.button_contacs);
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomViewPager mPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
                mPager.setCurrentItem(MainActivity.CONTACT_PAGE);
            }
        });

        layout = (ToolbarPanelLayout)view.findViewById(R.id.sliding_down_toolbar_layout);

        slideButton = (Button)view.findViewById(R.id.button_slide);
        slideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.openPanel();
                setAllInvisible();
                CustomViewPager mPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
                mPager.setPagingEnabled(false);
            }
        });

        layout.setToolbarPanelListener(new ToolbarPanelListener() {
            @Override
            public void onPanelSlide(Toolbar toolbar, View panelView, float slideOffset) {
                if(slideOffset == 0) {
                    setAllVisible();
                    CustomViewPager mPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
                    mPager.setPagingEnabled(true);
                }
                //System.out.println("offest: " + slideOffset);
            }

            @Override
            public void onPanelOpened(Toolbar toolbar, View panelView) {
            }

            @Override
            public void onPanelClosed(Toolbar toolbar, View panelView) {
                //System.out.println("called?");
                setAllVisible();
            }
        });

        Button resetButton = (Button)view.findViewById(R.id.button_reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.closePanel();
            }
        });

        Button settingsButton = (Button)view.findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fr = new SettingsFragment();
                ContactFragment.FragmentChangeListener fc=(ContactFragment.FragmentChangeListener)getActivity();
                fc.replaceFragment(fr);
                System.out.println("cliked settings");
            }
        });

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


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_send) {

        }
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if mCamera is unavailable
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            image = data;
            sendButton.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        if (mCamera != null)
        {
            mCamera.setPreviewCallback(null);
            mPreview.getHolder().removeCallback(mPreview);
           // mCamera.stopPreview();
            mCamera.release();
        }
    }

    public void resetCamera() {
        mCamera.stopPreview();
        mCamera.startPreview();

        sendButton.setVisibility(View.INVISIBLE);
        captureButton.setVisibility(View.VISIBLE);
    }

    public void setBackVisible() {
        if(backButton != null && contactButton != null) {
            backButton.setVisibility(View.VISIBLE);
            contactButton.setVisibility(View.INVISIBLE);
        }
    }

    public void setBackInvisible() {
        if(backButton != null && contactButton != null) {
            backButton.setVisibility(View.INVISIBLE);
            contactButton.setVisibility(View.VISIBLE);
        }
    }

    private OnSendListener mCallback;

    public interface OnSendListener {
        void onArticleSelected(String[] receiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mCallback = (OnSendListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCamera = getCameraInstance();
        // create a basic camera preview class that can be included in a View layout.
        mPreview = new CameraPreview(view.getContext(), mCamera);
        //add your preview class to the FrameLayout element.
        preview.addView(mPreview);
    }

    private void setAllInvisible() {
        contactButton.setVisibility(View.INVISIBLE);
        switchButton.setVisibility(View.INVISIBLE);
        slideButton.setVisibility(View.INVISIBLE);
        captureButton.setVisibility(View.INVISIBLE);
    }

    private void setAllVisible() {
        contactButton.setVisibility(View.VISIBLE);
        switchButton.setVisibility(View.VISIBLE);
        slideButton.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.VISIBLE);
    }

    private void switchCamera() {
        mCamera.stopPreview();
        mCamera.release();

        if(cameraId == cameraBackId) {
            cameraId = cameraFrontId;
        }
        else {
            cameraId = cameraBackId;
        }

        mCamera = Camera.open(cameraBackId);

        System.out.println("back id: " + cameraBackId);
        System.out.println("front id: " + cameraFrontId);

        try {
            //this step is critical or preview on new camera will no know where to render to
            mCamera.setPreviewDisplay(mPreview.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }
}
