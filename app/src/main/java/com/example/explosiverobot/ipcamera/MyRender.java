package com.example.explosiverobot.ipcamera;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by zhangyuanyuan on 2017/10/19.
 */

public class MyRender implements GLSurfaceView.Renderer {

    private static final String TAG = "MyRender";

    private final float[] textCoodBufferData;
    private final float[] positionBufferData;

    private int mWidth = 0;
    private int mHeight = 0;

    private ByteBuffer mUByteBuffer = null;
    private ByteBuffer mVByteBuffer = null;
    private ByteBuffer mYByteBuffer = null;

    private boolean bNeedSleep = true;

    private int programHandle = 0;
    private int vertexShader = 0;
    private int yuvFragmentShader = 0;
    private int texRangeSlot = 0;
    private int positionSlot = 0;
    private int[] texture = new int[3];
    private int[] textureSlot = new int[3];

    private FloatBuffer textCoodBuffer = null;
    private FloatBuffer positionBuffer = null;

    public MyRender(GLSurfaceView paramGLSurfaceView) {
        this.textCoodBufferData = setTextCoodBufferData();

        this.positionBufferData = setPositionBufferData();

        paramGLSurfaceView.setEGLContextClientVersion(2);
    }


    private float[] setTextCoodBufferData() {
        float[] arrayOfFloat = new float[16];
        arrayOfFloat[0] = 0.0F;
        arrayOfFloat[1] = 0.0F;
        arrayOfFloat[2] = 0.0F;
        arrayOfFloat[3] = 1.0F;
        arrayOfFloat[4] = 0.0F;
        arrayOfFloat[5] = 1.0F;
        arrayOfFloat[6] = 0.0F;
        arrayOfFloat[7] = 1.0F;
        arrayOfFloat[8] = 1.0F;
        arrayOfFloat[9] = 0.0F;
        arrayOfFloat[10] = 0.0F;
        arrayOfFloat[11] = 1.0F;
        arrayOfFloat[12] = 1.0F;
        arrayOfFloat[13] = 1.0F;
        arrayOfFloat[14] = 0.0F;
        arrayOfFloat[15] = 1.0F;
        return arrayOfFloat;
    }

    private float[] setPositionBufferData() {
        float[] arrayOfFloat = new float[16];
        arrayOfFloat[0] = -1.0F;
        arrayOfFloat[1] = 1.0F;
        arrayOfFloat[2] = 0.0F;
        arrayOfFloat[3] = 1.0F;
        arrayOfFloat[4] = -1.0F;
        arrayOfFloat[5] = -1.0F;
        arrayOfFloat[6] = 0.0F;
        arrayOfFloat[7] = 1.0F;
        arrayOfFloat[8] = 1.0F;
        arrayOfFloat[9] = 1.0F;
        arrayOfFloat[10] = 0.0F;
        arrayOfFloat[11] = 1.0F;
        arrayOfFloat[12] = 1.0F;
        arrayOfFloat[13] = -1.0F;
        arrayOfFloat[14] = 0.0F;
        arrayOfFloat[15] = 1.0F;
        return arrayOfFloat;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
        GLES20.glGenTextures(3, this.texture, 0);
        createShaders();
        loadVBOs();
    }

