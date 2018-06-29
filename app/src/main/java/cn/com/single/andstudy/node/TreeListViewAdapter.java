package cn.com.single.andstudy.node;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * @author li
 *         Create on 2018/6/25.
 * @Description
 */

public abstract class TreeListViewAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected List<Node> mAllNodes;
    protected List<Node> mVisibleNodes;
    protected LayoutInflater mInflater;

    protected ListView mTree;


    public interface OnTreeNodeClickListener{
        void onClick(Node node,int position);
    }

    private OnTreeNodeClickListener mOnTreeNodeClickListener;

    public void setOnTreeNodeClickListener(OnTreeNodeClickListener listener){
        mOnTreeNodeClickListener = listener;
    }

    private int level;

    public TreeListViewAdapter(ListView tree,Context context, List<T> datas, int defaultTreeLevel) throws IllegalAccessException {
        this.level = defaultTreeLevel;
        this.mContext = context;
        mTree = tree;

        mInflater = LayoutInflater.from(context);

        mAllNodes = TreeHelper.getSortedNodes(datas,defaultTreeLevel);

        mVisibleNodes = TreeHelper.filterVisibleNodes(mAllNodes);

        mTree.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                expandOrCollapse(position);

                if(mOnTreeNodeClickListener != null){
                    mOnTreeNodeClickListener.onClick(mVisibleNodes.get(position),position);
                }
            }
        });
    }

    /**
     * 点击收缩或展开
     * @param position
     */
    private void expandOrCollapse(int position) {
        Node n = mVisibleNodes.get(position);
        if(n != null){
            if(n.isLeaf()){
                return;
            }
            n.setExpand(!n.isExpand());

            mVisibleNodes = TreeHelper.filterVisibleNodes(mAllNodes);
            notifyDataSetChanged();
        }
    }


    @Override
    public int getCount() {
        return mVisibleNodes.size();
    }

    @Override
    public Object getItem(int position) {
        return mVisibleNodes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Node node = mVisibleNodes.get(position);

        convertView = getConvertView(node,position,convertView,parent);
        //设置内边距
        convertView.setPadding(node.getLevel()*30 ,3,3,3);

        return convertView;
    }

    /**
     *
     * @param node
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public abstract View getConvertView(Node node,int position,View convertView ,ViewGroup parent);

}
