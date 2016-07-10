
package com.tiger.quicknews.bean;

import java.util.List;

public class NewModle extends BaseModle {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * docid
     */
    private String docid;
    /**
     * title
     */
    private String title;
    /**
     * digest
     */
    private String digest;
    /**
     * imgsrcַ
     */
    private String imgsrc;
    /**
     * source
     */
    private String source;
    /**
     * ptime
     */
    private String ptime;
    /**
     * TAG
     */
    private String tag;
    /**
     * itemType
     */
    private String itemType;
    
    private ImagesModle imagesModle;
    
    public void setItemType(String type){
    	this.itemType = type;
    }
    
    public String getItemType(){
    	return itemType;
    }
    
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public ImagesModle getImagesModle() {
        return imagesModle;
    }

    public void setImagesModle(ImagesModle imagesModle) {
        this.imagesModle = imagesModle;
    }

    /**
     * imgHeadLists
     */
    private List<ImagesModle> imgHeadLists;

    public List<ImagesModle> getImgHeadLists() {
        return imgHeadLists;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPtime() {
        return ptime;
    }

    public void setPtime(String ptime) {
        this.ptime = ptime;
    }

    public void setImgHeadLists(List<ImagesModle> imgHeadLists) {
        this.imgHeadLists = imgHeadLists;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }
}
