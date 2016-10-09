package shutdown.chalmergps.jacobth.snapcha;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class SendListFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ListView contactView;
    private CustomSendAdapter2 adapter;
    private Map<String, Integer> iconMap;
    private String nameString;
    private Map<Integer, String> receiverMap;

    private OnFragmentInteractionListener mListener;

    public SendListFragment() {
        // Required empty public constructor
    }

    public static SendListFragment newInstance(String param1, String param2) {
        SendListFragment fragment = new SendListFragment();
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

        nameString = "";
        iconMap = new HashMap<>();
        receiverMap = new HashMap<>();
        addIcons();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_list, container, false);

        final TextView nameText = (TextView)view.findViewById(R.id.senderText);

        adapter=new CustomSendAdapter2(getContext(), MainActivity.contactList, iconMap);

        contactView = (ListView)view.findViewById(R.id.contactSendView);
        contactView.setAdapter(adapter);
        contactView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String tmpName = MainActivity.contactList[position];
                if(isAdded(receiverMap, position)) {
                    receiverMap.remove(position);
                    iconMap.remove(tmpName);
                    iconMap.put(tmpName, R.mipmap.uncheck);
                    MainActivity.getNames.remove(tmpName);
                    MainActivity.getNames.put(tmpName, false);
                }
                else {
                    receiverMap.put(position, tmpName);
                    iconMap.put(tmpName, R.mipmap.check);
                    MainActivity.getNames.put(tmpName, true);
                }
                String[] nameList = receiverMap.values().toArray(new String[0]);
                String names = TextUtils.join(", ", nameList);
                nameText.setText(names);
                adapter.notifyDataSetChanged();
            }
        });

        ImageButton sendButton = (ImageButton)view.findViewById(R.id.sendmulti);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SendImageThread thread = new SendImageThread(receiverMap.values().toArray(new String[0]));
                // thread.start();
                mCallback.onSendList(receiverMap.values().toArray(new String[0]));
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void addIcons() {
        for(String key : MainActivity.contactList)
            iconMap.put(key, R.mipmap.uncheck);
    }

    private boolean isAdded(Map<Integer, String> map, int key) {
        if(map.containsKey(key)) {
            return true;
        }
        else {
            return false;
        }
    }

    private OnSendListListener mCallback;

    public interface OnSendListListener {
        void onSendList(String[] receiver);
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            mCallback = (OnSendListListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
