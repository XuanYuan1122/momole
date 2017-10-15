package com.moemoe.lalala.model.entity;


import java.util.LinkedHashMap;


/**
 * Created by yi on 2017/9/27.
 */

public class JuQingMapShowEntity {
    private int index;
    private String name;
    private Talk talk;//"effect":"shock"
    private Content pose;//"effect":"shock,movein,moveout,soakin,soakout"
    private Content face;//"effect":"shock,movein,moveout,soakin,soakout"
    private Content extra;// "effect":"shock,movein,moveout,soakin,soakout"
    private Content vol;
    private Content bgm;
    private Content cg;//"effect":"shock"
    private Content bg;//"effect":"shock"
    private Content item;//"effect":"shock"
    private LinkedHashMap<String,Integer> choice;
    private boolean isShowCg;

    public JuQingMapShowEntity(){
        talk = new Talk();
        pose = new Content();
        face = new Content();
        extra = new Content();
        cg = new Content();
        bg = new Content();
        item = new Content();
        vol = new Content();
        bgm = new Content();
    }

    public class Talk {
        private String text;
        private String effect;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getEffect() {
            return effect;
        }

        public void setEffect(String effect) {
            this.effect = effect;
        }
    }

    public class Content {
        private String file;
        private String effect;
        private String md5;
        private String localPath;

        public String getMd5() {
            return md5;
        }

        public void setMd5(String md5) {
            this.md5 = md5;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getEffect() {
            return effect;
        }

        public void setEffect(String effect) {
            this.effect = effect;
        }

        public String getLocalPath() {
            return localPath;
        }

        public void setLocalPath(String localPath) {
            this.localPath = localPath;
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Talk getTalk() {
        return talk;
    }

    public void setTalk(Talk talk) {
        this.talk = talk;
    }

    public Content getPose() {
        return pose;
    }

    public void setPose(Content pose) {
        this.pose = pose;
    }

    public Content getFace() {
        return face;
    }

    public void setFace(Content face) {
        this.face = face;
    }

    public Content getExtra() {
        return extra;
    }

    public void setExtra(Content extra) {
        this.extra = extra;
    }

    public Content getVol() {
        return vol;
    }

    public void setVol(Content vol) {
        this.vol = vol;
    }

    public Content getBgm() {
        return bgm;
    }

    public void setBgm(Content bgm) {
        this.bgm = bgm;
    }

    public Content getCg() {
        return cg;
    }

    public void setCg(Content cg) {
        this.cg = cg;
    }

    public Content getBg() {
        return bg;
    }

    public void setBg(Content bg) {
        this.bg = bg;
    }

    public Content getItem() {
        return item;
    }

    public void setItem(Content item) {
        this.item = item;
    }

    public LinkedHashMap<String, Integer> getChoice() {
        return choice;
    }

    public void setChoice(LinkedHashMap<String, Integer> choice) {
        this.choice = choice;
    }

    public boolean isShowCg() {
        return isShowCg;
    }

    public void setShowCg(boolean showCg) {
        isShowCg = showCg;
    }
}
