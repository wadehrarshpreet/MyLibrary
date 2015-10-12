/*
 * Copyright (c) 2015. @WadehraArshpreet
 *
 * Created By Arshpreet Singh Wadehra 9/2/15 5:57 PM
 */

package com.davlib.mylibrary;

import android.graphics.Bitmap;
import net.glxn.qrgen.android.QRCode;
/***
 *Create By Arshpreet Singh Wadehra
 * @WadehrArshpreet
 */
public class QRCodeGenerator {
    public static Bitmap getQRCode(String text) {
        return QRCode.from(text).bitmap();
    }
}