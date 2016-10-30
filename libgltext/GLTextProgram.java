package ninja.jun.gl.libgltext;

import android.opengl.GLES20;
import android.util.Log;

/**
 * Shader program to drive GLText.
 */
public class GLTextProgram {
  private static final String TAG = "libgltext";

  private static final String VERTEX_SHADER =
      "uniform mat4 u_MVP;\n"
      + "attribute vec2 a_Position;\n"
      + "attribute vec2 a_TexCoord;\n"
      + "varying vec2 v_TexCoord;  \n"
      + "void main() {\n"
      + "  v_TexCoord = a_TexCoord;\n"
      + "  gl_Position = u_MVP * vec4(a_Position, 0.0, 1.0);\n"
      + "}\n";

  private static final String FRAGMENT_SHADER =
      "precision mediump float;\n"
      + "uniform sampler2D u_Texture;\n"
      + "uniform vec4 u_Color;\n"
      + "varying vec2 v_TexCoord;\n"
      + "void main() {\n"
      + "  float textAlpha = texture2D(u_Texture, v_TexCoord).w;\n"
      // TODO: why doesn't work if don't discard
      + "  if (textAlpha == 0.0) discard;\n"
      + "  gl_FragColor = textAlpha * u_Color;\n"
      + "}\n";

  private int program = 0;
  int u_MVP = -1;
  int u_Color = -1;
  int u_Texture = -1;
  int a_Position = -1;
  int a_Normal = -1;
  int a_Color = -1;
  int a_TexCoord = -1;

  void use() {
    GLES20.glUseProgram(program);
  }

  private void checkParam(int param, String sParam) {
    if (param < 0) {
      Log.i(TAG, "Fail to get " + sParam + " location in GLTextProgram");
    }
  }

  void getLocations() {
    checkParam((a_Position = GLES20.glGetAttribLocation(program, "a_Position")), "a_Position");
    checkParam((a_Normal = GLES20.glGetAttribLocation(program, "a_Normal")), "a_Normal");
    checkParam((a_TexCoord = GLES20.glGetAttribLocation(program, "a_TexCoord")), "a_TexCoord");
    checkParam((a_Color = GLES20.glGetAttribLocation(program, "a_Color")), "a_Color");
    checkParam((u_Color = GLES20.glGetUniformLocation(program, "u_Color")), "u_Color");
    checkParam((u_Texture = GLES20.glGetUniformLocation(program, "u_Texture")), "u_Texture");
    checkParam((u_MVP = GLES20.glGetUniformLocation(program, "u_MVP")), "u_MVP");

    checkGLError("GLText program params");
  }

  private int loadGLShader(int type, String code) {
    int shader = GLES20.glCreateShader(type);
    GLES20.glShaderSource(shader, code);
    GLES20.glCompileShader(shader);

    // Get the compilation status.
    final int[] compileStatus = new int[1];
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

    // If the compilation failed, delete the shader.
    if (compileStatus[0] == 0) {
      Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
      GLES20.glDeleteShader(shader);
      shader = 0;
    }

    if (shader == 0) {
      throw new RuntimeException("Error creating shader.");
    }

    return shader;
  }

  static void checkGLError(String label) {
    int error;
    while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
      Log.e(TAG, label + ": glError " + error);
      throw new RuntimeException(label + ": glError " + error);
    }
  }

  public void init() {
    int vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER);
    int fragShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER);

    program = GLES20.glCreateProgram();
    GLES20.glAttachShader(program, vertexShader);
    GLES20.glAttachShader(program, fragShader);
    GLES20.glLinkProgram(program);
    GLES20.glUseProgram(program);

    checkGLError("GLText program");

    getLocations();

    GLES20.glUniform1i(u_Texture, 0);
    checkGLError("GLText program set texure sampler");
  }
}
