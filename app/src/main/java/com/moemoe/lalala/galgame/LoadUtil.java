package com.moemoe.lalala.galgame;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import jp.live2d.util.UtDebug;

/**
 * Created by Haru on 2016/7/18 0018.
 */
public class LoadUtil {
    static final int GEN_TEX_LOOP = 999;

    public static int loadTexture(GL10 gl,InputStream in,boolean mipmap) throws IOException{
        Bitmap bitmap = BitmapFactory.decodeStream(in);
        int texture;
        if(mipmap){
            texture = buildMipmap(gl, bitmap) ;
        }else {
            texture = genTexture(gl) ;
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture) ;

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_MAG_FILTER,GL10.GL_LINEAR);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_T,GL10.GL_CLAMP_TO_EDGE);
            gl.glTexEnvf(GL10.GL_TEXTURE_ENV,GL10.GL_TEXTURE_ENV_MODE,GL10.GL_MODULATE);

            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0) ;
            bitmap.recycle();
        }
        return texture;
    }

    public static int buildMipmap(GL10 gl,Bitmap bitmap){
        return buildMipmap( gl, bitmap , true ) ;
    }

    public static int buildMipmap(GL10 gl,Bitmap srcBitmap,boolean recycle){
        Bitmap bitmap = srcBitmap;
        int level = 0;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int textureID = genTexture(gl);

        gl.glBindTexture(GL10.GL_TEXTURE_2D,textureID);
        try {
            ((GL11)gl).glTexParameteri(GL10.GL_TEXTURE_2D, GL11.GL_GENERATE_MIPMAP, GL10.GL_TRUE);
        }catch (Exception e){
            e.printStackTrace();
        }

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D,GL10.GL_TEXTURE_WRAP_T,GL10.GL_CLAMP_TO_EDGE);
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

        while (height >= 1 && width >= 1){
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);
            if (height == 1 || width == 1) {
                if( recycle || bitmap != srcBitmap ) bitmap.recycle() ;
                break;
            }
            level++;
            height /= 2;
            width /= 2;
            Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true );
            if( recycle || bitmap != srcBitmap ) bitmap.recycle();
            bitmap = bitmap2;
        }
        return textureID;
    }

    public static int genTexture(GL10 gl){
        int texture = 0;
        int i = 0;
        for(;i < GEN_TEX_LOOP;i++){
            int[] ret = {0};
            gl.glGenTextures(1,ret,0);
            texture = ret[0];
            if(texture < 0){
                gl.glDeleteTextures(1,ret,0);
            }else {
                break;
            }
        }
        if(i == GEN_TEX_LOOP){
            UtDebug.error( "gen texture loops over " + GEN_TEX_LOOP + "times @UtOpenGL" ) ;
            texture = 0 ;
        }
        return texture;
    }

}
