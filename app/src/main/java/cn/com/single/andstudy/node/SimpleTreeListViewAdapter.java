package cn.com.single.andstudy.node;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.com.single.andstudy.R;

/**
 * @author li
 *         Create on 2018/6/25.
 * @Description
 */

public class SimpleTreeListViewAdapter<T> extends TreeListViewAdapter {
    public SimpleTreeListViewAdapter(ListView tree, Context context
            , List<T> datas, int defaultTreeLevel) throws IllegalAccessException {
        super(tree, context, datas, defaultTreeLevel);
    }

    @Override
    public View getConvertView(Node node, int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.list_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.id_item_icon);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.id_item_name);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(node.getIcon() == -1){
            viewHolder.ivIcon.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.ivIcon.setVisibility(View.VISIBLE);
            viewHolder.ivIcon.setImageResource(node.getIcon());
        }

        viewHolder.tvName.setText(node.getName());

        return convertView;
    }

    /**
     * 动态插入节点
     * @param position
     * @param name
     */
    public void addExtraNode(int position, String name) {
        Node node = (Node) mVisibleNodes.get(position);
        int indexOf = mAllNodes.indexOf(node);
        //
        Node extraNode = new Node(-1,node.getId(),name);
        extraNode.setParent(node);
        node.getChildren().add(extraNode);

        mAllNodes.add(indexOf + 1,extraNode);

        mVisibleNodes = TreeHelper.filterVisibleNodes(mAllNodes);
        notifyDataSetChanged();
    }

    private class ViewHolder{
        ImageView ivIcon;
        TextView tvName;
    }
}
