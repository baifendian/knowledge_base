
package com.tiger.quicknews.view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tiger.quicknews.App;
import com.tiger.quicknews.R;
import com.tiger.quicknews.bean.NewModle;
import com.tiger.quicknews.utils.Options;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EViewGroup(R.layout.item_new)
public class NewItemView extends LinearLayout {

    @ViewById(R.id.left_image)
    protected ImageView leftImage;

    @ViewById(R.id.item_title)
    protected TextView itemTitle;

    @ViewById(R.id.item_content)
    protected TextView itemConten;

    @ViewById(R.id.article_top_layout)
    protected RelativeLayout imageLayout;
    @ViewById(R.id.btn_zhuanti)
    protected Button btn_zhuanti;
   
    
    @ViewById(R.id.layout_image)
    protected LinearLayout articleLayout;
    @ViewById(R.id.item_abstract)
    protected TextView itemAbstract;
    @ViewById(R.id.item_content1)
    protected TextView itemcontent1;
    
    
    
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    protected DisplayImageOptions options;

    public NewItemView(Context context) {
        super(context);
        options = Options.getListOptions();
    }

    public void setTexts(String titleText, String contentText, String currentItem) {
        articleLayout.setVisibility(View.VISIBLE);
        imageLayout.setVisibility(View.GONE);
        itemAbstract.setText(titleText);
        itemConten.setText(contentText);
        if ("北京".equals(currentItem)) {

        } else {
        	itemcontent1.setText(contentText);
        }
        
        /*if (!"".equals(imgUrl)) {
            leftImage.setVisibility(View.VISIBLE);
            imageLoader.displayImage(imgUrl, leftImage, options);
        } else {
            leftImage.setVisibility(View.GONE);
        }*/
    }

    public void setImages(String titleText, String contentText, String imgUrl) {
        imageLayout.setVisibility(View.VISIBLE);
        articleLayout.setVisibility(View.GONE);
        itemTitle.setText(titleText);
        if (!"".equals(imgUrl)) {
            leftImage.setVisibility(View.VISIBLE);
            imageLoader.displayImage(imgUrl, leftImage, options);
        } 
        itemConten.setText(contentText);
        btn_zhuanti.setFocusable(false);
        btn_zhuanti.setVisibility(View.GONE);
       /* List<String> imageModle = newModle.getImagesModle().getImgList();
        imageLoader.displayImage(imageModle.get(0), item_image0, options);
        imageLoader.displayImage(imageModle.get(1), item_image1, options);
        imageLoader.displayImage(imageModle.get(2), item_image2, options);*/
    }
}
