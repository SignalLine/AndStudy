package cn.com.single.andstudy.node;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.com.single.andstudy.R;

public class TreeNodeActivity extends AppCompatActivity {

    private ListView mTree;

    private SimpleTreeListViewAdapter<FileBean> mAdapter;
    private List<FileBean> mDatas;

    private List<OrgBean> orgBeans;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_node);

        mTree = (ListView) findViewById(R.id.id_list_view);

        initDatas();

        try {
            //1 展开 0 不展开
            mAdapter = new SimpleTreeListViewAdapter<>(mTree,this,mDatas,0);
            mTree.setAdapter(mAdapter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        mAdapter.setOnTreeNodeClickListener(new TreeListViewAdapter.OnTreeNodeClickListener() {
            @Override
            public void onClick(Node node, int position) {
                if(node.isLeaf()){
                    Toast.makeText(TreeNodeActivity.this, node.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        //动态添加
        mTree.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final EditText et = new EditText(TreeNodeActivity.this);

                new AlertDialog.Builder(TreeNodeActivity.this).setTitle("Add Node")
                        .setView(et)
                        .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAdapter.addExtraNode(position,et.getText().toString());
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();

                return true;
            }
        });
    }

    private void initDatas() {
        mDatas = new ArrayList<>();
        FileBean bean = new FileBean(1,0,"根目录1");
        mDatas.add(bean);
        bean = new FileBean(2,0,"根目录2");
        mDatas.add(bean);
        bean = new FileBean(3,0,"根目录3");
        mDatas.add(bean);

        bean = new FileBean(4,1,"根目录1-1");
        mDatas.add(bean);
        bean = new FileBean(5,1,"根目录1-2");
        mDatas.add(bean);

        bean = new FileBean(6,5,"根目录1-1-1");
        mDatas.add(bean);
        bean = new FileBean(7,2,"根目录2-1");
        mDatas.add(bean);

        bean = new FileBean(8,3,"根目录3-1");
        mDatas.add(bean);
        bean = new FileBean(9,8,"根目录3-1-1");
        mDatas.add(bean);
        bean = new FileBean(10,9,"根目录3-1-1-1");
        mDatas.add(bean);

//        orgBeans = new ArrayList<>();
//        OrgBean bean = new OrgBean(1,0,"根目录1");
//        orgBeans.add(bean);
//        bean = new OrgBean(2,0,"根目录2");
//        orgBeans.add(bean);
//        bean = new OrgBean(3,0,"根目录3");
//        orgBeans.add(bean);
//
//        bean = new OrgBean(4,1,"根目录1-1");
//        orgBeans.add(bean);
//        bean = new OrgBean(5,1,"根目录1-2");
//        orgBeans.add(bean);
//
//        bean = new OrgBean(6,5,"根目录1-1-1");
//        orgBeans.add(bean);
//        bean = new OrgBean(7,2,"根目录2-1");
//        orgBeans.add(bean);
//
//        bean = new OrgBean(8,3,"根目录3-1");
//        orgBeans.add(bean);
//        bean = new OrgBean(9,8,"根目录3-1-1");
//        orgBeans.add(bean);
//        bean = new OrgBean(10,9,"根目录3-1-1-1");
//        orgBeans.add(bean);
    }
}
