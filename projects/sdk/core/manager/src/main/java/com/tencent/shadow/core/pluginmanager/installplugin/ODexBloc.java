package com.tencent.shadow.core.pluginmanager.installplugin;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import dalvik.system.DexClassLoader;

public class ODexBloc {

    private static ConcurrentHashMap<String, Object> sLocks = new ConcurrentHashMap<>();

    public static File oDexPlugin(File root, File apkFile, String UUID, String partKey) throws InstallPluginException {

        String key = UUID + "_" + partKey;
        Object lock = sLocks.get(key);
        if (lock == null) {
            lock = new Object();
            sLocks.put(key, lock);
        }

        File oDexRoot = new File(root,"oDex");
        File oDexDir = new File(oDexRoot, UUID + "_" + partKey + "_odex");
        File copiedTagFile = new File(oDexDir, UUID + "_" + partKey + "_copied");

        synchronized (lock) {
            if (copiedTagFile.exists()) {
                return oDexDir;
            }

            //如果odex目录存在但是个文件，不是目录，那超出预料了。删除了也不一定能工作正常。
            if (oDexDir.exists() && oDexDir.isFile()) {
                throw new InstallPluginException("oDexDir=" + oDexDir.getAbsolutePath() + "已存在，但它是个文件，不敢贸然删除");
            }
            //创建oDex目录
            oDexDir.mkdirs();

            new DexClassLoader(apkFile.getAbsolutePath(), oDexDir.getAbsolutePath(), null, ODexBloc.class.getClassLoader());
        }

        return oDexDir;


    }
}