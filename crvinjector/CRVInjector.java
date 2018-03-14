package com.ntunin.cybervision.crvinjector;

import com.ntunin.cybervision.errno.ERRNO;
import whereareyou.ntunin.com.whereareyou.R;
import com.ntunin.cybervision.res.Res;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nikolay on 26.01.17.
 */

public abstract class CRVInjector {
    protected static CRVInjector injector;
    public abstract Object getInstance(String token);
    public abstract Object getInstance(int id);
    public abstract void setInstance(String token, Object instance);
    public abstract void setInstance(int id, Object instance);
    public static void setMain(CRVInjector main) {
        injector = main;
    }
    public static CRVInjector main() {
        if(injector == null) {
            injector = loadInjector();
        }
        return injector;
    }

    protected static CRVInjector loadInjector() {
        String className = Res.string(R.string.injector);
        try {
            Class<CRVInjector> injectorClass = (Class<CRVInjector>) Class.forName(className);
            Constructor<CRVInjector> constructor = injectorClass.getConstructor();
            return constructor.newInstance();
        } catch (ClassNotFoundException e) {
            ERRNO.write(R.string.class_not_fount);
        } catch (NoSuchMethodException e) {
            ERRNO.write(R.string.constructor_not_fount);
        } catch (IllegalAccessException e) {
            ERRNO.write(R.string.illegal_access);
        } catch (InstantiationException e) {
            ERRNO.write(R.string.could_not_create);
        } catch (InvocationTargetException e) {
            ERRNO.write(R.string.could_not_invoke);
        }
        return null;
    }
}
