package com.app.http.loader;

import com.app.cache.DiskCacheEntity;
import com.app.common.util.IOUtil;
import com.app.http.request.UriRequest;

import java.io.InputStream;

/**
 * Created by Haru on 2016/4/12 0012.
 */
/*package*/ class ByteArrayLoader extends Loader<byte[]> {

    @Override
    public Loader<byte[]> newInstance() {
        return new ByteArrayLoader();
    }

    @Override
    public byte[] load(final InputStream in) throws Throwable {
        return IOUtil.readBytes(in);
    }

    @Override
    public byte[] load(final UriRequest request) throws Throwable {
        request.sendRequest();
        return this.load(request.getInputStream());
    }

    @Override
    public byte[] loadFromCache(final DiskCacheEntity cacheEntity) throws Throwable {
        return null;
    }

    @Override
    public void save2Cache(final UriRequest request) {
    }
}