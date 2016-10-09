package shutdown.chalmergps.jacobth.snapcha;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity implements CameraFragment.OnFragmentInteractionListener,
        ContactFragment.OnFragmentInteractionListener, ImageFragment.OnFragmentInteractionListener,
        ContactFragment.FragmentChangeListener, SendListFragment.OnFragmentInteractionListener,
        CameraFragment.OnSendListener, SendListFragment.OnSendListListener, ContactFragment.OnGetImageListener,
        SettingsFragment.OnFragmentInteractionListener{

    private static final int NUM_PAGES = 2;
    public static final int CONTACT_PAGE = 0;
    public static final int CAM_PAGE = 1;
    private CustomViewPager mPager;
    private PagerAdapter mPagerAdapter;
    public static LruCache<String, List<Bitmap>> imageMap;
    public static List<Sender> sendList;
    public static String[] contactList;
    public static boolean isReturn;
    public static boolean isLoading;
    public static Map<String, Boolean> getNames;

    private CameraFragment cameraFragment;
    private ContactFragment contactFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mUpdateReceiver,
                new IntentFilter("load"));

        contactList = SendObject.getContacts();

        getNames = new HashMap<>();
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(position == CONTACT_PAGE) {
                    ContactFragment.receiver = "";
                    if(cameraFragment != null)
                        cameraFragment.setBackInvisible();
                    if(contactFragment != null)
                        contactFragment.adapter.notifyDataSetChanged();
                }
                else if(position == CAM_PAGE) {
                    if(ContactFragment.receiver != "") {
                        cameraFragment.setBackVisible();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        int size = 10 * 1024 * 1024;
        imageMap = new LruCache<>(size);
        sendList = new ArrayList<>();
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == CONTACT_PAGE) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            ContactFragment.receiver = "";
            System.out.println("here now");
            mPager.setPagingEnabled(true);
            super.onBackPressed();
        } else {
            mPager.setPagingEnabled(true);
            // Otherwise, select the previous step.
            if(cameraFragment != null)
                cameraFragment.resetCamera();
            if(mPager.getCurrentItem() != 1)
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    @Override
    public void onArticleSelected(String[] receiver) {
        new SendFilesTask().execute(receiver);
    }

    @Override
    public void onSendList(String[] receiver) {
        new SendFilesTask().execute(receiver);
    }

    @Override
    public void onGetImage(List<Sender> sender) {
        new GetFilesTask().execute(sender);
    }

    private class SendFilesTask extends AsyncTask<String[], Integer, Long> {
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected Long doInBackground(String[]... params) {
            int count = params[0].length;
            long totalSize = 0;
            for (int i = 0; i < count; i++) {
                SendObject.sendImage(params[0][i]);
                publishProgress((int) ((i / (float) count) * 100));
                if (isCancelled()) break;
            }
            return totalSize;
        }

        @Override
        protected void onPostExecute(Long result) {
            isLoading = false;
            getNames = new HashMap<>();
            contactFragment.adapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            isLoading = true;
            contactFragment.adapter.notifyDataSetChanged();
        }
    }

    private class GetFilesTask extends AsyncTask<List<Sender>, Integer, Long> {
        protected void onProgressUpdate(Integer... progress) {
        }

        @Override
        protected Long doInBackground(List<Sender>... params) {
            getSenders();
            int count = params[0].size();
            long totalSize = 0;
            for (int i = 0; i < count; i++) {
                SendObject.getImage(params[0].get(i));
                publishProgress((int) ((i / (float) count) * 100));
                if (isCancelled()) break;
            }
            return totalSize;
        }

        @Override
        protected void onPostExecute(Long result) {
            isLoading = false;
            getNames = new HashMap<>();
            if(contactFragment.adapter != null)
                contactFragment.adapter.notifyDataSetChanged();

            contactFragment.refreshLayout.setRefreshing(false);
        }

        @Override
        protected void onPreExecute() {
            //isLoading = true;
            //contactFragment.adapter.notifyDataSetChanged();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == CONTACT_PAGE) {
                contactFragment = new ContactFragment();
                return contactFragment;
            }
            else {
                cameraFragment = new CameraFragment();
                return cameraFragment;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("refreshed now");
        if(FirebaseMessagingService.hasImage) {
            new GetFilesTask().execute(sendList);
            FirebaseMessagingService.hasImage = false;
        }
    }

    private void getSenders() {
        String message = SendObject.getMessage();
        System.out.println("new message: " + message);
        if(message != "") {
            String[] tmp = message.split("\\,");
            for(int i = 0; i < tmp.length; i++) {
                String[] sender = tmp[i].split("\\:");
                MainActivity.sendList.add(new Sender(sender[0], sender[1]));
                ContactFragment.iconMap.remove(sender[0]);
                ContactFragment.iconMap.put(sender[0], R.mipmap.recive);
                MainActivity.getNames.put(sender[0], true);
                MainActivity.isLoading = true;
            }
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            ContactFragment.iconMap.remove(message);
            ContactFragment.iconMap.put(message, R.mipmap.opened);
            contactFragment.adapter.notifyDataSetChanged();
        }
    };

    private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new GetFilesTask().execute(sendList);
        }
    };

    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        System.out.println("destroyed");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
       // saveIcons();
    }

    private void saveIcons() {
        File file = new File(getDir("data", MODE_PRIVATE), "map");
        ObjectOutputStream outputStream = null;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(ContactFragment.iconMap);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSavedIcons() {
        try {
            ObjectInputStream os = new ObjectInputStream(new FileInputStream("map"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
