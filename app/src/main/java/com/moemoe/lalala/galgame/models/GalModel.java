//package com.moemoe.lalala.galgame.models;
//
//import com.moemoe.lalala.galgame.LAppModel;
//
//import java.io.File;
//
//import javax.microedition.khronos.opengles.GL10;
//
///**
// * Created by Haru on 2016/8/30.
// */
//public class GalModel extends LAppModel {
//    private String background;
//    private OnLoadModelListener onLoadModelListener;
//    private String path;
//    private String source;
//    private File zipFile;
//
//    public GalModel(String path){this.path = path;}
//
//    public void setOnLoadModelListener(OnLoadModelListener l){
//        onLoadModelListener = l;
//    }
//
//    public final void setModel(GalModel model){
//        path = model.path;
//        background = model.background;
//        source = model.source;
//    }
//
//    public final void check(GL10 gl10){
//        if(path != null){
//            try{
//                release();
//                load(gl10,path);
//                if(onLoadModelListener != null){
//                    onLoadModelListener.onLoadModel();
//                    onLoadModelListener = null;
//                }
//                feedIn();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            path = null;
//        }
//    }
//
//    public final boolean isZipFile(){
//        return (zipFile != null);
//    }
//
//    public void setZipFile(File file){
//        zipFile = file;
//    }
//
//    public File getZipFile(){
//        return zipFile;
//    }
//
//    public void setSource(File file){
//        if(file != null){
//            source = file.getAbsolutePath();
//        }
//    }
//
//    public File getSource(){
//        if(source != null){
//            return new File(source);
//        }
//        return null;
//    }
//
//    public void setPath(String s){
//        path = s;
//    }
//
//    public final String getPath(){
//        return path;
//    }
//
//    public final void setBackground(String s){
//        background = s;
//    }
//
//    public final String getBackground(){
//        if(super.getBackground() != null){
//            return super.getBackground();
//        }
//        return background;
//    }
//
//    public interface OnLoadModelListener{
//        void onLoadModel();
//    }
//}
