public class MemeModel {
    private String reference;
    private int length;
    private int width;

    public static MemeModel setModel(String ref, int len, int wid){
        MemeModel meme = new MemeModel();
        meme.setReference(ref);
        meme.setLength(len);
        meme.setWidth(wid);
        return meme;
    }

    @Override
    public String toString(){
        return reference + " : " + length + " : " + width;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }
}
