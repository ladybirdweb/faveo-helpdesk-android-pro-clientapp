package co.faveo.helpdesk.pro.client.model;

/**
 * Created by Sumit
 * This class is for creating the object for the NavDrawer item.
 */
public class NavDrawerItem {
    private boolean showNotify;
    private String title;
    private String count="";

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public NavDrawerItem() {

    }

    public NavDrawerItem(boolean showNotify, String title) {
        this.showNotify = showNotify;
        this.title = title;
    }

    public boolean isShowNotify() {
        return showNotify;
    }

    public void setShowNotify(boolean showNotify) {
        this.showNotify = showNotify;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}

