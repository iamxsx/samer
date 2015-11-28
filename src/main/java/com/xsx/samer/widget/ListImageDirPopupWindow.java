package com.xsx.samer.widget;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xsx.samer.R;
import com.xsx.samer.adapter.CommonBaseAdapter;
import com.xsx.samer.model.ImageFloder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFloder>
{
    private ListView mListDir;

    public ListImageDirPopupWindow(int width, int height,
                                   List<ImageFloder> datas, View convertView)
    {
        super(convertView, width, height, true, datas);
    }

    @Override
    public void initViews()
    {
        mListDir = (ListView) findViewById(R.id.id_list_dir);
        mListDir.setAdapter(new ListImageAdapter( mDatas,context));
    }

    public class ListImageAdapter extends CommonBaseAdapter<ImageFloder> {

        public ListImageAdapter(List<ImageFloder> list, Context context) {
            super(list, context);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CommonBaseAdapter.ViewHolder viewHolder=CommonBaseAdapter.ViewHolder
                    .getViewHolder(context, convertView, parent, R.layout.list_dir_item, position);
            ImageFloder imageFloder=list.get(position);
            ImageView iv_image=(ImageView) viewHolder.getView(R.id.iv_image);
            TextView tv_name = (TextView) viewHolder.getView(R.id.tv_name);
            TextView tv_count =(TextView) viewHolder.getView(R.id.tv_count);
            ImageLoader.getInstance().displayImage("file:///"+imageFloder.getFirstImagePath(), iv_image);
            tv_name.setText(imageFloder.getName());
            tv_count.setText(imageFloder.getCount()+"张");
            return viewHolder.getConvertView();
        }


    }

    public interface OnImageDirSelected
    {
        void selected(ImageFloder floder);
    }

    private OnImageDirSelected mImageDirSelected;

    public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
    {
        this.mImageDirSelected = mImageDirSelected;
    }

    @Override
    public void initEvents()
    {
        mListDir.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id)
            {

                if (mImageDirSelected != null)
                {
                    mImageDirSelected.selected(mDatas.get(position));
                }
            }
        });
    }

    @Override
    public void init()
    {
        // TODO Auto-generated method stub

    }

    @Override
    protected void beforeInitWeNeedSomeParams(Object... params)
    {
        // TODO Auto-generated method stub
    }

}
