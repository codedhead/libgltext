// This is based on the OpenGL ES 1.0 sample application from the Android Developer website:
// http://developer.android.com/resources/tutorials/opengl/opengl-es10.html

package com.android.texample;

import ninja.jun.gl.libgltext.GLText;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

public class TexampleRenderer implements GLSurfaceView.Renderer  {

   private GLText glText;                             // A GLText Instance
   private Context context;                           // Context (from Activity)

   private int width = 100;                           // Updated to the Current Width + Height in onSurfaceChanged()
   private int height = 100;

   public TexampleRenderer(Context context)  {
      super();
      this.context = context;                         // Save Specified Context
   }

   public void onSurfaceCreated(EGLConfig config) {
      // Set the background frame color
      GLES20.glClearColor( 0.5f, 0.5f, 0.5f, 1.0f );

      // Create the GLText
      glText = new GLText( context.getAssets() );

      // Load the font from file (set size + padding), creates the texture
      // NOTE: after a successful call to this the font is ready for rendering!
      glText.load( "Roboto-Regular.ttf", 14, 2, 2 );  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
   }

   public void onDrawFrame() {
      // Redraw background color
      GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );

      glText.beginProgram();

      // TEST: render the entire font texture
      glText.setColor( 1.0f, 1.0f, 1.0f, 1.0f );         // Set Color to Use
      glText.drawTexture( width, height );            // Draw the Entire Texture

      // TEST: render some strings with the font
      glText.begin( 1.0f, 1.0f, 1.0f, 1.0f );         // Begin Text Rendering (Set Color WHITE)
      glText.draw( "Test String :)", 0, 0 );          // Draw Test String
      glText.draw( "Line 1", 50, 50 );                // Draw Test String
      glText.draw( "Line 2", 100, 100 );              // Draw Test String
      glText.end();                                   // End Text Rendering

      glText.begin( 0.0f, 0.0f, 1.0f, 1.0f );         // Begin Text Rendering (Set Color BLUE)
      glText.draw( "More Lines...", 50, 150 );        // Draw Test String
      glText.draw( "The End.", 50, 150 + glText.getCharHeight() );  // Draw Test String
      glText.end();                                   // End Text Rendering

      glText.endProgram();
   }

   public void onSurfaceChanged(int width, int height) {
      GLES20.glViewport( 0, 0, width, height );

      // Setup orthographic projection
      float[] projection = new float[16];
      glText.useProgram();
      Matrix.orthoM(projection, 0,
         0, width,
         0, height,
         1.0f, -1.0f
      );
      glText.setProjectionMatrix(projection);

      // Save width and height
      this.width = width;                             // Save Current Width
      this.height = height;                           // Save Current Height
   }
}
