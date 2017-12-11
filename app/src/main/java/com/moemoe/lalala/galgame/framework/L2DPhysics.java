package com.moemoe.lalala.galgame.framework;

import java.io.InputStream;
import java.util.ArrayList;

import jp.live2d.ALive2DModel;
import jp.live2d.physics.PhysicsHair;
import jp.live2d.util.Json;
import jp.live2d.util.UtDebug;
import jp.live2d.util.UtFile;
import jp.live2d.util.UtSystem;

/**
 * Created by Haru on 2016/7/18 0018.
 */
public class L2DPhysics {
    private ArrayList<PhysicsHair> physicsList;
    private long startTimeMSec;


    public L2DPhysics()
    {
        physicsList=new ArrayList<PhysicsHair>();
        startTimeMSec = UtSystem.getUserTimeMSec() ;
    }



    public void updateParam(ALive2DModel model)
    {
        long timeMSec = UtSystem.getUserTimeMSec() - startTimeMSec  ;
        for (int i = 0; i < physicsList.size(); i++) {
            physicsList.get(i).update(model, timeMSec);
        }
    }



    public static L2DPhysics load(InputStream in) throws Exception
    {
        byte[] buf = UtFile.load(in) ;
        return load(buf);
    }



    public static L2DPhysics load(byte[] buf ) throws Exception
    {

        L2DPhysics ret = new L2DPhysics();

        Json.Value json = Json.parseFromBytes( buf ) ;


        Json.Value params = json.get("physics_hair");
        int paramNum = params.getVector(null).size();

        for (int i = 0; i < paramNum; i++)
        {
            Json.Value param = params.get(i);

            PhysicsHair physics=new PhysicsHair();

            Json.Value setup = param.get("setup");

            float length = setup.get("length").toFloat();

            float resist = setup.get("regist").toFloat();

            float mass = setup.get("mass").toFloat();
            physics.setup(length,resist,mass);


            Json.Value srcList = param.get("src");
            int srcNum = srcList.getVector(null).size();
            for (int j = 0; j < srcNum; j++)
            {
                Json.Value src = srcList.get(j);
                String id = src.get("id").toString();//param ID
                PhysicsHair.Src type=PhysicsHair.Src.SRC_TO_X;
                String typeStr=src.get("ptype").toString();
                if(typeStr.equals("x"))
                {
                    type=PhysicsHair.Src.SRC_TO_X;
                }
                else if(typeStr.equals("y"))
                {
                    type=PhysicsHair.Src.SRC_TO_Y;
                }
                else if(typeStr.equals("angle"))
                {
                    type=PhysicsHair.Src.SRC_TO_G_ANGLE;
                }else{
                    UtDebug.error("live2d", "Invalid parameter:PhysicsHair.Src");
                }

                float scale = src.get("scale").toFloat();
                float weight = src.get("weight").toFloat();
                physics.addSrcParam( type,id, scale, weight);
            }


            Json.Value targetList = param.get("targets");
            int targetNum = targetList.getVector(null).size();
            for (int j = 0; j < targetNum; j++)
            {
                Json.Value target = targetList.get(j);
                String id = target.get("id").toString();//param ID
                PhysicsHair.Target type=PhysicsHair.Target.TARGET_FROM_ANGLE;
                String typeStr=target.get("ptype").toString();
                if(typeStr.equals("angle"))
                {
                    type=PhysicsHair.Target.TARGET_FROM_ANGLE;
                }
                else if(typeStr.equals("angle_v"))
                {
                    type=PhysicsHair.Target.TARGET_FROM_ANGLE_V;
                }else{
                    UtDebug.error("live2d", "Invalid parameter:PhysicsHair.Target");
                }

                float scale = target.get("scale").toFloat();
                float weight = target.get("weight").toFloat();
                physics.addTargetParam( type,id, scale, weight);

            }

            ret.physicsList.add(physics);
        }

        return ret;
    }
}
