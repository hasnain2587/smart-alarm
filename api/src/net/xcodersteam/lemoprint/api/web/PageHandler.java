package net.xcodersteam.lemoprint.api.web;

import java.lang.annotation.*;

/**
 * Created by semoro on 23.04.15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PageHandler {
    String[] names();
}
