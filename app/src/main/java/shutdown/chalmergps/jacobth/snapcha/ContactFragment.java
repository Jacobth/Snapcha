package shutdown.chalmergps.jacobth.snapcha;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ContactFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ListView contactView;
    public SwipeRefreshLayout refreshLayout;
    public CustomListAdapter2 adapter;
    private String[] arrayList;
    private ArrayList<Integer> imgList;
    private ImageView imageView;
    public static List<Sender> sendList;
    public static Map<String, Integer> iconMap;
    private static final int REQUEST_CODE = 1;
    private ArrayList<String> sentTList;
    public static String receiver;

    public ContactFragment() {
        // Required empty public constructor
    }

    public interface FragmentChangeListener
    {
        void replaceFragment(Fragment fragment);
    }

    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
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

        imgList = new ArrayList<>();
        iconMap = new HashMap<>();
        getContacts();

        sendList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_contact, container, false);
        adapter = new CustomListAdapter2(getContext(), MainActivity.contactList, iconMap);
        contactView = (ListView)view.findViewById(R.id.contactView);
        contactView.setAdapter(adapter);

        receiver = "";

        contactView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(MainActivity.imageMap.get(MainActivity.contactList[position]) != null) {
                    String key = MainActivity.contactList[position];
                    iconMap.remove(key);
                    iconMap.put(key, R.mipmap.open);
                    CustomViewPager mPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
                    mPager.setPagingEnabled(false);
                    Fragment fr = new ImageFragment();
                    Bundle args = new Bundle();
                    args.putString("param1", key);
                    fr.setArguments(args);
                    FragmentChangeListener fc=(FragmentChangeListener)getActivity();
                    fc.replaceFragment(fr);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        contactView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String receiver = MainActivity.contactList[position];
                ContactFragment.receiver = receiver;
                CustomViewPager mPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
                mPager.setPagingEnabled(true);
                mPager.setCurrentItem(MainActivity.CAM_PAGE);
                return false;
            }
        });

        Button cameraButton = (Button)view.findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager mPager = (ViewPager) getActivity().findViewById(R.id.pager);
                mPager.setCurrentItem(MainActivity.CAM_PAGE);
            }
        });

        Button refreshButton = (Button)view.findViewById(R.id.button_refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImages();
                System.out.println("Size of photo map: " + MainActivity.imageMap.size());
                System.out.println("size of sendlist: " + sendList.size());
            }
        });

        refreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
     //           refreshLayout.setRefreshing(true);
                getImages();
            }
        });

        contactView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(contactView != null && contactView.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = contactView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = contactView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                refreshLayout.setEnabled(enable);
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
        System.out.println("resumed");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getContacts() {
        for(String contact : MainActivity.contactList) {
            iconMap.put(contact, R.mipmap.open);
        }
    }

    private void getImages() {
        mCallback.onGetImage(MainActivity.sendList);
    }

    @Override
    public void onResume() {
        super.onResume();
        CustomViewPager mPager = (CustomViewPager) getActivity().findViewById(R.id.pager);
        mPager.setPagingEnabled(true);
        // mPager.setCurrentItem(0);
        receiver = "";
        System.out.println("resumed");
    }

    private OnGetImageListener mCallback;

    public interface OnGetImageListener {
        void onGetImage(List<Sender> sender);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mCallback = (OnGetImageListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
