package com.moemoe.lalala.app;

import com.moemoe.lalala.rongyun.MoeMoeImagePlugin;

import java.util.ArrayList;
import java.util.List;

import io.rong.imkit.DefaultExtensionModule;
import io.rong.imkit.plugin.IPluginModule;
import io.rong.imlib.model.Conversation;

/**
 * Created by yi on 2017/9/8.
 */

public class MoeMoeExtensionModule extends DefaultExtensionModule {

    @Override
    public List<IPluginModule> getPluginModules(Conversation.ConversationType conversationType) {
        List<IPluginModule> pluginModuleList = new ArrayList<>();
        MoeMoeImagePlugin image = new MoeMoeImagePlugin();
        pluginModuleList.add(image);
        return pluginModuleList;
    }
}
