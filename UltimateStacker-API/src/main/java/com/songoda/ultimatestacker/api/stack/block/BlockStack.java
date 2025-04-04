package com.songoda.ultimatestacker.api.stack.block;

import com.songoda.core.database.Data;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.ultimatestacker.api.utils.Hologramable;
import com.songoda.ultimatestacker.api.utils.Stackable;

public interface BlockStack extends Stackable, Hologramable, Data {


    int getId();

    void destroy();

    XMaterial getMaterial();
}
