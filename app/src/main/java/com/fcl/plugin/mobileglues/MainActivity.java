package com.fcl.plugin.mobileglues;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.Nullable;

import com.fcl.plugin.mobileglues.settings.FolderPermissionManager;
import com.fcl.plugin.mobileglues.utils.Constants;

import java.util.logging.Level;
import java.util.logging.Logger;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;

public class Main {
    // EGL variables
    private EGLDisplay eglDisplay;
    private EGLContext eglContext;
    private EGLSurface eglSurface;

    // Manually set configuration values
    private int enableANGLE = 3;
    private int enableNoError = 0;
    private int maxGlslCacheSize = 64;

    public void initialize() {
        if (enableANGLE > 3 || enableANGLE < 0)
            enableANGLE = 0;
        if (enableNoError > 3 || enableNoError < 0)
            enableNoError = 0;
        
        if (maxGlslCacheSize == 0)
            maxGlslCacheSize = 64;

        Logger.getLogger("MG").log(Level.INFO, "Configuration initialized with values: " +
                "EnableANGLE=" + enableANGLE + ", EnableNoError=" + enableNoError + 
                ", MaxGlslCacheSize=" + maxGlslCacheSize);

        initEGL();
    }

    private void initEGL() {
        eglDisplay = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        int[] version = new int[2];
        EGL14.eglInitialize(eglDisplay, version, 0, version, 1);

        int[] configAttribs = {
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        EGL14.eglChooseConfig(eglDisplay, configAttribs, 0, configs, 0, 1, numConfigs, 0);

        EGLConfig eglConfig = configs[0];

        int[] contextAttribs = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        eglContext = EGL14.eglCreateContext(eglDisplay, eglConfig, EGL14.EGL_NO_CONTEXT, contextAttribs, 0);

        int[] surfaceAttribs = { EGL14.EGL_NONE };
        eglSurface = EGL14.eglCreatePbufferSurface(eglDisplay, eglConfig, surfaceAttribs, 0);

        EGL14.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext);

        Logger.getLogger("MG").log(Level.INFO, "EGL initialized successfully.");
    }

    private void checkPermissionSilently() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MGDirectoryUri = folderPermissionManager.getMGFolderUri();
            if (MGDirectoryUri != null) {
                initialize();
            }
        } else {
            initialize();
        }
    }

    public void render() {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    public void cleanup() {
        EGL14.eglDestroySurface(eglDisplay, eglSurface);
        EGL14.eglDestroyContext(eglDisplay, eglContext);
        EGL14.eglTerminate(eglDisplay);
    }
}
