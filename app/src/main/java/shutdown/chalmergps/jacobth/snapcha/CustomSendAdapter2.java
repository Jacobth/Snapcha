package shutdown.chalmergps.jacobth.snapcha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

public class CustomSendAdapter2 extends BaseAdapter {

    private final String[] itemName;
    private Map<String, Integer> imageMap;

    private LayoutInflater mInflater;

    public CustomSendAdapter2(Context context, String[] itemName, Map<String, Integer> imageMap) {
        mInflater = LayoutInflater.from(context);

        this.itemName =itemName;
        this.imageMap = imageMap;
    }

    @Override
    public int getCount() {
        return imageMap.size();
    }

    @Override
    public Object getItem(int position) {
        return itemName[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View view, ViewGroup parent) {
        view = mInflater.inflate(R.layout.sendrowlayout, null);

        String key = itemName[position];

        ImageView imageView = (ImageView) view.findViewById(R.id.box);
        TextView nameText = (TextView) view.findViewById(R.id.nameTextSend);

        imageView.setImageResource(imageMap.get(key));
        nameText.setText(itemName[position]);

        return view;
    }
}

