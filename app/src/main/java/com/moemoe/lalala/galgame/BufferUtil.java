package com.moemoe.lalala.galgame;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Haru on 2016/7/18 0018.
 */
public class BufferUtil {

    public static FloatBuffer createFloatBuffer( int floatCount ) {
        ByteBuffer data = ByteBuffer.allocateDirect( floatCount * 4);
        data.order(ByteOrder.nativeOrder());
        FloatBuffer p1 = data.asFloatBuffer() ;
        return p1;
    }

    public static FloatBuffer setupFloatBuffer( FloatBuffer preBuffer , float []array){

        if( preBuffer == null || preBuffer.capacity() < array.length ){
            preBuffer = createFloatBuffer( array.length * 2 ) ;
        }
        else{
            preBuffer.clear() ;
        }
        preBuffer.put(array) ;
        preBuffer.position(0) ;
        return preBuffer ;
    }

    public static ShortBuffer createShortBuffer( int shortCount ) {
        ByteBuffer data = ByteBuffer.allocateDirect( shortCount * 4);
        data.order(ByteOrder.nativeOrder());
        ShortBuffer p1 = data.asShortBuffer() ;
        return p1;
    }

    public static ShortBuffer setupShortBuffer( ShortBuffer preBuffer , short[] array ) {

        if( preBuffer == null || preBuffer.capacity() < array.length ){
            preBuffer = createShortBuffer(array.length * 2) ;
        }
        else{
            preBuffer.clear() ;
        }

        preBuffer.clear() ;
        preBuffer.put(array) ;
        preBuffer.position(0) ;

        return preBuffer ;
    }

}
