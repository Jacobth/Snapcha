package shutdown.chalmergps.jacobth.snapcha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Map;

public class CustomListAdapter2 extends BaseAdapter {
    private final String[] itemName;
    private Map<String, Integer> imageMap;
    private ProgressBar loader;
    private ImageView imageView;

    private LayoutInflater mInflater;

    public CustomListAdapter2(Context context, String[] itemName, Map<String, Integer> imageMap) {
        mInflater = LayoutInflater.from(context);
        this.itemName = itemName;
        this.imageMap = imageMap;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageMap.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return itemName[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.rowlayout, null);
        String key = itemName[position];

        imageView = (ImageView) convertView.findViewById(R.id.icon);
        TextView nameText = (TextView) convertView.findViewById(R.id.nameText);
        TextView dateText = (TextView) convertView.findViewById(R.id.dateText);
        loader = (ProgressBar) convertView.findViewById(R.id.loader);

        imageView.setImageResource(imageMap.get(key));
        nameText.setText(itemName[position]);
        dateText.setText("Position " + (position + 1));

        if(MainActivity.getNames.containsKey(itemName[position])) {
            boolean isLoading = MainActivity.getNames.get(itemName[position]);
            if (MainActivity.isLoading && isLoading) {
                showLoader();
            } else {
                hideLoader();
            }
        }

        return convertView;
    }

    private void showLoader() {
        imageView.setVisibility(View.INVISIBLE);
        loader.setVisibility(View.VISIBLE);
    }

    private void hideLoader() {
        imageView.setVisibility(View.VISIBLE);
        loader.setVisibility(View.INVISIBLE);
    }
}