    @Override
    public void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2) {
        GLES20.glViewport(0, 0, paramInt1, paramInt2);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        GLES20.glClear(16384);
        synchronized (this) {
            if ((this.mWidth == 0) || (this.mHeight == 0) || (this.mYByteBuffer == null) || (this.mUByteBuffer == null) || (this.mVByteBuffer == null))
                return;

            if (bNeedSleep) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            bNeedSleep = true;

            draw(this.mYByteBuffer, this.mUByteBuffer, this.mVByteBuffer, this.mWidth, this.mHeight);
        }
    }

    public long createShaders() {
        String fragmentShaderCode = "uniform sampler2D Ytex;\n";
        fragmentShaderCode += "uniform sampler2D Utex;\n";
        fragmentShaderCode += "uniform sampler2D Vtex;\n";
        fragmentShaderCode += "precision mediump float;  \n";
        fragmentShaderCode += "varying vec4 VaryingTexCoord0; \n";
        fragmentShaderCode += "vec4 color;\n";
        fragmentShaderCode += "void main()\n";
        fragmentShaderCode += "{\n";
        fragmentShaderCode += "float yuv0 = (texture2D(Ytex,VaryingTexCoord0.xy)).r;\n";
        fragmentShaderCode += "float yuv1 = (texture2D(Utex,VaryingTexCoord0.xy)).r;\n";
        fragmentShaderCode += "float yuv2 = (texture2D(Vtex,VaryingTexCoord0.xy)).r;\n";
        fragmentShaderCode += "\n";
        fragmentShaderCode += "color.r = yuv0 + 1.4022 * yuv2 - 0.7011;\n";
        fragmentShaderCode += "color.r = (color.r < 0.0) ? 0.0 : ((color.r > 1.0) ? 1.0 : color.r);\n";
        fragmentShaderCode += "color.g = yuv0 - 0.3456 * yuv1 - 0.7145 * yuv2 + 0.53005;\n";
        fragmentShaderCode += "color.g = (color.g < 0.0) ? 0.0 : ((color.g > 1.0) ? 1.0 : color.g);\n";
        fragmentShaderCode += "color.b = yuv0 + 1.771 * yuv1 - 0.8855;\n";
        fragmentShaderCode += "color.b = (color.b < 0.0) ? 0.0 : ((color.b > 1.0) ? 1.0 : color.b);\n";
        fragmentShaderCode += "gl_FragColor = color;\n";
        fragmentShaderCode += "}\n";

        String vertexShaderCode = "uniform mat4 uMVPMatrix;   \n";
        vertexShaderCode += "attribute vec4 vPosition;  \n";
        vertexShaderCode += "attribute vec4 myTexCoord; \n";
        vertexShaderCode += "varying vec4 VaryingTexCoord0; \n";
        vertexShaderCode += "void main(){               \n";
        vertexShaderCode += "VaryingTexCoord0 = myTexCoord; \n";
        vertexShaderCode += "gl_Position = vPosition; \n";
        vertexShaderCode += "}  \n";

        int[] arrayOfInt = new int[1];
        int i = compileShader(vertexShaderCode, 35633);
        this.vertexShader = i;
        if (i == 0)
            Log.e(TAG, "failed when compileShader(vertex)");

        int j = compileShader(fragmentShaderCode, 35632);
        this.yuvFragmentShader = j;
        if (j == 0)
            Log.e(TAG, "failed when compileShader(fragment)");
        this.programHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(this.programHandle, this.vertexShader);
        GLES20.glAttachShader(this.programHandle, this.yuvFragmentShader);
        GLES20.glLinkProgram(this.programHandle);
        GLES20.glGetProgramiv(this.programHandle, 35714, arrayOfInt, 0);

        if (arrayOfInt[0] == 0) {
            Log.e(TAG, "link program err:" + GLES20.glGetProgramInfoLog(this.programHandle));
            destroyShaders();
        }

        this.texRangeSlot = GLES20.glGetAttribLocation(this.programHandle, "myTexCoord");

        this.textureSlot[0] = GLES20.glGetUniformLocation(this.programHandle, "Ytex");
        this.textureSlot[1] = GLES20.glGetUniformLocation(this.programHandle, "Utex");
        this.textureSlot[2] = GLES20.glGetUniformLocation(this.programHandle, "Vtex");

        this.positionSlot = GLES20.glGetAttribLocation(this.programHandle, "vPosition");

        return 0;
    }

    public int loadVBOs() {
        this.textCoodBuffer = ByteBuffer.allocateDirect(4 * this.textCoodBufferData.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.textCoodBuffer.put(this.textCoodBufferData).position(0);

        this.positionBuffer = ByteBuffer.allocateDirect(4 * this.positionBufferData.length).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.positionBuffer.put(this.positionBufferData).position(0);

        return 0;
    }

    public long destroyShaders() {
        if (this.programHandle != 0) {
            GLES20.glDetachShader(this.programHandle, this.yuvFragmentShader);
            GLES20.glDetachShader(this.programHandle, this.vertexShader);
            GLES20.glDeleteProgram(this.programHandle);
            this.programHandle = 0;
        }
        if (this.yuvFragmentShader != 0) {
            GLES20.glDeleteShader(this.yuvFragmentShader);
            this.yuvFragmentShader = 0;
        }
        if (this.vertexShader != 0) {
            GLES20.glDeleteShader(this.vertexShader);
            this.vertexShader = 0;
        }
        return 0L;
    }

    public static int compileShader(String paramString, int paramInt) {
        int i = GLES20.glCreateShader(paramInt);
        if (i != 0) {
            int[] arrayOfInt = new int[1];
            GLES20.glShaderSource(i, paramString);
            GLES20.glCompileShader(i);
            GLES20.glGetShaderiv(i, 35713, arrayOfInt, 0);
            if (arrayOfInt[0] == 0) {
                GLES20.glDeleteShader(i);
                i = 0;
            }
        }
        return i;
    }

    public int draw(ByteBuffer paramByteBuffer1, ByteBuffer paramByteBuffer2, ByteBuffer paramByteBuffer3, int paramInt1, int paramInt2) {
        GLES20.glClear(16384);
        GLES20.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
        GLES20.glUseProgram(this.programHandle);
        paramByteBuffer1.position(0);
        GLES20.glActiveTexture(33984);
        loadTexture(this.texture[0], paramInt1, paramInt2, paramByteBuffer1);
        paramByteBuffer2.position(0);
        GLES20.glActiveTexture(33985);
        loadTexture(this.texture[1], paramInt1 >> 1, paramInt2 >> 1, paramByteBuffer2);
        paramByteBuffer3.position(0);
        GLES20.glActiveTexture(33986);
        loadTexture(this.texture[2], paramInt1 >> 1, paramInt2 >> 1, paramByteBuffer3);
        GLES20.glUniform1i(this.textureSlot[0], 0);
        GLES20.glUniform1i(this.textureSlot[1], 1);
        GLES20.glUniform1i(this.textureSlot[2], 2);

        this.positionBuffer.position(0);
        GLES20.glEnableVertexAttribArray(this.positionSlot);
        GLES20.glVertexAttribPointer(this.positionSlot, 4, GLES20.GL_FLOAT, false, 0, this.positionBuffer);

        //textcood
        this.textCoodBuffer.position(0);

        GLES20.glEnableVertexAttribArray(this.texRangeSlot);
        GLES20.glVertexAttribPointer(this.texRangeSlot, 4, GLES20.GL_FLOAT, false, 0, this.textCoodBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(this.positionSlot);

        GLES20.glDisableVertexAttribArray(this.texRangeSlot);

        return 0;
    }

    public int loadTexture(int paramInt1, int paramInt2, int paramInt3, Buffer paramBuffer) {
        GLES20.glBindTexture(3553, paramInt1);
        GLES20.glTexParameteri(3553, 10241, 9729);
        GLES20.glTexParameteri(3553, 10240, 9729);
        GLES20.glTexParameteri(3553, 10242, 33071);
        GLES20.glTexParameteri(3553, 10243, 33071);
        GLES20.glTexImage2D(3553, 0, 6409, paramInt2, paramInt3, 0, 6409, GLES20.GL_UNSIGNED_BYTE, paramBuffer);
        return 0;
    }

    public int writeSample(byte[] paramArrayOfByte, int width, int height) {
        synchronized (this) {
//            Print.e("writeSample");
            if ((width == 0) || (height == 0)) {
                return 0;
            }

            if ((width != this.mWidth) || (height != this.mHeight)) {
                this.mWidth = width;
                this.mHeight = height;
                this.mYByteBuffer = ByteBuffer.allocate(this.mWidth * this.mHeight);
                this.mUByteBuffer = ByteBuffer.allocate(this.mWidth * this.mHeight / 4);
                this.mVByteBuffer = ByteBuffer.allocate(this.mWidth * this.mHeight / 4);
            }

            if (this.mYByteBuffer != null) {
                this.mYByteBuffer.position(0);
                this.mYByteBuffer.put(paramArrayOfByte, 0, this.mWidth * this.mHeight);
                this.mYByteBuffer.position(0);
            }

            if (this.mUByteBuffer != null) {
                this.mUByteBuffer.position(0);
                this.mUByteBuffer.put(paramArrayOfByte, this.mWidth * this.mHeight, this.mWidth * this.mHeight / 4);
                this.mUByteBuffer.position(0);
            }

            if (this.mVByteBuffer != null) {
                this.mVByteBuffer.position(0);
                this.mVByteBuffer.put(paramArrayOfByte, 5 * (this.mWidth * this.mHeight) / 4, this.mWidth * this.mHeight / 4);
                this.mVByteBuffer.position(0);
            }

            bNeedSleep = false;

            return 1;

        }
    }
}
