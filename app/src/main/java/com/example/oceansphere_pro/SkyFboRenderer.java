package com.example.oceansphere_pro;


import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.example.oceansphere_pro.egl.ShaderUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class SkyFboRenderer {

    private Context context;

    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,

            0f, 0f,
            0f, 0f,
            0f, 0f,
            0f, 0f
    };
    private FloatBuffer vertexBuffer;

    private float[] fragmentData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };
    private FloatBuffer fragmentBuffer;

    private int program;
    private int vPosition;
    private int fPosition;
    private int sampler;

    private int vboId;

    private Bitmap bitmap;


    private int bitmapTextureid;


    public SkyFboRenderer(Context context,Bitmap bitmap) {
        this.context = context;

        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        fragmentBuffer = ByteBuffer.allocateDirect(fragmentData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(fragmentData);
        fragmentBuffer.position(0);

    }

    public void onCreate()
    {

        GLES20.glEnable (GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        String vertexSource = ShaderUtil.getRawResource(context, R.raw.vertex_shader_screen1);
        String fragmentSource = ShaderUtil.getRawResource(context, R.raw.fragment_shader_screen1);

        program = ShaderUtil.createProgram(vertexSource, fragmentSource);

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        sampler = GLES20.glGetUniformLocation(program, "sTexture");

        int [] vbos = new int[1];
        GLES20.glGenBuffers(1, vbos, 0);
        vboId = vbos[0];

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4 + fragmentData.length * 4, null, GLES20. GL_STATIC_DRAW);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, 0, vertexData.length * 4, vertexBuffer);
        GLES20.glBufferSubData(GLES20.GL_ARRAY_BUFFER, vertexData.length * 4, fragmentData.length * 4, fragmentBuffer);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        bitmapTextureid = ShaderUtil.loadBitmapTexture(bitmap);
    }

    public void onChange(int width, int height)
    {
        GLES20.glViewport(0, 0, width, height);
    }

    public void onDraw(int textureId)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f,0f, 0f, 1f);

        GLES20.glUseProgram(program);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);

        //fbo
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                0);

        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //bitmap
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bitmapTextureid);

        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                32);

        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);



        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
}
