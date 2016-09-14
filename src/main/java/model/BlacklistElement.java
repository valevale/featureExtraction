package model;



/**
 * Created by valerio on 1/19/16.
 */
public class BlacklistElement {
    private String value;
    private String tag;
    private String tagParent;

    public BlacklistElement() {
    }
    
    public BlacklistElement(String value, String tag, String tagParent) {
        this.value = value;
        this.tag = tag;
        this.tagParent = tagParent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BlacklistElement that = (BlacklistElement) o;

        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (tag != null ? !tag.equals(that.tag) : that.tag != null) return false;
        return tagParent != null ? tagParent.equals(that.tagParent) : that.tagParent == null;

    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (tag != null ? tag.hashCode() : 0);
        result = 31 * result + (tagParent != null ? tagParent.hashCode() : 0);
        return result;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTagParent() {
        return tagParent;
    }

    public void setTagParent(String tagParent) {
        this.tagParent = tagParent;
    }
}
